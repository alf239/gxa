TABLE_NAMES="ArrayDesign \
             Assay \
             AssayPV \
             AssayPVOntology \
             AssaySample \
             Bioentity \
             Bioentitybepv \
             Bioentityproperty \
             Bioentitypropertyvalue \
             DesignElement \
             DesignElementbioentity \
             Experiment \
             ExpressionAnalytics \
             Gene \
             GeneGPV \
             GeneProperty \
             GenePropertyValue \
             Ontology \
             OntologyTerm \
             Organism \
             Property \
             PropertyValue \
             Sample \
             SamplePV \
             SamplePVOntology \
             Software"

TABLE_NAMES_SCHEMA="ArrayDesign \
             Bioentity \
             Bioentitybepv \
             Bioentityproperty \
             Bioentitypropertyvalue \
             DesignElement \
             DesignElementbioentity \
             Gene \
             GeneGPV \
             GeneProperty \
             GenePropertyValue \
             Ontology \
             OntologyTerm \
             Organism \
             Property \
             PropertyValue \
             Software"

create_schema() {
    ATLAS_CONNECTION=$1
    ATLAS_INDEX_TABLESPACE=$2

    # scripts which must be executed first, in given order
    CORE_SCRIPTS="Types.sql Tables.sql Views.sql list_to_table.sql list_to_table_str.sql PKG_ATLASMGR.sql PKG_ATLASAPI.sql A2_SampleOrganism.sql \
    PKG_ATLASLDR.sql PKG_ATLASBELDR.sql CUR_AssayProperty.sql CUR_MergePropertyValue.sql CUR_PropertyValue.sql CUR_SampleProperty.sql \
    CUR_AllPropertyID.sql CUR_TwoValues.sql CUR_TwoFactors.sql CUR_MergeFactors.sql \
    TR_CUR_AssayProperty.sql TR_CUR_PropertyValue.sql TR_CUR_SampleProperty.sql CUR_OntologyMapping.sql \
    TR_CUR_OntologyMapping.sql CUR_SetCurated.sql CUR_SetUnCurated.sql CUR_ExperimentProperty.sql \
    UNFOLD_BE2BE.sql"
    SCHEMA_FOLDER=Schema

    if [ ! -z "$INDEX_TABLESPACE" ]; then
	sed "s/\/\*INDEX_TABLESPACE\*\//TABLESPACE $ATLAS_INDEX_TABLESPACE/" Schema/Tables.sql > Schema/TablesTablespace.sql
    fi

    for SCRIPT_NAME in $CORE_SCRIPTS
      do
      if [ ! -r Schema/$SCRIPT_NAME ]; then
	  echo "required script not found in Schema folder:" $SCRIPT_NAME; exit -1
      fi

      if [ "$SCRIPT_NAME" == "Tables.sql" ]; then
	  if [ ! -z "$INDEX_TABLESPACE" ]; then
	      SCRIPT_NAME=TablesTablespace.sql
	  fi
      fi

      echo "executing " $SCRIPT_NAME

      sqlplus -L -S $ATLAS_CONNECTION @Schema/$SCRIPT_NAME
      if [ "$?" -ne "0" ]; then
	  echo "can not execute script" $SCRIPT_NAME ; exit -1
      fi
    done
}

load_data() {
    ATLAS_CONNECTION=$1
    DATA_FOLDER=$2
    CTL_FOLDER=$3
    INSTALL_MODE=$4

    echo "call ATLASMGR.DisableConstraints();" | sqlplus -L -S $ATLAS_CONNECTION

    TABLE_NAMES_SET=$TABLE_NAMES
    if [ "$INSTALL_MODE" == "Schema" ]; then
      TABLE_NAMES_SET=$TABLE_NAMES_SCHEMA
    fi

    for LDR_CTL in $TABLE_NAMES_SET
      do
      echo "... $LDR_CTL"
      sqlldr $ATLAS_CONNECTION control=$CTL_FOLDER/$LDR_CTL.ctl data=$DATA_FOLDER/$LDR_CTL.dat

      LDR_RESULT="$?"

      if [ "$LDR_RESULT" -ne "0" ]; then
	  echo "can not execute sqlldr:" $LDR_CTL $LDR_RESULT ;
      fi

      cat $LDR_CTL.log >> install.log
      rm $LDR_CTL.log
    done

    echo "Enabling constraints and rebuilding sequences..."
    echo "call ATLASMGR.EnableConstraints();" | sqlplus -L -S $ATLAS_CONNECTION
    echo "call ATLASMGR.RebuildSequence();" | sqlplus -L -S $ATLAS_CONNECTION
}
