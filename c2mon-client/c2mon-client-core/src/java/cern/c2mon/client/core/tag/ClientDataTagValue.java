/*******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 * 
 * Copyright (C) 2004 - 2011 CERN. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Author: TIM team, tim.support@cern.ch
 ******************************************************************************/
 
package cern.c2mon.client.core.tag;
import java.sql.Timestamp;
import java.util.Collection;

import cern.c2mon.client.core.DataTagUpdateListener;
import cern.c2mon.shared.client.alarm.AlarmValue;
import cern.tim.shared.common.datatag.DataTagQuality;
import cern.tim.shared.rule.RuleExpression;

/**
 * The <code>ClientDataTagValue</code> interface is the immutable interface 
 * of a <code>ClientDataTag</code> object, as seen by a
 * <code>DataTagUpdateListener</code>. It only allows to get the different
 * fields from the <code>ClientDataTag</code> object.
 * @see ClientDataTag
 * @see DataTagUpdateListener
 * @author Matthias Braeger
 */
public interface ClientDataTagValue  {
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_UNKNOWN = Void.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_BOOLEAN = Boolean.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_FLOAT = Float.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_INTEGER = Integer.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_DOUBLE = Double.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_LONG = Long.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_SHORT = Short.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_STRING = String.class.hashCode();
  /** Hash Code type constant used by <code>getNumericType()</code> */
  int TYPE_BYTE = Byte.class.hashCode();
  
  /**
   * Returns the tag identifier
   * @return the tag identifier
   */
  Long getId();
  
  /**
   * Returns DataTagQuality object
   * @return the DataTagQuality object for this data tag.
   */
  DataTagQuality getDataTagQuality();

  /**
   * Returns the tag value
   * @return the tag value
   */
  Object getValue();
  
  /**
   * Returns the unit of the value
   * @return The unit of the value
   */
  String getUnit();
  
  /**
   * Returns the values of the registered alarms or an empty collection,
   * if no alarm is defined on that tag.
   * @return The collection of registered alarms
   */
  Collection<AlarmValue> getAlarms();
  
  /**
   * @return The list of alarms id's that are defined for that tag. 
   */
  Collection<Long> getAlarmIds();
  
  /**
   * @return <code>true</code>, if the value of this reference object was 
   * computed with a rule. In case of a client rule the identifier of the
   * referenced object is always <code>-1</code>.
   * @see ClientDataTagValue#getId()
   */
  boolean isRuleResult();
  
  /**
   * @return The <code>RuleExpression</code> object or null, if the reference
   *         does not represent a rule. In case of a client rule it always returns
   *         the local client <code>RuleExpression</code>.
   */
  RuleExpression getRuleExpression();
  
  /**
   * Returns the tag value description
   * @return the tag value description
   */
  String getDescription();

  /**
   * Returns the tag source timestamp
   * @return the tag source timestamp
   */
  Timestamp getSourceTimestamp();
  
  /**
   * Returns the time when the data tag update
   * has passed the server. This value might be
   * interesting for reordering the incoming events
   * in case of race conditions. 
   * @return the server timestamp, or null in case that
   *         this tag has not yet been initialized by
   *         the server.
   * @see #getTimestamp()
   */
  Timestamp getServerTimestamp();

  /**
   * Returns the tag name
   * @return the tag name
   */
  String getName();

  /**
   * Returns the tag type in the form of a java Class
   * @return the tag type in the form of a java Class, or <code>null</code>
   *         if no initial value has yet been received from the server
   */
  Class< ? > getType();
  
  /**
   * Returns the has code of the class type that is used by this
   * class instance. The return value is always one of the specified
   * <code>TYPE</code> constants.
   * @return The hash code of the class type
   * @see ClientDataTagValue#TYPE_BOOLEAN
   * @see ClientDataTagValue#TYPE_BYTE
   * @see ClientDataTagValue#TYPE_DOUBLE
   * @see ClientDataTagValue#TYPE_FLOAT
   * @see ClientDataTagValue#TYPE_INTEGER
   * @see ClientDataTagValue#TYPE_LONG
   * @see ClientDataTagValue#TYPE_SHORT
   * @see ClientDataTagValue#TYPE_STRING
   * @see ClientDataTagValue#TYPE_UNKNOWN
   */
  public long getTypeNumeric();
}
