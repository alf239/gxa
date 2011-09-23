-- Modify SOFTWARE TABLE
alter table a2_software add ISACTIVE VARCHAR2(1) default '0';

UPDATE A2_SOFTWARE SET ISACTIVE='1' WHERE NAME='miRBase' AND VERSION='14';
UPDATE A2_SOFTWARE SET ISACTIVE='1' WHERE NAME='Atlas' AND VERSION='Atlas';

-- Modify A2_BIOENTITYTYPE table
ALTER TABLE A2_BIOENTITYTYPE ADD IDENTIFIERPROPERTYID NUMBER(22,0) ;
ALTER TABLE A2_BIOENTITYTYPE ADD NAMEPROPERTYID NUMBER(22,0) ;

-- AnnotationSource table
CREATE SEQUENCE  A2_ANNOTATIONSRC_SEQ
    MINVALUE 1 MAXVALUE 1.00000000000000E+27
    INCREMENT BY 1 START WITH 1 CACHE 20
    NOORDER  NOCYCLE;

CREATE TABLE A2_ANNOTATIONSRC(
  annotationsrcid NUMBER(22,0)  CONSTRAINT NN_ANNOTATIONSRC_ID NOT NULL
  , SOFTWAREID NUMBER(22,0)  CONSTRAINT NN_ANNOTATIONSRC_SWID NOT NULL
  , ORGANISMID NUMBER(22,0) CONSTRAINT NN_ANNOTATIONSRC_ORGANISM NOT NULL
  , url VARCHAR2(512)
  , biomartorganismname VARCHAR2(255)
  , databaseName VARCHAR2(255)
  , mySqlDbName VARCHAR2(255)
  , mySqlDbUrl VARCHAR2(255)
  , annsrctype VARCHAR2(255) CONSTRAINT NN_ANNOTATIONSRC_ASTYPE NOT NULL
  , LOADDATE DATE
);

ALTER TABLE A2_ANNOTATIONSRC
  ADD CONSTRAINT PK_ANNOTATIONSRC
    PRIMARY KEY (annotationsrcid)
  ENABLE;

ALTER TABLE A2_ANNOTATIONSRC
  ADD CONSTRAINT FK_ANNOTATIONSRC_ORGANISM
    FOREIGN KEY (ORGANISMID)
    REFERENCES A2_ORGANISM (ORGANISMID)
    ON DELETE CASCADE
    ENABLE;

ALTER TABLE A2_ANNOTATIONSRC
  ADD CONSTRAINT UQ_ANNSRC_NAME_V_ORG_TYPE
    UNIQUE(SOFTWAREID, ANNSRCTYPE, ORGANISMID)
    ENABLE;

ALTER TABLE "A2_ANNOTATIONSRC"
  ADD CONSTRAINT "FK_ANNSRC_SOFTWARE"
    FOREIGN KEY ("SOFTWAREID")
    REFERENCES "A2_SOFTWARE" ("SOFTWAREID")
    ON DELETE CASCADE
    ENABLE;

CREATE OR REPLACE TRIGGER A2_ANNOTATIONSRC_INSERT
before insert on A2_ANNOTATIONSRC
for each row
begin
if(:new.annotationsrcid is null) then
select A2_ANNOTATIONSRC_SEQ.nextval into :new.annotationsrcid from dual;
end if;
END;
/

-- Link table AnnotationSource to BioEntity type
CREATE TABLE A2_ANNSRC_BIOENTITYTYPE(
  annotationsrcid NUMBER(22,0) CONSTRAINT NN_ANNSRCBET_ANNSRCID NOT NULL
  , BIOENTITYTYPEID NUMBER(22,0) CONSTRAINT NN_ANNSRCBET_BETID NOT NULL
  );

ALTER TABLE A2_ANNSRC_BIOENTITYTYPE
  ADD CONSTRAINT UQ_ANNSRCBETYPE_AS_BET
    UNIQUE(annotationsrcid, BIOENTITYTYPEID)
    ENABLE;

ALTER TABLE A2_ANNSRC_BIOENTITYTYPE
  ADD CONSTRAINT FK_A2_ANNSRCBETYPE_BETYPE
    FOREIGN KEY (BIOENTITYTYPEID)
    REFERENCES A2_BIOENTITYTYPE (BIOENTITYTYPEID)
    ON DELETE CASCADE
    ENABLE;

ALTER TABLE A2_ANNSRC_BIOENTITYTYPE
  ADD CONSTRAINT FK_ANNSRCBETYPE_ANNSRC
    FOREIGN KEY (annotationsrcid)
    REFERENCES A2_ANNOTATIONSRC (annotationsrcid)
    ON DELETE CASCADE
    ENABLE;

-- BioMart Property table
CREATE SEQUENCE  A2_BIOMARTPROPERTY_SEQ
    MINVALUE 1 MAXVALUE 1.00000000000000E+27
    INCREMENT BY 1 START WITH 1 CACHE 20
    NOORDER  NOCYCLE;

CREATE TABLE A2_BIOMARTPROPERTY (
 biomartpropertyId NUMBER(22,0) CONSTRAINT NN_BMPROPERTY_ID NOT NULL
, BIOENTITYPROPERTYID NUMBER(22,0) CONSTRAINT NN_BMPROPERTY_BEPROPERTYID NOT NULL
, NAME VARCHAR2(255) CONSTRAINT NN_BMPROPERTY_NAME NOT NULL
, annotationsrcid NUMBER(22,0) CONSTRAINT NN_BMPROPERTY_ANNSRCID NOT NULL
);

ALTER TABLE A2_BIOMARTPROPERTY
  ADD CONSTRAINT PK_BMPROPERTY
    PRIMARY KEY (biomartpropertyId)
  ENABLE;

ALTER TABLE A2_BIOMARTPROPERTY
  ADD CONSTRAINT FK_BIOMARTPROPERTY_PROPERTY
    FOREIGN KEY (BIOENTITYPROPERTYID)
    REFERENCES A2_BIOENTITYPROPERTY (BIOENTITYPROPERTYID)
    ON DELETE CASCADE
    ENABLE;

ALTER TABLE A2_BIOMARTPROPERTY
  ADD CONSTRAINT FK_BIOMARTPROPERTY_ANNSRC
    FOREIGN KEY (annotationsrcid)
    REFERENCES A2_ANNOTATIONSRC (annotationsrcid)
    ON DELETE CASCADE
    ENABLE;

ALTER TABLE A2_BIOMARTPROPERTY
  ADD CONSTRAINT UQ_BMPROP_ANNSRCID_NAME_BEPROP
    UNIQUE(annotationsrcid, NAME, BIOENTITYPROPERTYID)
    ENABLE;

CREATE OR REPLACE TRIGGER A2_BIOMARTPROPERTY_INSERT
before insert on A2_BIOMARTPROPERTY
for each row
begin
if(:new.biomartpropertyId is null) then
select A2_BIOMARTPROPERTY_SEQ.nextval into :new.biomartpropertyId from dual;
end if;
END;
/
-- BioMart ArrayDesign table
CREATE SEQUENCE  A2_BIOMARTARRAYDESIGN_SEQ
    MINVALUE 1 MAXVALUE 1.00000000000000E+27
    INCREMENT BY 1 START WITH 1 CACHE 20
    NOORDER  NOCYCLE;

CREATE TABLE A2_BIOMARTARRAYDESIGN (
 BIOMARTARRAYDESIGNID NUMBER(22,0) CONSTRAINT NN_BMAD_ID NOT NULL
, ARRAYDESIGNID NUMBER(22,0) CONSTRAINT NN_BMAD_ARAYDESIGNID NOT NULL
, NAME VARCHAR2(255) CONSTRAINT NN_BMAD_NAME NOT NULL
, annotationsrcid NUMBER(22,0) CONSTRAINT NN_BMAD_ANNSRCID NOT NULL
);

ALTER TABLE A2_BIOMARTARRAYDESIGN
  ADD CONSTRAINT PK_BIOMARTARRAYDESIGN
    PRIMARY KEY (BIOMARTARRAYDESIGNID)
  ENABLE;

ALTER TABLE A2_BIOMARTARRAYDESIGN
  ADD CONSTRAINT FK_BIOMARTARRAYDESIGN_AD
    FOREIGN KEY (ARRAYDESIGNID)
    REFERENCES A2_ARRAYDESIGN (ARRAYDESIGNID)
    ON DELETE CASCADE
    ENABLE;

ALTER TABLE A2_BIOMARTARRAYDESIGN
  ADD CONSTRAINT FK_BMAD_ANNSRC
    FOREIGN KEY (annotationsrcid)
    REFERENCES A2_ANNOTATIONSRC (annotationsrcid)
    ON DELETE CASCADE
    ENABLE;

ALTER TABLE A2_BIOMARTARRAYDESIGN
  ADD CONSTRAINT UQ_BMAD_ANNSRCID_NAME
    UNIQUE(annotationsrcid, NAME)
    ENABLE;

CREATE OR REPLACE TRIGGER A2_BIOMARTARRAYDESIGN_INSERT
before insert on A2_BIOMARTARRAYDESIGN
for each row
begin
if(:new.biomartarraydesignId is null) then
select A2_BIOMARTARRAYDESIGN_SEQ.nextval into :new.biomartarraydesignId from dual;
end if;
END;
/

UPDATE A2_BIOENTITYTYPE BET SET BET.IDENTIFIERPROPERTYID= (
SELECT BEP.BIOENTITYPROPERTYID FROM A2_BIOENTITYPROPERTY BEP WHERE BEP.NAME='enstranscript')
WHERE BET.NAME='enstranscript';

UPDATE A2_BIOENTITYTYPE BET SET BET.IDENTIFIERPROPERTYID= (
SELECT BEP.BIOENTITYPROPERTYID FROM A2_BIOENTITYPROPERTY BEP WHERE BEP.NAME='ensgene')
WHERE BET.NAME='ensgene';

UPDATE A2_BIOENTITYTYPE BET SET BET.NAMEPROPERTYID= (
SELECT BEP.BIOENTITYPROPERTYID FROM A2_BIOENTITYPROPERTY BEP WHERE BEP.NAME='symbol')
WHERE BET.NAME='enstranscript';

UPDATE A2_BIOENTITYTYPE BET SET BET.NAMEPROPERTYID= (
SELECT BEP.BIOENTITYPROPERTYID FROM A2_BIOENTITYPROPERTY BEP WHERE BEP.NAME='symbol')
WHERE BET.NAME='ensgene';

CREATE BITMAP INDEX "IDX_BIOENTITYBEPV_BEID_SWID"
ON "A2_BIOENTITYBEPV" (BIOENTITYID, SOFTWAREID)
/*INDEX_TABLESPACE*/;

CREATE BITMAP INDEX IDX_BIOENTITYBEPV_organismid
ON a2_bioentitybepv(a2_bioentity.organismid)
FROM a2_bioentitybepv, a2_bioentity
WHERE a2_bioentitybepv.bioentityid = a2_bioentity.bioentityid
/*INDEX_TABLESPACE*/;

exit;