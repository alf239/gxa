OPTIONS(DIRECT=TRUE,ROWS=1000000) LOAD DATA TRUNCATE INTO TABLE A2_SAMPLE FIELDS TERMINATED BY '\t' TRAILING NULLCOLS (SAMPLEID,ACCESSION CHAR(255),SPECIES CHAR(255),CHANNEL CHAR(255))
