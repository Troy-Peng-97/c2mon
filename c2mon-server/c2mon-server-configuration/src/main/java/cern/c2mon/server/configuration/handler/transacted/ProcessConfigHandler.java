/******************************************************************************
 * Copyright (C) 2010-2018 CERN. All rights not expressly granted are reserved.
 *
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 *
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package cern.c2mon.server.configuration.handler.transacted;

import cern.c2mon.cache.actions.alive.AliveTagService;
import cern.c2mon.cache.actions.process.ProcessCacheObjectFactory;
import cern.c2mon.cache.actions.process.ProcessService;
import cern.c2mon.cache.actions.state.SupervisionStateTagService;
import cern.c2mon.cache.api.C2monCache;
import cern.c2mon.cache.api.exception.CacheElementNotFoundException;
import cern.c2mon.server.cache.loading.ProcessDAO;
import cern.c2mon.server.common.alive.AliveTag;
import cern.c2mon.server.common.process.Process;
import cern.c2mon.server.configuration.config.ConfigurationProperties;
import cern.c2mon.server.configuration.impl.ProcessChange;
import cern.c2mon.server.daq.JmsContainerManager;
import cern.c2mon.shared.client.configuration.ConfigConstants;
import cern.c2mon.shared.client.configuration.ConfigurationElementReport;
import cern.c2mon.shared.common.CacheEvent;
import cern.c2mon.shared.common.ConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.UnexpectedRollbackException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Bean managing configuration updates to C2MON DataTags.
 *
 * @author Alexandros Papageorgiou
 */
@Slf4j
@Named
public class ProcessConfigHandler extends BaseConfigHandlerImpl<Process> {

  private final C2monCache<AliveTag> aliveTimerCache;
  private final ProcessService processService;
  private final JmsContainerManager jmsContainerManager;
  private final EquipmentConfigHandler equipmentConfigTransacted;
  private final SupervisionStateTagService stateTagService;
  private final boolean allowRunningProcessRemoval;
  private final AliveTagService aliveTagService;
  private AliveTagConfigHandler aliveTimerConfigHandler;

  /**
   * Autowired constructor.
   *
   * @param processCache the cache bean
   * @param processDAO   the DAO bean
   */
  @Inject
  public ProcessConfigHandler(final C2monCache<Process> processCache, final ProcessDAO processDAO,
                              final ProcessCacheObjectFactory processCacheObjectFactory,
                              final AliveTagService aliveTagService,
                              final ProcessService processService,
                              final SupervisionStateTagService stateTagService,
                              final ConfigurationProperties properties,
                              final JmsContainerManager jmsContainerManager,
                              final EquipmentConfigHandler equipmentConfigTransacted
                                     ) {
    super(processCache, processDAO, processCacheObjectFactory, ArrayList::new);
    this.aliveTagService = aliveTagService;
    this.aliveTimerCache = aliveTagService.getCache();
    this.processService = processService;
    this.stateTagService = stateTagService;
    this.allowRunningProcessRemoval = properties.isAllowRunningProcessRemoval();
    this.jmsContainerManager = jmsContainerManager;
    this.equipmentConfigTransacted = equipmentConfigTransacted;
  }

  /**
   * Ensures that the Alive-, Status- have appropriately the Process id set.
   *
   * @param process The process to which the control tags are assigned
   */
  @Override
  protected void doPostCreate(Process process) {
    // TODO (Alex) Switch to CacheEvent.INSERTED ?
    jmsContainerManager.subscribe(process);

    if (process.getAliveTagId() != null)
      aliveTagService.startOrUpdateTimestamp(process.getAliveTagId(), System.currentTimeMillis());

    cache.getCacheListenerManager().notifyListenersOf(CacheEvent.UPDATE_ACCEPTED, process);

    // TODO (Alex) unsubscribe on failures?
//    if (process != null) {
//      jmsContainerManager.unsubscribe(process);
//    }

    try {
      aliveTimerCache.computeQuiet(process.getAliveTagId(), aliveTimer -> {
        log.trace("Adding process id #{} to alive timer {} (#{})", process.getId(), aliveTimer.getSupervisedName(), aliveTimer.getId());
//        aliveTimer.setSupervisedId(process.getId());
        // TODO (Alex) Copy constructor here? Is it even possible that we would want to change a supervised id?
      });
    } catch (CacheElementNotFoundException e) {
      throw new ConfigurationException(ConfigurationException.INVALID_PARAMETER_VALUE,
        String.format("No Alive tag (%s) found for process #%d (%s).", process.getAliveTagId(), process.getId(), process.getName()));
    }
  }

  @Override
  public List<ProcessChange> update(Long id, Properties properties) {
    removeKeyIfExists(properties, "id");
    removeKeyIfExists(properties, "name");

    List<ProcessChange> processChanges = super.update(id, properties);

    if (properties.containsKey("aliveInterval") || properties.containsKey("aliveTagId")) {
      Process process = processService.getCache().get(id);
      aliveTagService.updateBasedOnSupervised(process);

      // TODO (Alex) Is this call correct? Looks like maybe they wanted to setReboot instead?
//      processChanges.requiresReboot();
    }

    return processChanges;
  }

  /**
   * Tries to remove the process and all its descendants. The process
   * itself is only completely removed if all the equipments, subequipments
   * and associated tags, commands are all removed successfully.
   * <p>
   * In the case of a failure, the removal is interrupted and the process
   * remains with whatever child objects remain at the point of failure.
   *
   * @param id     id of process
   * @param report the element report for the removal of the process, to which
   *               subreports can be attached
   */
  @Override
  public List<ProcessChange> remove(Long id, ConfigurationElementReport report) {
    Process process = cache.get(id);

    boolean isRunning = process.getStateTagId() != null && stateTagService.isRunning(process.getStateTagId());

    if (isRunning && !allowRunningProcessRemoval) {
      String message = "Unable to remove Process " + process.getName() + " as currently running - please stop it first.";
      log.warn(message);
      report.setFailure(message);
      return defaultValue.get();
    } else
      return super.remove(id, report);
  }

  @Override
  protected void doPreRemove(Process process, ConfigurationElementReport report) {

    Collection<Long> equipmentIds = new ArrayList<>(process.getEquipmentIds());

    //remove all associated equipment from system
    for (Long equipmentId : equipmentIds) {
      ConfigurationElementReport childElementReport = new ConfigurationElementReport(ConfigConstants.Action.REMOVE, ConfigConstants.Entity.EQUIPMENT, equipmentId);
      try {
        report.addSubReport(childElementReport);
        equipmentConfigTransacted.remove(equipmentId, childElementReport);
      } catch (RuntimeException ex) {
        log.error("Exception caught while applying the configuration change (Action, Entity, Entity id) = ("
          + ConfigConstants.Action.REMOVE + "; " + ConfigConstants.Entity.EQUIPMENT + "; " + equipmentId + ")", ex);
        childElementReport.setFailure("Exception caught while applying the configuration change.", ex);
        throw new UnexpectedRollbackException("Unexpected exception caught while removing an Equipment.", ex);
      }
    }

    log.debug("Removing Process control tags for process " + process.getId());
    Long aliveTagId = process.getAliveTagId();
    if (aliveTagId != null) {
      ConfigurationElementReport tagReport = new ConfigurationElementReport(ConfigConstants.Action.REMOVE, ConfigConstants.Entity.CONTROLTAG, aliveTagId);
      report.addSubReport(tagReport);
      aliveTimerConfigHandler.remove(aliveTagId, tagReport);
      aliveTagService.removeAliveTimer(aliveTagId);
      // TODO (Alex) Do we want to also remove it from process cache?
    }

    jmsContainerManager.unsubscribe(process);
  }

  /**
   * Removes an equipment reference from the process that contains it.
   *
   * @param equipmentId the equipment to remove
   * @param processId   the process to remove the equipment reference from
   * @throws UnexpectedRollbackException if this operation fails
   */
  public void removeEquipmentFromProcess(Long equipmentId, Long processId) {
    log.debug("Removing Process Equipment {} for processId {}", equipmentId, processId);
    cache.compute(processId, process -> process.getEquipmentIds().remove(equipmentId));
  }

}