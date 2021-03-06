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
package cern.c2mon.server.client.config;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import cern.c2mon.shared.util.jms.ActiveJmsSender;

/**
 * @author Justin Lewis Salmon
 */
@Configuration
public class AlarmJmsConfig {

  @Autowired
  private ClientProperties properties;

  @Autowired
  private SingleConnectionFactory clientSingleConnectionFactory;

  @Bean
  public JmsTemplate alarmTopicPublisherJmsTemplate() {
    int ttl = properties.getJms().getClientTopicMsgTimeToLive();
    JmsTemplate jmsTemplate = JmsTopicTemplateFactory.createJmsTemplate(clientSingleConnectionFactory, ttl);

    String alarmTopic = properties.getJms().getAlarmTopic();
    jmsTemplate.setDefaultDestination(new ActiveMQTopic(alarmTopic));

    return jmsTemplate;
  }

  @Bean
  public ActiveJmsSender alarmTopicPublisher() {
    ActiveJmsSender jmsSender = new ActiveJmsSender();
    jmsSender.setJmsTemplate(alarmTopicPublisherJmsTemplate());
    return jmsSender;
  }
}
