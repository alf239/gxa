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

package uk.ac.ebi.gxa.data;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.microarray.atlas.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class ExperimentWithData implements Closeable {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AtlasDataDAO atlasDataDAO;
    private final Experiment experiment;

    private final Map<ArrayDesign, NetCDFProxy> proxies = new HashMap<ArrayDesign, NetCDFProxy>();

    // cached data
    private final Map<ArrayDesign, String[]> designElementAccessions = new HashMap<ArrayDesign, String[]>();

    ExperimentWithData(@Nonnull AtlasDataDAO atlasDataDAO, @Nonnull Experiment experiment) {
        this.atlasDataDAO = atlasDataDAO;
        this.experiment = experiment;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    /**
     * @param criteria the criteria to choose arrayDesign
     * @return first arrayDesign used in experiment, that matches criteria;
     *         or null if no arrayDesign has been found
     */
    public ArrayDesign findArrayDesign(Predicate<ArrayDesign> criteria) {
        for (ArrayDesign ad : experiment.getArrayDesigns()) {
            if (criteria.apply(ad)) {
                return ad;
            }
        }
        return null;
    }

    // TODO: change to private
    public NetCDFProxy getProxy(ArrayDesign arrayDesign) throws AtlasDataException {
        NetCDFProxy p = proxies.get(arrayDesign);
        if (p == null) {
            try {
                p = new NetCDFProxyV1(atlasDataDAO.getNetCDFLocation(experiment, arrayDesign));
            } catch (IOException e) {
                throw new AtlasDataException(e);
            }
            proxies.put(arrayDesign, p);
        }
        return p;
    }

    /*
     * This method returns samples in the order they are stored in netcdf file.
     * While this order is important we have to use this method,
     * in future it would be replaced by Experiment method.
     */
    public List<Sample> getSamples(ArrayDesign arrayDesign) throws AtlasDataException {
        final String[] sampleAccessions;
        try {
            sampleAccessions = getProxy(arrayDesign).getSampleAccessions();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
        final ArrayList<Sample> samples = new ArrayList<Sample>(sampleAccessions.length);
        for (String accession : sampleAccessions) {
            samples.add(experiment.getSample(accession));
        }
        return samples;
    }

    /*
     * This method returns assays in the order they are stored in netcdf file.
     * While this order is important we have to use this method,
     * in future it would be replaced by Experiment method.
     */
    public List<Assay> getAssays(ArrayDesign arrayDesign) throws AtlasDataException {
        final String[] assayAccessions;
        try {
            assayAccessions = getProxy(arrayDesign).getAssayAccessions();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
        final ArrayList<Assay> assays = new ArrayList<Assay>(assayAccessions.length);
        for (String accession : assayAccessions) {
            assays.add(experiment.getAssay(accession));
        }
        return assays;
    }

    public int[][] getSamplesToAssays(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getSamplesToAssays();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public List<Integer> getSamplesForAssay(ArrayDesign arrayDesign, int iAssay) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getSamplesForAssay(iAssay);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public String[] getDesignElementAccessions(ArrayDesign arrayDesign) throws AtlasDataException {
        String[] array = designElementAccessions.get(arrayDesign);
        if (array == null) {
            try {
                array = getProxy(arrayDesign).getDesignElementAccessions();
                designElementAccessions.put(arrayDesign, array);
            } catch (IOException e) {
                throw new AtlasDataException(e);
            }
        }
        return array;
    }

    public long[] getGenes(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getGenes();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public List<KeyValuePair> getUniqueFactorValues(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getUniqueFactorValues();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public List<KeyValuePair> getUniqueValues(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getUniqueValues();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public String[] getFactors(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getFactors();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public String[] getCharacteristics(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getCharacteristics();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public String[] getCharacteristicValues(ArrayDesign arrayDesign, String characteristic) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getCharacteristicValues(characteristic);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public String[][] getFactorValues(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getFactorValues();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public String[] getFactorValues(ArrayDesign arrayDesign, String factor) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getFactorValues(factor);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public FloatMatrixProxy getExpressionValues(ArrayDesign arrayDesign, int[] deIndices) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getExpressionValues(deIndices);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public float[] getExpressionDataForDesignElementAtIndex(ArrayDesign arrayDesign, int designElementIndex) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getExpressionDataForDesignElementAtIndex(designElementIndex);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    /**
     * Extracts expression statistic values for given design element indices.
     *
     * @param deIndices an array of design element indices to extract expression statistic for
     * @return an instance of {@link ExpressionStatistics}
     * @throws AtlasDataException    if the data could not be read from the netCDF file
     * @throws AtlasDataException    if array of design element indices contains out of bound indices
     */
    public ExpressionStatistics getExpressionStatistics(ArrayDesign arrayDesign, int[] deIndices) throws AtlasDataException {
        try {
            return ExpressionStatistics.create(deIndices, getProxy(arrayDesign));
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    private List<ExpressionAnalysis> getAllExpressionAnalyses(ArrayDesign arrayDesign, int deIndex) throws AtlasDataException {
        return getExpressionAnalysesByFactor(arrayDesign, deIndex, null, null);
    }

    // returns
    //     list of ExpressionAnalyses for given ArrayDesign and ef/efv pair if ef is not null
    //     list of all ExpressionAnalyses for given ArrayDesign otherwise
    private List<ExpressionAnalysis> getExpressionAnalysesByFactor(ArrayDesign arrayDesign, int deIndex, String efName, String efvName) throws AtlasDataException {
        final String deAccession = getDesignElementAccessions(arrayDesign)[deIndex];
        final float[] p = getPValuesForDesignElement(arrayDesign, deIndex);
        final float[] t = getTStatisticsForDesignElement(arrayDesign, deIndex);

        final List<ExpressionAnalysis> list = new ArrayList<ExpressionAnalysis>();
        for (int efIndex = 0; efIndex < p.length; efIndex++) {
            final KeyValuePair uniqueValue = getUniqueValues(arrayDesign).get(efIndex);
            if (efName == null ||
                (uniqueValue.key.equals(efName) && uniqueValue.value.equals(efvName))) {
                list.add(new ExpressionAnalysis(
                    arrayDesign.getAccession(),
                    deAccession,
                    deIndex,
                    uniqueValue.key,
                    uniqueValue.value,
                    t[efIndex],
                    p[efIndex]
                ));
            }
        }
        return list;
    }

    /**
     * /**
     * For each gene in the keySet() of geneIdsToDEIndexes, and each efv in uniqueValues,
     * find the design element with a minPvalue and store it as an ExpressionAnalysis object in
     * geneIdsToEfToEfvToEA if the minPvalue found in this proxy is better than the one already in
     * geneIdsToEfToEfvToEA.
     *
     * @param geneIdsToDEIndexes geneId -> list of design element indexes containing data for that gene
     * @return geneId -> ef -> efv -> ea of best pValue for this geneid-ef-efv combination
     *         Note that ea contains proxyId and designElement index from which it came, so that
     *         the actual expression values can be easily retrieved later
     * @throws AtlasDataException in case of I/O errors
     */
    private Map<Long, Map<String, Map<String, ExpressionAnalysis>>> getExpressionAnalysesForDesignElementIndexes(
        ArrayDesign arrayDesign,
        Map<Long,List<Integer>> geneIdsToDEIndexes
    ) throws AtlasDataException {
        return getExpressionAnalysesForDesignElementIndexes(arrayDesign, geneIdsToDEIndexes, null, null, UpDownCondition.CONDITION_ANY);
    }

    /**
     * For each gene in the keySet() of geneIdsToDEIndexes,  and for either efVal-efvVal or (if both arguments are not null)
     * for each efv in uniqueValues, find the design element with a minPvalue and store it as an ExpressionAnalysis object in
     * geneIdsToEfToEfvToEA - if the minPvalue found in this proxy is better than the one already in
     * geneIdsToEfToEfvToEA.
     *
     * @param geneIdsToDEIndexes geneId -> list of design element indexes containing data for that gene
     * @param efVal              ef to retrieve ExpressionAnalyses for
     * @param efvVal             efv to retrieve ExpressionAnalyses for; if either efVal or efvVal are null,
     *                           ExpressionAnalyses for all ef-efvs will be retrieved
     * @param upDownCondition    desired expression; used only when efVal-efvVal are specified
     * @return geneId -> ef -> efv -> ea of best pValue for this geneid-ef-efv combination
     *         Note that ea contains proxyId and designElement index from which it came, so that
     *         the actual expression values can be easily retrieved later
     * @throws AtlasDataException in case of I/O errors
     */
    private Map<Long, Map<String, Map<String, ExpressionAnalysis>>> getExpressionAnalysesForDesignElementIndexes(
            ArrayDesign arrayDesign,
            final Map<Long, List<Integer>> geneIdsToDEIndexes,
            @Nullable final String efVal,
            @Nullable final String efvVal,
            final UpDownCondition upDownCondition
    ) throws AtlasDataException {
        final Map<Long, Map<String, Map<String, ExpressionAnalysis>>> result = new HashMap<Long, Map<String, Map<String, ExpressionAnalysis>>>();

        for (Map.Entry<Long, List<Integer>> entry : geneIdsToDEIndexes.entrySet()) {
            final Long geneId = entry.getKey();

            if (geneId == 0) continue; // skip geneid = 0

            final Map<String, Map<String, ExpressionAnalysis>> resultForGene =
                new HashMap<String, Map<String, ExpressionAnalysis>>();
            result.put(geneId, resultForGene);

            for (Integer deIndex : entry.getValue()) {
                List<ExpressionAnalysis> eaList = new ArrayList<ExpressionAnalysis>();
                if (efVal != null && efvVal != null) {
                    final List<ExpressionAnalysis> eas =
                        getExpressionAnalysesByFactor(arrayDesign, deIndex, efVal, efvVal);
                    // TODO: only 1st element of list is used. Why? -- NP
                    if (!eas.isEmpty() &&
                        upDownCondition.apply(UpDownExpression.valueOf(eas.get(0).getPValAdjusted(), eas.get(0).getTStatistic()))) {
                        eaList.add(eas.get(0));
                    }
                } else {
                    eaList.addAll(getAllExpressionAnalyses(arrayDesign, deIndex));
                }

                for (ExpressionAnalysis ea : eaList) {
                    final String ef = ea.getEfName();
                    final String efv = ea.getEfvName();

                    Map<String, ExpressionAnalysis> resultForFactor = resultForGene.get(ef);
                    if (resultForFactor == null) {
                        resultForFactor = new HashMap<String, ExpressionAnalysis>();
                        resultForGene.put(ef, resultForFactor);
                    }

                    ExpressionAnalysis prevBestPValueEA = resultForFactor.get(efv);
                    if ((prevBestPValueEA == null ||
                            // Mo stats were available in the previously seen ExpressionAnalysis
                            Float.isNaN(prevBestPValueEA.getPValAdjusted()) || Float.isNaN(prevBestPValueEA.getTStatistic()) ||
                            // Stats are available for ea, an it has a better pValue than the previous  ExpressionAnalysis
                            (!Float.isNaN(ea.getPValAdjusted()) && prevBestPValueEA.getPValAdjusted() > ea.getPValAdjusted()) ||
                            // Stats are available for ea, both pValues are equals, then the better one is the one with the higher absolute tStat
                            (!Float.isNaN(ea.getPValAdjusted()) && !Float.isNaN(ea.getTStatistic()) &&
                                    prevBestPValueEA.getPValAdjusted() == ea.getPValAdjusted() &&
                                    Math.abs(prevBestPValueEA.getTStatistic()) < Math.abs(ea.getTStatistic())))
                            ) {
                        if (ea.getPValAdjusted() > 1) {
                            // As the NA pvals/tstats  currently come back from ncdfs as 1.0E30, we convert them to Float.NaN
                            ea.setPValAdjusted(Float.NaN);
                            ea.setTStatistic(Float.NaN);

                        }
                        resultForFactor.put(efv, ea);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param ad
     * @param geneIds
     * @return Map: geneId -> List of design element indexes for ArrayDesign
     * @throws AtlasDataException
     */
    private Map<Long, List<Integer>> getGeneIdToDesignElementIndexes(ArrayDesign ad, final Collection<Long> geneIds) throws AtlasDataException {
        // Note that in a given NetCDF proxy more than one geneIndex (==designElementIndex) may correspond to one geneId
        // (i.e. proxy.getGenes() may contain duplicates, whilst proxy.getDesignElements() will not; and
        // proxy.getGenes().size() == proxy.getDesignElements().size())
        Map<Long, List<Integer>> geneIdToDEIndexes = new HashMap<Long, List<Integer>>();

        int deIndex = 0;
        for (Long geneId : getGenes(ad)) {
            if (geneIds.contains(geneId)) {
                List<Integer> deIndexes = geneIdToDEIndexes.get(geneId);
                if (deIndexes == null) {
                    deIndexes = new ArrayList<Integer>();
                }
                deIndexes.add(deIndex);
                geneIdToDEIndexes.put(geneId, deIndexes);
            }
            deIndex++;
        }
        return geneIdToDEIndexes;
    }

    /**
     * @param geneIds  ids of genes to plot
     * @param criteria other criteria to choose NetCDF to plot
     * @return geneId -> ef -> efv -> ea of best pValue for this geneid-ef-efv combination
     *         Note that ea contains arrayDesign and designElement index from which it came, so that
     *         the actual expression values can be easily retrieved later
     * @throws AtlasDataException in case of I/O errors
     */
    public Map<Long, Map<String, Map<String, ExpressionAnalysis>>> getExpressionAnalysesForGeneIds(@Nonnull Collection<Long> geneIds, @Nonnull Predicate<ArrayDesign> criteria) throws AtlasDataException {
        final ArrayDesign arrayDesign = findArrayDesign(Predicates.<ArrayDesign>and(new DataPredicates(this).containsGenes(geneIds), criteria));
        if (arrayDesign == null) {
            return null;
        }

        final Map<Long, List<Integer>> geneIdToDEIndexes = getGeneIdToDesignElementIndexes(arrayDesign, geneIds);
        return getExpressionAnalysesForDesignElementIndexes(arrayDesign, geneIdToDEIndexes);
    }

    public float[] getPValuesForDesignElement(ArrayDesign arrayDesign, int designElementIndex) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getPValuesForDesignElement(designElementIndex);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public float[] getTStatisticsForDesignElement(ArrayDesign arrayDesign, int designElementIndex) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getTStatisticsForDesignElement(designElementIndex);
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public TwoDFloatArray getAllExpressionData(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getAllExpressionData();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public TwoDFloatArray getTStatistics(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getTStatistics();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    public TwoDFloatArray getPValues(ArrayDesign arrayDesign) throws AtlasDataException {
        try {
            return getProxy(arrayDesign).getPValues();
        } catch (IOException e) {
            throw new AtlasDataException(e);
        }
    }

    /**
     * @param geneId
     * @param ef
     * @param efv
     * @param upDownCondition
     * @return best (according to expression) ExpressionAnalysis for geneId-ef-efv in experimentAccession's
     *         first proxy in which expression data for that combination exists
     */
    public ExpressionAnalysis getBestEAForGeneEfEfvInExperiment(Long geneId, String ef, String efv, UpDownCondition upDownCondition) {
        ExpressionAnalysis ea = null;
        try {
            final Collection<ArrayDesign> ads = experiment.getArrayDesigns();
            for (ArrayDesign ad : ads) {
                if (ea == null) {
                    Map<Long, List<Integer>> geneIdToDEIndexes = getGeneIdToDesignElementIndexes(ad, Collections.singleton(geneId));
                    Map<Long, Map<String, Map<String, ExpressionAnalysis>>> geneIdsToEfToEfvToEA =
                            getExpressionAnalysesForDesignElementIndexes(ad, geneIdToDEIndexes, ef, efv, upDownCondition);
                    if (geneIdsToEfToEfvToEA.containsKey(geneId) &&
                            geneIdsToEfToEfvToEA.get(geneId).containsKey(ef) &&
                            geneIdsToEfToEfvToEA.get(geneId).get(ef).containsKey(efv) &&

                            geneIdsToEfToEfvToEA.get(geneId).get(ef).get(efv) != null) {
                        ea = geneIdsToEfToEfvToEA.get(geneId).get(ef).get(efv);
                    }

                }
            }
        } catch (AtlasDataException e) {
            log.error("Failed to ExpressionAnalysis for gene id: " + geneId + "; ef: " + ef + " ; efv: " + efv + " in experiment: " + experiment);
        }
        return ea;
    }

    /**
     * @param arrayDesign
     * @param geneId
     * @param ef
     * @return Map: efv -> best ExpressionAnalysis for geneid-ef in this proxy
     * @throws AtlasDataException
     */
    public Map<String, ExpressionAnalysis> getBestEAsPerEfvInProxy(ArrayDesign arrayDesign, Long geneId, String ef) throws AtlasDataException {
        Map<Long, List<Integer>> geneIdToDEIndexes = getGeneIdToDesignElementIndexes(arrayDesign, Collections.singleton(geneId));
        Map<Long, Map<String, Map<String, ExpressionAnalysis>>> geneIdsToEfToEfvToEA =
                getExpressionAnalysesForDesignElementIndexes(arrayDesign, geneIdToDEIndexes);
        return geneIdsToEfToEfvToEA.get(geneId).get(ef);
    }

    public String getPathForR(ArrayDesign arrayDesign) {
        return atlasDataDAO.getNetCDFLocation(experiment, arrayDesign).getAbsolutePath();
    }

    public void close() {
        for (NetCDFProxy p : proxies.values()) {
            Closeables.closeQuietly(p);
        }
        proxies.clear();
    }
}
