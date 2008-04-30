SELECT XmlElement("experiment",XmlAttributes( experiment.experiment_id_key, experiment.experiment_identifier, experiment.experiment_description ),
(SELECT XmlAgg ( XmlForest ( experiment_type.value as "type") ) FROM ae1__experiment_type__dm experiment_type WHERE experiment.experiment_id_key=experiment_type.experiment_id_key),
(xmlelement("assay_attributes",(SELECT distinct XmlAgg(XmlForest ( ba_age.value as "ba_age" )) FROM ae1__assay_age__dm ba_age WHERE experiment.experiment_id_key=ba_age.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_biometric", XMLAttributes(ba_biometric.assay_id_key as "assay_id"), ba_biometric.value )) FROM ae1__assay_biometric__dm ba_biometric WHERE experiment.experiment_id_key=ba_biometric.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_cellline", XMLAttributes(ba_cellline.assay_id_key as "assay_id"), ba_cellline.value  )) FROM ae1__assay_cellline__dm ba_cellline WHERE experiment.experiment_id_key=ba_cellline.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_celltype", XMLAttributes(ba_celltype.assay_id_key as "assay_id"), ba_celltype.value  )) FROM ae1__assay_celltype__dm ba_celltype WHERE experiment.experiment_id_key=ba_celltype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_clinhistory", XMLAttributes( ba_clinhistory.assay_id_key as "assay_id"), ba_clinhistory.value  )) FROM ae1__assay_clinhistory__dm ba_clinhistory WHERE experiment.experiment_id_key=ba_clinhistory.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_clininfo", XMLAttributes( ba_clininfo.assay_id_key as "assay_id"), ba_clininfo.value  )) FROM ae1__assay_clininfo__dm ba_clininfo WHERE experiment.experiment_id_key=ba_clininfo.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_clintreatment", XMLAttributes( ba_clintreatment.assay_id_key as "assay_id"), ba_clintreatment.value  )) FROM ae1__assay_clintreatment__dm ba_clintreatment WHERE experiment.experiment_id_key=ba_clintreatment.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_compound" , XMLAttributes( ba_compound.assay_id_key as "assay_id"), ba_compound.value )) FROM ae1__assay_compound__dm ba_compound WHERE experiment.experiment_id_key=ba_compound.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_cultivar", XMLAttributes( ba_cultivar.assay_id_key as "assay_id"), ba_cultivar.value  )) FROM ae1__assay_cultivar__dm ba_cultivar WHERE experiment.experiment_id_key=ba_cultivar.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_devstage", XMLAttributes( ba_devstage.assay_id_key as "assay_id"), ba_devstage.value  )) FROM ae1__assay_devstage__dm ba_devstage WHERE experiment.experiment_id_key=ba_devstage.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_diseaseloc", XMLAttributes( ba_diseaseloc.assay_id_key as "assay_id"), ba_diseaseloc.value  )) FROM ae1__assay_diseaseloc__dm ba_diseaseloc WHERE experiment.experiment_id_key=ba_diseaseloc.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_diseasestaging", XMLAttributes( ba_diseasestaging.assay_id_key as "assay_id"), ba_diseasestaging.value  )) FROM ae1__assay_diseasestaging__dm ba_diseasestaging WHERE experiment.experiment_id_key=ba_diseasestaging.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_diseasestate", XMLAttributes( ba_diseasestate.assay_id_key as "assay_id"), ba_diseasestate.value  )) FROM ae1__assay_diseasestate__dm ba_diseasestate WHERE experiment.experiment_id_key=ba_diseasestate.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_dose", XMLAttributes( ba_dose.assay_id_key as "assay_id"), ba_dose.value  )) FROM ae1__assay_dose__dm ba_dose WHERE experiment.experiment_id_key=ba_dose.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_ecotype", XMLAttributes( ba_ecotype.assay_id_key as "assay_id"), ba_ecotype.value  )) FROM ae1__assay_ecotype__dm ba_ecotype WHERE experiment.experiment_id_key=ba_ecotype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_envhistory", XMLAttributes( ba_envhistory.assay_id_key as "assay_id"), ba_envhistory.value  )) FROM ae1__assay_envhistory__dm ba_envhistory WHERE experiment.experiment_id_key=ba_envhistory.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_familyhistory", XMLAttributes( ba_familyhistory.assay_id_key as "assay_id"), ba_familyhistory.value  )) FROM ae1__assay_familyhistory__dm ba_familyhistory WHERE experiment.experiment_id_key=ba_familyhistory.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_genmodif", XMLAttributes( ba_genmodif.assay_id_key as "assay_id"), ba_genmodif.value  )) FROM ae1__assay_genmodif__dm ba_genmodif WHERE experiment.experiment_id_key=ba_genmodif.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_genotype", XMLAttributes( ba_genotype.assay_id_key as "assay_id"), ba_genotype.value  )) FROM ae1__assay_genotype__dm ba_genotype WHERE experiment.experiment_id_key=ba_genotype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_histology", XMLAttributes( ba_histology.assay_id_key as "assay_id"), ba_histology.value  )) FROM ae1__assay_histology__dm ba_histology WHERE experiment.experiment_id_key=ba_histology.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_indgeneticchar", XMLAttributes( ba_indgeneticchar.assay_id_key as "assay_id"), ba_indgeneticchar.value  )) FROM ae1__assay_indgeneticchar__dm ba_indgeneticchar WHERE experiment.experiment_id_key=ba_indgeneticchar.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_individual", XMLAttributes( ba_individual.assay_id_key as "assay_id"), ba_individual.value  )) FROM ae1__assay_individual__dm ba_individual WHERE experiment.experiment_id_key=ba_individual.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_light" , XMLAttributes( ba_light.assay_id_key as "assay_id"), ba_light.value )) FROM ae1__assay_light__dm ba_light WHERE experiment.experiment_id_key=ba_light.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_media" ,XMLAttributes( ba_media.assay_id_key as "assay_id"),  ba_media.value )) FROM ae1__assay_media__dm ba_media WHERE experiment.experiment_id_key=ba_media.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_observation", XMLAttributes( ba_observation.assay_id_key as "assay_id"), ba_observation.value )) FROM ae1__assay_observation__dm ba_observation WHERE experiment.experiment_id_key=ba_observation.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_organism" , XMLAttributes(ba_organism.assay_id_key as "assay_id"), ba_organism.value  )) FROM ae1__assay_organism__dm ba_organism WHERE experiment.experiment_id_key=ba_organism.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_organismpart", XMLAttributes( ba_organismpart.assay_id_key as "assay_id"), ba_organismpart.value  )) FROM ae1__assay_organismpart__dm ba_organismpart WHERE experiment.experiment_id_key=ba_organismpart.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_organismstatus", XMLAttributes( ba_organismstatus.assay_id_key as "assay_id"), ba_organismstatus.value  )) FROM ae1__assay_organismstatus__dm ba_organismstatus WHERE experiment.experiment_id_key=ba_organismstatus.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_protocoltype", XMLAttributes( ba_protocoltype.assay_id_key as "assay_id"), ba_protocoltype.value  )) FROM ae1__assay_protocoltype__dm ba_protocoltype WHERE experiment.experiment_id_key=ba_protocoltype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_performer", XMLAttributes( ba_performer.assay_id_key as "assay_id"), ba_performer.value  )) FROM ae1__assay_performer__dm ba_performer WHERE experiment.experiment_id_key=ba_performer.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_phenotype", XMLAttributes( ba_phenotype.assay_id_key as "assay_id"), ba_phenotype.value  )) FROM ae1__assay_phenotype__dm ba_phenotype WHERE experiment.experiment_id_key=ba_phenotype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_qcdescrtype", XMLAttributes( ba_qcdescrtype.assay_id_key as "assay_id"), ba_qcdescrtype.value  )) FROM ae1__assay_qcdescrtype__dm ba_qcdescrtype WHERE experiment.experiment_id_key=ba_qcdescrtype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_sex", XMLAttributes( ba_sex.assay_id_key as "assay_id"), ba_sex.value  )) FROM ae1__assay_sex__dm ba_sex WHERE experiment.experiment_id_key=ba_sex.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_strainorline", XMLAttributes( ba_strainorline.assay_id_key as "assay_id"), ba_strainorline.value  )) FROM ae1__assay_strainorline__dm ba_strainorline WHERE experiment.experiment_id_key=ba_strainorline.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_targetcelltype", XMLAttributes( ba_targetcelltype.assay_id_key as "assay_id"), ba_targetcelltype.value  )) FROM ae1__assay_targetcelltype__dm ba_targetcelltype WHERE experiment.experiment_id_key=ba_targetcelltype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_temperature", XMLAttributes( ba_temperature.assay_id_key as "assay_id"), ba_temperature.value  )) FROM ae1__assay_temperature__dm ba_temperature WHERE experiment.experiment_id_key=ba_temperature.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_testtype", XMLAttributes( ba_testtype.assay_id_key as "assay_id"), ba_testtype.value  )) FROM ae1__assay_testtype__dm ba_testtype WHERE experiment.experiment_id_key=ba_testtype.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_testresult", XMLAttributes( ba_testresult.assay_id_key as "assay_id"), ba_testresult.value  )) FROM ae1__assay_testresult__dm ba_testresult WHERE experiment.experiment_id_key=ba_testresult.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_test", XMLAttributes( ba_test.assay_id_key as "assay_id"), ba_test.value  )) FROM ae1__assay_test__dm ba_test WHERE experiment.experiment_id_key=ba_test.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_time", XMLAttributes( ba_time.assay_id_key as "assay_id"), ba_time.value  )) FROM ae1__assay_time__dm ba_time WHERE experiment.experiment_id_key=ba_time.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_tumorgrading", XMLAttributes( ba_tumorgrading.assay_id_key as "assay_id"), ba_tumorgrading.value  )) FROM ae1__assay_tumorgrading__dm ba_tumorgrading WHERE experiment.experiment_id_key=ba_tumorgrading.experiment_id_key),
(SELECT distinct XmlAgg(XmlElement ( "ba_vehicle" ,  XMLAttributes(ba_vehicle.assay_id_key as "assay_id"), ba_vehicle.value)) FROM ae1__assay_vehicle__dm ba_vehicle WHERE experiment.experiment_id_key=ba_vehicle.experiment_id_key))),
(XmlElement("sample_attributes",
(SELECT distinct XmlAgg ( XmlElement ( "bs_unknown" , XMLAttributes(sample_all.assay_id_key as "assay_id", sample_all.sample_id_key as "sample_id") ,sample_all.value) ) FROM ae1__sample_all__dm sample_all WHERE sample_all.experiment_id_key=experiment.experiment_id_key),

(SELECT distinct XmlAgg ( XmlElement ( "bs_age" , XMLAttributes(sample_age.assay_id_key as "assay_id", sample_age.sample_id_key as "sample_id") ,sample_age.value) ) FROM AE1__SAMPLE_AGE__DM sample_age WHERE sample_age.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_BIOMETRIC", XMLAttributes(sample_BIOMETRIC.assay_id_key as "assay_id", sample_BIOMETRIC.sample_id_key as "sample_id") ,sample_BIOMETRIC.value) ) FROM AE1__SAMPLE_BIOMETRIC__DM sample_BIOMETRIC WHERE sample_BIOMETRIC.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_CELLLINE", XMLAttributes(sample_CELLLINE.assay_id_key as "assay_id", sample_CELLLINE.sample_id_key as "sample_id") ,sample_CELLLINE.value) ) FROM AE1__SAMPLE_CELLLINE__DM sample_CELLLINE WHERE sample_CELLLINE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_CELLTYPE", XMLAttributes(sample_CELLTYPE.assay_id_key as "assay_id", sample_CELLTYPE.sample_id_key as "sample_id") ,sample_CELLTYPE.value) ) FROM AE1__SAMPLE_CELLTYPE__DM sample_CELLTYPE WHERE sample_CELLTYPE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_CLINHISTORY", XMLAttributes(sample_CLINHISTORY.assay_id_key as "assay_id", sample_CLINHISTORY.sample_id_key as "sample_id") ,sample_CLINHISTORY.value) ) FROM AE1__SAMPLE_CLINHISTORY__DM sample_CLINHISTORY WHERE sample_CLINHISTORY.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_CLININFO", XMLAttributes(sample_CLININFO.assay_id_key as "assay_id", sample_CLININFO.sample_id_key as "sample_id") ,sample_CLININFO.value) ) FROM AE1__SAMPLE_CLININFO__DM sample_CLININFO WHERE sample_CLININFO.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_CLINTREATMENT", XMLAttributes(sample_CLINTREATMENT.assay_id_key as "assay_id", sample_CLINTREATMENT.sample_id_key as "sample_id") ,sample_CLINTREATMENT.value) ) FROM AE1__SAMPLE_CLINTREATMENT__DM sample_CLINTREATMENT WHERE sample_CLINTREATMENT.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_CULTIVAR", XMLAttributes(sample_CULTIVAR.assay_id_key as "assay_id", sample_CULTIVAR.sample_id_key as "sample_id") ,sample_CULTIVAR.value) ) FROM AE1__SAMPLE_CULTIVAR__DM sample_CULTIVAR WHERE sample_CULTIVAR.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_DEVSTAGE", XMLAttributes(sample_DEVSTAGE.assay_id_key as "assay_id", sample_DEVSTAGE.sample_id_key as "sample_id") ,sample_DEVSTAGE.value) ) FROM AE1__SAMPLE_DEVSTAGE__DM sample_DEVSTAGE WHERE sample_DEVSTAGE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_DISEASELOC", XMLAttributes(sample_DISEASELOC.assay_id_key as "assay_id", sample_DISEASELOC.sample_id_key as "sample_id") ,sample_DISEASELOC.value) ) FROM AE1__SAMPLE_DISEASELOC__DM sample_DISEASELOC WHERE sample_DISEASELOC.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_DISEASESTAGING", XMLAttributes(sample_DISEASESTAGING.assay_id_key as "assay_id", sample_DISEASESTAGING.sample_id_key as "sample_id") ,sample_DISEASESTAGING.value) ) FROM AE1__SAMPLE_DISEASESTAGING__DM sample_DISEASESTAGING WHERE sample_DISEASESTAGING.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_DISEASESTATE", XMLAttributes(sample_DISEASESTATE.assay_id_key as "assay_id", sample_DISEASESTATE.sample_id_key as "sample_id") ,sample_DISEASESTATE.value) ) FROM AE1__SAMPLE_DISEASESTATE__DM sample_DISEASESTATE WHERE sample_DISEASESTATE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_ECOTYPE", XMLAttributes(sample_ECOTYPE.assay_id_key as "assay_id", sample_ECOTYPE.sample_id_key as "sample_id") ,sample_ECOTYPE.value) ) FROM AE1__SAMPLE_ECOTYPE__DM sample_ECOTYPE WHERE sample_ECOTYPE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_ENVHISTORY", XMLAttributes(sample_ENVHISTORY.assay_id_key as "assay_id", sample_ENVHISTORY.sample_id_key as "sample_id") ,sample_ENVHISTORY.value) ) FROM AE1__SAMPLE_ENVHISTORY__DM sample_ENVHISTORY WHERE sample_ENVHISTORY.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_FAMILYHISTORY", XMLAttributes(sample_FAMILYHISTORY.assay_id_key as "assay_id", sample_FAMILYHISTORY.sample_id_key as "sample_id") ,sample_FAMILYHISTORY.value) ) FROM AE1__SAMPLE_FAMILYHISTORY__DM sample_FAMILYHISTORY WHERE sample_FAMILYHISTORY.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_GENMODIF", XMLAttributes(sample_GENMODIF.assay_id_key as "assay_id", sample_GENMODIF.sample_id_key as "sample_id") ,sample_GENMODIF.value) ) FROM AE1__SAMPLE_GENMODIF__DM sample_GENMODIF WHERE sample_GENMODIF.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_GENOTYPE", XMLAttributes(sample_GENOTYPE.assay_id_key as "assay_id", sample_GENOTYPE.sample_id_key as "sample_id") ,sample_GENOTYPE.value) ) FROM AE1__SAMPLE_GENOTYPE__DM sample_GENOTYPE WHERE sample_GENOTYPE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_HISTOLOGY", XMLAttributes(sample_HISTOLOGY.assay_id_key as "assay_id", sample_HISTOLOGY.sample_id_key as "sample_id") ,sample_HISTOLOGY.value) ) FROM AE1__SAMPLE_HISTOLOGY__DM sample_HISTOLOGY WHERE sample_HISTOLOGY.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_INDGENETICCHAR", XMLAttributes(sample_INDGENETICCHAR.assay_id_key as "assay_id", sample_INDGENETICCHAR.sample_id_key as "sample_id") ,sample_INDGENETICCHAR.value) ) FROM AE1__SAMPLE_INDGENETICCHAR__DM sample_INDGENETICCHAR WHERE sample_INDGENETICCHAR.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_INDIVIDUAL", XMLAttributes(sample_INDIVIDUAL.assay_id_key as "assay_id", sample_INDIVIDUAL.sample_id_key as "sample_id") ,sample_INDIVIDUAL.value) ) FROM AE1__SAMPLE_INDIVIDUAL__DM sample_INDIVIDUAL WHERE sample_INDIVIDUAL.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_INITIALTIME", XMLAttributes(sample_INITIALTIME.assay_id_key as "assay_id", sample_INITIALTIME.sample_id_key as "sample_id") ,sample_INITIALTIME.value) ) FROM AE1__SAMPLE_INITIALTIME__DM sample_INITIALTIME WHERE sample_INITIALTIME.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_OBSERVATION", XMLAttributes(sample_OBSERVATION.assay_id_key as "assay_id", sample_OBSERVATION.sample_id_key as "sample_id") ,sample_OBSERVATION.value) ) FROM AE1__SAMPLE_OBSERVATION__DM sample_OBSERVATION WHERE sample_OBSERVATION.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_ORGANISMPART", XMLAttributes(sample_ORGANISMPART.assay_id_key as "assay_id", sample_ORGANISMPART.sample_id_key as "sample_id") ,sample_ORGANISMPART.value) ) FROM AE1__SAMPLE_ORGANISMPART__DM sample_ORGANISMPART WHERE sample_ORGANISMPART.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_ORGANISMSTATUS", XMLAttributes(sample_ORGANISMSTATUS.assay_id_key as "assay_id", sample_ORGANISMSTATUS.sample_id_key as "sample_id") ,sample_ORGANISMSTATUS.value) ) FROM AE1__SAMPLE_ORGANISMSTATUS__DM sample_ORGANISMSTATUS WHERE sample_ORGANISMSTATUS.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_PHENOTYPE", XMLAttributes(sample_PHENOTYPE.assay_id_key as "assay_id", sample_PHENOTYPE.sample_id_key as "sample_id") ,sample_PHENOTYPE.value) ) FROM AE1__SAMPLE_PHENOTYPE__DM sample_PHENOTYPE WHERE sample_PHENOTYPE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_SEX", XMLAttributes(sample_SEX.assay_id_key as "assay_id", sample_SEX.sample_id_key as "sample_id") ,sample_SEX.value) ) FROM AE1__SAMPLE_SEX__DM sample_SEX WHERE sample_SEX.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_STRAINORLINE", XMLAttributes(sample_STRAINORLINE.assay_id_key as "assay_id", sample_STRAINORLINE.sample_id_key as "sample_id") ,sample_STRAINORLINE.value) ) FROM AE1__SAMPLE_STRAINORLINE__DM sample_STRAINORLINE WHERE sample_STRAINORLINE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_TARGETCELLTYPE", XMLAttributes(sample_TARGETCELLTYPE.assay_id_key as "assay_id", sample_TARGETCELLTYPE.sample_id_key as "sample_id") ,sample_TARGETCELLTYPE.value) ) FROM AE1__SAMPLE_TARGETCELLTYPE__DM sample_TARGETCELLTYPE WHERE sample_TARGETCELLTYPE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_TESTRESULT", XMLAttributes(sample_TESTRESULT.assay_id_key as "assay_id", sample_TESTRESULT.sample_id_key as "sample_id") ,sample_TESTRESULT.value) ) FROM AE1__SAMPLE_TESTRESULT__DM sample_TESTRESULT WHERE sample_TESTRESULT.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_TESTTYPE", XMLAttributes(sample_TESTTYPE.assay_id_key as "assay_id", sample_TESTTYPE.sample_id_key as "sample_id") ,sample_TESTTYPE.value) ) FROM AE1__SAMPLE_TESTTYPE__DM sample_TESTTYPE WHERE sample_TESTTYPE.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_TEST", XMLAttributes(sample_TEST.assay_id_key as "assay_id", sample_TEST.sample_id_key as "sample_id") ,sample_TEST.value) ) FROM AE1__SAMPLE_TEST__DM sample_TEST WHERE sample_TEST.experiment_id_key=experiment.experiment_id_key),
(SELECT distinct XmlAgg ( XmlElement ("bs_TUMORGRADING", XMLAttributes(sample_TUMORGRADING.assay_id_key as "assay_id", sample_TUMORGRADING.sample_id_key as "sample_id") ,sample_TUMORGRADING.value) ) FROM AE1__SAMPLE_TUMORGRADING__DM sample_TUMORGRADING WHERE sample_TUMORGRADING.experiment_id_key=experiment.experiment_id_key)
))).getClobVal() as xml FROM ae1__experiment__main experiment WHERE experiment.experiment_accession='E-AFMX-1';

select * from ae1__assay_vehicle__dm where assay_id_key = 188506999;


select '(SELECT distinct XmlAgg ( XmlElement ("' || replace(replace(table_name,'AE1__SAMPLE','bs'),'__DM','')  ||
'", XMLAttributes(' ||replace(replace(table_name,'AE1__SAMPLE','sample'),'__DM','') ||
'.assay_id_key as "assay_id", ' || replace(replace(table_name,'AE1__SAMPLE','sample'),'__DM','')  || '.sample_id_key as "sample_id") ,' || 
replace(replace(table_name,'AE1__SAMPLE','sample'),'__DM','')  || '.value) ) FROM ' || table_name || ' ' ||
replace(replace(table_name,'AE1__SAMPLE','sample'),'__DM','')  ||
' WHERE ' || replace(replace(table_name,'AE1__SAMPLE','sample'),'__DM','')  || '.experiment_id_key=experiment.experiment_id_key)'
from all_Tables where table_name like 'AE1__SAMPLE_%' and table_name not in ('AE1__SAMPLE__MAIN','AE1__SAMPLE_AGE__DM','AE1__SAMPLE_ALL__DM');























SELECT XmlElement("experiment",XmlAttributes( experiment.experiment_id_key, experiment.experiment_identifier, experiment.experiment_description ),
(SELECT XmlAgg ( XmlForest ( experiment_type.value as "type") ) FROM ae1__experiment_type__dm experiment_type WHERE experiment.experiment_id_key=experiment_type.experiment_id_key),
(xmlelement("assay_attributes",(SELECT distinct XmlAgg(XmlForest ( ba_age.value as "ba_age" )) FROM ae1__assay_age__dm ba_age WHERE experiment.experiment_id_key=ba_age.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_biometric.value as "ba_biometric" )) FROM ae1__assay_biometric__dm ba_biometric WHERE experiment.experiment_id_key=ba_biometric.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_cellline.value as "ba_cellline" )) FROM ae1__assay_cellline__dm ba_cellline WHERE experiment.experiment_id_key=ba_cellline.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_celltype.value as "ba_celltype" )) FROM ae1__assay_celltype__dm ba_celltype WHERE experiment.experiment_id_key=ba_celltype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_clinhistory.value as "ba_clinhistory" )) FROM ae1__assay_clinhistory__dm ba_clinhistory WHERE experiment.experiment_id_key=ba_clinhistory.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_clininfo.value as "ba_clininfo" )) FROM ae1__assay_clininfo__dm ba_clininfo WHERE experiment.experiment_id_key=ba_clininfo.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_clintreatment.value as "ba_clintreatment" )) FROM ae1__assay_clintreatment__dm ba_clintreatment WHERE experiment.experiment_id_key=ba_clintreatment.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_compound.value as "ba_compound" )) FROM ae1__assay_compound__dm ba_compound WHERE experiment.experiment_id_key=ba_compound.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_cultivar.value as "ba_cultivar" )) FROM ae1__assay_cultivar__dm ba_cultivar WHERE experiment.experiment_id_key=ba_cultivar.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_devstage.value as "ba_devstage" )) FROM ae1__assay_devstage__dm ba_devstage WHERE experiment.experiment_id_key=ba_devstage.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_diseaseloc.value as "ba_diseaseloc" )) FROM ae1__assay_diseaseloc__dm ba_diseaseloc WHERE experiment.experiment_id_key=ba_diseaseloc.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_diseasestaging.value as "ba_diseasestaging" )) FROM ae1__assay_diseasestaging__dm ba_diseasestaging WHERE experiment.experiment_id_key=ba_diseasestaging.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_diseasestate.value as "ba_diseasestate" )) FROM ae1__assay_diseasestate__dm ba_diseasestate WHERE experiment.experiment_id_key=ba_diseasestate.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_dose.value as "ba_dose" )) FROM ae1__assay_dose__dm ba_dose WHERE experiment.experiment_id_key=ba_dose.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_ecotype.value as "ba_ecotype" )) FROM ae1__assay_ecotype__dm ba_ecotype WHERE experiment.experiment_id_key=ba_ecotype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_envhistory.value as "ba_envhistory" )) FROM ae1__assay_envhistory__dm ba_envhistory WHERE experiment.experiment_id_key=ba_envhistory.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_familyhistory.value as "ba_familyhistory" )) FROM ae1__assay_familyhistory__dm ba_familyhistory WHERE experiment.experiment_id_key=ba_familyhistory.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_genmodif.value as "ba_genmodif" )) FROM ae1__assay_genmodif__dm ba_genmodif WHERE experiment.experiment_id_key=ba_genmodif.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_genotype.value as "ba_genotype" )) FROM ae1__assay_genotype__dm ba_genotype WHERE experiment.experiment_id_key=ba_genotype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_histology.value as "ba_histology" )) FROM ae1__assay_histology__dm ba_histology WHERE experiment.experiment_id_key=ba_histology.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_indgeneticchar.value as "ba_indgeneticchar" )) FROM ae1__assay_indgeneticchar__dm ba_indgeneticchar WHERE experiment.experiment_id_key=ba_indgeneticchar.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_individual.value as "ba_individual" )) FROM ae1__assay_individual__dm ba_individual WHERE experiment.experiment_id_key=ba_individual.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_light.value as "ba_light" )) FROM ae1__assay_light__dm ba_light WHERE experiment.experiment_id_key=ba_light.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_media.value as "ba_media" )) FROM ae1__assay_media__dm ba_media WHERE experiment.experiment_id_key=ba_media.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_observation.value as "ba_observation" )) FROM ae1__assay_observation__dm ba_observation WHERE experiment.experiment_id_key=ba_observation.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_organism.value as "ba_organism" )) FROM ae1__assay_organism__dm ba_organism WHERE experiment.experiment_id_key=ba_organism.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_organismpart.value as "ba_organismpart" )) FROM ae1__assay_organismpart__dm ba_organismpart WHERE experiment.experiment_id_key=ba_organismpart.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_organismstatus.value as "ba_organismstatus" )) FROM ae1__assay_organismstatus__dm ba_organismstatus WHERE experiment.experiment_id_key=ba_organismstatus.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_protocoltype.value as "ba_protocoltype" )) FROM ae1__assay_protocoltype__dm ba_protocoltype WHERE experiment.experiment_id_key=ba_protocoltype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_performer.value as "ba_performer" )) FROM ae1__assay_performer__dm ba_performer WHERE experiment.experiment_id_key=ba_performer.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_phenotype.value as "ba_phenotype" )) FROM ae1__assay_phenotype__dm ba_phenotype WHERE experiment.experiment_id_key=ba_phenotype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_qcdescrtype.value as "ba_qcdescrtype" )) FROM ae1__assay_qcdescrtype__dm ba_qcdescrtype WHERE experiment.experiment_id_key=ba_qcdescrtype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_sex.value as "ba_sex" )) FROM ae1__assay_sex__dm ba_sex WHERE experiment.experiment_id_key=ba_sex.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_strainorline.value as "ba_strainorline" )) FROM ae1__assay_strainorline__dm ba_strainorline WHERE experiment.experiment_id_key=ba_strainorline.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_targetcelltype.value as "ba_targetcelltype" )) FROM ae1__assay_targetcelltype__dm ba_targetcelltype WHERE experiment.experiment_id_key=ba_targetcelltype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_temperature.value as "ba_temperature" )) FROM ae1__assay_temperature__dm ba_temperature WHERE experiment.experiment_id_key=ba_temperature.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_testtype.value as "ba_testtype" )) FROM ae1__assay_testtype__dm ba_testtype WHERE experiment.experiment_id_key=ba_testtype.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_testresult.value as "ba_testresult" )) FROM ae1__assay_testresult__dm ba_testresult WHERE experiment.experiment_id_key=ba_testresult.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_test.value as "ba_test" )) FROM ae1__assay_test__dm ba_test WHERE experiment.experiment_id_key=ba_test.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_time.value as "ba_time" )) FROM ae1__assay_time__dm ba_time WHERE experiment.experiment_id_key=ba_time.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_tumorgrading.value as "ba_tumorgrading" )) FROM ae1__assay_tumorgrading__dm ba_tumorgrading WHERE experiment.experiment_id_key=ba_tumorgrading.experiment_id_key),
(SELECT distinct XmlAgg(XmlForest ( ba_vehicle.value as "ba_vehicle",  ba_vehicle.assay_id_key as id)) FROM ae1__assay_vehicle__dm ba_vehicle WHERE experiment.experiment_id_key=ba_vehicle.experiment_id_key))),
(XmlElement("sample_attributes",(SELECT distinct XmlAgg ( XmlForest ( sample_all.value as "bs_unknown" ) ) FROM ae1__sample_all__dm sample_all WHERE sample_all.experiment_id_key=experiment.experiment_id_key
)))).getClobVal() as xml FROM ae1__experiment__main experiment WHERE experiment.experiment_accession='E-AFMX-1';

