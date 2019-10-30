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
package cern.c2mon.server.common.alive;

import cern.c2mon.shared.common.Cacheable;

import java.util.Collection;

public interface AliveTimer extends Cacheable {
//---------------------------------------------------------------------------
  // CONSTANT DEFINITIONS
  //---------------------------------------------------------------------------

  /**
   * If the aliveType is ALIVE_TYPE_PROCESS, the alive tag is generated by the
   * DAQ process.
   */
  String ALIVE_TYPE_PROCESS = "PROC";

  /**
   * If the aliveType is ALIVE_TYPE_EQUIPMENT, the alive tag is generated by the
   * equipment from which TIM acquires the data.
   */
  String ALIVE_TYPE_EQUIPMENT = "EQ";

  /**
   * If the aliveType is ALIVE_TYPE_SUBEQUIPMENT, the alive tag is generated by the
   * subequipment attached to one of the equipments.
   */
  String ALIVE_TYPE_SUBEQUIPMENT = "SUBEQ";

  /**
   * Indicates the description of the different types of aliveTimer. This text
   * should appear as part the messages send to the clients
   */
  String SUBEQUIPMENT_MSG = "SubEquipment";

  String EQUIPMENT_MSG = "Equipment";

  String PROCESS_MSG = "Process";

  /**
   * The ALIVE_TOLERANCE_FACTOR is used to allow for delays in alive
   * transmission. If, for example, an alive tag is expected to be sent every
   * minute, and the tolerance factor is 2, the alive will only expire if the
   * tag has not been received after 2 minutes.
   * Therefore, a factor of two allow for one alive tag to be lost without the
   * system noticing the interruption.
   */
  short ALIVE_TOLERANCE_FACTOR = 2;

  long getLastUpdate();

  /**
   * Sets the item as {@code active} and returns
   *
   * @return true if the state was changed ({}
   */
  boolean setActive(boolean active);

  void setLastUpdate(long lastUpdate);

  String getRelatedName();

  boolean isActive();

  Integer getAliveInterval();

  Object getAliveTypeDescription();

  boolean isProcessAliveType();

  Long getRelatedStateTagId();

  /**
   * Returns the identifier of the equipment or process linked to the alive tag.
   * @return id of cache object
   */
  Long getRelatedId();

  boolean isEquipmentAliveType();

  /**
   * Returns the alive timers that are dependent on this one
   * (only one level down: equipment alives for a process,
   * subequipment alives for an equipment).
   * @return
   */
  Collection<Long> getDependentAliveTimerIds();
}
