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

package ae3.service;

import ae3.dao.AtlasSolrDAO;
import ae3.model.AtlasGene;
import ae3.service.structuredquery.UpdownCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uk.ac.ebi.gxa.properties.AtlasProperties;
import uk.ac.ebi.gxa.utils.EfvTree;
import uk.ac.ebi.mydas.configuration.DataSourceConfiguration;
import uk.ac.ebi.mydas.configuration.PropertyType;
import uk.ac.ebi.mydas.controller.CacheManager;
import uk.ac.ebi.mydas.datasource.AnnotationDataSource;
import uk.ac.ebi.mydas.exceptions.BadReferenceObjectException;
import uk.ac.ebi.mydas.exceptions.DataSourceException;
import uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException;
import uk.ac.ebi.mydas.model.*;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * DAS1.6
 *
 * Created Using IntelliJ IDEA. Date: 18-Jul-2007 Time: 16:51:37
 *
 * @author Phil Jones, EMBL-EBI, pjones@ebi.ac.uk
 *         <p/>
 *         NOTE TO DATA SOURCE DEVELOPERS:
 *         <p/>
 *         This template is based upon the AnnotationDataSource interface, there are however three other interfaces
 *         available that may be more appropriate for your needs, described here:
 *         <p/>
 *         <a href="http://code.google.com/p/mydas/wiki/HOWTO_WritePluginIntro"> Writing a MyDas Data Source - Selecting
 *         the Best Inteface </a>
 */

public class GxaS4DasDataSource implements AnnotationDataSource {
    CacheManager cacheManager = null;
    ServletContext svCon;
    Map<String, PropertyType> globalParameters;
    DataSourceConfiguration config;

    AtlasSolrDAO atlasSolrDAO;
    AtlasProperties atlasProperties;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected String getDasBaseUrl(){
        return atlasProperties.getProperty("atlas.dasbase");
    }

    private static final String ANATOMOGRAM_LEGEND = 
            "Number of published studies where the gene over/under-expressed compared to the gene's overall mean expression level in the study.";
    private static final String ANATOMOGRAM_ALT_IMAGE =
            "Atlas anatomogram";
    private static final String PROVENANCE_NOTE =
            "Data source: GXA ";
    private static final String PROVENANCE_NOTE_CONT =
            "The Gene Expression Atlas (GXA) is a database of curated functional " +
                    "genomics data, including microarray and next-generation sequencing " +
                    "studies.  It is 'semantically enriched', meaning the data presented is " +
                    "calculated as a summary across subsets of the underlying ArrayExpress " +
                    "Archive.  With GXA you can perform targeted searching, for example to find " +
                    "condition-specific gene expression patterns as well as broader exploratory " +
                    "searches for biologically interesting genes/samples. About GXA: ";
     private static final String PROVENANCE_UC =  "Provenance";
     private static final String PROVENANCE_LC =  PROVENANCE_UC.toLowerCase();

    /**
     * This method is called by the MydasServlet class at Servlet initialisation.
     * <p/>
     * The AnnotationDataSource is passed the servletContext, a handle to globalParameters in the form of a Map
     * &lt;String, String&gt; and a DataSourceConfiguration object.
     * <p/>
     * The latter two parameters contain all of the pertinent information in the ServerConfig.xml file relating to the
     * server as a whole and specifically to this data source.  This mechanism allows the datasource author to set up
     * required configuration in one place, including AnnotationDataSource specific configuration.
     * <p/>
     * <bold>It is highly desirable for the implementation to test itself in this init method and throw a
     * DataSourceException if it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     *
     * @param servletContext   being the ServletContext of the servlet container that the Mydas servlet is running in.
     * @param globalParameters being a Map &lt;String, String&gt; of keys and values as defined in the ServerConfig.xml
     *                         file.
     * @param dataSourceConfig containing the pertinent information frmo the ServerConfig.xml file for this datasource,
     *                         including (optionally) a Map of datasource specific configuration.
     * @throws uk.ac.ebi.mydas.exceptions.DataSourceException
     *          should be thrown if there is any fatal problem with loading this data source.  <bold>It is highly
     *          desirable for the implementation to test itself in this init method and throw a DataSourceException if
     *          it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     */
    public void init(ServletContext servletContext, Map<String, PropertyType> globalParameters,
                     DataSourceConfiguration dataSourceConfig) throws DataSourceException {
        this.svCon = servletContext;
        this.globalParameters = globalParameters;
        this.config = dataSourceConfig;

        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(svCon);
        atlasSolrDAO = (AtlasSolrDAO)context.getBean("atlasSolrDAO");
        atlasProperties = (AtlasProperties)context.getBean("atlasProperties");
    }

    /**
     * This method is called when the DAS server is shut down and should be used to clean up resources such as database
     * connections as required.
     */
    public void destroy() {
    }


    private int iCountTypes = 0;
    private String currentType = "";

    private String getSortableCaption(String caption) {
        iCountTypes += 1;
        if (0 != caption.compareTo(currentType)) {
            //iCountTypes+=1; Sept-8-2009 let's number all
            currentType = caption;
        }
        return (iCountTypes < 100 ? "0" : "") + (iCountTypes < 10 ? "0" : "") + String.valueOf(iCountTypes) + ". " +
                caption;
    }

    public static int SortOrd(String val) {
        if (val.equalsIgnoreCase("gene")) {
            return 1;
        }
        else if (val.equalsIgnoreCase("organism part")) {
            return 2;
        }
        else if (val.equalsIgnoreCase("disease state")) {
            return 3;
        }
        else if (val.equalsIgnoreCase("cell type")) {
            return 4;
        }
        else if (val.equalsIgnoreCase("cell line")) {
            return 5;
        }
        else if (val.equalsIgnoreCase("compound treatment")) {
            return 6;
        }
        else if (val.equalsIgnoreCase("experiment")) {
            return 8;
        }
        else {
            return 7;
        }
    }

    //

    public DasFeature getGeneDasFeature(AtlasGene gene) throws DataSourceException {
        try {
            String notes = String.format("%1$s differential expression summary",gene.getGeneName());
            return (new DasFeature(
                    gene.getGeneIdentifier(),
                    "differential expression summary", // ,gene.getGeneIdentifier(),
                    new DasType("description","description",null,getSortableCaption("Gene")),
                    new DasMethod("ExperimentalFactor","ExperimentalFactor",""),
                    0,
                    0,
                    0.0,
                    DasFeatureOrientation.ORIENTATION_NOT_APPLICABLE,
                    DasPhase.PHASE_NOT_APPLICABLE,
                    Collections.singleton(notes),
                    Collections.singletonMap(new URL(getDasBaseUrl() + "/gene/" + gene.getGeneIdentifier()),
                                             "View in Gene Expression Atlas"),
                    null,
                    null,
                    null
            ));
        }
        catch (MalformedURLException e) {
            throw new DataSourceException("Tried to create an invalid URL for a LINK element.", e);
        }
    }

    public String getHeatmapString(AtlasGene atlasGene, EfvTree.EfEfv<UpdownCounter> row) throws DataSourceException {
            String notes = "";
            if (row.getPayload().getUps() > 0) {
                notes += "up in " + row.getPayload().getUps();
            }

            if (row.getPayload().getDowns() > 0) {
                if (row.getPayload().getUps() > 0) {
                    notes += " and ";
                }
                notes += "down in " + row.getPayload().getDowns();
            }
            //notes+=" experiments";

            String featureLabel;

            String FactorValue = row.getEfv();
            String ExperimentFactor = row.getEf();

            featureLabel = ExperimentFactor + ":" + FactorValue;

            notes = "[" + row.getPayload().getUps() + " up/" + row.getPayload().getDowns() + " dn]";

            //replace last "; "  with "."
            if (notes.endsWith("; ")) {
                notes = notes.substring(0, notes.lastIndexOf("; ")) + ".";
            }

            return FactorValue + ' ' + notes;
   }

   public DasFeature getFactorDasFeature(AtlasGene atlasGene, String factor, List<EfvTree.EfEfv<UpdownCounter>> all_rows) throws DataSourceException {

       try{
        List<EfvTree.EfEfv<UpdownCounter>> my_rows = new ArrayList<EfvTree.EfEfv<UpdownCounter>>();

        for(EfvTree.EfEfv<UpdownCounter> r : all_rows){
            if(r.getEf().equals(factor)){
                my_rows.add(r);
            }
        }

        String notes = "";
        int iCount = 0;

        boolean efStudiedForGene = (my_rows.size() != 0);

        for(EfvTree.EfEfv<UpdownCounter> r : my_rows){
            ++iCount;
            if(iCount>=5){
              continue;
            }
            if(notes.length() > 0){
                notes += ", ";
            }
            notes += r.getEfv();
        }

        if( iCount > 5){
            notes += String.format(", ... (%1$d more)", (iCount-5));
        }

        return new DasFeature(
                    atlasProperties.getCuratedEf(factor),
                    atlasProperties.getCuratedEf(factor),
                    new DasType("summary","summary",null,getSortableCaption(atlasProperties.getCuratedEf(factor))),
                    new DasMethod("ExperimentalFactor","ExperimentalFactor",null),
                    0,
                    0,
                    0.0,
                    DasFeatureOrientation.ORIENTATION_NOT_APPLICABLE,
                    DasPhase.PHASE_NOT_APPLICABLE,
                    Collections.singleton(efStudiedForGene ? notes : "Not studied for this gene" ),
                    (efStudiedForGene ?
                        Collections.singletonMap(
                                new URL(getDasBaseUrl() + "/gene/" + atlasGene.getGeneIdentifier() + "?ef=" + factor),
                                "View all") :
                        new HashMap<URL, String>()),
                    null,
                    null,
                    null
            );
       }
       /*catch (MalformedURLException e) {
            throw new DataSourceException("Tried to create an invalid URL for a LINK element.", e);
       }*/
       catch (Exception e) {
            throw new DataSourceException("Error creating DasFeature.", e);
       }
    }

    public DasFeature getProvenanceDasFeature() throws DataSourceException {

        try {
            List<String> notes = new ArrayList<String>();
            String dataRelease = atlasProperties.getDataRelease();
            String releaseDate = atlasProperties.getLastReleaseDate();
            notes.add(PROVENANCE_NOTE + dataRelease + " ("+releaseDate+").");
            notes.add(PROVENANCE_NOTE_CONT);
            return new DasFeature(
                    PROVENANCE_UC,
                    PROVENANCE_UC,
                    new DasType("atlas-provenance", "description", "description", PROVENANCE_LC),
                    new DasMethod(PROVENANCE_LC, PROVENANCE_LC, PROVENANCE_LC),
                    0,
                    0,
                    0.0,
                    DasFeatureOrientation.ORIENTATION_NOT_APPLICABLE,
                    DasPhase.PHASE_NOT_APPLICABLE,
                    notes,
                    Collections.singletonMap(new URL(getDasBaseUrl()), getDasBaseUrl()),
                    null,
                    null,
                    null
            );
        }
        catch (Exception e) {
            throw new DataSourceException("Error creating DasFeature.", e);
        }
    }

        public DasFeature getPlainTextDasFeature(String caption, String description) throws DataSourceException {

        try{

         return new DasFeature(
                     caption,
                     caption,
                     new DasType("summary","summary","summary","summary"),
                     new DasMethod("summary","summary","summary"),
                     0,
                     0,
                     0.0,
                     DasFeatureOrientation.ORIENTATION_NOT_APPLICABLE,
                     DasPhase.PHASE_NOT_APPLICABLE,
                     Collections.singleton(description), //notes -- do not show notes
                     null, //no links
                     null,
                     null,
                     null
             );
        }
        catch (Exception e) {
             throw new DataSourceException("Error creating DasFeature.", e);
        }
     }


    public DasFeature getImageDasFeature(AtlasGene atlasGene) throws DataSourceException {
        try{

            // LinkedHashMap is used for storing links because the order of links is significant to the
            // way they are interpreted by s4
            Map<URL, String> links = new LinkedHashMap<URL, String>();
            links.put(new URL(getDasBaseUrl() + "/anatomogram/" + atlasGene.getGeneIdentifier() + ".png"), ANATOMOGRAM_LEGEND);
            links.put(new URL(getDasBaseUrl() + "/gene/" + atlasGene.getGeneIdentifier()+"?ef=organism_part"), ANATOMOGRAM_ALT_IMAGE);
            return new DasFeature(
                     "Anatomogram" //String featureId,
                     ,atlasGene.getGeneIdentifier()//String featureLabel,
                     ,new DasType("image","image",null,"image")                      //String typeId,
                     ,new DasMethod("image","image",null)                      //String typeCategory,
                     ,0                            //int startCoordinate,
                     ,0                            //int endCoordinate,
                     ,0.0                          //Double score,
                     ,DasFeatureOrientation.ORIENTATION_NOT_APPLICABLE //DasFeatureOrientation orientation,
                     ,DasPhase.PHASE_NOT_APPLICABLE  //DasPhase phase,
                     ,Collections.singleton("anatomogram")                //Collection<String> notes,
                     ,links
                     ,null                              //Collection<DasTarget> targets,
                     ,null                              //Collection<DasGroup> groups
                     ,null
                    );
        }
        catch (Exception e){
            throw new DataSourceException("Error creating Image DasFeature.", e);
        }
    }

    /**
     * This method returns a List of DasAnnotatedSegment objects, describing the annotated segment and the features of
     * the segmentId passed in as argument.
     *
     * @param segmentReference being the reference of the segment requested in the DAS request (not including start and
     *                         stop coordinates)
     *                         <p/>
     *                         If your datasource implements only this interface, the MydasServlet will handle
     *                         restricting the features returned to the start / stop coordinates in the request and you
     *                         will only need to implement this method to return Features.  If on the other hand, your
     *                         data source includes massive segments, you may wish to implement the {@link
     *                         uk.ac.ebi.mydas.datasource.RangeHandlingAnnotationDataSource} interface.  It will then be
     *                         the responsibility of your AnnotationDataSource plugin to restrict the features returned
     *                         for the requested range.
     * @return A DasAnnotatedSegment object.  This describes the segment that is annotated, limited to the information
     *         required for the /DASGFF/GFF/SEGMENT element.  References a Collection of DasFeature objects.   Note that
     *         this is a basic Collection - this gives you complete control over the details of the Collection type - so
     *         you can create your own comparators etc.
     * @throws uk.ac.ebi.mydas.exceptions.BadReferenceObjectException
     *          in the event that your server does not include information about this segment.
     * @throws uk.ac.ebi.mydas.exceptions.DataSourceException
     *          should be thrown if there is any fatal problem with loading this data source.  <bold>It is highly
     *          desirable for the implementation to test itself in this init method and throw a DataSourceException if
     *          it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     */
    public DasAnnotatedSegment getFeatures(String segmentReference)
        throws BadReferenceObjectException, DataSourceException {
        long begin_time = System.currentTimeMillis();

        iCountTypes = 0;

        log.info(String.format("DAS query: %s" ,segmentReference));

        AtlasGene atlasGene = atlasSolrDAO.getGeneByIdentifier(segmentReference).getGene();

        if (null == atlasGene) {
            log.warn(String.format("DAS segment not found: %s" ,segmentReference));
            throw new BadReferenceObjectException("can not find gene with ID=" + segmentReference, "DAS");
        }

        ArrayList<DasFeature> feat = new ArrayList<DasFeature>();

        feat.add(getGeneDasFeature(atlasGene)); //first row - gene

        List<EfvTree.EfEfv<UpdownCounter>> heatmaps = atlasGene.getHeatMap(atlasProperties.getGeneHeatmapIgnoredEfs()).getValueSortedList();

        for(String factor : atlasProperties.getDasFactors()) {
            feat.add(getFactorDasFeature(atlasGene,factor,heatmaps));
        }

        feat.add(getImageDasFeature(atlasGene));

        feat.add(getProvenanceDasFeature());

        DasAnnotatedSegment result =
                new DasAnnotatedSegment(segmentReference, 1, 1, "1.0", "GXA annotation for " + segmentReference, feat);

        log.info(String.format("das response constructed in %d ms", System.currentTimeMillis() - begin_time));

        return result;
    }

    /**
     * This method is used to implement the DAS types command.  (See <a href="http://biodas.org/documents/spec.html#types">
     * DAS 1.53 Specification : types command</a>.  This method should return a Collection containing <b>all</b> the
     * types described by the data source (one DasType object for each type ID).
     * <p/>
     * For some data sources it may be desirable to populate this Collection from a configuration file or to
     *
     * @return a Collection of DasType objects - one for each type id described by the data source.
     * @throws uk.ac.ebi.mydas.exceptions.DataSourceException
     *          should be thrown if there is any fatal problem with loading this data source.  <bold>It is highly
     *          desirable for the implementation to test itself in this init method and throw a DataSourceException if
     *          it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     */
    public Collection<DasType> getTypes() throws DataSourceException {
        Collection<DasType> types = new ArrayList<DasType>(5);
        types.add(new DasType("summary","summary","summary","Gene summary"));
        types.add(new DasType("description","description","description","description"));
        types.add(new DasType("image", "image","image", "image"));
        types.add(new DasType("atlas-provenance", "description", "description", PROVENANCE_LC));
        return types;
    }

    /**
     * <b>For some Datasources, especially ones with many entry points, this method may be hard or impossible to
     * implement.  If this is the case, you should just throw an {@link uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException}
     * as your implementation of this method, so that a suitable error HTTP header (X-DAS-Status: 501 Unimplemented
     * feature) is returned to the DAS client as described in the DAS 1.53 protocol.</b><br/><br/>
     * <p/>
     * This method is used by the features command when no segments are included, but feature_id and / or group_id
     * filters have been included, to meet the following specification:<br/><br/>
     * <p/>
     * "<b>feature_id</b> (zero or more; new in 1.5)<br/> Instead of, or in addition to, <b>segment</b> arguments, you
     * may provide one or more <b>feature_id</b> arguments, whose values are the identifiers of particular features.  If
     * the server supports this operation, it will translate the feature ID into the segment(s) that strictly enclose
     * them and return the result in the <i>features</i> response.  It is possible for the server to return multiple
     * segments if the requested feature is present in multiple locations. <b>group_id</b> (zero or more; new in
     * 1.5)<br/> The <b>group_id</b> argument, is similar to <b>feature_id</b>, but retrieves segments that contain the
     * indicated feature group."  (Direct quote from the DAS 1.53 specification, available from <a
     * href="http://biodas.org/documents/spec.html#features">http://biodas.org/documents/spec.html#features</a>.)
     * <p/>
     * Note that if segments are included in the request, this method is not used, so feature_id and group_id filters
     * accompanying a list of segments will work correctly, even if your implementation of this method throws an {@link
     * uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException}.
     *
     * @param featureIdCollection a Collection&lt;String&gt; of feature_id values included in the features command /
     *                            request. May be a <code>java.util.Collections.EMPTY_LIST</code> but will <b>not</b> be
     *                            null.
     * @param groupIdCollection   a Collection&lt;String&gt; of group_id values included in the features command /
     *                            request. May be a <code>java.util.Collections.EMPTY_LIST</code> but will <b>not</b> be
     *                            null.
     * @return A Collection of {@link uk.ac.ebi.mydas.model.DasAnnotatedSegment} objects. These describe the segments
     *         that is annotated, limited to the information required for the /DASGFF/GFF/SEGMENT element.  Each
     *         References a Collection of DasFeature objects.   Note that this is a basic Collection - this gives you
     *         complete control over the details of the Collection type - so you can create your own comparators etc.
     * @throws uk.ac.ebi.mydas.exceptions.DataSourceException
     *          should be thrown if there is any fatal problem with loading this data source.  <bold>It is highly
     *          desirable for the implementation to test itself in this init method and throw a DataSourceException if
     *          it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     * @throws uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException
     *          Throw this if you cannot provide a working implementation of this method.
     */
    public Collection<DasAnnotatedSegment> getFeatures(Collection<String> featureIdCollection,
                                                       Collection<String> groupIdCollection)
            throws UnimplementedFeatureException, DataSourceException {
        return null;
    }

    /**
     * This method allows the DAS server to report a total count for a particular type for all annotations across the
     * entire data source.  If it is not possible to retrieve this value from your dsn, you should return
     * <code>null</code>.
     *
     * @param type containing the information needed to retrieve the type count (type id and optionally the method id
     *             and category id.  Note that the last two may be null, which needs to be taken into account by the
     *             implementation.)
     * @return The total count <i>across the entire data source</i> (not just for one segment) for the specified type.
     *         If it is not possible to determine this count, this method should return <code>null</code>.
     * @throws uk.ac.ebi.mydas.exceptions.DataSourceException
     *          should be thrown if there is any fatal problem with loading this data source.  <bold>It is highly
     *          desirable for the implementation to test itself in this init method and throw a DataSourceException if
     *          it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     */
    public Integer getTotalCountForType(DasType type) throws DataSourceException {
        return null;
    }

    /**
     * The mydas DAS server implements caching within the server.  This method passes your datasource a reference to a
     * {@link uk.ac.ebi.mydas.controller.CacheManager} object.  To implement this method, you should simply retain a
     * reference to this object. In your code you can then make use of this object to manipulate caching in the mydas
     * servlet.
     * <p/>
     * At present the {@link uk.ac.ebi.mydas.controller.CacheManager} class provides you with a single method public
     * void emptyCache() that you can call if (for example) the underlying data source has changed.
     *
     * @param cacheManager a reference to a {@link uk.ac.ebi.mydas.controller.CacheManager} object that the data source
     *                     can use to empty the cache for this data source.
     */
    public void registerCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * This method returns a URL, based upon a request built as part of the DAS 'link' command. The nature of this URL
     * is entirely up to the data source implementor.
     * <p/>
     * The mydas servlet will redirect to the URL provided.  This command is intended for use in an internet browser, so
     * the URL returned should be a valid internet address.  The page can return content of any MIME type and is
     * intended to be 'human readable' rather than material for consumption by a DAS client.
     * <p/>
     * The link command takes two mandatory arguments: <ul> <li> a 'field' parameter which is limited to one of five
     * valid values.  This method is guaranteed to be called with the 'field' parameter set to one of these values (any
     * other request will be handled as an error by the mydas DAS server servlet.)  The 'field' parameter will be one of
     * the five static String constants that are members of the AnnotationDataSource interface. </li> <li> an 'id'
     * field.  Again, this will be validated by the mydas servlet to ensure that it is a non-null, non-zero length
     * String. </li> <ul> See <a href="http://biodas.org/documents/spec.html#feature_linking">DAS 1.53 Specification:
     * Linking to a Feature</a> for details.
     * <p/>
     * If your data source does not implement this method, an UnimplementedFeatureException should be thrown.
     *
     * @param field one of 'feature', 'type', 'method', 'category' or 'target' as documented in the DAS 1.53
     *              specification
     * @param id    being the ID of the indicated annotation field
     * @return a valid URL.
     * @throws uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException
     *          in the event that the DAS data source does not implement the link command
     * @throws uk.ac.ebi.mydas.exceptions.DataSourceException
     *          should be thrown if there is any fatal problem with loading this data source.  <bold>It is highly
     *          desirable for the implementation to test itself in this init method and throw a DataSourceException if
     *          it fails, e.g. to attempt to get a Connection to a database and read a record.</bold>
     */
    public URL getLinkURL(String field, String id) throws UnimplementedFeatureException, DataSourceException {
        return null;
    }

    public Collection<DasAnnotatedSegment> getFeatures(Collection<String> s,Integer i){
        return null;
    }

    public DasAnnotatedSegment getFeatures(String s, Integer i)
        throws BadReferenceObjectException, DataSourceException{
        return getFeatures(s);
    }

    // TODO
    public java.util.Collection<uk.ac.ebi.mydas.model.DasEntryPoint> getEntryPoints(java.lang.Integer integer, java.lang.Integer integer1)
            throws uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException, uk.ac.ebi.mydas.exceptions.DataSourceException {
        throw new UnimplementedFeatureException("No implemented");

    }
    // TODO
    public java.lang.String getEntryPointVersion() throws uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException, uk.ac.ebi.mydas.exceptions.DataSourceException {
        throw new UnimplementedFeatureException("No implemented");
    }
    // TODO
    public int getTotalEntryPoints() throws uk.ac.ebi.mydas.exceptions.UnimplementedFeatureException, uk.ac.ebi.mydas.exceptions.DataSourceException {
       throw new UnimplementedFeatureException("No implemented");
    }
}
