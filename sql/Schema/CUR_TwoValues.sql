--DROP TYPE CUR_TwoValues_Table;
CREATE OR REPLACE TYPE CUR_TwoValues AS OBJECT
       (Assay   VARCHAR(255),
        Value1  VARCHAR2(255),
        Value2  VARCHAR2(255),
        SampleID INTEGER);
/
CREATE OR REPLACE TYPE CUR_TwoValues_Table AS TABLE OF CUR_TwoValues;
/
exit;
/
