/*******************************************************************************
rebuilding sequences for all tables in schema

naming conventions and DB structure assumptions:
Each table has one autoincrement PK, updated in trigger from corresponding 
sequence. SEQUENCE_NAME = TABLE_NAME || "_SEQ"

call RebuildSequence();
********************************************************************************/
create or replace procedure RebuildSequence
AS
 cursor c1 is select SEQUENCE_NAME, LAST_NUMBER from user_sequences;
 q varchar2(8000);
 TABLE_NAME varchar2(255);
 PK_NAME varchar2(255);
 MaxID int;
 Delta int;
 NewID int;
begin 
for rec in c1
 loop
    Select REPLACE(rec.SEQUENCE_NAME, '_SEQ','') into TABLE_NAME from dual;
    
    select c.COLUMN_NAME into PK_NAME 
    from user_tab_columns c
    join user_constraints k on k.table_name = c.table_name
    join user_indexes i on i.INDEX_NAME = k.INDEX_NAME and i.TABLE_NAME = k.TABLE_NAME 
    join user_ind_columns ic on ic.INDEX_NAME = i.INDEX_NAME and ic.TABLE_NAME = i.TABLE_NAME
    where ic.COLUMN_NAME = c.COLUMN_NAME
    and k.constraint_type ='P'
    and k.TABLE_NAME = REBUILDSEQUENCE.TABLE_NAME;
    
    q := 'SELECT MAX($PK_NAME) FROM $TABLE_NAME';
    q := REPLACE(q,'$PK_NAME',PK_NAME);
    q := REPLACE(q,'$TABLE_NAME',TABLE_NAME);
    
    dbms_output.put_line(q);
    Execute immediate q INTO MaxID; 

    Delta := MaxID - rec.LAST_NUMBER;
    
    if (Delta > 0) then
    q := 'ALTER SEQUENCE $SEQUENCE_NAME INCREMENT BY $DELTA';
    q := REPLACE(q,'$SEQUENCE_NAME',rec.SEQUENCE_NAME); 
    q := REPLACE(q,'$DELTA',DELTA);
    dbms_output.put_line(q);
    Execute immediate q;
    
    q := 'SELECT $SEQUENCE_NAME.nextval FROM dual';
    q := REPLACE(q,'$SEQUENCE_NAME',rec.SEQUENCE_NAME); 
    dbms_output.put_line(q);
    Execute immediate q into NewID; --!!not called w/o INTO!!
    
    q := 'ALTER SEQUENCE $SEQUENCE_NAME INCREMENT BY 1';
    q := REPLACE(q,'$SEQUENCE_NAME',rec.SEQUENCE_NAME); 
    dbms_output.put_line(q);
    Execute immediate q;
    
    end if;
 end loop;
END;