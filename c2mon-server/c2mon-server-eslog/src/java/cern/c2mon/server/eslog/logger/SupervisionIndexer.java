/******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 * <p/>
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 * <p/>
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package cern.c2mon.server.eslog.logger;

import cern.c2mon.server.eslog.structure.mappings.Mapping;
import cern.c2mon.server.eslog.structure.mappings.SupervisionMapping;
import cern.c2mon.shared.client.supervision.SupervisionEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Alban Marguet
 */
@Service
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class SupervisionIndexer extends Indexer {
  private final String supervisionType = "supervision";

  @Autowired
  public SupervisionIndexer(final Connector connector) {
    super(connector);
  }

  @PostConstruct
  public void init() {
    super.init();
  }

  public void waitForConnection() {
    while (!connector.isConnected()) {
      isAvailable = false;
    }
    isAvailable = true;
  }

  public void logSupervisionEvent(SupervisionEvent supervisionEvent) {
    if (supervisionEvent != null && supervisionEvent.getEventTime() != null ) {
      String indexName = generateSupervisionIndex(supervisionEvent.getEventTime().getTime());

      SupervisionMapping supervisionMapping = new SupervisionMapping();
      supervisionMapping.configure(connector.getShards(), connector.getReplica());
      supervisionMapping.setProperties(Mapping.ValueType.supervisionType);
      String mapping = supervisionMapping.getMapping();

      connector.handleSupervisionQuery(indexName, mapping, supervisionEvent);
    }
    else {
      log.debug("logSupervisionEvent() - Could not instantiate SupervisionEventImpl, null value.");
    }
  }

  public String generateSupervisionIndex(long time) {
    return supervisionPrefix + millisecondsToYearMonth(time);
  }
}