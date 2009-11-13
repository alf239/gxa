package uk.ac.ebi.gxa.model.impl;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import uk.ac.ebi.ae3.indexbuilder.AbstractOnceIndexTest;
import uk.ac.ebi.gxa.model.*;

import java.util.Collections;
import java.util.Collection;
import java.util.Date;

/**
 * @author pashky
 */
public class ExpressionStatDaoTest extends AbstractOnceIndexTest {
    static SolrServer geneServer;
    
    ExpressionStatDao statDao;

    @BeforeClass
    public static void makeServer() {
        geneServer = new EmbeddedSolrServer(getContainer(), "atlas");
    }

    @Before
    public void setUp() {
        statDao = new ExpressionStatDao();
        statDao.setGeneServer(geneServer);
        statDao.setDao(new Dao() {
            public QueryResultSet<ArrayDesign> getArrayDesign(ArrayDesignQuery atlasArrayDesignQuery, PageSortParams pageSortParams) throws GxaException {
                return null;
            }

            public QueryResultSet<ArrayDesign> getArrayDesign(ArrayDesign atlasArrayDesignQuery) throws GxaException {
                return null;
            }

            public ArrayDesign getArrayDesignByAccession(AccessionQuery accession) throws GxaException {
                return null;
            }

            public QueryResultSet<Assay> getAssay(AssayQuery atlasAssayQuery, PageSortParams pageSortParams) throws GxaException {
                return null;
            }

            public QueryResultSet<Assay> getAssay(AssayQuery atlasAssayQuery) throws GxaException {
                return null;
            }

            public Assay getAssayByAccession(AccessionQuery accession) throws GxaException {
                return null;
            }

            public QueryResultSet<Sample> getSample(SampleQuery atlasSampleQuery, PageSortParams pageSortParams) throws GxaException {
                return null;
            }

            public QueryResultSet<Sample> getSample(SampleQuery atlasSampleQuery) throws GxaException {
                return null;
            }

            public Sample getSampleByAccession(AccessionQuery accession) throws GxaException {
                return null;
            }

            public QueryResultSet<Experiment> getExperiment(ExperimentQuery atlasExperimentQuery, PageSortParams pageSortParams) throws GxaException {
                return null;
            }

            public QueryResultSet<Experiment> getExperiment(ExperimentQuery atlasExperimentQuery) throws GxaException {
                QueryResultSet<Experiment> res = new QueryResultSet<Experiment>();
                res.setIsMulti(false);
                res.setItem(new Experiment() {
                    public String getDescription() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Collection<String> getType() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Date getLoadDate() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public DEGStatus getDEGStatus() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public String getPerformer() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public String getLab() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Collection<String> getAssayAccessions() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Collection<String> getSampleAccessions() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public int getId() {
                        return 0;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public String getAccession() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public PropertyCollection getProperties() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                return res;
            }

            public Experiment getExperimentByAccession(AccessionQuery accession) throws GxaException {
                return getExperiment(null).getItem();
            }

            public QueryResultSet<Property> getProperty(PropertyQuery atlasPropertyQuery, PageSortParams pageSortParams) throws GxaException {
                return null;
            }

            public QueryResultSet<Property> getProperty(PropertyQuery atlasPropertyQuery) throws GxaException {
                QueryResultSet<Property> res = new QueryResultSet<Property>();
                res.setIsMulti(false);
                res.setItem(new Property() {
                    public String getName() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public Collection<String> getValues() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public int getId() {
                        return 0;  //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public String getAccession() {
                        return null;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                return res;
            }

            public Property getPropertyByAccession(AccessionQuery accession) throws GxaException {
                return null;
            }

            public QueryResultSet<Gene> getGene(GeneQuery atlasGeneQuery, PageSortParams pageSortParams) throws GxaException {
                return null;
            }

            public QueryResultSet<Gene> getGene(GeneQuery atlasGeneQuery) throws GxaException {
                return null;
            }

            public Gene getGeneByAccession(AccessionQuery accession) throws GxaException {
                return null;
            }

            public <T extends ExpressionStat> QueryResultSet<T> getExpressionStat(ExpressionStatQuery atlasExpressionStatQuery, PageSortParams pageSortParams) throws GxaException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            public <T extends ExpressionStat> QueryResultSet<T> getExpressionStat(ExpressionStatQuery atlasExpressionStatQuery) throws GxaException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    @After
    public void tearDown() {
        statDao = null;
    }

    @Test
    public void testGetExpressionStat() throws GxaException {

        QueryResultSet<GeneExpressionStat<PropertyExpressionStat<ExperimentExpressionStat>>> result = statDao.getExpressionStat(new ExpressionStatQuery().hasGene(new GeneQuery().hasId("153074124")));
        assertNotNull(result);

        assertEquals(true, result.isFound());
        assertEquals(1, result.getNumberOfResults());
        assertNotNull(result.getItems());
        assertNotNull(result.getItem());
        assertEquals("ENSG00000120471", result.getItem().getGene());
    }

    @Test
    public void testGetExpressionStatPageSort() {
        // Add your code here
    }
}
