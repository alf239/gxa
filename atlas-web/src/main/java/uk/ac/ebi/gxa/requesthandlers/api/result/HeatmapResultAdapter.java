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
 * http://ostolop.github.com/gxa/
 */

package uk.ac.ebi.gxa.requesthandlers.api.result;

import ae3.dao.AtlasDao;
import ae3.model.AtlasExperiment;
import ae3.model.AtlasGene;
import ae3.model.ListResultRowExperiment;
import ae3.service.structuredquery.*;
import uk.ac.ebi.gxa.utils.FilterIterator;
import uk.ac.ebi.gxa.utils.JoinIterator;
import uk.ac.ebi.gxa.utils.MappingIterator;
import uk.ac.ebi.gxa.requesthandlers.base.restutil.RestOut;
import uk.ac.ebi.gxa.index.Experiment;
import uk.ac.ebi.gxa.efo.Efo;
import uk.ac.ebi.gxa.requesthandlers.api.result.ApiQueryResults;

import java.util.Iterator;

/**
 * @author pashky
 */
public class HeatmapResultAdapter implements ApiQueryResults<HeatmapResultAdapter.ResultRow> {
    private final AtlasStructuredQueryResult r;
    private final AtlasDao dao;
    private final Efo efo;

    public HeatmapResultAdapter(AtlasStructuredQueryResult r, AtlasDao dao, Efo efo) {
        this.r = r;
        this.dao = dao;
        this.efo = efo;
    }

    public long getTotalResults() {
        return r.getTotal();
    }

    public long getNumberOfResults() {
        return r.getSize();
    }

    public long getStartingFrom() {
        return r.getStart();
    }

    @RestOut(xmlItemName = "result")
    public class ResultRow {
        private final StructuredResultRow row;

        public abstract class Expression {
            UpdownCounter counter;

            public int getUpExperiments() {
                return counter.getUps();
            }

            public int getDownExperiments() {
                return counter.getDowns();
            }

            public double getUpPvalue() {
                return counter.getMpvUp();
            }

            public double getDownPvalue() {
                return counter.getMpvDn();
            }

            public Iterator<ListResultRowExperiment> getExperiments() {
                return new FilterIterator<Experiment, ListResultRowExperiment>(expiter()) {
                    public ListResultRowExperiment map(Experiment e) {
                        AtlasExperiment aexp = dao.getExperimentById(e.getId());
                        if (aexp == null) {
                            return null;
                        }
                        return new ListResultRowExperiment(e.getId(), aexp.getAccession(),
                                                           aexp.getDescription(), e.getPvalue(),
                                                           e.getExpression());
                    }
                };
            }

            abstract Iterator<Experiment> expiter();
        }

        public class EfvExp extends ResultRow.Expression {
            private EfvTree.EfEfv<Integer> efefv;

            public EfvExp(EfvTree.EfEfv<Integer> efefv) {
                this.efefv = efefv;
                this.counter = row.getCounters().get(efefv.getPayload());
            }

            public String getEf() {
                return efefv.getEf();
            }

            public String getEfv() {
                return efefv.getEfv();
            }

            Iterator<Experiment> expiter() {
                return row.getGene().getExperimentsTable().findByEfEfv(efefv.getEf(), efefv.getEfv()).iterator();
            }
        }

        public class EfoExp extends ResultRow.Expression {
            private EfoTree.EfoItem<Integer> efoItem;

            public EfoExp(EfoTree.EfoItem<Integer> efoItem) {
                this.efoItem = efoItem;
                this.counter = row.getCounters().get(efoItem.getPayload());
            }

            public String getEfoTerm() {
                return efoItem.getTerm();
            }

            public String getEfoId() {
                return efoItem.getId();
            }

            Iterator<Experiment> expiter() {
                return row.getGene().getExperimentsTable()
                        .findByEfoSet(efo.getTermAndAllChildrenIds(efoItem.getId())).iterator();
            }
        }

        public ResultRow(StructuredResultRow row) {
            this.row = row;
        }

        public AtlasGene getGene() {
            return row.getGene();
        }

        public Iterator<ResultRow.Expression> getExpressions() {
            return new JoinIterator<
                    EfvTree.EfEfv<Integer>,
                    EfoTree.EfoItem<Integer>,
                    ResultRow.Expression
                    >(r.getResultEfvs().getNameSortedList().iterator(),
                      r.getResultEfos().getExplicitList().iterator()) {

                public Expression map1(EfvTree.EfEfv<Integer> from) {
                    if (row.getCounters().get(from.getPayload()).isZero()) {
                        return null;
                    }
                    return new ResultRow.EfvExp(from);
                }

                public Expression map2(EfoTree.EfoItem<Integer> from) {
                    if (row.getCounters().get(from.getPayload()).isZero()) {
                        return null;
                    }
                    return new ResultRow.EfoExp(from);
                }
            };
        }
    }

    public Iterator<ResultRow> getResults() {
        return new MappingIterator<StructuredResultRow, ResultRow>(r.getResults().iterator()) {
            public ResultRow map(StructuredResultRow srr) {
                return new ResultRow(srr);
            }
        };
    }
}
