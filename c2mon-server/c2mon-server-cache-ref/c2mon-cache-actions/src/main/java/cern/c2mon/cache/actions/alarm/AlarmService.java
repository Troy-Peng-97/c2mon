package cern.c2mon.cache.actions.alarm;

import cern.c2mon.cache.actions.oscillation.OscillationUpdater;
import cern.c2mon.cache.actions.tag.UnifiedTagCacheFacade;
import cern.c2mon.cache.api.C2monCache;
import cern.c2mon.cache.api.listener.impl.SingleThreadListener;
import cern.c2mon.cache.config.tag.TagCacheFacade;
import cern.c2mon.server.common.alarm.Alarm;
import cern.c2mon.server.common.alarm.AlarmCacheObject;
import cern.c2mon.server.common.alarm.TagWithAlarms;
import cern.c2mon.server.common.tag.Tag;
import cern.c2mon.shared.common.CacheEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static cern.c2mon.cache.actions.alarm.AlarmCacheObjectController.updateAlarmBasedOnTag;

/**
 * @author Szymon Halastra
 * @author Alexandros Papageorgiou
 * @author Brice Copy
 */
@Slf4j
@Service
public class AlarmService implements AlarmAggregator {

  /**
   * We decided to distribute all alarms on the same topic in order to reduce
   * the number of topics for SonicMQ, the client has to make the decision if
   * the received alarm is useful for it, otherwise it will discard the alarm
   *
   * @see AlarmService#getTopicForAlarm(Alarm)
   */
  public static final String ALARM_TOPIC = "tim.alarm";

  private C2monCache<Alarm> alarmCacheRef;

  private TagCacheFacade tagCacheRef;

  private List<AlarmAggregatorListener> alarmUpdateObservable = new ArrayList<>();

  private AlarmCacheObjectController alarmCacheUpdater;

  private UnifiedTagCacheFacade unifiedTagCacheFacade;

  private OscillationUpdater oscillationUpdater;

  @Inject
  public AlarmService(final C2monCache<Alarm> alarmCacheRef, final TagCacheFacade tagCacheRef,
                      final AlarmCacheObjectController alarmCacheUpdater, final UnifiedTagCacheFacade unifiedTagCacheFacade,
                      final OscillationUpdater oscillationUpdater) {
    this.alarmCacheRef = alarmCacheRef;
    this.tagCacheRef = tagCacheRef;
    this.alarmCacheUpdater = alarmCacheUpdater;
    this.unifiedTagCacheFacade = unifiedTagCacheFacade;
    this.oscillationUpdater = oscillationUpdater;
  }

  @PostConstruct
  public void init() {
    unifiedTagCacheFacade.registerListener(new SingleThreadListener<Tag>(tag -> {
      log.trace("Evaluating alarm for tag " + tag.getId() + " due to supervision status notification.");
      evaluateAlarms(tag);
    }), CacheEvent.SUPERVISION_CHANGE);


    unifiedTagCacheFacade.registerListener(new SingleThreadListener<Tag>(tag -> {
      log.trace("Evaluating alarm for tag " + tag.getId() + " due to update notification.");
      List<Alarm> alarmList = evaluateAlarms(tag);
      notifyListeners(tag, alarmList);
    }), CacheEvent.UPDATE_ACCEPTED);
  }

  public Alarm update(final Long alarmId, final Tag tag) {
    return alarmCacheRef.executeTransaction(() -> {
      Alarm alarm = alarmCacheRef.get(alarmId);
      return update((AlarmCacheObject) alarm, tag, true);
    });
  }

  public void evaluateAlarm(Long alarmId) {
    alarmCacheRef.executeTransaction(() -> {
      Alarm alarm = alarmCacheRef.get(alarmId);
      Tag tag = tagCacheRef.get(alarm.getDataTagId());
      return update(alarmId, tag);
    });
  }

  public List<Alarm> evaluateAlarms(final Tag tag) {
    List<Alarm> linkedAlarms = new ArrayList<>();
    alarmCacheRef.executeTransaction(() -> {
      for (Long alarmId : tag.getAlarmIds()) {
        try {
          linkedAlarms.add(update(alarmId, tag));
        } catch (Exception e) {
          log.error("Exception caught when attempting to evaluate alarm ID " + alarmId + "  for tag " + tag.getId() + " - publishing to the client with no attached alarms.", e);
        }
      }
    });
    return linkedAlarms;
  }

  private AlarmCacheObject update(final AlarmCacheObject alarmCacheObject, final Tag tag, boolean updateOscillation) {
    if (updateOscillation) {
      oscillationUpdater.updateOscillationStatus(alarmCacheObject);
    }

    boolean wasAlreadyOscillating = alarmCacheObject.isOscillating();

    updateAlarmBasedOnTag(alarmCacheObject, tag);

    if (wasAlreadyOscillating) {
      alarmCacheRef.putQuiet(alarmCacheObject.getId(), alarmCacheObject);
    } else {
      alarmCacheRef.put(alarmCacheObject.getId(), alarmCacheObject);
    }
    return alarmCacheObject;
  }

  /**
   * Accesses and locks Tag in cache, fetches associated
   * alarms (since Alarm evaluation is on the same thread as
   * the Tag cache update, these correspond to the Tag value
   * and cannot be modified during this method).
   */
  public TagWithAlarms getTagWithAlarms(Long id) {
    return alarmCacheRef.executeTransaction(() -> {
      Tag tag = tagCacheRef.get(id);
      Collection<Alarm> alarms = new LinkedList<>();
      for (Long alarmId : tag.getAlarmIds()) {
        alarms.add(alarmCacheRef.get(alarmId));
      }
      return new TagWithAlarms(tag, alarms);
    });
  }

  /**
   * Derives a valid JMS topic name for distributing the alarm's values to
   * clients (currently the same for all alarms, so returns a constant).
   *
   * @param alarm the alarm for which the topic should be provided
   * @return a valid JMS topic name for the alarm
   */
  public String getTopicForAlarm(final Alarm alarm) {
    return ALARM_TOPIC;
  }


  @Override
  public void registerForTagUpdates(AlarmAggregatorListener aggregatorObserver) {
    alarmUpdateObservable.add(aggregatorObserver);
  }

  /**
   * Notify the listeners of a tag update with associated alarms.
   *
   * @param tag       the Tag that has been updated
   * @param alarmList the associated list of evaluated alarms
   */
  private void notifyListeners(final Tag tag, final List<Alarm> alarmList) {
    for (AlarmAggregatorListener listener : alarmUpdateObservable) {
      listener.notifyOnUpdate(tag.clone(), alarmList);
    }
  }

  /**
   * Resets the oscillation flag to false and computes the alarm state corresponding to the actual tag value.
   * It will also updates the Alarm cache and notify the listeners.
   *
   * @param alarmCacheObject The current alarm object in the cache
   * @param tag   The tag update
   * @return The updated alarm object
   */
  public void stopOscillating(final AlarmCacheObject alarmCacheObject, final Tag tag) {
    alarmCacheObject.setOscillating(false);
    update(alarmCacheObject, tag, false);
  }
}