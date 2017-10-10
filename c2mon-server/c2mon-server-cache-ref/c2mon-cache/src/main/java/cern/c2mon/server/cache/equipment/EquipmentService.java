package cern.c2mon.server.cache.equipment;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cern.c2mon.cache.api.Cache;
import cern.c2mon.cache.api.service.AbstractEquipmentService;
import cern.c2mon.cache.api.service.SupervisedService;
import cern.c2mon.server.cache.CoreAbstractEquipmentService;
import cern.c2mon.server.cache.SupervisedServiceImpl;
import cern.c2mon.server.cache.alivetimer.AliveTimerService;
import cern.c2mon.server.cache.commfault.CommFaultService;
import cern.c2mon.server.common.datatag.DataTag;
import cern.c2mon.server.common.equipment.Equipment;
import cern.c2mon.server.common.process.Process;
import cern.c2mon.shared.client.supervision.SupervisionEvent;
import cern.c2mon.shared.common.supervision.SupervisionConstants;

/**
 * @author Szymon Halastra
 */

@Slf4j
@Service
public class EquipmentService implements SupervisedService<Equipment>, AbstractEquipmentService {

  private final Cache<Long, Equipment> equipmentCacheRef;

  private final Cache<Long, Process> processCacheRef;

  private final Cache<Long, DataTag> dataTagCacheRef;

  private final SupervisedService<Equipment> supervisedService;

  private final AbstractEquipmentService coreEquipmentService;

  @Autowired
  public EquipmentService(final Cache<Long, Equipment> equipmentCacheRef, final Cache<Long, Process> processCacheRef,
                          final Cache<Long, DataTag> dataTagCacheRef, final AliveTimerService aliveTimerService, final CommFaultService commFaultService) {
    this.equipmentCacheRef = equipmentCacheRef;
    this.processCacheRef = processCacheRef;
    this.dataTagCacheRef = dataTagCacheRef;

    this.supervisedService = new SupervisedServiceImpl(equipmentCacheRef, aliveTimerService);
    this.coreEquipmentService = new CoreAbstractEquipmentService<>(equipmentCacheRef, commFaultService);
  }

  //TODO: write this method
  public Collection<? extends Long> getDataTagIds(long equipmentId) {
    return null;
  }

  public Cache getCache() {
    return this.equipmentCacheRef;
  }

  @Override
  public SupervisionEvent getSupervisionStatus(Long id) {
    return supervisedService.getSupervisionStatus(id);
  }

  @Override
  public void refreshAndNotifyCurrentSupervisionStatus(Long id) {
    supervisedService.refreshAndNotifyCurrentSupervisionStatus(id);
  }

  @Override
  public void start(Long id, Timestamp timestamp) {
    supervisedService.start(id, timestamp);
  }

  @Override
  public void start(Equipment supervised, Timestamp timestamp) {
    supervisedService.start(supervised, timestamp);
  }

  @Override
  public void stop(Equipment supervised, Timestamp timestamp) {
    supervisedService.stop(supervised, timestamp);
  }

  @Override
  public void stop(Long id, Timestamp timestamp) {
    supervisedService.stop(id, timestamp);
  }

  @Override
  public void resume(Long id, Timestamp timestamp, String message) {
    supervisedService.resume(id, timestamp, message);
  }

  @Override
  public void suspend(Long id, Timestamp timestamp, String message) {
    supervisedService.suspend(id, timestamp, message);
  }

  @Override
  public boolean isRunning(Equipment supervised) {
    return supervisedService.isRunning(supervised);
  }

  @Override
  public boolean isRunning(Long id) {
    return supervisedService.isRunning(id);
  }

  @Override
  public boolean isUncertain(Equipment supervised) {
    return supervisedService.isUncertain(supervised);
  }

  @Override
  public void removeAliveTimer(Long id) {
    supervisedService.removeAliveTimer(id);
  }

  @Override
  public void loadAndStartAliveTag(Long supervisedId) {
    supervisedService.loadAndStartAliveTag(supervisedId);
  }

  @Override
  public void removeAliveDirectly(Long aliveId) {
    supervisedService.removeAliveTimer(aliveId);
  }

  @Override
  public SupervisionConstants.SupervisionEntity getSupervisionEntity() {
    return supervisedService.getSupervisionEntity();
  }

  @Override
  public void setSupervisionEntity(SupervisionConstants.SupervisionEntity entity) {
    supervisedService.setSupervisionEntity(entity);
  }

  @Override
  public Long getProcessIdForAbstractEquipment(Long abstractEquipmentId) {
    return coreEquipmentService.getProcessIdForAbstractEquipment(abstractEquipmentId);
  }

  @Override
  public Map<Long, Long> getAbstractEquipmentControlTags() {
    return coreEquipmentService.getAbstractEquipmentControlTags();
  }

  @Override
  public void removeCommFault(Long abstractEquipmentId) {
    coreEquipmentService.removeCommFault(abstractEquipmentId);
  }
}