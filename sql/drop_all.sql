BEGIN
FOR I IN 1..4 
LOOP

DROP FUNCTION LIST_TO_TABLE;
DROP INDEX A2_CONFIG_PROPERTIES_PK;
DROP INDEX A2_TASKMAN_OPERATIONLOG_IDX1;
DROP INDEX A2_TASKMAN_OPERATIONLOG_IDX2;
DROP INDEX A2_TASKMAN_OPERATIONLOG_PK;
DROP INDEX A2_TASKMAN_TASKSTAGELOG_IDX1;
DROP INDEX A2_TASKMAN_TASKSTAGELOG_PK;
DROP INDEX A2_TASKMAN_TASKSTAGE_PK;
DROP INDEX IDX_ANALYTICS_DESIGNELEMENT;
DROP INDEX IDX_ANALYTICS_EXPERIMENT;
DROP INDEX IDX_ANALYTICS_PROPERTY;
DROP INDEX IDX_ARRAYDESIGN_ACCESSION;
DROP INDEX IDX_ASSAYPVONTOLOGY_ASSAYPV;
DROP INDEX IDX_ASSAYPVONTOLOGY_TERM;
DROP INDEX IDX_ASSAYPV_ASSAY;
DROP INDEX IDX_ASSAYPV_PROPERTYVALUE;
DROP INDEX IDX_ASSAYSAMPLE_ASSAY;
DROP INDEX IDX_ASSAYSAMPLE_SAMPLE;
DROP INDEX IDX_ASSAY_ARRAYDESIGN;
DROP INDEX IDX_ASSAY_EXPERIMENT;
DROP INDEX IDX_DESIGNELEMENT_ARRAYDESIGN;
DROP INDEX IDX_DESIGNELEMENT_GENE;
DROP INDEX IDX_EV_ASSAYID;
DROP INDEX IDX_EXPERIMENT_ACCESSION;
DROP INDEX IDX_GENEGPV_GENE;
DROP INDEX IDX_GENEGPV_PROPERTY;
DROP INDEX IDX_GENEPROPERTYVALUE_PROPERTY;
DROP INDEX IDX_GENE_ORGANISM;
DROP INDEX IDX_LOAD_ACCESSION;
DROP INDEX IDX_ONTOLOGYTERM_ONTOLOGY;
DROP INDEX IDX_PROPERTYVALUE_NAME;
DROP INDEX IDX_PROPERTYVALUE_PROPERTY;
DROP INDEX IDX_PROPERTY_NAME;
DROP INDEX IDX_SAMPLEPROPERTY_PROPERTY;
DROP INDEX IDX_SAMPLEPROPERTY_PS;
DROP INDEX IDX_SAMPLEPROPERTY_SAMPLE;
DROP INDEX IDX_SAMPLEPROPERTY_SP;
DROP INDEX IDX_SAMPLEPVONTOLOGY_ONTOLOGY;
DROP INDEX IDX_SAMPLEPVONTOLOGY_SAMPLEPV;
DROP INDEX PK_ARRAYDESIGN;
DROP INDEX PK_ASSAY;
DROP INDEX PK_ASSAYPV;
DROP INDEX PK_ASSAYPVONTOLOGY;
DROP INDEX PK_ASSAYSAMPLE;
DROP INDEX PK_DESIGNELEMENT;
DROP INDEX PK_EXPERIMENT;
DROP INDEX PK_EXPRESSIONANALYTICS;
DROP INDEX PK_EXPRESSIONVALUE;
DROP INDEX PK_GENE;
DROP INDEX PK_GENEGPV;
DROP INDEX PK_GENEPROPERTY;
DROP INDEX PK_GENEPROPERTYVALUE;
DROP INDEX PK_LOAD_MONITOR;
DROP INDEX PK_ONTOLOGY;
DROP INDEX PK_ONTOLOGYTERM;
DROP INDEX PK_ORGANISM;
DROP INDEX PK_PROPERTY;
DROP INDEX PK_PROPERTYVALUE;
DROP INDEX PK_SAMPLE;
DROP INDEX PK_SAMPLEPV;
DROP INDEX PK_SAMPLEPVONTOLOGY;
DROP INDEX UQ_ANALYTICS;
DROP INDEX UQ_ARRAYDESIGN_NAME;
DROP INDEX UQ_ASSAYPV;
DROP INDEX UQ_ASSAYPVONTOLOGY;
DROP INDEX UQ_ASSAYSAMPLE;
DROP INDEX UQ_ASSAY_ACCESSION;
DROP INDEX UQ_DESIGNELEMENT_ACCESSION;
DROP INDEX UQ_EXPRESSIONVALUE;
DROP INDEX UQ_GENEGPV;
DROP INDEX UQ_GENEPROPERTYVALUE_VALUE;
DROP INDEX UQ_GENEPROPERTY_NAME;
DROP INDEX UQ_GENE_IDENTIFIER;
DROP INDEX UQ_ONTOLOGYTERM_ACCESSION;
DROP INDEX UQ_ONTOLOGYTERM_TERM;
DROP INDEX UQ_ORGANISM_NAME;
DROP INDEX UQ_PROPERTYVALUE_NAME;
DROP INDEX UQ_SAMPLEPVONTOLOGY;
DROP PACKAGE ATLASAPI;
DROP PACKAGE ATLASLDR;
DROP PACKAGE ATLASMGR;
DROP PACKAGE BODY ATLASAPI;
DROP PACKAGE BODY ATLASLDR;
DROP PACKAGE BODY ATLASMGR;
DROP SEQUENCE A2_ARRAYDESIGN_SEQ;
DROP SEQUENCE A2_ASSAYPVONTOLOGY_SEQ;
DROP SEQUENCE A2_ASSAYPV_SEQ;
DROP SEQUENCE A2_ASSAYSAMPLE_SEQ;
DROP SEQUENCE A2_ASSAY_SEQ;
DROP SEQUENCE A2_DESIGNELEMENT_SEQ;
DROP SEQUENCE A2_EXPERIMENT_SEQ;
DROP SEQUENCE A2_EXPRESSIONANALYTICS_SEQ;
DROP SEQUENCE A2_EXPRESSIONVALUE_SEQ;
DROP SEQUENCE A2_GENEGPV_SEQ;
DROP SEQUENCE A2_GENEPROPERTYVALUE_SEQ;
DROP SEQUENCE A2_GENEPROPERTY_SEQ;
DROP SEQUENCE A2_GENE_SEQ;
DROP SEQUENCE A2_ONTOLOGYTERM_SEQ;
DROP SEQUENCE A2_ORGANISM_SEQ;
DROP SEQUENCE A2_PROPERTYVALUE_SEQ;
DROP SEQUENCE A2_PROPERTY_SEQ;
DROP SEQUENCE A2_SAMPLEPVONTOLOGY_SEQ;
DROP SEQUENCE A2_SAMPLEPV_SEQ;
DROP SEQUENCE A2_SAMPLE_SEQ;
DROP SEQUENCE A2_TASKMAN_OPERATIONLOG_SEQ;
DROP SEQUENCE A2_TASKMAN_TASKSTAGELOG_SEQ;
DROP SEQUENCE LOAD_MONITOR_SEQ;
DROP TABLE A2_ARRAYDESIGN;
DROP TABLE A2_ASSAY;
DROP TABLE A2_ASSAYPV;
DROP TABLE A2_ASSAYPVONTOLOGY;
DROP TABLE A2_ASSAYSAMPLE;
DROP TABLE A2_CONFIG_PROPERTY;
DROP TABLE A2_DESIGNELEMENT;
DROP TABLE A2_EXPERIMENT;
DROP TABLE A2_EXPRESSIONANALYTICS;
DROP TABLE A2_EXPRESSIONVALUE;
DROP TABLE A2_GENE;
DROP TABLE A2_GENEGPV;
DROP TABLE A2_GENEPROPERTY;
DROP TABLE A2_GENEPROPERTYVALUE;
DROP TABLE A2_ONTOLOGY;
DROP TABLE A2_ONTOLOGYTERM;
DROP TABLE A2_ORGANISM;
DROP TABLE A2_PROPERTY;
DROP TABLE A2_PROPERTYVALUE;
DROP TABLE A2_SAMPLE;
DROP TABLE A2_SAMPLEPV;
DROP TABLE A2_SAMPLEPVONTOLOGY;
DROP TABLE A2_TASKMAN_OPERATIONLOG;
DROP TABLE A2_TASKMAN_TASKSTAGE;
DROP TABLE A2_TASKMAN_TASKSTAGELOG;
DROP TABLE LOAD_MONITOR;
DROP TABLE TMP_DESIGNELEMENTMAP;
DROP TRIGGER A2_ARRAYDESIGN_INSERT;
DROP TRIGGER A2_ASSAYPVONTOLOGY_INSERT;
DROP TRIGGER A2_ASSAYPV_INSERT;
DROP TRIGGER A2_ASSAYSAMPLE_INSERT;
DROP TRIGGER A2_ASSAY_INSERT;
DROP TRIGGER A2_DESIGNELEMENT_INSERT;
DROP TRIGGER A2_EXPERIMENT_INSERT;
DROP TRIGGER A2_EXPRESSIONANALYTICS_INSERT;
DROP TRIGGER A2_EXPRESSIONVALUE_INSERT;
DROP TRIGGER A2_GENEGPV_INSERT;
DROP TRIGGER A2_GENEPROPERTYVALUE_INSERT;
DROP TRIGGER A2_GENEPROPERTY_INSERT;
DROP TRIGGER A2_GENE_INSERT;
DROP TRIGGER A2_ONTOLOGYTERM_INSERT;
DROP TRIGGER A2_ORGANISM_INSERT;
DROP TRIGGER A2_PROPERTYVALUE_INSERT;
DROP TRIGGER A2_PROPERTY_INSERT;
DROP TRIGGER A2_SAMPLEPVONTOLOGY_INSERT;
DROP TRIGGER A2_SAMPLEPV_INSERT;
DROP TRIGGER A2_SAMPLE_INSERT;
DROP TRIGGER A2_TASKMAN_OPERATIONLOG_TRG;
DROP TRIGGER A2_TASKMAN_TASKSTAGELOG_TRG;
DROP TYPE ACCESSIONQUERY;
DROP TYPE ACCESSIONTABLE;
DROP TYPE ARRAYDESIGNQUERY;
DROP TYPE ASSAYQUERY;
DROP TYPE DESIGNELEMENT2;
DROP TYPE DESIGNELEMENTTABLE;
DROP TYPE EXPERIMENTQUERY;
DROP TYPE EXPRESSIONANALYTICS;
DROP TYPE EXPRESSIONANALYTICSTABLE;
DROP TYPE EXPRESSIONVALUE;
DROP TYPE EXPRESSIONVALUETABLE;
DROP TYPE GENEINFO;
DROP TYPE GENEINFOTABLE;
DROP TYPE GENEPROPERTYQUERY;
DROP TYPE GENEQUERY;
DROP TYPE INTARRAY;
DROP TYPE INTRECORD;
DROP TYPE PAGESORTPARAMS;
DROP TYPE PROPERTY;
DROP TYPE PROPERTYQUERY;
DROP TYPE PROPERTYTABLE;
DROP TYPE SAMPLEQUERY;
DROP TYPE TBLINT;
DROP TYPE TBLINTEGER;
DROP TYPE TBLVARCHAR;
DROP VIEW A2_ONTOLOGYMAPPING;
DROP VIEW VWARRAYDESIGN;
DROP VIEW VWARRAYDESIGNELEMENT;
DROP VIEW VWASSAY;
DROP VIEW VWASSAYPROPERTY;
DROP VIEW VWASSAYSAMPLE;
DROP VIEW VWCHECK;
DROP VIEW VWEXPERIMENT;
DROP VIEW VWEXPERIMENTASSAY;
DROP VIEW VWEXPERIMENTFACTORS;
DROP VIEW VWEXPERIMENTSAMPLE;
DROP VIEW VWEXPRESSIONANALYTICSBYGENE;
DROP VIEW VWGENE;
DROP VIEW VWGENEIDPROPERTY;
DROP VIEW VWGENEIDS;
DROP VIEW VWGENEPROPERTIES;
DROP VIEW VWGENEPROPERTYVALUE;
DROP VIEW VWPROPERTYVALUE;
DROP VIEW VWSAMPLE;
DROP VIEW VWSAMPLEASSAY;
DROP VIEW VWSAMPLEPROPERTY;

END LOOP;

PURGE RECYCLEBIN;

END;

quit;