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
package cern.c2mon.server.common.alarm;

import java.sql.Timestamp;

import lombok.Data;

import cern.c2mon.server.common.metadata.Metadata;
import cern.c2mon.shared.common.Cacheable;

/**
 * Alarm object held in the cache.
 *
 * Imported more or less as-is into C2MON.
 *
 * Note: in TIM1 care was taken to make sure this is "" and not null - be
 * careful when sending to LASER as this may be the reason (?)
 *
 * @author Mark Brightwell
 *
 */
@Data
public class AlarmCacheObject implements Cloneable, Cacheable, Alarm {

  /** Serial version UID */
  private static final long serialVersionUID = 794087757524662419L;

  /**
   * Internal identifier of the AlarmCacheObject.
   */
  private Long id;

  /**
   * Unique identifier of the DataTagCacheObject to which the alarm is attached.
   * The Alarm is activated or terminated depending on the current value of this
   * data tag.
   */
  private Long dataTagId;

  /**
   * LASER fault family of the alarm.
   **/
  private String faultFamily;

  /**
   * LASER fault member of the alarm.
   **/
  private String faultMember;

  /**
   * LASER fault code of the alarm.
   **/
  private int faultCode;

  /**
   * AlarmCondition used to determine the alarm's current state
   **/
  private AlarmCondition condition;

  /**
   * The meta data of the Alatm. The meta data can be arbitrary and of of the
   * type String, Numeric and Boolean. Not every Alarm needs to have a meta
   * data. Also the meta data don't have to be every time the same.
   */
  private Metadata metadata;

  /**
   * The alarm's current state
   **/
  private String state;

  /**
   * Timestamp of the last state change
   **/

  private boolean lastActiveState;
  private int counterFault;
  private long firstOscTS;

  /** Set to <code>true</code>, if alarm starts oscillating */
  private boolean oscillating;

  public void setState(String newState) {
    this.state = newState;

  }

  private Timestamp timestamp;

  /**
   * Optional info property
   **/
  private String info;

  /**
   * Name of the JMS topic on which the alarm will be distributed to clients.
   */
  private String topic = "c2mon.client.alarm";

  /**
   * Default constructor.
   */
  public AlarmCacheObject() {
    // Initialise run-time parameters with default values
    // (overwritten on loading if DB has none null values)
    this.state = AlarmCondition.TERMINATE;
    this.timestamp = new Timestamp(0);
    this.info = "";
  }

  /**
   * Constructor setting Alarm id.
   *
   * @param id
   *          the id of the Alarm
   */
  public AlarmCacheObject(final Long id) {
    this();
    this.id = id;
  }

  /**
   * Create a deep clone of this AlarmCacheObject.
   *
   * @return a deep clone of this AlarmCacheObject
   * @throws CloneNotSupportedException
   *           should never be thrown
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    AlarmCacheObject alarmCacheObject = (AlarmCacheObject) super.clone();
    if (this.condition != null) {
      alarmCacheObject.condition = (AlarmCondition) this.condition.clone();
    }
    if (this.timestamp != null) {
      alarmCacheObject.timestamp = (Timestamp) this.timestamp.clone();
    }
    return alarmCacheObject;
  }

  @Override
  public final Metadata getMetadata() {
    if (this.metadata == null) {
      this.metadata = new Metadata();
    }
    return this.metadata;
  }

  /**
   * Getter method.
   *
   * @return the unique identifier of the DataTag to which the alarm is attached
   */
  @Override
  public final Long getTagId() {
    return this.dataTagId;
  }

  /**
   * Checks if the Alarm state is ACTIVE.
   *
   * @return true if the alarm is currently active.
   */
  @Override
  public boolean isActive() {
    return this.state != null && this.state.equals(AlarmCondition.ACTIVE);
  }

  @Override
  public String toString() {
    StringBuffer str = new StringBuffer();

    str.append(getId());
    str.append('\t');
    str.append(getTagId());
    str.append('\t');
    str.append(getTimestamp());
    str.append('\t');
    str.append(getFaultFamily());
    str.append('\t');
    str.append(getFaultMember());
    str.append('\t');
    str.append(getFaultCode());
    str.append('\t');
    str.append(getState());
    if (getInfo() != null) {
      str.append('\t');
      str.append(getInfo());
    }

    return str.toString();
  }

}
