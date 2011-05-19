#!/bin/bash

# TIM2 server
# Script for starting up the test server in single server mode. Specifies a different log directory /log/single-server/
# change SERVER home to switch between dev and dist

########
# JAVA #
########

export JAVA_HOME=/usr/java/jdk
export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH/
JAVA_BIN=$JAVA_HOME/jre/bin

#############
# arguments #
#############

#first argument is process name (used for logging file also)
PROCESS_NAME=$1

###############
# DIRECTORIES #
###############

#server home
SERVER_HOME=$HOME/dev/tim2-prototype

#config home
CONF_HOME=$SERVER_HOME/config

#application home
APP_HOME=$SERVER_HOME

#Terracotta installation directory
TERRACOTTA_HOME=~/opt/terracotta

#directory of shared libraries
SHARED_LIB_HOME=$HOME/dist/libs



#################
# CONFIGURATION #
#################

#Terracotta server host
TC_HOST=cs-ccr-tim4

#terracotta server name (one of them)
#TC_NAME=server1

#terracotta DSO port
TC_PORT=9510

#Terracotta configuration location (either file or host:port)
TERRACOTTA_CONFIG=$TC_HOST:$TC_PORT

#.tim.properties location
TIM_PROPERTIES=~/.tim2.properties

#log4j configuration file
LOG4J_CONF_FILE=$CONF_HOME/log4j.tim.xml

#log directory
LOG_DIR=$SERVER_HOME/log

##############
# LIBRAIRIES #
##############

#librairies (links to required libs)
APP_LIBS=$APP_HOME/libs/*

#Terracotta module libraries
TC_MOD_HOME=$TERRACOTTA_HOME/platform/modules/org/terracotta/modules
TERRACOTTA_MODULES=$TC_MOD_HOME/tim-distributed-cache/1.3.2/tim-distributed-cache-1.3.2.jar:$TC_MOD_HOME/tim-ehcache-2.0/1.5.2/tim-ehcache-2.0-1.5.2.jar:$TC_MOD_HOME/tim-concurrent-collections/1.3.2/tim-concurrent-collections-1.3.2.jar:$TC_MOD_HOME/tim-async-processing/1.3.2/tim-async-processing-1.3.2.jar:$TC_MOD_HOME/tim-annotations/1.5.1/tim-annotations-1.5.1.jar

#Spring modules
SPRING_MODULES=$SHARED_LIB_HOME/spring-modules/spring-modules-cache

#libs for Ehcache
EHCACHE_LIBS=$SHARED_LIB_HOME/ehcache/ehcache-core-2.0.1.jar:$SHARED_LIB_HOME/slf4j/slf4j-api-1.5.8.jar:$SHARED_LIB_HOME/slf4j/slf4j-jdk14-1.5.8.jar
#$SHARED_LIB_HOME/ehcache/ehcache-terracotta-2.0.1.jar:

#other libs
#OTHER_LIBS=$SHARED_LIB_HOME/jta/jta-1.1.jar:$SHARED_LIB_HOME/fuse/jencks-amqpool-2.2.jar:$SHARED_LIB_HOME/jencks/jencks-2.1.jar:$SHARED_LIB_HOME/apache-activemq/activemq-ra-5.3.0.jar

#apache commons libs
#APACHE_COMMONS_LIBS=$SHARED_LIB_HOME/apache-commons/commons-cli-1.1.jar:$SHARED_LIB_HOME/apache-commons/commons-io-1.4.jar

#application jar location (now in lib location)
APPLICATION_JAR=$APP_HOME/dist/single-server-prototype.jar

#all needed librairies (including cachetest)
REQUIRED_LIBS=$APP_LIBS:$APPLICATION_JAR     
#:$TERRACOTTA_MODULES:$SPRING_MODULES:$EHCACHE_LIBSb
echo required libs: $REQUIRED_LIBS

#run the java package
COMMAND_OPTIONS="-Xms2048m -Xmx2048m -XX:+UseParallelGC -XX:NewRatio=1 -Dserver.process.name=$PROCESS_NAME  -Dlog4j.configuration=$LOG4J_CONF_FILE -Dtim.log.dir=$LOG_DIR -Dtim.home=$SERVER_HOME -Dtim.properties.location=$TIM_PROPERTIES -classpath ${REQUIRED_LIBS}"
#-Dcom.tc.l1.cachemanager.criticalObjectThreshold=100000 -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:InitialSurvivorRatio=4 -XX:MinSurvivorRatio=4 -XX:-UseAdaptiveSizePolicy  -Dcom.tc.l1.cachemanager.percentageToEvict=10 -Dcom.tc.l1.cachemanager.threshold=70 -Dcom.tc.l1.cachemanager.monitorOldGenOnly=false -Dtc.config=$TERRACOTTA_CONFIG 

echo run with options: $COMMAND_OPTIONS

$JAVA_BIN/java $COMMAND_OPTIONS cern.tim.server.lifecycle.ServerStartup
