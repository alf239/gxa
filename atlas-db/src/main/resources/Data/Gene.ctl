OPTIONS(DIRECT=TRUE,ROWS=1000000) LOAD DATA TRUNCATE INTO TABLE A2_GENE FIELDS TERMINATED BY '\t' TRAILING NULLCOLS (GENEID,ORGANISMID,IDENTIFIER CHAR(255),NAME CHAR(255))
