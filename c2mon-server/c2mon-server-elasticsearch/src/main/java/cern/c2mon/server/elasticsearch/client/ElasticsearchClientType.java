/******************************************************************************
 * Copyright (C) 2010-2019 CERN. All rights not expressly granted are reserved.
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
package cern.c2mon.server.elasticsearch.client;

/**
 * Defines available clients to communicate with Elasticsearch server
 *
 * @author Serhiy Boychenko
 */
public enum ElasticsearchClientType {
  REST(9200),
  TRANSPORT(9300);

  private final int defaultPort;

  ElasticsearchClientType(int defaultPort) {
    this.defaultPort = defaultPort;
  }

  /**
   * @return default port used by this client to communicate with Elasticsearch server
   */
  public int getDefaultPort() {
    return defaultPort;
  }
}
