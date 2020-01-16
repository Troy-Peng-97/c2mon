package cern.c2mon.cache.actions.alive;

import cern.c2mon.cache.actions.AbstractCacheServiceImpl;
import cern.c2mon.cache.actions.commfault.CommFaultService;
import cern.c2mon.cache.actions.state.SupervisionStateTagService;
import cern.c2mon.cache.api.C2monCache;
import cern.c2mon.server.common.alive.AliveTag;
import cern.c2mon.server.common.supervision.Supervised;
import cern.c2mon.server.common.thread.Event;
import cern.c2mon.shared.common.CacheEvent;
import cern.c2mon.shared.common.datatag.SourceDataTagValue;
import cern.c2mon.shared.common.supervision.SupervisionEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Manages operations on {@link AliveTag}s
 *
 * @author Szymon Halastra, Alexandros Papageorgiou Koufidis
 * @see AliveTag
 */
@Slf4j
@Service
public class AliveTagService extends AbstractCacheServiceImpl<AliveTag> {

  private CommFaultService commFaultService;
  private final SupervisionStateTagService stateTagService;

  @Inject
  public AliveTagService(C2monCache<AliveTag> aliveTimerCacheRef, CommFaultService commFaultService,
                         SupervisionStateTagService stateTagService) {
    super(aliveTimerCacheRef, new AliveTagCacheFlow());
    this.commFaultService = commFaultService;
    this.stateTagService = stateTagService;
  }

  @PostConstruct
  public void init() {
    // After caches have been populated
    getCache().getCacheListenerManager().registerListener(this::cascadeUpdate, CacheEvent.UPDATE_ACCEPTED);
    // TODO (Alex) Should we also listen to events from CommFaultTags that change AliveTags? Is this something that happens?
  }

  private void cascadeUpdate(@NonNull AliveTag aliveTag) {
    if (aliveTag.getSupervisedEntity() == null) {
      throw new IllegalArgumentException("AliveTag " +aliveTag.getName() +" does not have a valid SupervisedEntity assigned ");
    }

    if (aliveTag.getSupervisedEntity() == SupervisionEntity.PROCESS) {
      stateTagService.updateBasedOnControl(aliveTag.getStateTagId(), aliveTag);
    } else
      commFaultService.updateBasedOnAliveTimer(aliveTag);
  }

  public boolean isRegisteredAliveTimer(final Long id) {
    return cache.containsKey(id);
  }

  /**
   * Same as {@link AliveTagService#start(long,long)}, but will start the object
   * regardless of previous state (active or not)
   */
  public void startOrUpdateTimestamp(long aliveTimerId, long timestamp) throws NullPointerException {
    setAliveTimerAsActive(aliveTimerId, true, timestamp);
  }

  /**
   * Find the {@code AliveTimer} object with {@code aliveTimerId} in the cache
   * and if it is stopped (not active), then do
   *
   * <ul>
   *   <li>{@code AliveTimer#setActive(true)}
   *   <li>{@code AliveTimer#setLastUpdate(now)}
   *   <li>Reinsert into cache
   * </ul>
   * <p>
   * The timestamp will not be updated, unless there is a change.
   * The cache object will not be reinserted, unless there is a change.
   *
   * @param aliveTimerId the alive timer id for the object to be force started
   * @throws NullPointerException when {@code aliveTimerId} is null
   */
  public void start(long aliveTimerId, long timestamp) throws NullPointerException {
    setAliveTimerAsActive(aliveTimerId, true, timestamp);
  }

  /**
   * Find the {@code AliveTimer} object with {@code aliveTimerId} in the cache
   * and if it is started (active), then do
   *
   * <ul>
   *   <li>{@code AliveTimer#setActive(false)}
   *   <li>{@code AliveTimer#setLastUpdate(now)}
   *   <li>Reinsert into cache
   * </ul>
   * <p>
   * The timestamp will not be updated, unless there is a change.
   * The cache object will not be reinserted, unless there is a change.
   *
   * @param aliveTimerId the alive timer id for the object to be force started
   * @throws NullPointerException when {@code aliveTimerId} is null
   */
  public void stop(long aliveTimerId, long timestamp) throws NullPointerException {
    setAliveTimerAsActive(aliveTimerId, false, timestamp);
  }

  /**
   * Check whether this alive timer has expired. Adds an additional time buffer
   * as some clocks may be slightly out of sync.
   *
   * @return true if the alive timer is active and it has not been updated since
   * at least "aliveInterval" milliseconds.
   */
  public boolean hasExpired(final Long aliveTimerId) {
    AliveTag aliveTimer = cache.get(aliveTimerId);
    return (System.currentTimeMillis() - aliveTimer.getLastUpdate() > aliveTimer.getAliveInterval() + aliveTimer.getAliveInterval() / 3);
  }

  /**
   * Will set all previously inactive {@link AliveTag}s as active
   * <p>
   * Timestamps will not be affected on previously active {@code AliveTimer}s
   */
  public void startAllInactiveTimers() {
    log.debug("Starting all alive timers in the cache.");
    filterAndSetActive(true);
  }

  /**
   * Will set all previously inactive {@link AliveTag}s as inactive (stopped)
   * <p>
   * Timestamps will not be affected on previously inactive {@code AliveTimer}s
   */
  public void stopAllActiveTimers() {
    log.debug("Stopping all alive timers in the cache.");
    filterAndSetActive(false);
  }

  /**
   * Stops and removes this alive by alive id. Should only be
   * used when it is no longer referenced by a supervised object
   * (for instance on reconfiguration error recovery).
   *
   * @param aliveId id of the alive
   */
  public void removeAliveTimer(long aliveId) {
    stop(aliveId, System.currentTimeMillis());
    cache.remove(aliveId);
  }

  public void createAliveTimerFor(Supervised supervised) {
    AliveTag aliveTimer = new AliveTag(supervised.getAliveTagId(), supervised.getId(), supervised.getName(),
      supervised.getSupervisionEntity(), null, supervised.getStateTagId(), supervised.getAliveInterval());
    cache.put(aliveTimer.getId(), aliveTimer);
  }

  private void filterAndSetActive(boolean active) {
    try {
      for (AliveTag aliveTimer : cache.query(aliveTimer -> aliveTimer.getValue() != active)) {
        log.debug("Attempting to set alive timer " + aliveTimer.getId() + " and dependent alive timers to " + active);
        aliveTimer.setValue(active);
        aliveTimer.setLastUpdate(System.currentTimeMillis());
        cache.put(aliveTimer.getId(), aliveTimer);
      }
    } catch (Exception e) {
      log.error("Unable to retrieve list of alive timers from cache when attempting to set new active status to " + active, e);
    }
  }

  private void setAliveTimerAsActive(long aliveTimerId, boolean active, long timestamp) {
    log.debug("Attempting to set alive timer " + aliveTimerId + " and dependent alive timers to " + active);

    if (!cache.containsKey(aliveTimerId)) {
      log.error("Cannot locate the AliveTimer in the cache (Id is " + aliveTimerId + ") - unable to stop it.");
      return;
    }

    try {
      cache.compute(aliveTimerId, aliveTimer -> {
        if (aliveTimer.setValueAndGetDifferent(active) || timestamp > aliveTimer.getLastUpdate())
          aliveTimer.setLastUpdate(timestamp);
      });
    } catch (Exception e) {
      log.error("Unable to stop the alive timer " + aliveTimerId, e);
    }
  }

  public final String generateSourceXML(final AliveTag aliveTag) {
    StringBuilder str = new StringBuilder("    <DataTag id=\"");
    str.append(aliveTag.getId());
    str.append("\" name=\"");
    str.append(aliveTag.getSupervisedName());
    str.append("\" control=\"true\">\n");

    if (aliveTag.getAddress() != null) {
      str.append(aliveTag.getAddress().toConfigXML());
    }

    str.append("    </DataTag>\n");
    return str.toString();
  }

  /**
   * Updates the tag object if the value is not filtered out. Contains the logic on when a
   * AliveTagCacheObject should be updated with new values and when not (in particular
   * timestamp restrictions).
   *
   * <p>Also notifies the listeners if an update was performed.
   *
   * <p>Notice the tag is not put back in the cache here.
   *
   * @param sourceDataTagValue the source value received from the DAQ
   * @return true if an update was performed (i.e. the value was not filtered out)
   */
  public Event<Boolean> updateFromSource(final SourceDataTagValue sourceDataTagValue) {
    return cache.executeTransaction(() -> {
      final AliveTag aliveTag = cache.get(sourceDataTagValue.getId());

      if (sourceDataTagValue == null) {
        log.error("Attempting to update a dataTag with a null source value - ignoring update.");
        return new Event<>(aliveTag.getCacheTimestamp().getTime(), false);
      }

      // TODO (Alex) This does not properly account for potential filterout as part of cache.put. Should it?
//      Event<Boolean> returnValue = updateFromSource(aliveTag, sourceDataTagValue);

      if (sourceDataTagValue.isValid()) {
        cache.putQuiet(sourceDataTagValue.getId(), aliveTag);
      } else {
        cache.put(sourceDataTagValue.getId(), aliveTag);
      }

      // TODO (Alex) Implement this based on the contents of sourceDataTagValue used
      return new Event<>(System.currentTimeMillis(), false);
    });
  }

  /**
   * Updates the AliveTag based on new supervised properties, e.g after a reconfiguration
   * @param supervised
   */
  public void updateBasedOnSupervised(Supervised supervised) {
    // TODO (Alex)
  }
}
