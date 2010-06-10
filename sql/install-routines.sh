TABLE_NAMES="ArrayDesign \
             Assay \
             AssayPV \
             AssayPVOntology \
             AssaySample \
             DesignElement \
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
             SamplePVOntology"

TABLE_NAMES_SCHEMA="ArrayDesign \
             DesignElement \
             Gene \
             GeneGPV \
             GeneProperty \
             GenePropertyValue \
             Ontology \
             OntologyTerm \
             Organism \
             Property \
             PropertyValue"

create_schema() {
    ATLAS_CONNECTION=$1
    ATLAS_INDEX_TABLESPACE=$2

    # scripts which must be executed first, in given order
    CORE_SCRIPTS="Types.sql Tables.sql Views.sql list_to_table.sql PKG_ATLASMGR.sql PKG_ATLASAPI.sql \
    PKG_ATLASLDR.sql CUR_AssayProperty.sql CUR_MergePropertyValue.sql CUR_PropertyValue.sql CUR_SampleProperty.sql \
    TR_CUR_AssayProperty.sql TR_CUR_PropertyValue.sql TR_CUR_SampleProperty.sql CUR_OntologyMapping.sql TR_CUR_OntologyMapping.sql"
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
