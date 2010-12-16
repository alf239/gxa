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

package ae3.model;

import org.apache.solr.common.SolrDocument;

public class AtlasRNASeqExperiment extends AtlasExperiment {

    protected AtlasRNASeqExperiment(SolrDocument exptdoc) {
        super(exptdoc);
    }

    public Type getType() {
        return Type.RNA_SEQ;
    }

    public String getRunNames() {
        /*
        final File runsDir = new File(atlasNetCDFDAO.getDataDirectory(getAccession()), "runs");
        final File[] runs = runsDir.listFiles();
        if (runs == null || runs.length == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (File f : runs) {
            builder.append(f.getName());
            builder.append(',');
        }
        final int len = builder.length();
        builder.delete(len - 1, len);
        return builder.toString();
        */
        final StringBuilder builder = new StringBuilder();
        for (String f : getSampleCharacteristicValues().keySet()) {
            builder.append(f);
            builder.append(',');
        }
        final int len = builder.length();
        builder.delete(len - 1, len);
        return builder.toString();
    }
}
