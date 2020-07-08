/******************************************************************************
 * Copyright (C) 2010-2020 CERN. All rights not expressly granted are reserved.
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
package cern.c2mon.daq.common;

import cern.c2mon.shared.common.datatag.SourceDataTagQuality;
import cern.c2mon.shared.common.datatag.ValueUpdate;

/**
 * Interface for equipment message senders.
 *
 * @author Andreas Lang
 */
public interface IEquipmentMessageSender {

  /**
   * Should be invoked each time you want to propagate the
   * supervision alive coming from the supervised equipment.
   */
  void sendSupervisionAlive();

  /**
   * Sends the value update for the given tag to the server, if not filtered out by the DAQ core
   *
   * @param tagId The unique id of the tag which shall be updated
   * @param update The tag value update to send
   * @return True if the tag has been send successfully to the server. False if
   *         the tag has been invalidated or filtered out.
   */
  boolean update(Long tagId, ValueUpdate update);

  /**
   * Sends the value update for the given tag to the server, if not filtered out by the DAQ core
   *
   * @param tagName The unique name of the tag which shall be updated
   * @param update The tag value update to send
   * @return True if the tag has been send successfully to the server. False if
   *         the tag has been invalidated or filtered out.
   */
  boolean update(String tagName, ValueUpdate update);

  /**
   * Invalidates the given tag and sends a quality update to the server, if not yet done.
   * The value remains the same.
   *
   * @param tagId The id of the tag to invalidate
   * @param quality the new tag quality
   */
  void update(Long tagId, SourceDataTagQuality quality);

  /**
   * Invalidates the given tag and sends a quality update to the server, if not yet done.
   * The value remains the same, but the source timestamp will be adjusted.
   *
   * @param tagName The unique name of the tag which shall be updated
   * @param quality the new tag quality
   */
  void update(String tagName, SourceDataTagQuality quality);

  /**
   * Invalidates the given tag and sends a quality update to the server, if not yet done.
   * The value remains the same.
   *
   * @param tagId The id of the tag to invalidate
   * @param quality the new tag quality
   * @param sourceTimestamp time when the SourceDataTag's value became invalid
   */
  void update(Long tagId, SourceDataTagQuality quality, long sourceTimestamp);

  /**
   * Invalidates the given tag and sends a quality update to the server, if not yet done.
   *
   * @param tagName The unique name of the tag which shall be updated
   * @param quality the new tag quality
   * @param sourceTimestamp time when the SourceDataTag's value became invalid
   */
  void update(String tagName, SourceDataTagQuality quality, long sourceTimestamp);

  /**
   * Invalidates the given tag and sends a quality + value update to the server, if not yet done.
   *
   * @param tagId The id of the tag to invalidate
   * @param update The tag value to send.
   * @param quality the new tag quality
   */
  void update(Long tagId, ValueUpdate update, SourceDataTagQuality quality);

  /**
   * Invalidates the given tag and sends a quality + value update to the server, if not yet done.
   *
   * @param tagName The unique name of the tag which shall be updated
   * @param update The tag value to send.
   * @param quality the new tag quality
   */
  void update(String tagName, ValueUpdate update, SourceDataTagQuality quality);
  
  /**
   * Sends a note to the business layer, to confirm that the equipment is not
   * properly configured, or connected to its data source
   */
  void confirmEquipmentStateIncorrect();

  /**
   * Sends a note to the business layer, to confirm that the equipment is not
   * properly configured, or connected to its data source
   *
   * @param pDescription additional description
   */
  void confirmEquipmentStateIncorrect(final String pDescription);

  /**
   * Sends a note to the business layer, to confirm that the equipment is
   * properly configured, connected to its source and running
   */
  void confirmEquipmentStateOK();

  /**
   * Sends a note to the business layer, to confirm that the equipment is
   * properly configured, connected to its source and running
   *
   * @param pDescription additional description
   */
  void confirmEquipmentStateOK(final String pDescription);
}
