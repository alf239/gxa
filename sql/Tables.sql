/*******************************************************************************/
/*  Tables, Sequences, Indexes and Triggers in Atlas2 schema     
/*
/*                                              
/*******************************************************************************/

--------------------------------------------------------
--  DDL for Sequence A2_ARRAYDESIGN_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ARRAYDESIGN_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_ASSAYONTOLOGY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ASSAYPVONTOLOGY_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_ASSAYPV_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ASSAYPV_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_ASSAYSAMPLE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ASSAYSAMPLE_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_ASSAY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ASSAY_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_DESIGNELEMENT_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_DESIGNELEMENT_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_EXPERIMENT_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_EXPERIMENT_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_EXPRESSIONANALYTICS_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_EXPRESSIONANALYTICS_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_EXPRESSIONVALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_EXPRESSIONVALUE_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_GENEPROPERTYVALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_GENEPROPERTYVALUE_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_GENEPROPERTY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_GENEPROPERTY_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
   
--------------------------------------------------------
--  DDL for Sequence A2_GENEPROPERTY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_GENEGPV_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_GENE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_GENE_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_ONTOLOGYTERM_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ONTOLOGYTERM_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_PROPERTYVALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_PROPERTYVALUE_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_PROPERTY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_PROPERTY_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_SAMPLEPVONTOLOGY_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_SAMPLEPVONTOLOGY_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_SAMPLEPV_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_SAMPLEPV_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_SAMPLE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_SAMPLE_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Sequence A2_ORGANISM_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "A2_ORGANISM_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
--------------------------------------------------------
--  DDL for Sequence LOAD_MONITOR_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "LOAD_MONITOR_SEQ"  MINVALUE 1 MAXVALUE 1.00000000000000E+27 INCREMENT BY 1 START WITH 1 CACHE 20 ORDER  NOCYCLE;

--------------------------------------------------------
--  DDL for Table A2_ARRAYDESIGN
--------------------------------------------------------

  CREATE TABLE "A2_ARRAYDESIGN" ("ARRAYDESIGNID" NUMBER(22,0), "ACCESSION" VARCHAR2(255), "TYPE" VARCHAR2(255), "NAME" VARCHAR2(255), "PROVIDER" VARCHAR2(255)) ;
 
--------------------------------------------------------
--  DDL for Table A2_ASSAY
--------------------------------------------------------

  CREATE TABLE "A2_ASSAY" ("ASSAYID" NUMBER(22,0), "ACCESSION" VARCHAR2(255), "EXPERIMENTID" NUMBER(22,0), "ARRAYDESIGNID" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_ASSAYPVONTOLOGY
--------------------------------------------------------

  CREATE TABLE "A2_ASSAYPVONTOLOGY" ("ASSAYPVONTOLOGYID" NUMBER(22,0), "ONTOLOGYTERMID" NUMBER(22,0), "ASSAYPVID" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_ASSAYPV
--------------------------------------------------------

  CREATE TABLE "A2_ASSAYPV" ("ASSAYPVID" NUMBER(22,0), "ASSAYID" NUMBER(22,0), "PROPERTYVALUEID" NUMBER(22,0), "ISFACTORVALUE" NUMBER(22,0)); 
 
--------------------------------------------------------
--  DDL for Table A2_ASSAYSAMPLE
--------------------------------------------------------

  CREATE TABLE "A2_ASSAYSAMPLE" ("ASSAYSAMPLEID" NUMBER(22,0), "ASSAYID" NUMBER(22,0), "SAMPLEID" NUMBER(22,0)) ;
 
--------------------------------------------------------
--  DDL for Table A2_DESIGNELEMENT
--------------------------------------------------------

  CREATE TABLE "A2_DESIGNELEMENT" ("DESIGNELEMENTID" NUMBER(22,0), "ARRAYDESIGNID" NUMBER(22,0), "GENEID" NUMBER(22,0), "ACCESSION" VARCHAR2(255), "NAME" VARCHAR2(255), "TYPE" VARCHAR2(255), "ISCONTROL" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_EXPERIMENT
--------------------------------------------------------

  CREATE TABLE "A2_EXPERIMENT" ("EXPERIMENTID" NUMBER(22,0), "ACCESSION" VARCHAR2(255), "DESCRIPTION" VARCHAR2(2000), "PERFORMER" VARCHAR2(2000), "LAB" VARCHAR2(2000), "LOADDATE" DATE);

--------------------------------------------------------
--  DDL for Table A2_EXPRESSIONANALYTICS
--------------------------------------------------------

  CREATE TABLE "A2_EXPRESSIONANALYTICS" ("EXPRESSIONID" NUMBER(22,0), "EXPERIMENTID" NUMBER(22,0), "PROPERTYVALUEID" NUMBER(22,0), "TSTAT" FLOAT(125), "PVALADJ" FLOAT(125), "FPVAL" FLOAT(125), "FPVALADJ" FLOAT(125), "DESIGNELEMENTID" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_EXPRESSIONVALUE
--------------------------------------------------------

  CREATE TABLE "A2_EXPRESSIONVALUE" ("EXPRESSIONVALUEID" NUMBER(12,0), "VALUE" FLOAT(126), "DESIGNELEMENTID" NUMBER(12,0), "ASSAYID" NUMBER(12,0));

--------------------------------------------------------
--  DDL for Table A2_GENE
--------------------------------------------------------

  CREATE TABLE "A2_GENE" ("GENEID" NUMBER(22,0), "ORGANISMID" NUMBER(22,0), "IDENTIFIER" VARCHAR2(255), "NAME" VARCHAR2(255));

--------------------------------------------------------
--  DDL for Table A2_GENEPROPERTY
--------------------------------------------------------

  CREATE TABLE "A2_GENEPROPERTY" ("GENEPROPERTYID" NUMBER(22,0), "NAME" VARCHAR2(255), "AE2TABLENAME" VARCHAR2(255));

--------------------------------------------------------
--  DDL for Table A2_GENEPROPERTYVALUE
--------------------------------------------------------

  CREATE TABLE "A2_GENEPROPERTYVALUE" ("GENEPROPERTYVALUEID" NUMBER(22,0), "GENEPROPERTYID" NUMBER(22,0), "VALUE" VARCHAR2(255));

--------------------------------------------------------
--  DDL for Table A2_GENEPROPERTYVALUE
--------------------------------------------------------

  CREATE TABLE "A2_GENEGPV" ("GENEGPVID" NUMBER(22,0), "GENEID" NUMBER(22,0), "GENEPROPERTYVALUEID" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_ONTOLOGY
--------------------------------------------------------

  CREATE TABLE "A2_ONTOLOGY" ("ONTOLOGYID" NUMBER(22,0), "NAME" VARCHAR2(255), "SOURCE_URI" VARCHAR2(500), "DESCRIPTION" VARCHAR2(4000), "VERSION" VARCHAR2(50));

--------------------------------------------------------
--  DDL for Table A2_ONTOLOGYTERM
--------------------------------------------------------

  CREATE TABLE "A2_ONTOLOGYTERM" ("ONTOLOGYTERMID" NUMBER(22,0), "ONTOLOGYID" NUMBER(22,0), "TERM" VARCHAR2(4000), "ACCESSION" VARCHAR2(255), "DESCRIPTION" VARCHAR2(4000) /*, "ORIG_VALUE" VARCHAR2(255), "ORIG_VALUE_SRC" VARCHAR2(255)*/);

--------------------------------------------------------
--  DDL for Table A2_PROPERTY
--------------------------------------------------------

  CREATE TABLE "A2_PROPERTY" ("PROPERTYID" NUMBER(22,0), "NAME" VARCHAR2(255), "AE1TABLENAME_ASSAY" VARCHAR2(255), "AE1TABLENAME_SAMPLE" VARCHAR2(255), "ASSAYPROPERTYID" NUMBER(22,0), "SAMPLEPROPERTYID" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_PROPERTYVALUE
--------------------------------------------------------

  CREATE TABLE "A2_PROPERTYVALUE" ("PROPERTYVALUEID" NUMBER(22,0), "PROPERTYID" NUMBER(22,0), "NAME" VARCHAR2(255));

--------------------------------------------------------
--  DDL for Table A2_SAMPLE
--------------------------------------------------------

  CREATE TABLE "A2_SAMPLE" ("SAMPLEID" NUMBER(22,0), "ACCESSION" VARCHAR2(255), "SPECIES" VARCHAR2(255), "CHANNEL" VARCHAR2(255));

--------------------------------------------------------
--  DDL for Table A2_SAMPLEPVONTOLOGY 
--------------------------------------------------------

  CREATE TABLE "A2_SAMPLEPVONTOLOGY" ("SAMPLEPVONTOLOGYID" NUMBER(22,0), "ONTOLOGYTERMID" NUMBER(22,0), "SAMPLEPVID" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_SAMPLEPV
--------------------------------------------------------

  CREATE TABLE "A2_SAMPLEPV" ("SAMPLEPVID" NUMBER(22,0), "SAMPLEID" NUMBER(22,0), "PROPERTYVALUEID" NUMBER(22,0), "ISFACTORVALUE" NUMBER(22,0));

--------------------------------------------------------
--  DDL for Table A2_ORGANISM
--------------------------------------------------------

  CREATE TABLE "A2_ORGANISM" ("ORGANISMID" NUMBER(22,0), "NAME" VARCHAR2(255));

--------------------------------------------------------
--  DDL for Table LOAD_MONITOR
--------------------------------------------------------

  CREATE TABLE "LOAD_MONITOR" ("ID" NUMBER, "ACCESSION" VARCHAR2(255), "STATUS" VARCHAR2(50), "NETCDF" VARCHAR2(50), "SIMILARITY" VARCHAR2(50), "RANKING" VARCHAR2(50), "SEARCHINDEX" VARCHAR2(50), "LOAD_TYPE" VARCHAR2(250));

--------------------------------------------------------
--  Constraints for Table A2_ARRAYDESIGN
--------------------------------------------------------

  ALTER TABLE "A2_ARRAYDESIGN" ADD CONSTRAINT "PK_ARRAYDESIGN" PRIMARY KEY ("ARRAYDESIGNID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;
--------------------------------------------------------
--  Constraints for Table A2_ASSAY
--------------------------------------------------------

  ALTER TABLE "A2_ASSAY" ADD CONSTRAINT "PK_ASSAY" PRIMARY KEY ("ASSAYID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_ASSAYPVONTOLOGY
--------------------------------------------------------

  ALTER TABLE "A2_ASSAYPVONTOLOGY" ADD CONSTRAINT "PK_ASSAYPVONTOLOGY" PRIMARY KEY ("ASSAYPVONTOLOGYID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_ASSAYPV
--------------------------------------------------------

  ALTER TABLE "A2_ASSAYPV" ADD CONSTRAINT "PK_ASSAYPV" PRIMARY KEY ("ASSAYPVID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_ASSAYSAMPLE
--------------------------------------------------------

  ALTER TABLE "A2_ASSAYSAMPLE" ADD CONSTRAINT "PK_ASSAYSAMPLE" PRIMARY KEY ("ASSAYSAMPLEID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_DESIGNELEMENT
--------------------------------------------------------

  ALTER TABLE "A2_DESIGNELEMENT" ADD CONSTRAINT "PK_DESIGNELEMENT" PRIMARY KEY ("DESIGNELEMENTID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_EXPERIMENT
--------------------------------------------------------

  ALTER TABLE "A2_EXPERIMENT" ADD CONSTRAINT "PK_EXPERIMENT" PRIMARY KEY ("EXPERIMENTID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_EXPRESSIONANALYTICS
--------------------------------------------------------

  ALTER TABLE "A2_EXPRESSIONANALYTICS" ADD CONSTRAINT "PK_EXPRESSIONANALYTICS" PRIMARY KEY ("EXPRESSIONID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_EXPRESSIONVALUE
--------------------------------------------------------

  ALTER TABLE "A2_EXPRESSIONVALUE" ADD CONSTRAINT "PK_EXPRESSIONVALUE" PRIMARY KEY ("EXPRESSIONVALUEID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_GENE
--------------------------------------------------------

  ALTER TABLE "A2_GENE" ADD CONSTRAINT "PK_GENE" PRIMARY KEY ("GENEID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*TABLESPACE "ATLAS2_DATA"*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_GENEPROPERTY
--------------------------------------------------------

  ALTER TABLE "A2_GENEPROPERTY" ADD CONSTRAINT "PK_GENEPROPERTY" PRIMARY KEY ("GENEPROPERTYID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_GENEPROPERTYVALUE
--------------------------------------------------------

  ALTER TABLE "A2_GENEPROPERTYVALUE" ADD CONSTRAINT "PK_GENEPROPERTYVALUE" PRIMARY KEY ("GENEPROPERTYVALUEID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_GENEPROPERTYVALUE
--------------------------------------------------------

  ALTER TABLE "A2_GENEGPV" ADD CONSTRAINT "PK_GENEGPV" PRIMARY KEY ("GENEGPVID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_ONTOLOGY
--------------------------------------------------------

  ALTER TABLE "A2_ONTOLOGY" ADD CONSTRAINT "PK_ONTOLOGY" PRIMARY KEY ("ONTOLOGYID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_ONTOLOGYTERM
--------------------------------------------------------

  ALTER TABLE "A2_ONTOLOGYTERM" ADD CONSTRAINT "PK_ONTOLOGYTERM" PRIMARY KEY ("ONTOLOGYTERMID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_PROPERTY
--------------------------------------------------------

  ALTER TABLE "A2_PROPERTY" ADD CONSTRAINT "PK_PROPERTY" PRIMARY KEY ("PROPERTYID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_PROPERTYVALUE
--------------------------------------------------------

  ALTER TABLE "A2_PROPERTYVALUE" ADD CONSTRAINT "PK_PROPERTYVALUE" PRIMARY KEY ("PROPERTYVALUEID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_SAMPLE
--------------------------------------------------------

  ALTER TABLE "A2_SAMPLE" ADD CONSTRAINT "PK_SAMPLE" PRIMARY KEY ("SAMPLEID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_SAMPLEPVONTOLOGY
--------------------------------------------------------

  ALTER TABLE "A2_SAMPLEPVONTOLOGY" ADD CONSTRAINT "PK_SAMPLEPVONTOLOGY" PRIMARY KEY ("SAMPLEPVONTOLOGYID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_SAMPLEPV
--------------------------------------------------------

  ALTER TABLE "A2_SAMPLEPV" ADD CONSTRAINT "PK_SAMPLEPV" PRIMARY KEY ("SAMPLEPVID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table A2_ORGANISM
--------------------------------------------------------

  ALTER TABLE "A2_ORGANISM" ADD CONSTRAINT "PK_ORGANISM" PRIMARY KEY ("ORGANISMID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 1048576 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  Constraints for Table LOAD_MONITOR
--------------------------------------------------------

  ALTER TABLE "LOAD_MONITOR" ADD CONSTRAINT "PK_LOAD_MONITOR" PRIMARY KEY ("ID") USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255  STORAGE(INITIAL 131072 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645 PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT) /*INDEX_TABLESPACE*/  ENABLE;

--------------------------------------------------------
--  DDL for Index A2_IDX_EXPRESSION_FV
--------------------------------------------------------

  CREATE INDEX "A2_IDX_EXPRESSION_FV" ON "A2_EXPRESSIONANALYTICS" ("PROPERTYVALUEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ANALYTICS_DESIGNELEMENT
--------------------------------------------------------

  CREATE INDEX "IDX_ANALYTICS_DESIGNELEMENT" ON "A2_EXPRESSIONANALYTICS" ("DESIGNELEMENTID") /*INDEX_TABLESPACE*/;
--------------------------------------------------------
--  DDL for Index IDX_ARRAYDESIGN_ACCESSION
--------------------------------------------------------

  CREATE INDEX "IDX_ARRAYDESIGN_ACCESSION" ON "A2_ARRAYDESIGN" ("ACCESSION") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ASSAYPROPERTY_AP
--------------------------------------------------------

  CREATE INDEX "IDX_ASSAYPROPERTY_AP" ON "A2_ASSAYPV" ("ASSAYID", "PROPERTYVALUEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ASSAYPROPERTY_ASSAY
--------------------------------------------------------

  CREATE INDEX "IDX_ASSAYPROPERTY_ASSAY" ON "A2_ASSAYPV" ("ASSAYID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ASSAYPROPERTY_PEROPERTY
--------------------------------------------------------

  CREATE INDEX "IDX_ASSAYPROPERTY_PEROPERTY" ON "A2_ASSAYPV" ("PROPERTYVALUEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ASSAY_ARRAYDESIGN
--------------------------------------------------------

  CREATE INDEX "IDX_ASSAY_ARRAYDESIGN" ON "A2_ASSAY" ("ARRAYDESIGNID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ASSAY_EXPERIMENT
--------------------------------------------------------

  CREATE INDEX "IDX_ASSAY_EXPERIMENT" ON "A2_ASSAY" ("EXPERIMENTID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_DESIGNELEMENT_GENE
--------------------------------------------------------

  CREATE INDEX "IDX_DESIGNELEMENT_GENE" ON "A2_DESIGNELEMENT" ("GENEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_EA_EXPERIMENT
--------------------------------------------------------

  CREATE INDEX "IDX_EA_EXPERIMENT" ON "A2_EXPRESSIONANALYTICS" ("EXPERIMENTID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_EV_ASSAYID
--------------------------------------------------------

  CREATE INDEX "IDX_EV_ASSAYID" ON "A2_EXPRESSIONVALUE" ("ASSAYID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_EV_DESIGNELEMENT
--------------------------------------------------------

  CREATE INDEX "IDX_EV_DESIGNELEMENT" ON "A2_EXPRESSIONVALUE" ("DESIGNELEMENTID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_EXPERIMENT_ACCESSION
--------------------------------------------------------

  CREATE INDEX "IDX_EXPERIMENT_ACCESSION" ON "A2_EXPERIMENT" ("ACCESSION") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_GENEPROPERTYVALUE_PROPERTY
--------------------------------------------------------

  CREATE INDEX "IDX_GENEPROPERTYVALUE_PROPERTY" ON "A2_GENEPROPERTYVALUE" ("GENEPROPERTYID") /*INDEX_TABLESPACE*/;
  
--------------------------------------------------------
--  DDL for Index IDX_GENEPROPERTYVALUE_PROPERTY
--------------------------------------------------------

  CREATE INDEX "IDX_GENEGPV_PROPERTY" ON "A2_GENEGPV" ("GENEPROPERTYVALUEID") /*INDEX_TABLESPACE*/;

  CREATE INDEX "IDX_GENEGPV_GENE" ON "A2_GENEGPV" ("GENEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_GENE_ORGANISM
--------------------------------------------------------

  CREATE INDEX "IDX_GENE_ORGANISM" ON "A2_GENE" ("ORGANISMID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_LOAD_ACCESSION
--------------------------------------------------------

  CREATE INDEX "IDX_LOAD_ACCESSION" ON "LOAD_MONITOR" ("ACCESSION") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_ONTOLOGYTERM_ONTOLOGY
--------------------------------------------------------

  CREATE INDEX "IDX_ONTOLOGYTERM_ONTOLOGY" ON "A2_ONTOLOGYTERM" ("ONTOLOGYID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_PROPERTYVALUE_NAME
--------------------------------------------------------

  CREATE INDEX "IDX_PROPERTYVALUE_NAME" ON "A2_PROPERTYVALUE" ("NAME") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_PROPERTYVALUE_PROPERTY
--------------------------------------------------------

  CREATE INDEX "IDX_PROPERTYVALUE_PROPERTY" ON "A2_PROPERTYVALUE" ("PROPERTYID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_PROPERTY_NAME
--------------------------------------------------------

  CREATE INDEX "IDX_PROPERTY_NAME" ON "A2_PROPERTY" ("NAME") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_SAMPLEPROPERTY_PROPERTY
--------------------------------------------------------

  CREATE INDEX "IDX_SAMPLEPROPERTY_PROPERTY" ON "A2_SAMPLEPV" ("PROPERTYVALUEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_SAMPLEPROPERTY_PS
--------------------------------------------------------

  CREATE INDEX "IDX_SAMPLEPROPERTY_PS" ON "A2_SAMPLEPV" ("PROPERTYVALUEID", "SAMPLEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_SAMPLEPROPERTY_SAMPLE
--------------------------------------------------------


  CREATE INDEX "IDX_SAMPLEPROPERTY_SAMPLE" ON "A2_SAMPLEPV" ("SAMPLEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for Index IDX_SAMPLEPROPERTY_SP
--------------------------------------------------------

  CREATE INDEX "IDX_SAMPLEPROPERTY_SP" ON "A2_SAMPLEPV" ("SAMPLEID", "PROPERTYVALUEID") /*INDEX_TABLESPACE*/;

--------------------------------------------------------
--  DDL for TABLE ASSAYONTOLOGY
--------------------------------------------------------

  CREATE INDEX "IDX_ASSAYPVONTOLOGY_ASSAYPV" ON "A2_ASSAYPVONTOLOGY" ("ASSAYPVID") /*INDEX_TABLESPACE*/;

  CREATE INDEX "IDX_ASSAYPVONTOLOGY_TERM" ON  "A2_ASSAYPVONTOLOGY" ("ONTOLOGYTERMID") /*INDEX_TABLESPACE*/;
 
/*
--------------------------------------------------------
--  DDL for Index PK_ARRAYDESIGN
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ARRAYDESIGN" ON "A2_ARRAYDESIGN" ("ARRAYDESIGNID")

--------------------------------------------------------
--  DDL for Index PK_ASSAY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ASSAY" ON "A2_ASSAY" ("ASSAYID")
--------------------------------------------------------
--  DDL for Index PK_ASSAYONTOLOGY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ASSAYONTOLOGY" ON "A2_ASSAYONTOLOGY" ("ASSAYONTOLOGYID")
--------------------------------------------------------
--  DDL for Index PK_ASSAYPV
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ASSAYPV" ON "A2_ASSAYPV" ("ASSAYPVID")
--------------------------------------------------------
--  DDL for Index PK_ASSAYSAMPLE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ASSAYSAMPLE" ON "A2_ASSAYSAMPLE" ("ASSAYSAMPLEID")
--------------------------------------------------------
--  DDL for Index PK_DESIGNELEMENT
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_DESIGNELEMENT" ON "A2_DESIGNELEMENT" ("DESIGNELEMENTID")
--------------------------------------------------------
--  DDL for Index PK_EXPERIMENT
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_EXPERIMENT" ON "A2_EXPERIMENT" ("EXPERIMENTID")
--------------------------------------------------------
--  DDL for Index PK_EXPRESSIONANALYTICS
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_EXPRESSIONANALYTICS" ON "A2_EXPRESSIONANALYTICS" ("EXPRESSIONID")
--------------------------------------------------------
--  DDL for Index PK_GENE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_GENE" ON "A2_GENE" ("GENEID")
--------------------------------------------------------
--  DDL for Index PK_GENEPROPERTY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_GENEPROPERTY" ON "A2_GENEPROPERTY" ("GENEPROPERTYID")
--------------------------------------------------------
--  DDL for Index PK_GENEPROPERTYVALUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_GENEPROPERTYVALUE" ON "A2_GENEPROPERTYVALUE" ("GENEPROPERTYVALUEID")
--------------------------------------------------------
--  DDL for Index PK_LOADMONITOR
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_LOADMONITOR" ON "LOAD_MONITOR" ("ID")
--------------------------------------------------------
--  DDL for Index PK_ONTOLOGY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ONTOLOGY" ON "A2_ONTOLOGY" ("ONTOLOGYID")
--------------------------------------------------------
--  DDL for Index PK_ONTOLOGYTERM
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ONTOLOGYTERM" ON "A2_ONTOLOGYTERM" ("ONTOLOGYTERMID")
--------------------------------------------------------
--  DDL for Index PK_PROPERTY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_PROPERTY" ON "A2_PROPERTY" ("PROPERTYID")
--------------------------------------------------------
--  DDL for Index PK_PROPERTYVALUE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_PROPERTYVALUE" ON "A2_PROPERTYVALUE" ("PROPERTYVALUEID")
--------------------------------------------------------
--  DDL for Index PK_SAMPLE
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_SAMPLE" ON "A2_SAMPLE" ("SAMPLEID")
--------------------------------------------------------
--  DDL for Index PK_SAMPLEONTOLOGY
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_SAMPLEONTOLOGY" ON "A2_SAMPLEONTOLOGY" ("SAMPLEONTOLOGYID")
--------------------------------------------------------
--  DDL for Index PK_SAMPLEPV
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_SAMPLEPV" ON "A2_SAMPLEPV" ("SAMPLEPVID")
--------------------------------------------------------
--  DDL for Index PK_ORGANISM
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ORGANISM" ON "A2_ORGANISM" ("ORGANISMID")
*/

--------------------------------------------------------
--  Ref Constraints for Table A2_ASSAY
--------------------------------------------------------

  ALTER TABLE "A2_ASSAY" ADD CONSTRAINT "FK_ASSAY_ARRAYDESIGN" FOREIGN KEY ("ARRAYDESIGNID") REFERENCES "A2_ARRAYDESIGN" ("ARRAYDESIGNID") ENABLE;
 
  ALTER TABLE "A2_ASSAY" ADD CONSTRAINT "FK_ASSAY_EXPERIMENT" FOREIGN KEY ("EXPERIMENTID") REFERENCES "A2_EXPERIMENT" ("EXPERIMENTID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table A2_ASSAYONTOLOGY
--------------------------------------------------------

  ALTER TABLE "A2_ASSAYPVONTOLOGY" ADD CONSTRAINT "FK_ASSAYPVONTOLOGY_ASSAYPV" FOREIGN KEY ("ASSAYPVID") REFERENCES "A2_ASSAYPV" ("ASSAYPVID") ENABLE;

  ALTER TABLE "A2_ASSAYPVONTOLOGY" ADD CONSTRAINT "FK_ASSAYPVONTOLOGY_ONTOLOGY" FOREIGN KEY ("ONTOLOGYTERMID") REFERENCES "A2_ONTOLOGYTERM" ("ONTOLOGYTERMID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table A2_ASSAYPV
--------------------------------------------------------

  ALTER TABLE "A2_ASSAYPV" ADD CONSTRAINT "FK_ASSAYPROPERTY_PROPERTY" FOREIGN KEY ("PROPERTYVALUEID") REFERENCES "A2_PROPERTYVALUE" ("PROPERTYVALUEID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table A2_ASSAYSAMPLE
--------------------------------------------------------

  ALTER TABLE "A2_ASSAYSAMPLE" ADD CONSTRAINT "FK_ASSAYSAMPLE_ASSAY" FOREIGN KEY ("ASSAYID") REFERENCES "A2_ASSAY" ("ASSAYID") ENABLE;
 
  ALTER TABLE "A2_ASSAYSAMPLE" ADD CONSTRAINT "FK_ASSAYSAMPLE_SAMPLE" FOREIGN KEY ("SAMPLEID") REFERENCES "A2_SAMPLE" ("SAMPLEID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table A2_DESIGNELEMENT
--------------------------------------------------------

  ALTER TABLE "A2_DESIGNELEMENT" ADD CONSTRAINT "FK_DESIGNELEMENT_ARRAYDESIGN" FOREIGN KEY ("ARRAYDESIGNID") REFERENCES "A2_ARRAYDESIGN" ("ARRAYDESIGNID") ENABLE;
 
  ALTER TABLE "A2_DESIGNELEMENT" ADD CONSTRAINT "FK_DESIGNELEMENT_GENE" FOREIGN KEY ("GENEID") REFERENCES "A2_GENE" ("GENEID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_EXPRESSIONANALYTICS
--------------------------------------------------------

  ALTER TABLE "A2_EXPRESSIONANALYTICS" ADD CONSTRAINT "FK_ANALYTICS_EXPERIMENT" FOREIGN KEY ("EXPERIMENTID") REFERENCES "A2_EXPERIMENT" ("EXPERIMENTID") ENABLE;
 
  ALTER TABLE "A2_EXPRESSIONANALYTICS" ADD CONSTRAINT "FK_ANALYTICS_PROPERTYVALUE" FOREIGN KEY ("PROPERTYVALUEID") REFERENCES "A2_PROPERTYVALUE" ("PROPERTYVALUEID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_GENE
--------------------------------------------------------

  ALTER TABLE "A2_GENE" ADD CONSTRAINT "FK_GENE_ORGANISM" FOREIGN KEY ("ORGANISMID") REFERENCES "A2_ORGANISM" ("ORGANISMID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_GENEPROPERTYVALUE
--------------------------------------------------------

  
  ALTER TABLE "A2_GENEPROPERTYVALUE" ADD CONSTRAINT "FK_GENEPROPERTYVALUE_PROPERTY" FOREIGN KEY ("GENEPROPERTYID") REFERENCES "A2_GENEPROPERTY" ("GENEPROPERTYID") ENABLE;

  ALTER TABLE "A2_GENEGPV" ADD CONSTRAINT "FK_GENEGPV_GENE" FOREIGN KEY ("GENEID") REFERENCES "A2_GENE" ("GENEID") ENABLE;
  
  ALTER TABLE "A2_GENEGPV" ADD CONSTRAINT "FK_GENEGPV_GPV" FOREIGN KEY ("GENEPROPERTYVALUEID") REFERENCES "A2_GENEPROPERTYVALUE" ("GENEPROPERTYVALUEID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_ONTOLOGYTERM
--------------------------------------------------------

  ALTER TABLE "A2_ONTOLOGYTERM" ADD CONSTRAINT "FK_ONTOLOGYTERM_ONTOLOGY" FOREIGN KEY ("ONTOLOGYID") REFERENCES "A2_ONTOLOGY" ("ONTOLOGYID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_PROPERTYVALUE
--------------------------------------------------------

  ALTER TABLE "A2_PROPERTYVALUE" ADD CONSTRAINT "FK_PROPERTY_PROPERTYVALUE" FOREIGN KEY ("PROPERTYID") REFERENCES "A2_PROPERTY" ("PROPERTYID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_SAMPLEONTOLOGY
--------------------------------------------------------

  ALTER TABLE "A2_SAMPLEPVONTOLOGY" ADD CONSTRAINT "FK_SAMPLEPVONTOLOGY_SAMPLEPV" FOREIGN KEY ("SAMPLEPVID") REFERENCES "A2_SAMPLEPV" ("SAMPLEPVID") ENABLE;
 
  ALTER TABLE "A2_SAMPLEPVONTOLOGY" ADD CONSTRAINT "FK_SAMPLEPVONTOLOGY_ONTOLOGY" FOREIGN KEY ("ONTOLOGYTERMID") REFERENCES "A2_ONTOLOGYTERM" ("ONTOLOGYTERMID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_SAMPLEPV
--------------------------------------------------------

  ALTER TABLE "A2_SAMPLEPV" ADD CONSTRAINT "FK_SAMPLEPROPERTY_SAMPLE" FOREIGN KEY ("SAMPLEID") REFERENCES "A2_SAMPLE" ("SAMPLEID") ENABLE;
 
  ALTER TABLE "A2_SAMPLEPV" ADD CONSTRAINT "FK_SAMPLEPROPERTY_PROPERTY" FOREIGN KEY ("PROPERTYVALUEID") REFERENCES "A2_PROPERTYVALUE" ("PROPERTYVALUEID") ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table A2_EXPRESSIONVALUE
--------------------------------------------------------

  ALTER TABLE "A2_EXPRESSIONVALUE" ADD CONSTRAINT "FK_EV_DESIGNELEMENT" FOREIGN KEY ("DESIGNELEMENTID") REFERENCES "A2_DESIGNELEMENT" ("DESIGNELEMENTID") ENABLE;
 
  ALTER TABLE "A2_EXPRESSIONVALUE" ADD CONSTRAINT "FK_EXPRESSIONVALUE_ASSAY" FOREIGN KEY ("ASSAYID") REFERENCES "A2_ASSAY" ("ASSAYID") ENABLE;


--------------------------------------------------------
--  DDL for Trigger A2_ASSAYPVONTOLOGY_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ASSAYPVONTOLOGY_INSERT"  

before insert on A2_AssayPvOntology
for each row
begin
if(:new.AssayPvOntologyID is null) then
select A2_AssayPvOntology_seq.nextval into :new.AssayPvOntologyID from dual;
end if;
end;
/

ALTER TRIGGER "A2_ASSAYPVONTOLOGY_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ASSAYPV_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ASSAYPV_INSERT"  

before insert on A2_ASSAYPV
for each row
begin
if(:new.ASSAYPVID is null) then
select A2_ASSAYPV_seq.nextval into :new.ASSAYPVID from dual;
end if;
end;
/

ALTER TRIGGER "A2_ASSAYPV_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ASSAYSAMPLE_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ASSAYSAMPLE_INSERT"  

before insert on A2_AssaySample
for each row
begin
if(:new.AssaySampleID is null) then
select A2_AssaySample_seq.nextval into :new.AssaySampleID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ASSAYSAMPLE_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ASSAY_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ASSAY_INSERT"  

before insert on A2_Assay
for each row
begin
if(:new.AssayID is null) then
select A2_Assay_seq.nextval into :new.AssayID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ASSAY_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ArrayDesign_Insert
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ArrayDesign_Insert"  

before insert on A2_ArrayDesign
for each row
begin
if( :new.ArrayDesignID is null) then
select A2_ArrayDesign_seq.nextval into :new.ArrayDesignID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ArrayDesign_Insert" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_DesignElement_Insert
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_DesignElement_Insert"  

before insert on A2_DesignElement
for each row
begin
if(:new.DesignElementID is null) then
select A2_DesignElement_seq.nextval into :new.DesignElementID from dual;
end if;
end;
/
ALTER TRIGGER "A2_DesignElement_Insert" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_EXPERIMENT_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_EXPERIMENT_INSERT"  

before insert on A2_Experiment
for each row
begin
if(:new.ExperimentID is null) then
  select A2_Experiment_seq.nextval into :new.ExperimentID from dual;
end if;
end;
/

ALTER TRIGGER "A2_EXPERIMENT_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ExpressionAnalytics_Insert
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ExpressionAnalytics_Insert"  

before insert on A2_ExpressionAnalytics
for each row
begin
if(:new.ExpressionID is null) then
select A2_ExpressionAnalytics_seq.nextval into :new.ExpressionID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ExpressionAnalytics_Insert" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ExpressionValue_Insert
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ExpressionValue_Insert"  
 

before insert on A2_ExpressionValue
for each row
begin
if(:new.ExpressionValueID is null) then
select A2_ExpressionValue_seq.nextval into :new.ExpressionValueID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ExpressionValue_Insert" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_GENEPropertyValue_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_GENEPropertyValue_INSERT"  

before insert on A2_GENEPropertyValue
for each row
begin
if(:new.GENEPropertyValueID is null) then
select A2_GENEPropertyValue_seq.nextval into :new.GENEPropertyValueID from dual;
end if;
end;
/
ALTER TRIGGER "A2_GENEPropertyValue_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_GENEProperty_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_GENEProperty_INSERT"  

before insert on A2_GENEProperty
for each row
begin
if(:new.GENEPropertyID is null) then
select A2_GENEProperty_seq.nextval into :new.GENEPropertyID from dual;
end if;
end;
/
ALTER TRIGGER "A2_GENEProperty_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_GENE_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_GENE_INSERT"  

before insert on A2_GENE
for each row
begin
if(:new.GENEID is null) then
select A2_GENE_seq.nextval into :new.GENEID from dual;
end if;
end;
/
ALTER TRIGGER "A2_GENE_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ONTOLOGYTERM_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ONTOLOGYTERM_INSERT"  

before insert on A2_OntologyTerm
for each row
begin
if( :new.OntologyTermID is null) then
select A2_OntologyTerm_seq.nextval into :new.OntologyTermID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ONTOLOGYTERM_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_PROPERTYVALUE_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_PROPERTYVALUE_INSERT"  

before insert on A2_PropertyValue
for each row
begin
if ( :new.PropertyValueID is null) then 
  select A2_PropertyValue_seq.nextval into :new.PropertyValueID from dual;
end if;
end;
/
ALTER TRIGGER "A2_PROPERTYVALUE_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_PROPERTY_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_PROPERTY_INSERT"  

before insert on A2_Property
for each row
begin
if(:new.PropertyID is null) then
  select A2_Property_seq.nextval into :new.PropertyID from dual;
end if;
end;
/
ALTER TRIGGER "A2_PROPERTY_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_SAMPLEPVONTOLOGY_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_SAMPLEPVONTOLOGY_INSERT"  

before insert on A2_SamplePvOntology
for each row
begin
if(:new.SamplePvOntologyID is null) then
select A2_SamplePvOntology_seq.nextval into :new.SamplePvOntologyID from dual;
end if;
end;
/
ALTER TRIGGER "A2_SAMPLEPVONTOLOGY_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_SAMPLEPV_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_SAMPLEPV_INSERT"  

before insert on A2_SAMPLEPV
for each row
begin
if(:new.SAMPLEPVID is null) then
select A2_SAMPLEPV_seq.nextval into :new.SAMPLEPVID from dual;
end if;
end;
/
ALTER TRIGGER "A2_SAMPLEPV_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_SAMPLE_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_SAMPLE_INSERT"  

before insert on A2_Sample
for each row
begin
if(:new.SampleID is null) then
select A2_Sample_seq.nextval into :new.SampleID from dual;
end if;
end;
/
ALTER TRIGGER "A2_SAMPLE_INSERT" ENABLE;
--------------------------------------------------------
--  DDL for Trigger A2_ORGANISM_INSERT
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "A2_ORGANISM_INSERT"  

before insert on A2_ORGANISM
for each row
begin
if(:new.ORGANISMID is null) then
select A2_ORGANISM_seq.nextval into :new.ORGANISMID from dual;
end if;
end;
/
ALTER TRIGGER "A2_ORGANISM_INSERT" ENABLE;
/

  CREATE OR REPLACE TRIGGER "A2_GENEGPV_INSERT"  

before insert on A2_GENEGPV
for each row
begin
if(:new.GENEGPVID is null) then
select A2_GENEGPV_seq.nextval into :new.GENEGPVID from dual;
end if;
end;
/
ALTER TRIGGER "A2_GENEGPV_INSERT" ENABLE;
/
--------------------------------------------------------
--  DDL for View A2_ONTOLOGYMAPPING
--------------------------------------------------------

-- COMMIT WORK;
--ROLLBACK;

quit;
/