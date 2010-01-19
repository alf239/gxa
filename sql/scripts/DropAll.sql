DROP TABLE A2_ARRAYDESIGN;
DROP SEQUENCE A2_ARRAYDESIGN_SEQ;
DROP TABLE A2_ASSAY;
DROP TABLE A2_ASSAYONTOLOGY;
DROP TRIGGER A2_ASSAYONTOLOGY_INSERT;
DROP SEQUENCE A2_ASSAYONTOLOGY_SEQ;
DROP TABLE A2_ASSAYPROPERTYVALUE;
DROP TRIGGER A2_ASSAYPROPERTYVALUE_INSERT;
DROP SEQUENCE A2_ASSAYPROPERTYVALUE_SEQ;
DROP TABLE A2_ASSAYSAMPLE;
DROP TRIGGER A2_ASSAYSAMPLE_INSERT;
DROP SEQUENCE A2_ASSAYSAMPLE_SEQ;
DROP TRIGGER A2_ASSAY_INSERT;
DROP SEQUENCE A2_ASSAY_SEQ;
DROP TRIGGER A2_ArrayDesign_Insert;
DROP TABLE A2_DESIGNELEMENT;
DROP SEQUENCE A2_DESIGNELEMENT_SEQ;
DROP TRIGGER A2_DesignElement_Insert;
DROP TABLE A2_EXPERIMENT;
DROP TRIGGER A2_EXPERIMENT_INSERT;
DROP SEQUENCE A2_EXPERIMENT_SEQ;
DROP TABLE A2_EXPRESSIONANALYTICS;
DROP SEQUENCE A2_EXPRESSIONANALYTICS_SEQ;
DROP PROCEDURE A2_EXPRESSIONGET;
DROP TABLE A2_EXPRESSIONVALUE;
DROP INDEX A2_EXPRESSIONVALUE_PK;
DROP SEQUENCE A2_EXPRESSIONVALUE_SEQ;
DROP TRIGGER A2_ExpressionAnalytics_Insert;
DROP TRIGGER A2_ExpressionValue_Insert;
DROP TABLE A2_GENE;
DROP TABLE A2_GENEPROPERTY;
DROP TABLE A2_GENEPROPERTYVALUE;
DROP SEQUENCE A2_GENEPROPERTYVALUE_SEQ;
DROP SEQUENCE A2_GENEPROPERTY_SEQ;
DROP TRIGGER A2_GENEPropertyValue_INSERT;
DROP TRIGGER A2_GENEProperty_INSERT;
DROP TRIGGER A2_GENE_INSERT;
DROP SEQUENCE A2_GENE_SEQ;
DROP INDEX A2_IDX_EXPRESSION_FV;
DROP INDEX A2_IDX_EXPRESSION_GENE;
DROP TABLE A2_ONTOLOGY;
DROP VIEW A2_ONTOLOGYMAPPING;
DROP TABLE A2_ONTOLOGYTERM;
DROP TRIGGER A2_ONTOLOGYTERM_INSERT;
DROP SEQUENCE A2_ONTOLOGYTERM_SEQ;
DROP PROCEDURE A2_ORGANISMPARTGET;
DROP TABLE A2_PROPERTY;
DROP TABLE A2_PROPERTYVALUE;
DROP TRIGGER A2_PROPERTYVALUE_INSERT;
DROP SEQUENCE A2_PROPERTYVALUE_SEQ;
DROP TRIGGER A2_PROPERTY_INSERT;
DROP SEQUENCE A2_PROPERTY_SEQ;
DROP TABLE A2_SAMPLE;
DROP TABLE A2_SAMPLEONTOLOGY;
DROP TRIGGER A2_SAMPLEONTOLOGY_INSERT;
DROP SEQUENCE A2_SAMPLEONTOLOGY_SEQ;
DROP TABLE A2_SAMPLEPROPERTYVALUE;
DROP TRIGGER A2_SAMPLEPROPERTYVALUE_INSERT;
DROP SEQUENCE A2_SAMPLEPROPERTYVALUE_SEQ;
DROP TRIGGER A2_SAMPLE_INSERT;
DROP SEQUENCE A2_SAMPLE_SEQ;
DROP TABLE A2_SPEC;
DROP TRIGGER A2_SPEC_INSERT;
DROP SEQUENCE A2_SPEC_SEQ;
DROP TYPE ACCESSIONQUERY;
DROP TYPE ACCESSIONTABLE;
DROP TYPE ARRAYDESIGNQUERY;
DROP TYPE ASSAYQUERY;
DROP TABLE ATLAS;
DROP PACKAGE ATLASAPI;
DROP PACKAGE BODY ATLASAPI;
DROP PACKAGE ATLASLDR;
DROP PACKAGE BODY ATLASLDR;
DROP TYPE DESIGNELEMENT;
DROP TYPE DESIGNELEMENTTABLE;
DROP TYPE EXPERIMENTQUERY;
DROP TYPE EXPRESSIONANALYTICS;
DROP TYPE EXPRESSIONANALYTICSTABLE;
DROP TYPE EXPRESSIONVALUE;
DROP TYPE EXPRESSIONVALUETABLE;
DROP FUNCTION F1;
DROP TYPE GENEPROPERTYQUERY;
DROP TYPE GENEQUERY;
DROP TABLE HUGE;
DROP INDEX IDX_ANALYTICS_DESIGNELEMENT;
DROP INDEX IDX_ARRAYDESIGN_ACCESSION;
DROP INDEX IDX_ASSAYPROPERTY_AP;
DROP INDEX IDX_ASSAYPROPERTY_ASSAY;
DROP INDEX IDX_ASSAYPROPERTY_PEROPERTY;
DROP INDEX IDX_ASSAY_ARRAYDESIGN;
DROP INDEX IDX_ASSAY_EXPERIMENT;
DROP INDEX IDX_ATLAS_EXPERIMENT_EF_EFV;
DROP INDEX IDX_ATLAS_EXPERIMENT_ID;
DROP INDEX IDX_ATLAS_LASTHOPE;
DROP INDEX IDX_DESIGNELEMENT_GENE;
DROP INDEX IDX_EA_EXPERIMENT;
DROP INDEX IDX_EV_ASSAYID;
DROP INDEX IDX_EV_DESIGNELEMENT;
DROP INDEX IDX_EXPERIMENT_ACCESSION;
DROP INDEX IDX_GENEPROPERTYVALUE_PROPERTY;
DROP INDEX IDX_GENE_SPEC;
DROP INDEX IDX_GPV_GENE;
DROP INDEX IDX_GPV_GENEPROPERTY;
DROP INDEX IDX_LAST_HOPE2;
DROP INDEX IDX_LOAD_ACCESSION;
DROP INDEX IDX_ONTOLOGYTERM_ONTOLOGY;
DROP INDEX IDX_PROPERTYVALUE_NAME;
DROP INDEX IDX_PROPERTYVALUE_PROPERTY;
DROP INDEX IDX_PROPERTY_NAME;
DROP INDEX IDX_SAMPLEPROPERTY_PROPERTY;
DROP INDEX IDX_SAMPLEPROPERTY_PS;
DROP INDEX IDX_SAMPLEPROPERTY_SAMPLE;
DROP INDEX IDX_SAMPLEPROPERTY_SP;
DROP INDEX IDX_TMP_ASSAYONTOLOGYID;
DROP INDEX IDX_TMP_EFV;
DROP INDEX IDX_TMP_SAMPLEONTOLOGYID;
DROP TYPE INTARRAY;
DROP TYPE INTRECORD;
DROP FUNCTION LIST_TO_TABLE;
DROP TABLE LOAD_MONITOR;
DROP SEQUENCE LOAD_MONITOR_SEQ;
DROP TYPE PAGESORTPARAMS;
DROP INDEX PK_ARRAYDESIGN;
DROP INDEX PK_ASSAY;
DROP INDEX PK_ASSAYONTOLOGY;
DROP INDEX PK_ASSAYPROPERTYVALUE;
DROP INDEX PK_ASSAYSAMPLE;
DROP INDEX PK_DESIGNELEMENT;
DROP INDEX PK_EXPERIMENT;
DROP INDEX PK_EXPRESSIONANALYTICS;
DROP INDEX PK_GENE;
DROP INDEX PK_GENEPROPERTY;
DROP INDEX PK_GENEPROPERTYVALUE;
DROP INDEX PK_LOADMONITOR;
DROP INDEX PK_ONTOLOGY;
DROP INDEX PK_ONTOLOGYTERM;
DROP INDEX PK_PROPERTY;
DROP INDEX PK_PROPERTYVALUE;
DROP INDEX PK_SAMPLE;
DROP INDEX PK_SAMPLEONTOLOGY;
DROP INDEX PK_SAMPLEPROPERTYVALUE;
DROP INDEX PK_SOMETABLE;
DROP INDEX PK_SPEC;
DROP TABLE PLAN_TABLE;
DROP TYPE PROPERTY;
DROP TYPE PROPERTYQUERY;
DROP TYPE PROPERTYTABLE;
DROP TYPE SAMPLEQUERY;
DROP TABLE SOMETABLE;
DROP VIEW SQLRU1;
DROP INDEX SYS_C008295;
DROP INDEX SYS_C008297;
DROP TABLE T;
DROP TYPE TARATYPE;
DROP TYPE TBLINT;
DROP TYPE TBLINTEGER;
DROP TYPE TBLVARCHAR;
DROP PROCEDURE TEST_ASSAYSET;
DROP PROCEDURE TEST_PROPERTYGET;
DROP PROCEDURE TEST_VERSION_REFCURSOR;
DROP TABLE TMP_A_EF;
DROP TABLE TMP_SOURCEMAPPING;
DROP TABLE TMP_SOURCEMAPPINGASSAY;
DROP PROCEDURE TMP_UPDATEEXPRESSIONANALYTICS;
DROP PROCEDURE UTIL_A;
DROP PACKAGE UT_ATLASAPI;
DROP PACKAGE BODY UT_ATLASAPI;
DROP PACKAGE UT_ATLASLDR;
DROP PROCEDURE VERSION_REFCURSOR;
DROP VIEW VWARRAYDESIGN;
DROP VIEW VWARRAYDESIGNELEMENT;
DROP VIEW VWASSAY;
DROP VIEW VWASSAYPROPERTY;
DROP VIEW VWASSAYSAMPLE;
DROP VIEW VWEXPERIMENT;
DROP VIEW VWEXPERIMENTASSAY;
DROP VIEW VWEXPERIMENTFACTORS;
DROP VIEW VWEXPERIMENTSAMPLE;
DROP VIEW VWGENE;
DROP VIEW VWGENEPROPERTYVALUE;
DROP VIEW VWPIVOTGENEAGAIN;
DROP VIEW VWPIVOTGENEALL;
DROP VIEW VWPIVOTGENEEF;
DROP VIEW VWPIVOTGENEPROPERTY1;
DROP VIEW VWPIVOTGENEPROPERTY2;
DROP VIEW VWPROPERTYVALUE;
DROP VIEW VWSAMPLE;
DROP VIEW VWSAMPLEASSAY;
DROP VIEW VWSAMPLEPROPERTY;
DROP PROCEDURE A2_ARRAYDESIGNSET;
DROP PROCEDURE A2_ASSAYSET;
DROP PROCEDURE A2_EXPERIMENTSET;
DROP VIEW VWGENEPROPERTY;


select * from all_objects where object_name like 'A2_%'
