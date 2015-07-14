-- removes all data in DB cache account
delete from alarm;
commit;
delete from datatag where tagcontroltag=0;
commit;
delete from commandtag;
commit;
delete from equipment where eq_parent_id is not null;
commit;
delete from equipment;
commit;
delete from process;
commit;
delete from datatag where tagcontroltag=1;
commit;

--this data should always be available in the TIMSRVTEST account when a new Junit test is run (it must not be deleted during the test)

-- control tags needed for cache module tests
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1200, 'P_TEST_PROCESS:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1205, 'P_TEST_EQUIPMENT:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');


-- control tags for Process and Equipment TESTHANDLER03
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1220, 'P_TESTHANDLER03:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1221, 'P_TESTHANDLER03:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1222, 'E_TESTHANDLER_TESTHANDLER03:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1223, 'E_TESTHANDLER_TESTHANDLER03:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');

-- control tags for process P_TESTHANDLER04
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1260, 'P_TESTHANDLER04:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1261, 'P_TESTHANDLER04:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');

-- control tags for equipment E_TESTHANDLER04
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1262, 'E_TESTHANDLER_TESTHANDLER04:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1263, 'E_TESTHANDLER_TESTHANDLER04:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');


-- control tags for equipment E_TEST_2
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1240, 'E_TEST_2:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1241, 'E_TEST_2:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');


-- control tags ready for subequipment (included subequipment to prevent DB delete from succeeding and losing control tags...)
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1230, 'SUB_E_TESTHANDLER_TESTHANDLER03:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1231, 'SUB_E_TESTHANDLER_TESTHANDLER03:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1232, 'SUB_E_TESTHANDLER_TESTHANDLER03:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');

-- control tags not associated to Equipment but used to test config loading (& mapper tests)
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1250, 'E_CONFIG_TEST:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGADDRESS)
VALUES (1251, 'E_CONFIG_TEST:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED', '<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.SimpleHardwareAddressImpl"><address>test</address></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1252, 'E_CONFIG_TEST:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');

-- process

INSERT INTO PROCESS (PROCID, PROCNAME, PROCDESC, PROCSTATE_TAGID, PROCALIVE_TAGID, PROCALIVEINTERVAL, PROCMAXMSGSIZE, PROCMAXMSGDELAY, PROCSTATE)
VALUES (50,'P_TESTHANDLER03','PROCESS TESTHANDLER03',1220,1221,60000,100,150,'DOWN');

INSERT INTO PROCESS (PROCID, PROCNAME, PROCDESC, PROCSTATE_TAGID, PROCALIVE_TAGID, PROCALIVEINTERVAL, PROCMAXMSGSIZE, PROCMAXMSGDELAY, PROCSTATE)
VALUES (51,'P_TESTHANDLER04','PROCESS TESTHANDLER04',1260,1261,60000,100,150,'DOWN');

-- equipment

INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PROCID, EQADDRESS)
VALUES (150, 'E_TESTHANDLER_TESTHANDLER03', 'TESTHANDLER03 EQUIPMENT', 'cern.c2mon.driver.testhandler.TestMessageHandler', 1222, 60000, 1223, 50, 'interval=1000;eventProb=0.02;inRangeProb=1;outDeadBandProb=1;switchProb=1;startIn=0.2;aliveInterval=30000');

INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PROCID, EQADDRESS)
VALUES (170, 'E_TESTHANDLER_TESTHANDLER04', 'TESTHANDLER04 EQUIPMENT', 'cern.c2mon.driver.testhandler.TestMessageHandler', 1262, 60000, 1263, 51, 'interval=1000;eventProb=0.02;inRangeProb=1;outDeadBandProb=1;switchProb=1;startIn=0.2;aliveInterval=30000');

INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PROCID, EQADDRESS)
VALUES (160, 'E_TEST_2', 'E_TEST_2 EQUIPMENT', 'cern.c2mon.driver.testhandler.TestMessageHandler', 1240, 60000, 1241, 50, 'interval=1000;eventProb=0.02;inRangeProb=1;outDeadBandProb=1;switchProb=1;startIn=0.2;aliveInterval=30000');

-- subequipment

INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PARENT_ID)
VALUES (250, 'SUB_E_TESTHANDLER_TESTHANDLER03', 'TESTHANDLER03 SUBEQUIPMENT', '-', 1230, 1231, 30000, 1232, 150);

-- tags

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200000, 'sys.proc.serverinit', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.proc.serverinit</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200001, 'sys.mem.inactpct', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,'60000,60001,60002,60003,60004,60005,60006','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200002, 'sys.loadavg', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,'60000,60001,60002,60003,60004,60005,60006,60007,60008,60009','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.loadavg</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200003, 'tag_200003', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,'60000,60001,60002,60003,60004,60005,60006,60007,60008,60009','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200004, 'tag_200004', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200005, 'tag_200005', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200010, 'tag_200010', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',170,'60011,60012','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');

-- rules

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60000, 'DIAMON_CLIC_CS-CCR-DEV3', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '59999');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60001, 'DIAMON_CLIC_CS-CCR-DEV4', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60008,60009,60010');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60002, 'DIAMON_CLIC_CS-CCR-DEV5', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60003, 'DIAMON_CLIC_CS-CCR-DEV6', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60007,60008,60009,60010');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60004, 'DIAMON_CLIC_CS-CCR-DEV7', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60007,60008');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60005, 'DIAMON_CLIC_CS-CCR-DEV8', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60006, 'DIAMON_CLIC_CS-CCR-DEV9', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60007');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60007, 'DIAMON_CLIC_CS-CCR-DEV10', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60006 > 0)|(#200002 < 200)|(#60004 > 450)[2],(#60003 < 500)|(#200003 > 400)[1],true[0]', '60009,60010');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60008, 'DIAMON_CLIC_CS-CCR-DEV11', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60001 > 0)|(#200002 < 200)|(#60004 > 450)[2],(#60003 < 500)|(#200003 > 400)[1],true[0]', '60010');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60009, 'DIAMON_CLIC_CS-CCR-DEV12', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60001 > 0)|(#200002 < 200)|(#60004 > 450)[2],(#60007 < 500)|(#200003 > 400)[1],true[0]');

INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60010, 'DIAMON_CLIC_CS-CCR-DEV13', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60008> 0)|(#60007 < 200)|(#60004 > 450)[2],(#60007 < 500)|(#60001 > 400)[1],true[0]');

--depends on 2 processes and 2 equipments
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60011, 'RULE_WITH_MULTIPLE_PARENTS', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60008> 0)|(#60007 < 200)|(#60004 > 450)[2],(#60007 < 500)|(#60001 > 400)|(#200010 > 300)[1],true[0]');

-- new rules for parent id loading test
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60012, 'RULE_ON_EQUIP_170', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200010 > 0)[0]', '59999');

-- use id to get to start in SQL query
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (59999, 'RULE_ON_EQUIP_150_170', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60000 > 0)|(#60012 < 200)[1]');


--alarms
insert into alarm (ALARMID, ALARM_TAGID, ALARMFFAMILY, ALARMFMEMBER, ALARMFCODE, ALARMSTATE, ALARMTIME, ALARMINFO, ALARMCONDITION, ALA_PUBLISHED, ALA_PUB_STATE, ALA_PUB_TIME, ALA_PUB_INFO)
values (350000, 60000, 'TEST_FAMILY', 'TEST_MEMBER', '20', 'TERMINATE', NULL, 'TEST_INFO', '<AlarmCondition class="cern.c2mon.server.common.alarm.ValueAlarmCondition"><alarm-value type="Integer">33</alarm-value></AlarmCondition>', 1, 'ACTIVE', current_timestamp, 'alarm info');

insert into alarm (ALARMID, ALARM_TAGID, ALARMFFAMILY, ALARMFMEMBER, ALARMFCODE, ALARMSTATE, ALARMTIME, ALARMINFO, ALARMCONDITION, ALA_PUBLISHED, ALA_PUB_STATE, ALA_PUB_TIME, ALA_PUB_INFO)
values (350001, 60000, 'TEST_FAMILY', 'TEST_MEMBER', '20', 'TERMINATE', NULL, 'TEST_INFO', '<AlarmCondition class="cern.c2mon.server.common.alarm.ValueAlarmCondition"><alarm-value type="Integer">33</alarm-value></AlarmCondition>', 0, 'TERMINATE', current_timestamp, 'alarm info');

INSERT INTO COMMANDTAG(CMDID, CMDNAME, CMDDESC, CMDMODE, CMDDATATYPE, CMDSOURCERETRIES, CMDSOURCETIMEOUT, CMDEXECTIMEOUT, CMDCLIENTTIMEOUT, CMDHARDWAREADDRESS, CMDMINVALUE, CMDMAXVALUE, CMD_EQID, CMDRBACCLASS, CMDRBACDEVICE, CMDRBACPROPERTY)
VALUES (11000, 'CMD_11000', 'CMD_11000_DESC', 0, 'Integer', 1, 1000, 1000, 5000, '<HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.OPCHardwareAddressImpl"><opc-item-name>CDE_N2_ARR_EJP_UAPE_X93X94</opc-item-name><command-pulse-length>100</command-pulse-length></HardwareAddress>',NULL,NULL,150,'TIM_COMMANDS','TIM_CMD_WATER_MEY','WRITE');

INSERT INTO COMMANDTAG(CMDID, CMDNAME, CMDDESC, CMDMODE, CMDDATATYPE, CMDSOURCERETRIES, CMDSOURCETIMEOUT, CMDEXECTIMEOUT, CMDCLIENTTIMEOUT, CMDHARDWAREADDRESS, CMDMINVALUE, CMDMAXVALUE, CMD_EQID, CMDRBACCLASS, CMDRBACDEVICE, CMDRBACPROPERTY)
VALUES (11001, 'CMD_11001', 'CMD_11001_DESC', 0, 'Integer', 1, 1000, 1000, 5000, '<HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.OPCHardwareAddressImpl"><opc-item-name>CDE_N2_ARR_EJP_UAPE_X93X94</opc-item-name><command-pulse-length>100</command-pulse-length></HardwareAddress>',NULL,NULL,150,'TIM_COMMANDS','TIM_CMD_TYPE2','WRITE');
