SET DATABASE SQL SYNTAX ORA TRUE;

CREATE SEQUENCE CONFIG_ID_SEQUENCE START WITH 1 INCREMENT BY 1
CREATE SEQUENCE PROCESS_ID_SEQUENCE START WITH 100000 INCREMENT BY 1
CREATE SEQUENCE EQUIPMENT_ID_SEQUENCE START WITH 500000 INCREMENT BY 1
CREATE SEQUENCE TAG_ID_SEQUENCE START WITH 1000000 INCREMENT BY 1
CREATE SEQUENCE ALARM_ID_SEQUENCE START WITH 1000000 INCREMENT BY 1

CREATE TABLE IF NOT EXISTS PROCESS (
  PROCID             INTEGER NOT NULL PRIMARY KEY,
  PROCNAME           VARCHAR(60) NOT NULL UNIQUE,
  PROCDESC           VARCHAR(100),
  PROCSTATE_TAGID    INTEGER NOT NULL,
  PROCALIVE_TAGID    INTEGER,
  PROCALIVEINTERVAL  INTEGER,
  PROCMAXMSGSIZE     INTEGER NOT NULL,
  PROCMAXMSGDELAY    INTEGER NOT NULL,
  PROCCURRENTHOST    VARCHAR(25),
  PROCSTATE          VARCHAR(20),
  PROCSTATUSTIME     TIMESTAMP(6),
  PROCSTATUSDESC     VARCHAR(300),
  PROCSTARTUPTIME    TIMESTAMP(6),
  PROCREBOOT         INTEGER,
  PROCPIK            INTEGER,
  PROCLOCALCONFIG    VARCHAR(1)
);

CREATE TABLE IF NOT EXISTS EQUIPMENT (
  EQID               INTEGER NOT NULL PRIMARY KEY,
  EQNAME             VARCHAR(60) NOT NULL UNIQUE,
  EQDESC             VARCHAR(100),
  EQHANDLERCLASS     VARCHAR(100) NOT NULL,
  EQADDRESS          VARCHAR(350),
  EQSTATE_TAGID      INTEGER NOT NULL,
  EQALIVE_TAGID      INTEGER,
  EQALIVEINTERVAL    INTEGER,
  EQCOMMFAULT_TAGID  INTEGER,
  EQ_PROCID          INTEGER,
  EQ_PARENT_ID       INTEGER,
  EQSTATE            VARCHAR(20),
  EQSTATUSTIME       TIMESTAMP(6),
  EQSTATUSDESC       VARCHAR(300)
);

CREATE TABLE IF NOT EXISTS DATATAG (
  TAGID               INTEGER NOT NULL PRIMARY KEY,
  TAGNAME             VARCHAR(255) NOT NULL UNIQUE,
  TAGDESC             VARCHAR(100),
  TAGMODE             INTEGER NOT NULL,
  TAGDATATYPE         VARCHAR(200) NOT NULL,
  TAGCONTROLTAG       INTEGER NOT NULL,
  TAGVALUE            VARCHAR(4000),
  TAGVALUEDESC        VARCHAR(1000),
  TAGTIMESTAMP        TIMESTAMP(6),
  TAGDAQTIMESTAMP     TIMESTAMP(6),
  TAGSRVTIMESTAMP     TIMESTAMP(6),
  TAGQUALITYCODE      INTEGER,
  TAGQUALITYDESC      VARCHAR(1000),
  TAGRULE             VARCHAR(4000),
  TAGRULEIDS          VARCHAR(500),
  TAG_EQID            INTEGER,
  TAGMINVAL           VARCHAR(4000),
  TAGMAXVAL           VARCHAR(4000),
  TAGUNIT             VARCHAR(50),
  TAGSIMULATED        INTEGER,
  TAGLOGGED           INTEGER,
  TAGADDRESS          VARCHAR(4000),
  TAGDIPADDRESS       VARCHAR(500),
  TAGJAPCADDRESS      VARCHAR(500),
  TAGMETADATA         VARCHAR(4000)
);

CREATE TABLE IF NOT EXISTS EXPRESSION (
  TAGID               INTEGER NOT NULL,
  EXPRESSIONNAME      VARCHAR(255) NOT NULL,
  EXPRESSION          VARCHAR(4000),
  EXPRESSIONDATATYPE  VARCHAR(200) NOT NULL,
  EXPRESSIONVALUE     VARCHAR(4000),
  EXPRESSIONALARM     INTEGER NOT NULL,
  EXPRESSIONMETADATA  VARCHAR(4000),
  PRIMARY KEY (TAGID,EXPRESSIONNAME)
);

CREATE TABLE IF NOT EXISTS ALARM (
  ALARMID         INTEGER NOT NULL PRIMARY KEY,
  ALARM_TAGID     INTEGER,
  ALARMPRIORITY   INTEGER,
  ALARMFFAMILY    VARCHAR(20) NOT NULL,
  ALARMFMEMBER    VARCHAR(64) NOT NULL,
  ALARMFCODE      INTEGER NOT NULL,
  ALARMSTATE      VARCHAR(10),
  ALARMTIME       TIMESTAMP(6),
  ALARMINFO       VARCHAR(100),
  ALARMCONDITION  VARCHAR(500),
  ALA_PUBLISHED   INTEGER,
  ALA_PUB_STATE   VARCHAR(10),
  ALA_PUB_TIME    TIMESTAMP(6),
  ALA_PUB_INFO    VARCHAR(100),
  ALARMMETADATA   VARCHAR(4000)
);

CREATE TABLE IF NOT EXISTS COMMANDTAG (
  CMDID               INTEGER NOT NULL PRIMARY KEY,
  CMDNAME             VARCHAR(60) NOT NULL UNIQUE,
  CMDDESC             VARCHAR(100),
  CMDMODE             INTEGER NOT NULL,
  CMDDATATYPE         VARCHAR(200) NOT NULL,
  CMDSOURCERETRIES    INTEGER NOT NULL,
  CMDSOURCETIMEOUT    INTEGER NOT NULL,
  CMDEXECTIMEOUT      INTEGER NOT NULL,
  CMDCLIENTTIMEOUT    INTEGER NOT NULL,
  CMDRBACCLASS        VARCHAR(50),
  CMDRBACDEVICE       VARCHAR(50),
  CMDRBACPROPERTY     VARCHAR(50),
  CMDHARDWAREADDRESS  VARCHAR(4000) NOT NULL,
  CMDMINVALUE         VARCHAR(4000),
  CMDMAXVALUE         VARCHAR(4000),
  CMDMETADATA         VARCHAR(4000),
  CMD_EQID            INTEGER
);

ALTER TABLE PROCESS ADD CONSTRAINT FK_PROCALIVETAGID_DATATAG FOREIGN KEY (PROCALIVE_TAGID) REFERENCES DATATAG (TAGID);
ALTER TABLE PROCESS ADD CONSTRAINT FK_PROSTATETAGID_DATATAG FOREIGN KEY (PROCSTATE_TAGID) REFERENCES DATATAG (TAGID);

ALTER TABLE EQUIPMENT ADD CONSTRAINT FK_EQUIPMENT_PARENT_ID FOREIGN KEY (EQ_PARENT_ID) REFERENCES EQUIPMENT (EQID);
ALTER TABLE EQUIPMENT ADD CONSTRAINT FK_EQUIPMENT_PROCID_PROCESS FOREIGN KEY (EQ_PROCID) REFERENCES PROCESS (PROCID);
ALTER TABLE EQUIPMENT ADD CONSTRAINT FK_EQUIPMENT_EQALIVE_DATATAG FOREIGN KEY (EQALIVE_TAGID) REFERENCES DATATAG (TAGID);
ALTER TABLE EQUIPMENT ADD CONSTRAINT FK_EQUIPMENT_EQCOMMF_DATATAG FOREIGN KEY (EQCOMMFAULT_TAGID) REFERENCES DATATAG (TAGID);
ALTER TABLE EQUIPMENT ADD CONSTRAINT FK_EQUIPMENT_EQSTATE_DATATAG FOREIGN KEY (EQSTATE_TAGID) REFERENCES DATATAG (TAGID);

ALTER TABLE DATATAG ADD CONSTRAINT FK__DATATAG_EQID_EQUIPMENT FOREIGN KEY (TAG_EQID) REFERENCES EQUIPMENT (EQID);

ALTER TABLE ALARM ADD CONSTRAINT FK_ALARM_TAGID_DATATAG FOREIGN KEY (ALARM_TAGID) REFERENCES DATATAG (TAGID);

ALTER TABLE COMMANDTAG ADD CONSTRAINT FK_COMMAND_EQUIPMENT_EQID FOREIGN KEY (CMD_EQID) REFERENCES EQUIPMENT (EQID);


DROP INDEX IF EXISTS I_EQUIPMENT_EQPROCID;
CREATE INDEX I_EQUIPMENT_EQPROCID ON EQUIPMENT (EQ_PROCID);

DROP INDEX IF EXISTS I__DATATAG_TAGEQUID;
CREATE INDEX I__DATATAG_TAGEQUID ON DATATAG (TAG_EQID);

DROP INDEX IF EXISTS I_ALARM_TAGID;
CREATE INDEX I_ALARM_TAGID ON ALARM (ALARM_TAGID);

DROP INDEX IF EXISTS IDX_CMDEQID;
CREATE INDEX IDX_CMDEQID ON COMMANDTAG (CMD_EQID);


DROP VIEW IF EXISTS ALIVETIMER;
CREATE VIEW ALIVETIMER (ALIVEID, ALIVETYPE, ALIVEINTERVAL, RELATEDID, RELATEDNAME, RELATEDSTATETAG, PARENTALIVEID, PARENTID, PARENTNAME, PARENTTYPE) AS
SELECT
    procalive_tagid AS ALIVEID,
    'PROC' AS ALIVETYPE,
    procaliveinterval AS ALIVEINTERVAL,
    procid AS RELATEDID,
    procname AS RELATEDNAME,
    procstate_tagid AS RELATEDSTATETAG,
    null AS PARENTALIVEID,
    null AS PARENTID,
    null AS PARENTNAME,
    null AS PARENTTYPE
  FROM process p
  WHERE
    procalive_tagid IS NOT NULL
UNION
  SELECT
    eqalive_tagid ALIVEID,
    'EQ' AS ALIVETYPE,
    eqaliveinterval ALIVEINTERVAL,
    eqid RELATEDID,
    eqname RELATEDNAME,
    eqstate_tagid RELATEDSTATETAG,
    procalive_tagid PARENTALIVEID,
    procid PARENTID,
    procname PARENTNAME,
    'PROC' AS PARENTTYPE
  FROM process p, equipment e
  WHERE
    eqalive_tagid IS NOT NULL AND
    procid = eq_procid
UNION
  SELECT
    a.eqalive_tagid ALIVEID,
    'SUBEQ' AS ALIVETYPE,
    a.eqaliveinterval ALIVEINTERVAL,
    a.eqid RELATEDID,
    a.eqname RELATEDNAME,
    a.eqstate_tagid RELATEDSTATETAG,
    b.eqalive_tagid PARENTALIVEID,
    b.eqid PARENTID,
    b.eqname PARENTNAME,
    'EQ' AS PARENTTYPE
FROM equipment a, equipment b
WHERE
     a.eqalive_tagid IS NOT NULL AND
     a.eq_parent_id = b.eqid;


DROP VIEW IF EXISTS COMMFAULTTAG;
CREATE VIEW COMMFAULTTAG (COMMFAULTID, EQID, EQNAME, EQSTATETAG, EQALIVETAG) AS
SELECT EQUIPMENT.EQCOMMFAULT_TAGID COMMFAULTID
          ,EQUIPMENT.EQID EQID
          ,EQUIPMENT.EQNAME EQNAME
          ,EQUIPMENT.EQSTATE_TAGID EQSTATETAG
          ,EQUIPMENT.EQALIVE_TAGID EQALIVETAG
FROM EQUIPMENT;





CREATE TABLE DEVICECLASS (
  DEVCLASSID        INTEGER NOT NULL,
  DEVCLASSNAME      VARCHAR(60) NOT NULL,
  DEVCLASSDESC      VARCHAR(100)
  );

ALTER TABLE DEVICECLASS ADD
  CONSTRAINT PK_DEVICECLASS_DEVCLASSID PRIMARY KEY (DEVCLASSID);

ALTER TABLE DEVICECLASS ADD
  CONSTRAINT UQ_DEVICECLASS_DEVCLASSNAME UNIQUE (DEVCLASSNAME);

--

CREATE TABLE PROPERTY (
  PROPID            INTEGER NOT NULL,
  PROPNAME          VARCHAR(60) NOT NULL,
  PROPDESC          VARCHAR(100),
  PROPDEVCLASSID    INTEGER NOT NULL
  );

ALTER TABLE PROPERTY ADD
  CONSTRAINT PK_PROP_PROPID PRIMARY KEY (PROPID);

ALTER TABLE PROPERTY ADD
  CONSTRAINT UQ_PROP_PROPDEVCLSID_PROPNAME UNIQUE (PROPDEVCLASSID, PROPNAME);

ALTER TABLE PROPERTY ADD
  CONSTRAINT FK_PROP_DEVCLASSID_DEVICECLASS FOREIGN KEY (PROPDEVCLASSID) REFERENCES DEVICECLASS (DEVCLASSID);

--

CREATE TABLE COMMAND (
  CMDID             INTEGER NOT NULL,
  CMDNAME           VARCHAR(60) NOT NULL,
  CMDDESC           VARCHAR(100),
  CMDDEVCLASSID     INTEGER NOT NULL
  );

ALTER TABLE COMMAND ADD
  CONSTRAINT PK_CMD_CMDID PRIMARY KEY (CMDID);

ALTER TABLE COMMAND ADD
  CONSTRAINT UQ_CMD_CMDDEVCLASSID_CMDNAME UNIQUE (CMDDEVCLASSID, CMDNAME);

ALTER TABLE COMMAND ADD
  CONSTRAINT FK_CMD_DEVCLASSID_DEVICECLASS FOREIGN KEY (CMDDEVCLASSID) REFERENCES DEVICECLASS (DEVCLASSID);

--

CREATE TABLE FIELD (
  FIELDID          INTEGER NOT NULL,
  FIELDNAME        VARCHAR(60) NOT NULL,
  FIELDPROPID      INTEGER NOT NULL
  );

ALTER TABLE FIELD ADD
  CONSTRAINT PK_FIELD_FIELDID PRIMARY KEY (FIELDID);

ALTER TABLE FIELD ADD
  CONSTRAINT UQ_FIELD_FIELDPROPID_FIELDNAME UNIQUE (FIELDPROPID, FIELDNAME);

ALTER TABLE FIELD ADD
  CONSTRAINT FK_FIELD_PROPID_PROPERTY FOREIGN KEY (FIELDPROPID) REFERENCES PROPERTY (PROPID);

--

CREATE TABLE DEVICE (
  DEVID             INTEGER NOT NULL,
  DEVNAME           VARCHAR(60) NOT NULL,
  DEVCLASSID        INTEGER NOT NULL
  );

ALTER TABLE DEVICE ADD
  CONSTRAINT PK_DEV_DEVID PRIMARY KEY (DEVID);

ALTER TABLE DEVICE ADD
  CONSTRAINT FK_DEV_DEVCLASSID_DEVICECLASS FOREIGN KEY (DEVCLASSID) REFERENCES DEVICECLASS (DEVCLASSID);

--

CREATE TABLE DEVICEPROPERTY (
  DVPPROPID         INTEGER NOT NULL,
  DVPNAME           VARCHAR(60) NOT NULL,
  DVPVALUE          VARCHAR(4000),
  DVPCATEGORY       VARCHAR(20),
  DVPRESULTTYPE     VARCHAR(20),
  DVPDEVID          INTEGER NOT NULL
  );

ALTER TABLE DEVICEPROPERTY ADD
  CONSTRAINT PK_DVP_DVPDEVID_DVPPROPID PRIMARY KEY (DVPDEVID, DVPPROPID);

ALTER TABLE DEVICEPROPERTY ADD
  CONSTRAINT FK_DVP_DVPDEVID_DEVICE FOREIGN KEY (DVPDEVID) REFERENCES DEVICE (DEVID);

ALTER TABLE DEVICEPROPERTY ADD
  CONSTRAINT FK_DVP_PROPID_PROPERTY FOREIGN KEY (DVPPROPID) REFERENCES PROPERTY (PROPID);

--

CREATE TABLE DEVICECOMMAND (
  DVCCMDID          INTEGER NOT NULL,
  DVCNAME           VARCHAR(60) NOT NULL,
  DVCVALUE          VARCHAR(4000),
  DVCCATEGORY       VARCHAR(20),
  DVCRESULTTYPE     VARCHAR(20),
  DVCDEVID          INTEGER NOT NULL
  );

ALTER TABLE DEVICECOMMAND ADD
  CONSTRAINT PK_DVC_DVCDEVID_DVCCMDID PRIMARY KEY (DVCDEVID, DVCCMDID);

ALTER TABLE DEVICECOMMAND ADD
  CONSTRAINT FK_DVC_DVCDEVID_DEVICE FOREIGN KEY (DVCDEVID) REFERENCES DEVICE (DEVID);

ALTER TABLE DEVICECOMMAND ADD
  CONSTRAINT FK_DVC_CMDID_COMMAND FOREIGN KEY (DVCCMDID) REFERENCES COMMAND (CMDID);

--

CREATE TABLE PROPERTYFIELD (
  PRFFIELDID        INTEGER NOT NULL,
  PRFFIELDNAME      VARCHAR(60) NOT NULL,
  PRFVALUE          VARCHAR(4000),
  PRFCATEGORY       VARCHAR(20),
  PRFRESULTTYPE     VARCHAR(20),
  PRFPROPID         INTEGER NOT NULL,
  PRFDEVID          INTEGER NOT NULL
  );

ALTER TABLE PROPERTYFIELD ADD
  CONSTRAINT PK_PRF_FIELDID_PROPID_DEVID PRIMARY KEY (PRFFIELDID, PRFPROPID, PRFDEVID);

ALTER TABLE PROPERTYFIELD ADD
  CONSTRAINT FK_PRF_PRFFIELDID_FIELD FOREIGN KEY (PRFFIELDID) REFERENCES FIELD (FIELDID);

ALTER TABLE PROPERTYFIELD ADD
  CONSTRAINT FK_PRF_PRFPROPID_PROPERTY FOREIGN KEY (PRFPROPID) REFERENCES PROPERTY (PROPID);

ALTER TABLE PROPERTYFIELD ADD
  CONSTRAINT FK_PRF_PRFDEVID_DEVICE FOREIGN KEY (PRFDEVID) REFERENCES DEVICE (DEVID);
