package cern.c2mon.client.core.config.dynamic;

import cern.c2mon.client.common.tag.Tag;
import cern.c2mon.client.core.service.ConfigurationService;
import cern.c2mon.client.core.service.TagService;
import cern.c2mon.client.core.config.C2monClientDynConfigProperties;
import cern.c2mon.client.core.config.C2monClientDynConfigProperties.ProcessEquipmentURIMapping;
import cern.c2mon.client.core.config.dynamic.strategy.ITagConfigStrategy;
import cern.c2mon.client.core.config.dynamic.strategy.TagConfigStrategy;
import cern.c2mon.shared.client.configuration.ConfigConstants;
import cern.c2mon.shared.client.configuration.ConfigurationReport;
import cern.c2mon.shared.client.configuration.api.equipment.Equipment;
import cern.c2mon.shared.client.configuration.api.process.Process;
import cern.c2mon.shared.client.configuration.api.tag.CommandTag;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.client.process.ProcessNameResponse;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Component
@ConditionalOnProperty(prefix = "c2mon.client.dynconfig", name = "active", havingValue = "true")
@NoArgsConstructor
public class DynConfigService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private TagService tagService;

    @Autowired
    private C2monClientDynConfigProperties config;

    /**
     * Delete the C2MON tag corresponding to a given URI if it exists.
     * @param uri the uri describing the address for which the corresponding C2MON tag shall be deleted
     * @throws DynConfigException if the Tag exists but could not be deleted.
     */
    public void deleteTagForURI(URI uri) throws DynConfigException {
        String tagName = URIParser.toTagName(uri);
        Collection<Tag> tags = tagService.findByName(tagName);
        if (!tags.isEmpty()) {
            log.info("Deleting tags");
            deleteTags(tags);
        } else {
            log.info("No data tags to delete");
        }
    }

    /**
     * /** For a given URI, query the corresponding tag. Create tag if not found.
     * @param uri describes hardware address for which a C2MON tag shall be fetched or created.
     * @return A C2MON tag that can be used to subscribe to data.
     * @throws DynConfigException if the URI cannot be parsed or the tag cannot be created.
     */
    public Tag getTagForURI(URI uri) throws DynConfigException {
        String tagName = URIParser.toTagName(uri);
        Collection<Tag> tagsForUri = tagService.findByName(tagName);
        if (tagsForUri.isEmpty()) {
            log.info("No tag could be found for  URI {}. Creating... ", uri);
            final ConfigurationReport report = createTags(uri, tagName);
            String tagIds = tagsForUri.stream().map(t -> String.valueOf(t.getId())).collect(Collectors.joining(", "));
            log.info("Tags {} processed with status {}: {}", tagIds, report.getStatus(), report.getStatusDescription());
            tagsForUri = tagService.findByName(tagName);
            if (report.getStatus().equals(ConfigConstants.Status.FAILURE) || !tagsForUri.iterator().hasNext()) {
                throw new DynConfigException(DynConfigException.Context.CREATE_TAG);
            }
        } else {
            log.info("Tag {} already exists. ", tagsForUri);
        }
        return tagsForUri.iterator().next();
    }

    /**
     * For a set of URIs, query the corresponding tag. Create tags if not found.
     * @param uris a collection of uris each describing a hardware address for which a C2MON tag shall be fetched or
     *             created
     * @return A C2MON tag per uri that can be used to subscribe to data.
     */
    public List<Tag> getTagsForURI(Collection<URI> uris) {
        List<Tag> tags = new ArrayList<>();
        for (URI uri : uris) {
            try {
                tags.add(getTagForURI(uri));
            } catch (DynConfigException e) {
                log.error("Could not find a mapping for URI {}. Skip and proceed...", uri.toString(), e);
            }
        }
        return tags;
    }

    private void deleteTags(Collection<? extends Tag> dataTags) throws DynConfigException {
        Set<Long> tagIds = dataTags.stream().map(Tag::getId).collect(Collectors.toSet());
        ConfigurationReport report = configurationService.removeDataTagsById(tagIds);
        tagService.refresh(tagIds);
        log.info("Tags {} processed with status {}: {}", tagIds.stream().map(String::valueOf).collect(Collectors.joining(", ")), report.getStatus(), report.getStatusDescription());
        if (report.getStatus().equals(ConfigConstants.Status.FAILURE)) {
            throw new DynConfigException(DynConfigException.Context.DELETE_TAG);
        }
    }

    private ConfigurationReport createTags(URI uri, String tagName) throws DynConfigException {
        ITagConfigStrategy strategy = ITagConfigStrategy.of(uri);
        ProcessEquipmentURIMapping mapping = findMappingFor(strategy, tagName);
        log.info("Using mapping {}", mapping.toString());
        final Optional<ProcessNameResponse> processCandidate = anyRunningProcesses(mapping);
        if (!processCandidate.isPresent()) {
            log.info("No compatible process is running. Creating process and equipment. ");
            createProcess(mapping);
            createEquipment(mapping, strategy);
        } else if (!isEquipmentInProcess(processCandidate.get().getProcessName(), mapping)) {
            log.info("No compatible equipment is running. Creating equipment. ");
            createEquipment(mapping, strategy);
        }
        final TagConfigStrategy.TagType tagType = URIParser.getTagType(uri);
        if (tagType.equals(TagConfigStrategy.TagType.COMMAND)) {
            final CommandTag commandTag = strategy.prepareCommandTagConfigurations();
            return configurationService.createCommandTag(mapping.getEquipmentName(), commandTag);
        } else {
            DataTag dataTag = strategy.prepareDataTagConfigurations();
            return configurationService.createDataTag(mapping.getEquipmentName(), dataTag);
        }
    }

    private Optional<ProcessNameResponse> anyRunningProcesses(ProcessEquipmentURIMapping mapping) {
        return configurationService.getProcessNames().stream()
                .filter(response -> response.getProcessName().contains(mapping.getProcessName()))
                .findFirst();
    }

    private boolean isEquipmentInProcess(String processName, ProcessEquipmentURIMapping mapping) {
        return configurationService.getProcessXml(processName).contains(mapping.getEquipmentName());
    }

    private void createProcess(ProcessEquipmentURIMapping mapping) {
        Process process = Process.create(mapping.getProcessName())
                .id(mapping.getProcessId())
                .description(mapping.getProcessDescription())
                .build();
        configurationService.createProcess(process);
        log.info("Process created: {}", process.getName());
    }

    private void createEquipment(ProcessEquipmentURIMapping mapping, ITagConfigStrategy strategy) throws DynConfigException {
        Equipment equipment = strategy.prepareEquipmentConfiguration(mapping);
        configurationService.createEquipment(mapping.getProcessName(), equipment);
        log.info("Equipment created: {}", equipment.getName());
    }

    private ProcessEquipmentURIMapping findMappingFor(ITagConfigStrategy strategy, String tagName) throws DynConfigException {
        return config.getMappings().stream()
                .filter(m -> strategy.matches(m.getUriPattern()))
                .findFirst()
                .orElseThrow(() -> new DynConfigException(DynConfigException.Context.NO_MAPPING, tagName));
    }
}