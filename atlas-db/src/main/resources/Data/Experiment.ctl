OPTIONS(DIRECT=TRUE,ROWS=1000000) LOAD DATA TRUNCATE INTO TABLE A2_EXPERIMENT FIELDS TERMINATED BY '\t' TRAILING NULLCOLS (EXPERIMENTID,ACCESSION CHAR(255),DESCRIPTION CHAR(2000),PERFORMER CHAR(2000),LAB CHAR(2000),LOADDATE DATE "YYYY-MM-DD HH24:MI:SS")
