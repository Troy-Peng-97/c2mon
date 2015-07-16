/*******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 *
 * Copyright (C) 2004 - 2014 CERN. This program is free software; you can
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
package cern.c2mon.client.ext.device.property;

import java.sql.Timestamp;
import java.util.Collection;

import cern.c2mon.client.common.tag.ClientDataTagValue;
import cern.c2mon.client.common.tag.TypeNumeric;
import cern.c2mon.client.ext.device.Device;
import cern.c2mon.shared.client.alarm.AlarmValue;
import cern.c2mon.shared.client.tag.TagMode;
import cern.c2mon.shared.common.datatag.DataTagQuality;
import cern.c2mon.shared.common.type.TypeConverter;
import cern.c2mon.shared.rule.RuleExpression;

/**
 * This class represents a constant value object, which is used as a static
 * property of a {@link Device}.
 *
 * @author Justin Lewis Salmon
 */
public class ClientConstantValue<T> implements ClientDataTagValue {

  /**
   * The ID of this tag.
   */
  private Long id;

  /**
   * The actual constant value.
   */
  private final T value;

  /**
   * The type of the value.
   */
  private final Class<T> resultType;

  /**
   * Default constructor.
   *
   * @param value the constant value
   * @param resultType the type of the constant value
   */
  public ClientConstantValue(final T value, final Class<T> resultType) {
    if (value == null) {
      throw new NullPointerException("ClientConstantValue cannot be instantiated with null value argument");
    }

    this.id = -1L;
    this.value = value;

    if (resultType == null) {
      this.resultType = (Class<T>) String.class;
    } else {
      this.resultType = resultType;
    }
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public DataTagQuality getDataTagQuality() {
    return null;
  }

  @Override
  public T getValue() {
    return TypeConverter.castToType(value, resultType);
  }

  @Override
  public String getValueDescription() {
    return null;
  }

  @Override
  public String getUnit() {
    return null;
  }

  @Override
  public TagMode getMode() {
    return null;
  }

  @Override
  public boolean isSimulated() {
    return false;
  }

  @Override
  public Collection<AlarmValue> getAlarms() {
    return null;
  }

  @Override
  public Collection<Long> getAlarmIds() {
    return null;
  }

  @Override
  public Collection<Long> getEquipmentIds() {
    return null;
  }

  @Override
  public Collection<Long> getSubEquipmentIds() {
    return null;
  }

  @Override
  public Collection<Long> getProcessIds() {
    return null;
  }

  @Override
  public boolean isRuleResult() {
    return false;
  }

  @Override
  public RuleExpression getRuleExpression() {
    return null;
  }

  @Override
  public String getDescription() {
    return null;
  }

  @Override
  public Timestamp getTimestamp() {
    return null;
  }

  @Override
  public Timestamp getDaqTimestamp() {
    return null;
  }

  @Override
  public Timestamp getServerTimestamp() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Class<?> getType() {
    return resultType;
  }

  @Override
  public TypeNumeric getTypeNumeric() {
    Class< ? > type = getType();
    if (type != null) {
      int typeNumeric = type.hashCode();
      for (TypeNumeric t : TypeNumeric.values()) {
        if (t.getCode() == typeNumeric) {
          return t;
        }
      }
    }
    
    return TypeNumeric.TYPE_UNKNOWN;
  }

  @Override
  public boolean isAliveTag() {
    return false;
  }

  @Override
  public boolean isControlTag() {
    return false;
  }

}
