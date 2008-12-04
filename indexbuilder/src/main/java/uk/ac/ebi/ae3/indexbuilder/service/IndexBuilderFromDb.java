/**
 * EBI Microarray Informatics Team (c) 2007-2008
 */
package uk.ac.ebi.ae3.indexbuilder.service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.common.SolrInputDocument;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import uk.ac.ebi.ae3.indexbuilder.Constants;
import uk.ac.ebi.ae3.indexbuilder.dao.ExperimentDwJdbcDao;
import uk.ac.ebi.ae3.indexbuilder.dao.ExperimentJdbcDao;
import uk.ac.ebi.ae3.indexbuilder.model.Experiment;
import uk.ac.ebi.ae3.indexbuilder.utils.XmlUtil;
/**
 * 
 * Class description goes here.
 *
 * @version 	1.0 2008-04-01
 * @author 	Miroslaw Dylag
 */
public class IndexBuilderFromDb extends IndexBuilderService
{
    	/** */
	private ExperimentJdbcDao experimentDao;
	private ExperimentDwJdbcDao experimentDwDao;
    //private String mageDir;

	
	/**
	 * 
	 * @param confService
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public IndexBuilderFromDb() throws ParserConfigurationException, IOException, SAXException
	{
		
	}

	/**
	 * 
	 */
	@Override
	protected void createIndexDocs() throws Exception
	{
			Collection<Experiment> colExp=experimentDao.getExperiments(null);		
			Iterator<Experiment> it=colExp.iterator();
			while (it.hasNext())
			{
				Experiment exp=it.next();
				String xml=experimentDao.getExperimentAsXml(exp);
				
				SolrInputDocument doc = null;
				doc = XmlUtil.createSolrInputDoc(xml);
				String xmlDw=experimentDwDao.getExperimentAsXml(exp);
				//Add information about ftp files to SolrDocument
				getInfoFromFtp(exp.getAccession(), doc);
//				if (experimentDwDao.experimentExists(exp))
				if(xmlDw!="")
				{
				  log.info("exists in DW");
				  doc.addField(Constants.FIELD_EXP_IN_DW, true);
				  XmlUtil.addExperimentFromDW(xmlDw, doc);
				}
				else
				{
					log.info("does not exist in DW");
					doc.addField(Constants.FIELD_EXP_IN_DW, false);					
				}
				if (doc!=null)
				{
					getSolrEmbeddedIndex().addDoc(doc);
				}

			}

		
	}
	

	public ExperimentJdbcDao getExperimentDao()
	{
		return experimentDao;
	}

	public void setExperimentDao(ExperimentJdbcDao experimentDao)
	{
		this.experimentDao = experimentDao;
	}

	public ExperimentDwJdbcDao getExperimentDwDao()
	{
		return experimentDwDao;
	}

	public void setExperimentDwDao(ExperimentDwJdbcDao experimentDwDao)
	{
		this.experimentDwDao = experimentDwDao;
	}
	
	
	


}
