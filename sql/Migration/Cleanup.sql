alter table a2_assaypvontology drop constraint UQ_ASSAYPVONTOLOGY;
alter table a2_genepropertyvalue drop constraint UQ_GENEPROPERTYVALUE_VALUE;
alter table a2_propertyvalue drop constraint UQ_PROPERTYVALUE_NAME;
alter table a2_samplepvontology drop constraint UQ_SAMPLEPVONTOLOGY;

delete from a2_assaypvontology where ASSAYPVID is null

select * from a2_PropertyValue where PropertyID = 28 and name like 'mammary%'

select PropertyID, TRIM(Name), count(1) from a2_propertyvalue
group by PropertyID, TRIM(Name)
having count(1) > 1

select * from a2_PropertyValue where PropertyID = 28 and name like 'mammary%'
select * from a2_PropertyValue where PropertyID = 28 and name like 'mammary%'

delete from a2_propertyvalue where propertyvalueid = 17977
delete from a2_samplepvontology where SAMPLEPVID is null;

select * from a2_expressionvalue where rownum < 10

call ATLASMGR.DisableConstraints();
call ATLASMGR.EnableConstraints();

select distinct SampleID  from a2_SamplePV 
where not exists(select 1 from a2_Sample where a2_Sample.SampleID = a2_SamplePV.SampleID )
and rownum < 10

select distinct AssayID  from a2_AssayPV 
where not exists(select 1 from a2_Assay where a2_Assay.AssayID = a2_AssayPV.AssayID )
and rownum < 10

Select * from a2_SamplePV where SampleID = 886628464
select * from a2_propertyvalue where propertyvalueid = 2854
1006

select * from A2_ASSAYPVONTOLOGY where AssayPVID is null

select * from a2_Proper

 ALTER TABLE "A2_SAMPLEPVONTOLOGY" 
  ADD CONSTRAINT "UQ_SAMPLEPVONTOLOGY" 
    UNIQUE(ONTOLOGYTERMID, SAMPLEPVID) 
    ENABLE;  
    
select ONTOLOGYTERMID, SAMPLEPVID, count(1)
from a2_samplepvontology    
group by ONTOLOGYTERMID, SAMPLEPVID
having count(1) > 1

 ALTER TABLE "A2_PROPERTYVALUE" 
  ADD CONSTRAINT "UQ_PROPERTYVALUE_NAME" 
    UNIQUE("PROPERTYID","NAME") 
    ENABLE;
    
      ALTER TABLE "A2_GENEPROPERTYVALUE" 
  ADD CONSTRAINT "UQ_GENEPROPERTYVALUE_VALUE" 
    UNIQUE("GENEPROPERTYID","VALUE") 
    ENABLE; 
    
    select "GENEPROPERTYID","VALUE", count(1)
    from A2_GENEPROPERTYVALUE
    group by "GENEPROPERTYID","VALUE"
    having count(1) > 1
    
    commit
    
    select * from a2_genepropertyvalue 
    where genepropertyid = 8 
    and value = 'TRANSLATION ELONGATION'
    
    select * from a2_GeneGPV where GenePropertyValueID in (
      select GenePropertyValueID from a2_genepropertyvalue 
      where genepropertyid = 8 
      and value = 'TRANSLATION ELONGATION'
    )
    
    drop view  tmp_GenePropertyValueFix
    
    create view tmp_GenePropertyValueFix as
    select p.GenePropertyValueID 
         , MIN(p1.GenePropertyValueID) MinGenePropertyValueID
    from a2_genepropertyvalue p
    join a2_GenePropertyValue p1
    on p.GenePropertyID = p1.GenePropertyID
    and p.Value = p1.Value
    and p1.GenePropertyValueID < p.GenePropertyValueID
    group by p.GenePropertyValueID
    
    update a2_GeneGPV
    set GenePropertyValueID = (select MinGenePropertyValueID 
                                from tmp_GenePropertyValueFix f
                                where f.GenePropertyValueID = a2_GeneGPV.GenePropertyValueID)
    where exists (select 1  
                  from tmp_GenePropertyValueFix f
                  where f.GenePropertyValueID = a2_GeneGPV.GenePropertyValueID)
    
    
    delete from a2_genepropertyvalue t1
    where exists (select 1 from a2_genepropertyvalue t2
                  where  t1.GenePropertyID = t2.GenePropertyID
    and t1.Value = t2.Value
    and t2.GenePropertyValueID < t1.GenePropertyValueID)
    
 update a2_GeneGPV set genepropertyvalueid =    
    

 ALTER TABLE "A2_ASSAYPVONTOLOGY" 
  ADD CONSTRAINT "UQ_ASSAYPVONTOLOGY" 
    UNIQUE(ONTOLOGYTERMID, ASSAYPVID) 
    ENABLE; 


delete from a2_genepropertyvalue t1
where exists(select 1 from a2_genepropertyvalue t2 
             where t1.GENEPROPERTYID = t2.GENEPROPERTYID 
             and t1.Value = t2.value 
             and t1.Genepropertyvalueid > t2.genepropertyvalueid)
             
             
create table a2_expressionvalue_tmp NOLOGGING PARALLEL 32
as select *
from a2_expressionvalue
where exists (select 1 from a2_assay
 where a2_Assay.AssayID = a2_expressionvalue.AssayID)
and exists (select 1 from a2_designelement 
 where a2_designelement.DesignElementID = a2_expressionvalue.DesignElementID)              


select 'ALTER INDEX ' || INDEX_NAME || ' REBUILD PARALLEL 32 NOLOGGING;' from user_indexes; 

ALTER INDEX A2_CONFIG_PROPERTIES_PK REBUILD PARALLEL 32 NOLOGGING;        
ALTER INDEX A2_TASKMAN_TASKSTAGE_PK REBUILD PARALLEL 32 NOLOGGING;        
ALTER INDEX A2_TASKMAN_OPERATIONLOG_PK REBUILD PARALLEL 32 NOLOGGING;     
ALTER INDEX A2_TASKMAN_OPERATIONLOG_IDX1 REBUILD PARALLEL 32 NOLOGGING;   
ALTER INDEX A2_TASKMAN_OPERATIONLOG_IDX2 REBUILD PARALLEL 32 NOLOGGING;   
ALTER INDEX A2_TASKMAN_TASKSTAGELOG_PK REBUILD PARALLEL 32 NOLOGGING;     
ALTER INDEX A2_TASKMAN_TASKSTAGELOG_IDX1 REBUILD PARALLEL 32 NOLOGGING;   
ALTER INDEX PK_LOAD_MONITOR REBUILD PARALLEL 32 NOLOGGING;                
ALTER INDEX IDX_LOAD_ACCESSION REBUILD PARALLEL 32 NOLOGGING;             
ALTER INDEX PK_EXPRESSIONANALYTICS REBUILD PARALLEL 32 NOLOGGING;         
ALTER INDEX IDX_ANALYTICS_PROPERTY REBUILD PARALLEL 32 NOLOGGING;         
ALTER INDEX IDX_ANALYTICS_DESIGNELEMENT REBUILD PARALLEL 32 NOLOGGING;    
ALTER INDEX IDX_ANALYTICS_EXPERIMENT REBUILD PARALLEL 32 NOLOGGING;       
ALTER INDEX UQ_ANALYTICS REBUILD PARALLEL 32 NOLOGGING;                   
ALTER INDEX IDX_EV_ASSAYID REBUILD PARALLEL 32 NOLOGGING;                 
ALTER INDEX UQ_SAMPLEPVONTOLOGY REBUILD PARALLEL 32 NOLOGGING;            
ALTER INDEX PK_SAMPLEPVONTOLOGY REBUILD PARALLEL 32 NOLOGGING;            
ALTER INDEX IDX_SAMPLEPVONTOLOGY_SAMPLEPV REBUILD PARALLEL 32 NOLOGGING;  
ALTER INDEX IDX_SAMPLEPVONTOLOGY_ONTOLOGY REBUILD PARALLEL 32 NOLOGGING;  
ALTER INDEX PK_ASSAYPVONTOLOGY REBUILD PARALLEL 32 NOLOGGING;             
ALTER INDEX IDX_ASSAYPVONTOLOGY_ASSAYPV REBUILD PARALLEL 32 NOLOGGING;    
ALTER INDEX IDX_ASSAYPVONTOLOGY_TERM REBUILD PARALLEL 32 NOLOGGING;       
ALTER INDEX UQ_ASSAYPVONTOLOGY REBUILD PARALLEL 32 NOLOGGING;             
ALTER INDEX PK_ONTOLOGYTERM REBUILD PARALLEL 32 NOLOGGING;                
ALTER INDEX IDX_ONTOLOGYTERM_ONTOLOGY REBUILD PARALLEL 32 NOLOGGING;      
ALTER INDEX UQ_ONTOLOGYTERM_ACCESSION REBUILD PARALLEL 32 NOLOGGING;      
ALTER INDEX UQ_ONTOLOGYTERM_TERM REBUILD PARALLEL 32 NOLOGGING;           
ALTER INDEX PK_ONTOLOGY REBUILD PARALLEL 32 NOLOGGING;                    
ALTER INDEX PK_SAMPLEPV REBUILD PARALLEL 32 NOLOGGING;                    
ALTER INDEX IDX_SAMPLEPROPERTY_PROPERTY REBUILD PARALLEL 32 NOLOGGING;    
ALTER INDEX IDX_SAMPLEPROPERTY_PS REBUILD PARALLEL 32 NOLOGGING;          
ALTER INDEX IDX_SAMPLEPROPERTY_SAMPLE REBUILD PARALLEL 32 NOLOGGING;      
ALTER INDEX IDX_SAMPLEPROPERTY_SP REBUILD PARALLEL 32 NOLOGGING;          
ALTER INDEX PK_ASSAYPV REBUILD PARALLEL 32 NOLOGGING;                     
ALTER INDEX IDX_ASSAYPV_PROPERTYVALUE REBUILD PARALLEL 32 NOLOGGING;      
ALTER INDEX IDX_ASSAYPV_ASSAY REBUILD PARALLEL 32 NOLOGGING;              
ALTER INDEX UQ_ASSAYPV REBUILD PARALLEL 32 NOLOGGING;                     
ALTER INDEX PK_ASSAYSAMPLE REBUILD PARALLEL 32 NOLOGGING;                 
ALTER INDEX IDX_ASSAYSAMPLE_ASSAY REBUILD PARALLEL 32 NOLOGGING;          
ALTER INDEX IDX_ASSAYSAMPLE_SAMPLE REBUILD PARALLEL 32 NOLOGGING;         
ALTER INDEX UQ_ASSAYSAMPLE REBUILD PARALLEL 32 NOLOGGING;                 
ALTER INDEX PK_SAMPLE REBUILD PARALLEL 32 NOLOGGING;                      
ALTER INDEX PK_ASSAY REBUILD PARALLEL 32 NOLOGGING;                       
ALTER INDEX IDX_ASSAY_ARRAYDESIGN REBUILD PARALLEL 32 NOLOGGING;          
ALTER INDEX IDX_ASSAY_EXPERIMENT REBUILD PARALLEL 32 NOLOGGING;           
ALTER INDEX UQ_ASSAY_ACCESSION REBUILD PARALLEL 32 NOLOGGING;             
ALTER INDEX PK_EXPERIMENT REBUILD PARALLEL 32 NOLOGGING;                  
ALTER INDEX IDX_EXPERIMENT_ACCESSION REBUILD PARALLEL 32 NOLOGGING;       
ALTER INDEX PK_PROPERTYVALUE REBUILD PARALLEL 32 NOLOGGING;               
ALTER INDEX IDX_PROPERTYVALUE_NAME REBUILD PARALLEL 32 NOLOGGING;         
ALTER INDEX IDX_PROPERTYVALUE_PROPERTY REBUILD PARALLEL 32 NOLOGGING;     
ALTER INDEX UQ_PROPERTYVALUE_NAME REBUILD PARALLEL 32 NOLOGGING;          
ALTER INDEX PK_PROPERTY REBUILD PARALLEL 32 NOLOGGING;                    
ALTER INDEX IDX_PROPERTY_NAME REBUILD PARALLEL 32 NOLOGGING;              
ALTER INDEX PK_DESIGNELEMENT REBUILD PARALLEL 32 NOLOGGING;               
ALTER INDEX IDX_DESIGNELEMENT_GENE REBUILD PARALLEL 32 NOLOGGING;         
ALTER INDEX IDX_DESIGNELEMENT_ARRAYDESIGN REBUILD PARALLEL 32 NOLOGGING;  
ALTER INDEX UQ_DESIGNELEMENT_ACCESSION REBUILD PARALLEL 32 NOLOGGING;     
ALTER INDEX PK_ARRAYDESIGN REBUILD PARALLEL 32 NOLOGGING;                 
ALTER INDEX IDX_ARRAYDESIGN_ACCESSION REBUILD PARALLEL 32 NOLOGGING;      
ALTER INDEX UQ_ARRAYDESIGN_NAME REBUILD PARALLEL 32 NOLOGGING;            
ALTER INDEX PK_GENEGPV REBUILD PARALLEL 32 NOLOGGING;                     
ALTER INDEX IDX_GENEGPV_PROPERTY REBUILD PARALLEL 32 NOLOGGING;           
ALTER INDEX IDX_GENEGPV_GENE REBUILD PARALLEL 32 NOLOGGING;               
ALTER INDEX UQ_GENEGPV REBUILD PARALLEL 32 NOLOGGING;                     
ALTER INDEX PK_GENE REBUILD PARALLEL 32 NOLOGGING;                        
ALTER INDEX IDX_GENE_ORGANISM REBUILD PARALLEL 32 NOLOGGING;              
ALTER INDEX UQ_GENE_IDENTIFIER REBUILD PARALLEL 32 NOLOGGING;             
ALTER INDEX PK_GENEPROPERTYVALUE REBUILD PARALLEL 32 NOLOGGING;           
ALTER INDEX IDX_GENEPROPERTYVALUE_PROPERTY REBUILD PARALLEL 32 NOLOGGING; 
ALTER INDEX PK_GENEPROPERTY REBUILD PARALLEL 32 NOLOGGING;                
ALTER INDEX UQ_GENEPROPERTY_NAME REBUILD PARALLEL 32 NOLOGGING;           
ALTER INDEX PK_ORGANISM REBUILD PARALLEL 32 NOLOGGING;                    
ALTER INDEX UQ_ORGANISM_NAME REBUILD PARALLEL 32 NOLOGGING;   

select 5721/60 from dual;


call dbms_stats.gather_schema_stats(
ownname ΚΚΚΚΚΚΚΚΚ=> 'ATLAS2',
options ΚΚΚΚΚΚΚΚΚ=> 'GATHER AUTO',
estimate_percent => 100,
method_opt ΚΚΚΚΚΚ=> 'for all columns size repeat',
degree ΚΚΚΚΚΚΚΚΚΚ=> 34
);

call dbms_stats.gather_schema_stats(
 ownname => 'atlas2'
,options => 'GATHER AUTO'
,estimate_percent => 100
,method_opt => 'for all columns size repeat'
,degree => 34)


