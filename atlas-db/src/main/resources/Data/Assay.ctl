OPTIONS(DIRECT=TRUE,ROWS=1000000) LOAD DATA TRUNCATE INTO TABLE A2_ASSAY FIELDS TERMINATED BY '\t' TRAILING NULLCOLS (ASSAYID,ACCESSION CHAR(255),EXPERIMENTID,ARRAYDESIGNID)
