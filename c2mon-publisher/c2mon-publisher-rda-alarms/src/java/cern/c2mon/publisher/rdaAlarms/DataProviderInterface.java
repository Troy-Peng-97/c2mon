/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.c2mon.publisher.rdaAlarms;

/**
 * The dataprovider is used only to find the source name for an incoming alarm. 
 * If the implementation requires a connection (like for DB), the class must 
 * build it during during construction.
 * 
 * The close method is called when the publisher is stopped. getSource() is obviously
 * called repeatedly, each time an incoming alarm has no known source.
 * 
 * @author mbuttner
 */
public interface DataProviderInterface {

    String getSource(String alarmId) throws Exception;
    void close();
}
