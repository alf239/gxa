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
 * http://gxa.github.com/gxa
 */

package  uk.ac.ebi.gxa.model;

import java.util.Collection;

/**
 * Assay
 * User: Andrey
 * Date: Oct 20, 2009
 * Time: 5:44:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Assay extends Accessible, Annotated {

        /**
         * Experiment this assay was used in.
         * @return experiment accession
         */
        public String getExperimentAccession();

        /**
         * Samples mixed in assay.
         * @return list of samples accession
        */
        public Collection<String> getSampleAccessions();

        /**
         * Gets column position in expression matrix of the array design, this assay belongs to
         * @return position
         */
        public int getPositionInMatrix();


}