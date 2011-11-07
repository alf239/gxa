alter table a2_samplepv add propertyid NUMBER(22,0);

update a2_samplepv spv set spv.propertyid = (select propertyid from a2_propertyvalue pv 
    where spv.propertyvalueid=pv.propertyvalueid);



alter table a2_assaypv add propertyid NUMBER(22,0);

update a2_assaypv apv set apv.propertyid = (select propertyid from a2_propertyvalue pv
    where apv.propertyvalueid=pv.propertyvalueid);



alter table a2_propertyvalue drop constraint uq_propertyvalue_name;
alter table a2_propertyvalue drop column propertyid;