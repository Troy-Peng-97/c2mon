package cern.c2mon.server.configuration.handler.transacted;

import cern.c2mon.cache.actions.supervision.SupervisedCacheService;
import cern.c2mon.cache.api.factory.AbstractCacheObjectFactory;
import cern.c2mon.server.cache.loading.ConfigurableDAO;
import cern.c2mon.server.common.alive.AliveTag;
import cern.c2mon.server.common.commfault.CommFaultTag;
import cern.c2mon.server.common.control.ControlTag;
import cern.c2mon.server.common.supervision.SupervisionStateTag;
import cern.c2mon.server.configuration.impl.ProcessChange;
import cern.c2mon.server.configuration.parser.factory.EntityFactory;
import cern.c2mon.shared.client.configuration.ConfigConstants;
import cern.c2mon.shared.client.configuration.ConfigurationElement;
import cern.c2mon.shared.client.configuration.ConfigurationElementReport;
import cern.c2mon.shared.common.CacheEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractControlTagConfigHandler<
  CONTROL extends ControlTag, CONTROL_ENTITY extends cern.c2mon.shared.client.configuration.api.tag.ControlTag>
  extends BaseConfigHandlerImpl<CONTROL> {

  protected final SupervisedCacheService<CONTROL> service;
  private final EntityFactory<CONTROL_ENTITY> entityFactory;
  private static final Map<Class<? extends ControlTag>, ConfigConstants.Entity> constantMap;

  static {
    constantMap = new HashMap<>();
    constantMap.put(AliveTag.class, ConfigConstants.Entity.ALIVETAG);
    constantMap.put(CommFaultTag.class, ConfigConstants.Entity.COMMFAULTTAG);
    constantMap.put(SupervisionStateTag.class, ConfigConstants.Entity.STATETAG);
  }

  protected AbstractControlTagConfigHandler(
    SupervisedCacheService<CONTROL> service,
    ConfigurableDAO<CONTROL> cacheLoaderDAO,
    AbstractCacheObjectFactory<CONTROL> factory,
    EntityFactory<CONTROL_ENTITY> entityFactory) {
    super(service.getCache(), cacheLoaderDAO, factory, ArrayList::new);
    this.service = service;
    this.entityFactory = entityFactory;
  }

  @Override
  protected void doPostCreate(CONTROL cacheable) {
    cache.getCacheListenerManager().notifyListenersOf(CacheEvent.INSERTED, cacheable);
  }

  @Override
  protected void doPreRemove(CONTROL controlTag, ConfigurationElementReport report) {
    super.doPreRemove(controlTag, report);
    service.stop(controlTag.getId(), System.currentTimeMillis());
  }

  @Override
  protected List<ProcessChange> removeReturnValue(CONTROL controlTag, ConfigurationElementReport report) {
    final List<ProcessChange> processChanges = super.removeReturnValue(controlTag, report);
    ConfigurationElementReport tagReport = new ConfigurationElementReport(ConfigConstants.Action.REMOVE,
      constantMap.get(controlTag.getClass()), controlTag.getId());
    report.addSubReport(tagReport);
    return processChanges;
  }

  public abstract List<ProcessChange> createBySupervised(ConfigurationElement configurationElement);

  protected List<ProcessChange> createBySupervised(ConfigurationElement configurationElement, String propertyName, Supplier<CONTROL_ENTITY> supplier) {
    List<ProcessChange> changes = new ArrayList<>();

    Long supervisionId = null;
    if (configurationElement.getElementProperties().containsKey(propertyName)) {
      supervisionId = Long.parseLong(configurationElement.getElementProperties().getProperty(propertyName));
    }

    if (supervisionId == null || !cache.containsKey(supervisionId)) {
      CONTROL_ENTITY controlTag = supplier.get();
      ConfigurationElement configStatusTag = entityFactory.createInstance(controlTag).get(0);
      configurationElement.getElementProperties().setProperty(propertyName, controlTag.getId().toString());
      changes = super.create(configStatusTag);
    }

    return changes;
  }
}