OPTIONS(DIRECT=TRUE,ROWS=1000000) LOAD DATA TRUNCATE INTO TABLE A2_ARRAYDESIGN FIELDS TERMINATED BY '\t' TRAILING NULLCOLS (ARRAYDESIGNID,ACCESSION CHAR(255),TYPE CHAR(255),NAME CHAR(255),PROVIDER CHAR(255))
