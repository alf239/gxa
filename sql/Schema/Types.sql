/*
 * Copyright 2008-2010 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa/
 */

--------------------------------------------------------
--  DDL for Type INTRECORD
--------------------------------------------------------
  CREATE OR REPLACE TYPE "INTRECORD" AS OBJECT(ID INTEGER);
/
--------------------------------------------------------
--  DDL for Type TBLINT
--------------------------------------------------------

  CREATE OR REPLACE TYPE "TBLINT" AS TABLE OF INTRECORD;
/
--------------------------------------------------------
--  DDL for Type ACCESSIONQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ACCESSIONQUERY" as Object(
  Id int
  ,Accession varchar2(255)
) FINAL;
/
--------------------------------------------------------
--  DDL for Type ACCESSIONTABLE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ACCESSIONTABLE" as table of varchar2(255);
/
--------------------------------------------------------
--  DDL for Type ARRAYDESIGNQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ARRAYDESIGNQUERY" as Object(
  "AccessionQuery" AccessionQuery
  ,Name varchar2(255)
  ,Type varchar2(255)
  ,Provider varchar2(255)  
);
/
--------------------------------------------------------
--  DDL for Type ASSAYQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ASSAYQUERY" as Object(
   "AccessionQuery" AccessionQuery
  ,properties TBLINT
);
/
--------------------------------------------------------
--  DDL for Type DESIGNELEMENT
--------------------------------------------------------

  CREATE OR REPLACE TYPE "DESIGNELEMENT2" is object (
         Accession  varchar2(255)  ---Composite Element Name
      ,  EntryName  varchar2(255)  ---embl,affymetrix_netaffx,blocks,interpro,locus,ensembl,swall,genbank,unigene,refseq,scop,omim,pfam,ec,kegg,pkr_hanks,cp450,gpcrdb
      ,  EntryValue varchar2(255)  ---value of the element 
);
/

--------------------------------------------------------
--  DDL for Type IDVALUE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "IDVALUE" is object (
         id int 
      ,  Value  varchar2(255) 
);
/

--------------------------------------------------------
--  DDL for Type DESIGNELEMENTTABLE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "DESIGNELEMENTTABLE" is table of DESIGNELEMENT2;
/  

--------------------------------------------------------
--  DDL for Type IDVALUETABLE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "IDVALUETABLE" is table of IDVALUE;
/  

--------------------------------------------------------
--  DDL for Type EXPERIMENTQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "EXPERIMENTQUERY" as Object(
   "AccessionQuery" AccessionQuery
  ,lab varchar2(255)
  ,performer varchar2(255)
  ,genes TBLINT
  ,properties TBLINT
);
/

--------------------------------------------------------
--  DDL for Type GENEPROPERTYQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "GENEPROPERTYQUERY" as Object(
 PropertyID int
,PropertyValue varchar2(255) 
,FullTextQuery varchar2(255)  
);
/
--------------------------------------------------------
--  DDL for Type GENEQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "GENEQUERY" as Object(
   "AccessionQuery" AccessionQuery
  ,properties TBLINT
  ,species varchar2(255)
);
/
/
--------------------------------------------------------
--  DDL for Type PROPERTY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "PROPERTY" as object(
  Accession varchar2(255)
  ,Name varchar2(255)
  ,Value varchar2(255)
  ,Ontologies varchar2(255)
);
/
--------------------------------------------------------
--  DDL for Type PROPERTYQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "PROPERTYQUERY" as Object(
 PropertyID int
,PropertyValue varchar2(255) 
,FullTextQuery varchar2(255)  
,samples TBLINT
,assays TBLINT
,experiments TBLINT
,isAssay int --return only properties associated with assay
,isSample int   --return only properties associated with assay
);
/
--------------------------------------------------------
--  DDL for Type PROPERTYTABLE DROP TYPE PROPERTYTABLE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "PROPERTYTABLE" as table of Property
/
--------------------------------------------------------
--  DDL for Type SAMPLEQUERY
--------------------------------------------------------

  CREATE OR REPLACE TYPE "SAMPLEQUERY" as Object(
   "AccessionQuery" AccessionQuery
  ,properties TBLINT
);
/
--------------------------------------------------------
--  DDL for Type TBLINTEGER
--------------------------------------------------------

  CREATE OR REPLACE TYPE "TBLINTEGER" 
  as table of integer;
/
--------------------------------------------------------
--  DDL for Type TBLVARCHAR
--------------------------------------------------------

  CREATE OR REPLACE TYPE "TBLVARCHAR" 
  as table of varchar(2000);
/

CREATE OR REPLACE TYPE GeneInfo
 as Object(
   OrganismID int
  ,Identifier varchar2(255)
  ,Name varchar2(255)
);
/

CREATE OR REPLACE TYPE GeneInfoTable as table of GeneInfo;
/

create or replace type PropertyOntology AS OBJECT(
   SomePVID INTEGER -- asssay or sample
  ,Ontology Varchar(255)
);
/
create or replace type PropertyOntologyTable as table of PropertyOntology;
/

create or replace type Varchar2Object AS OBJECT(
  Val Varchar2(255)
);
/
create or replace type Varchar2Table as table of Varchar2Object;
/

exit;

