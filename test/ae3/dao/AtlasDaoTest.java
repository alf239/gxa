package ae3.dao;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import ae3.AtlasAbstractTest;
import ae3.dao.AtlasDao;
import ae3.dao.AtlasObjectNotFoundException;
import ae3.model.AtlasExperiment;

public class AtlasDaoTest extends AtlasAbstractTest
{
	@Test
	public void test_getExperimentByIdAER() throws AtlasObjectNotFoundException
	{
		  AtlasExperiment exp=AtlasDao.getExperimentByIdAER("1324144279");
		  assertNotNull(exp);
		  assertNotNull(exp.getAerExpAccession());
		  assertNotNull(exp.getAerExpId());
		  assertNotNull(exp.getAerExpName());
		  assertNotNull(exp.getDwExpId());
		  if (exp.getDwExpId() != null)
		  {
			  AtlasExperiment exp1=AtlasDao.getExperimentByIdDw(exp.getDwExpId().toString());
			  assertNotNull(exp1);
			  assertNotNull(exp1.getDwExpAccession());
			  assertNotNull(exp1.getDwExpId());
			  assertNotNull(exp1.getDwExpDescription());			  
		  }

		  log.info("######################## Experiment name: " + exp.getAerExpAccession());
		  log.info("######################## Experiment id: " + exp.getAerExpId());
		  log.info("######################## Experiment id: " + exp.getDwExpId());
		  
	}
	
	@Test
	public void test_getExperimentByIdDw() throws AtlasObjectNotFoundException
	{
		  AtlasExperiment exp=AtlasDao.getExperimentByIdDw("334420710");
		  assertNotNull(exp);
		  assertNotNull(exp.getDwExpAccession());
		  assertNotNull(exp.getAerExpId());
		  assertNotNull(exp.getDwExpType());
		  log.info("######################## Experiment name: " + exp.getDwExpAccession());
		  log.info("######################## Experiment id: " + exp.getAerExpId());
		  log.info("######################## Experiment id: " + exp.getDwExpId());
		  
	}

	@Test	
	public void test_getExperimentByAccession() throws AtlasObjectNotFoundException
	{
		AtlasExperiment exp=AtlasDao.getExperimentByAccession("E-MEXP-980");
	}
	

	
}
