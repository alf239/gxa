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

package ae3.service.structuredquery;

import ae3.model.AtlasGene;

import java.util.List;

/**
 * Structured query result row representing one gene and it's up/down counters 
 * @author pashky
*/
public class StructuredResultRow implements Comparable<StructuredResultRow>{
    private AtlasGene gene;

    private List<UpdownCounter> updownCounters;

    public StructuredResultRow(AtlasGene gene, List<UpdownCounter> updownCounters) {
        this.gene = gene;
        this.updownCounters = updownCounters;
    }

    public AtlasGene getGene() {
        return gene;
    }

    public List<UpdownCounter> getCounters() {
        return updownCounters;
    }

    /**
     * @return sum total of studies from updownCounters
     */
    public Integer getTotalUpDnStudies() {
        Integer totalStudies = 0;
        for (UpdownCounter counter : updownCounters) {
            totalStudies += counter.getNoStudies();
        }
        return totalStudies;
    }

     /**
     * @return sum total of studies from updownCounters
     */
    public Integer getTotalNoneDEStudies() {
        Integer totalNoneDEStudies = 0;
        for (UpdownCounter counter : updownCounters) {
            totalNoneDEStudies += counter.getNones();
        }
        return totalNoneDEStudies;
    }

    public boolean isZero() {
        return getTotalUpDnStudies() + getTotalNoneDEStudies() == 0;
    }

    /**
     * TODO
     * @param o
     * @return
     */
    public int compareTo(StructuredResultRow o) {
        if (getTotalUpDnStudies() == o.getTotalUpDnStudies() && getTotalUpDnStudies() == 0)
            return -Integer.valueOf(getTotalNoneDEStudies()).compareTo(o.getTotalNoneDEStudies());
        else if (getTotalUpDnStudies() == o.getTotalUpDnStudies()) {
            if (getGene().getGeneName() == null) {
                return 1;
            } else if (o.getGene().getGeneName() == null) {
                return -1;
            } else
                return Integer.valueOf(getGene().getGeneName().compareTo(o.getGene().getGeneName()));
        } else
            return -Integer.valueOf(getTotalUpDnStudies()).compareTo(o.getTotalUpDnStudies());
    }
}
