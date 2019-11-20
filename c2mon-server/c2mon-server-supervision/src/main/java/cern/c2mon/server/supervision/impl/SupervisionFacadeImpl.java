/******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
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
package cern.c2mon.server.supervision.impl;

import cern.c2mon.cache.actions.equipment.EquipmentService;
import cern.c2mon.cache.actions.process.ProcessService;
import cern.c2mon.cache.actions.subequipment.SubEquipmentService;
import cern.c2mon.server.common.supervision.Supervised;
import cern.c2mon.server.supervision.SupervisionFacade;
import cern.c2mon.shared.client.supervision.SupervisionEvent;
import cern.c2mon.shared.common.supervision.SupervisionConstants.SupervisionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of the SupervisionFacade.
 * 
 * @author Mark Brightwell
 *
 */
@Service
@ManagedResource(objectName="cern.c2mon:name=supervisionFacade")
@Slf4j
public class SupervisionFacadeImpl implements SupervisionFacade {
  
  /**
   * Delay at server start-up before current supervision status saved to the DB.
   */
  public static final int INITIAL_LOGGING_DELAY = 120;

  private final ProcessService processService;
  private final EquipmentService equipmentService;
  private final SubEquipmentService subEquipmentService;
 
  /**
   * Management value tracing the number of requests for the supervision status
   * that are waiting for a response.
   */
  private volatile AtomicInteger pendingRequests = new AtomicInteger(0);


  @Inject
  public SupervisionFacadeImpl(final ProcessService processService, final EquipmentService equipmentService,
                               final SubEquipmentService subEquipmentService) {

    this.processService = processService;
    this.equipmentService = equipmentService;
    this.subEquipmentService = subEquipmentService;
  }

  @Override
  public Collection<SupervisionEvent> getAllSupervisionStates() {
    try {
      pendingRequests.getAndIncrement();

      Collection<SupervisionEvent> supervisionCollection = new ArrayList<>();
      supervisionCollection.addAll(processService.getAllSupervisionEvents());
      supervisionCollection.addAll(equipmentService.getAllSupervisionEvents());
      supervisionCollection.addAll(subEquipmentService.getAllSupervisionEvents());

      return supervisionCollection;   
    } finally {
      pendingRequests.getAndDecrement();
    }    
  }
  
  @Override
  public void refreshStateTags() {
    Timestamp refreshTime = new Timestamp(System.currentTimeMillis());
    for (Long key : processCache.getKeys()) {
      refreshStateTag(processCache.get(key), refreshTime);            
    }
    for (Long key : equipmentCache.getKeys()) {
      refreshStateTag(equipmentCache.get(key), refreshTime);
    }
    for (Long key : subEquipmentCache.getKeys()) {
      refreshStateTag(subEquipmentCache.get(key), refreshTime);
    }    
  }
  
  /**
   * Refreshes the state tag, using the current supervision status.
   * Will only update the status tags in the cache if they have actually changed.
   * @param supervised supervised object
   */
  private void refreshStateTag(final Supervised supervised, final Timestamp refreshTime) {
    try {
      Long stateTagId;
      String message;
      SupervisionStatus status;
     
      stateTagId = supervised.getStateTagId();
      message = supervised.getStatusDescription();
      status = supervised.getSupervisionStatus();                   
       
      controlTagFacade.updateAndValidate(stateTagId, status.toString(), message, refreshTime);
    } catch (Exception e) {
      LOGGER.error("Error while refreshing state tag for " + supervised.getSupervisionEntity() 
          + " " + supervised.getId() + " - unable to refresh this tag.", e);
    }    
  }

  /**
   * For management purposes.
   * Updates the supervision timestamp of all Processes and (Sub)Equipments, notifying
   * all listeners of the change (republishing supervision events also and re-evaluating
   * all alarms); takes some minutes to refresh all alarms.
   */
  @ManagedOperation(description="Refresh supervision timestamps of Processes/(Sub)Equipments and notify all listeners.")
  @Override
  public void refreshAllSupervisionStatus() {
    notifyAllSupervisedCachesOfUpdate();
  }

  /**
   * Notifies all listeners of all supervised cache objects (so Process, Equipment,
   * SubEquipments) with the current object. In particular the SupervisionNotifier
   * is called.
   *
   * <p>This is used to refresh all listeners with the latest values for supervision
   * purposes (in case of a previous server failure when some may not have been logged to DB).
   */
  private void notifyAllSupervisedCachesOfUpdate() {
    for (Long key : processCache.getKeys()) {
      processFacade.refreshAndnotifyCurrentSupervisionStatus(key);
    }
    for (Long key : equipmentCache.getKeys()) {
      equipmentFacade.refreshAndnotifyCurrentSupervisionStatus(key);
    }
    for (Long key : subEquipmentCache.getKeys()) {
      subEquipmentFacade.refreshAndnotifyCurrentSupervisionStatus(key);
    }
  }
  
  /**
   * For management purposes.
   * @return the number of supervison status requests pending
   */
  @ManagedAttribute(description="Number of pending supervison status requests.")
  public int getPendingRequests() {
    return pendingRequests.intValue();
  }
  
}
