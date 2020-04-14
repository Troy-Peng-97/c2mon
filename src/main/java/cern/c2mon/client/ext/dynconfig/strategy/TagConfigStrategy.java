package cern.c2mon.client.ext.dynconfig.strategy;

import cern.c2mon.client.ext.dynconfig.DynConfigException;
import cern.c2mon.client.ext.dynconfig.config.ProcessEquipmentURIMapping;
import cern.c2mon.client.ext.dynconfig.query.IQueryObj;
import cern.c2mon.client.ext.dynconfig.query.QueryKey;
import cern.c2mon.client.ext.dynconfig.query.QueryObj;
import cern.c2mon.shared.client.configuration.api.equipment.Equipment;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.common.datatag.DataTagAddress;
import cern.c2mon.shared.common.datatag.address.HardwareAddress;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A common superclass to the protocol-specific implementation classes of {@link ITagConfigStrategy}. This class handles
 * logic common across all strategies such as creating the IQueryObj, DataTag and Equipment objects with proper parameter values.
 */
@Slf4j
@NoArgsConstructor
public abstract class TagConfigStrategy {
    /**
     * Those Query Keys that must be especially handled by the strategies either through value conversions or nonstandard method calls
     */
    public static final QueryKey<String> TAG_NAME = new QueryKey<>("tagName");
    private static final QueryKey<String> TAG_DESCRIPTION = new QueryKey<>("tagDescription", "dynamically configured tag", false);
    private static final QueryKey<Class<?>> DATA_TYPE = new QueryKey<>("dataType", Object.class, false);

    protected String messageHandler;
    protected IQueryObj queryObj;

    /**
     * Check whether a regular expression matches the strategy including the parameters parsed from the query URI
     * @param pattern a regular expression
     * @return whether of not the pattern matches the strategy including the parameters parsed from the query URI
     */
    public boolean matches(String pattern) {
        return queryObj.matches(pattern);
    }

    /**
     * Create the equipment configuration which can then be passed to the C2MON server
     * @param mapping contains additional specification regarding the C2MON-internal equipment name and description
     * @throws DynConfigException if the equipment address passed through the original query is malformed
     * @return equipmentBuilder the equipmentBuilder to extend with protocol-specific fields
     */
    public Equipment prepareEquipmentConfiguration(ProcessEquipmentURIMapping mapping) throws DynConfigException {
        Equipment.CreateBuilder builder = Equipment.create(mapping.getEquipmentName(), messageHandler)
                .description(mapping.getEquipmentDescription())
                .address(queryObj.getUriWithoutParams());
        return builder.build();
    }

    protected void createQueryObj(URI uri, Collection<? extends QueryKey<?>> protocolKeys) throws DynConfigException {
        List<QueryKey<?>> keys = Stream.concat(protocolKeys.stream(), Stream.of(TAG_NAME, TAG_DESCRIPTION, DATA_TYPE))
                .collect(Collectors.toList());
        this.queryObj = new QueryObj(uri, keys);
    }

    protected DataTag toTagConfiguration(HardwareAddress hwAddress) throws DynConfigException {
        DataTagAddress address = new DataTagAddress(hwAddress);
        queryObj.applyQueryPropertiesTo(address);

        DataTag.CreateBuilder builder = DataTag
                .create(queryObj.get(TAG_NAME).get(0), queryObj.get(DATA_TYPE, Class.class).get(0), address)
                .description(StringUtils.join(queryObj.get(TAG_DESCRIPTION), ", "));
        queryObj.applyQueryPropertiesTo(builder);
        return builder.build();
    }
}
