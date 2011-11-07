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

import ae3.service.AtlasStatisticsQueryService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.FacetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import uk.ac.ebi.gxa.dao.AssayDAO;
import uk.ac.ebi.gxa.dao.PropertyDAO;
import uk.ac.ebi.gxa.dao.exceptions.RecordNotFoundException;
import uk.ac.ebi.gxa.index.builder.IndexBuilder;
import uk.ac.ebi.gxa.index.builder.IndexBuilderEventHandler;
import uk.ac.ebi.gxa.properties.AtlasProperties;
import uk.ac.ebi.gxa.statistics.EfvAttribute;
import uk.ac.ebi.gxa.statistics.StatisticsType;
import uk.ac.ebi.microarray.atlas.model.Property;
import uk.ac.ebi.microarray.atlas.model.PropertyName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static uk.ac.ebi.gxa.exceptions.LogUtil.createUnexpected;


/**
 * EFVs listing and autocompletion helper implementation
 *
 * @author pashky
 * @see AutoCompleter
 */
public class AtlasEfvService implements AutoCompleter, IndexBuilderEventHandler, DisposableBean {
    private SolrServer solrServerProp;
    private AtlasProperties atlasProperties;
    private IndexBuilder indexBuilder;
    private AtlasStatisticsQueryService atlasStatisticsQueryService;
    private PropertyDAO propertyDAO;
    private AssayDAO assayDAO;

    final private Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, PrefixNode> prefixTrees = newHashMap();
    private Set<String> allFactors = newHashSet();

    public void setSolrServerProp(SolrServer solrServerProp) {
        this.solrServerProp = solrServerProp;
    }

    public void setAtlasStatisticsQueryService(AtlasStatisticsQueryService atlasStatisticsQueryService) {
        this.atlasStatisticsQueryService = atlasStatisticsQueryService;
    }

    public void setAtlasProperties(AtlasProperties atlasProperties) {
        this.atlasProperties = atlasProperties;
    }

    public void setPropertyDAO(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

    public void setAssayDAO(AssayDAO assayDAO) {
        this.assayDAO = assayDAO;
    }

    public Set<PropertyName> getOptionsFactors() {
        return getFilteredFactors(atlasProperties.getOptionsIgnoredEfs());
    }

    private SortedSet<PropertyName> getFilteredFactors(Collection<String> ignored) {
        SortedSet<PropertyName> result = newTreeSet();
        for (PropertyName propertyName : assayDAO.getPropertyNames()) {
            if (!ignored.contains(propertyName.getName()))
                result.add(propertyName);
        }
        return result;
    }

    public Set<String> getAllFactors() {
        if (allFactors.isEmpty()) {
            SolrQuery q = new SolrQuery("*:*");
            q.setRows(0);
            q.addFacetField("property_f");
            q.setFacet(true);
            q.setFacetLimit(-1);
            q.setFacetMinCount(1);
            q.setFacetSort(FacetParams.FACET_SORT_COUNT);
            try {
                QueryResponse qr = solrServerProp.query(q);
                if (qr.getFacetFields().get(0).getValues() != null)
                    for (FacetField.Count ffc : qr.getFacetFields().get(0).getValues()) {
                        allFactors.add(ffc.getName());
                    }
            } catch (SolrServerException e) {
                throw createUnexpected("Can't fetch all factors", e);
            }
        }
        return allFactors;
    }

    private PrefixNode treeGetOrLoad(String property) {
        PrefixNode root;
        synchronized (prefixTrees) {
            if (!prefixTrees.containsKey(property)) {
                log.info("Loading factor values and counts for " + property);

                root = new PrefixNode();
                try {
                    final PropertyName propertyName = propertyDAO.getByName(property);
                    final List<Property> properties = assayDAO.getProperties(propertyName);
                    for (Property p : properties) {
                        EfvAttribute attr = new EfvAttribute(p.getName(), p.getValue());
                        int geneCount = atlasStatisticsQueryService.getBioEntityCountForEfvAttribute(attr, StatisticsType.UP_DOWN);
                        if (geneCount > 0) {
                            root.add(attr.getEfv(), geneCount);
                        }
                    }
                    prefixTrees.put(property, root);
                    log.info("Done loading factor values and counts for " + property);
                } catch (RecordNotFoundException e) {
                    throw createUnexpected(e.getMessage(), e);
                }
            }
            root = prefixTrees.get(property);
        }
        return root;
    }

    public Collection<String> listAllValues(String property) {
        final List<String> result = new ArrayList<String>();
        PrefixNode.WalkResult rc = new PrefixNode.WalkResult() {
            public void put(String name, int count) {
                result.add(name);
            }

            public boolean enough() {
                return false;
            }
        };
        PrefixNode root = treeGetOrLoad(property);
        if (root != null)
            root.collect("", rc);
        return result;
    }

    public Collection<AutoCompleteItem> autoCompleteValues(String property, @Nonnull String prefix, int limit) {
        return autoCompleteValues(property, prefix, limit, null);
    }

    public Collection<AutoCompleteItem> autoCompleteValues(final String name, @Nonnull String prefix, final int limit, @Nullable Map<String, String> filters) {
        return treeAutocomplete(getPropertyList(name), prefix.toLowerCase(), limit);
    }

    private Collection<PropertyName> getPropertyList(String name) {
        if (isNullOrEmpty(name))
            return getOptionsFactors();

        try {
            final PropertyName propertyName = propertyDAO.getByName(name);

            if (isProhibited(propertyName))
                return emptyList();

            return asList(propertyName);
        } catch (RecordNotFoundException e) {
            log.warn("Unknown property name requested: " + name, e);
            return emptyList();
        }
    }

    private boolean isProhibited(PropertyName propertyName) {
        return !getOptionsFactors().contains(propertyName);
    }

    private Collection<AutoCompleteItem> treeAutocomplete(Collection<PropertyName> propertyNames, final @Nonnull String prefix, final int limit) {
        final List<AutoCompleteItem> result = new ArrayList<AutoCompleteItem>();

        for (final PropertyName propertyName : propertyNames) {
            PrefixNode root = treeGetOrLoad(propertyName.getName());
            if (root != null) {
                root.walk(prefix, 0, "", new PrefixNode.WalkResult() {
                    public void put(String name, int count) {
                        result.add(
                                new EfvAutoCompleteItem(
                                        propertyName.getName(),
                                        propertyName.getDisplayName(),
                                        name,
                                        (long) count,
                                        new Rank(1.0 * prefix.length() / name.length())));
                    }

                    public boolean enough() {
                        return limit >= 0 && result.size() >= limit;
                    }
                });
            }
        }
        return result;
    }

    private String curatedName(String name) {
        try {
            PropertyName propertyName = propertyDAO.getByName(name);
            return propertyName.getDisplayName();
        } catch (RecordNotFoundException e) {
            throw createUnexpected("Unknown property: " + name, e);
        }
    }

    public void setIndexBuilder(IndexBuilder indexBuilder) {
        this.indexBuilder = indexBuilder;
        indexBuilder.registerIndexBuildEventHandler(this);
    }

    public void onIndexBuildFinish() {
        allFactors.clear();
        prefixTrees.clear();
    }

    public void onIndexBuildStart() {

    }

    public void destroy() throws Exception {
        if (indexBuilder != null)
            indexBuilder.unregisterIndexBuildEventHandler(this);
    }
}
