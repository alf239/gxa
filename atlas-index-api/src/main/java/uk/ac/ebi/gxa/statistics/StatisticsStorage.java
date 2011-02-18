package uk.ac.ebi.gxa.statistics;

import com.google.common.collect.Multiset;
import it.uniroma3.mat.extendedset.ConciseSet;

import java.io.Serializable;
import java.util.*;

/**
 * Class encapsulating bit storage of all statistics in StatisticType enum
 */
public class StatisticsStorage<GeneIdType> implements Serializable {
    private static final long serialVersionUID = -6912773133413719982L;

    // Map: StatisticsType -> Statistics (Statistics class contains experiment counts for indexes in geneIndex, in experiments in experimentIndex
    // and attributes in attributeIndex (see below))
    Map<StatisticsType, Statistics> stats = new EnumMap<StatisticsType, Statistics>(StatisticsType.class);

    // Index mapping Long gene ids to (ConciseSet-storable) Integer values
    private ObjectIndex<GeneIdType> geneIndex;
    // Index mapping Experiment objects to unique Integer values - to reduce space consumption by each Statistics object
    private ObjectIndex<Experiment> experimentIndex;
    // Index mapping Attributes to unique Integer values - to reduce space consumption by each Statistics object
    private ObjectIndex<Attribute> attributeIndex;
    // Map efo term -> Experiment index -> Set<Attribute Index>
    // Map Attribute index -> Experiment Index -> efo term
    private EfoIndex efoIndex;


    // Setter methods

    public void addStatistics(StatisticsType statisticsType, Statistics stats) {
        this.stats.put(statisticsType, stats);
    }

    public void setExperimentIndex(ObjectIndex<Experiment> experimentIndex) {
        this.experimentIndex = experimentIndex;
    }

    public void setGeneIndex(ObjectIndex<GeneIdType> objectIndex) {
        this.geneIndex = objectIndex;
    }

    public void setAttributeIndex(ObjectIndex<Attribute> objectIndex) {
        this.attributeIndex = objectIndex;
    }

    public void setEfoIndex(EfoIndex efoIndex) {
        this.efoIndex = efoIndex;
    }

    public void setScoresAcrossAllEfos(Multiset<Integer> scores, StatisticsType statType) {
        stats.get(statType).setScoresAcrossAllEfos(scores);
    }


    // Experiment-related getter methods

    public Experiment getExperimentForIndex(Integer index) {
        return experimentIndex.getObjectForIndex(index);
    }

    Collection<Experiment> getExperimentsForIndexes(Collection<Integer> indexes) {
        return experimentIndex.getObjectsForIndexes(indexes);
    }

    public Integer getIndexForExperiment(Experiment experiment) {
        return experimentIndex.getIndexForObject(experiment);
    }

    // Gene-related getter methods

    public GeneIdType getGeneIdForIndex(Integer index) {
        return geneIndex.getObjectForIndex(index);
    }

    ConciseSet getIndexesForGeneIds(Collection<GeneIdType> geneIds) {
        return geneIndex.getIndexesForObjects(geneIds);
    }

    public Integer getIndexForGeneId(GeneIdType geneId) {
        return geneIndex.getIndexForObject(geneId);
    }

    // Attribute-related getter methods

    public Attribute getAttributeForIndex(Integer index) {
        return attributeIndex.getObjectForIndex(index);
    }

    public Integer getIndexForAttribute(Attribute attribute) {
        return attributeIndex.getIndexForObject(attribute);
    }

    public Map<Integer, ConciseSet> getStatisticsForAttribute(Integer attributeIndex, StatisticsType statType) {
        return stats.get(statType).getStatisticsForAttribute(attributeIndex);
    }

    /**
     * Delegates call to Statistics object corresponding to statType
     * @param attributeIndex
     * @param geneIndex
     * @param statType
     * @return Set of indexes of experiments with non-zero statType counts for attributeIndex-geneIndex tuple
     */
    public Set<Integer> getExperimentsForGeneAndAttribute(Integer attributeIndex, Integer geneIndex, StatisticsType statType) {
         return stats.get(statType).getExperimentsForGeneAndAttribute(attributeIndex, geneIndex);
    }

    /**
     * Delegates call to Statistics object corresponding to statType
     * @param geneIdx
     * @param statType
     * @return Set of Ef-only attribute indexes that have statType up/down experiment counts for geneIdx
     */
    public Set<Integer> getScoringEfAttributesForGene(final Integer geneIdx,
                                             final StatisticsType statType) {
        return stats.get(statType).getScoringEfAttributesForGene(geneIdx);
    }

        /**
     * Delegates call to Statistics object corresponding to statType
     * @param geneIdx
     * @param statType
     * @return Set of Ef-only attribute indexes that have statType up/down experiment counts for geneIdx
     */
    public Set<Integer> getScoringEfvAttributesForGene(final Integer geneIdx,
                                             final StatisticsType statType) {
        return stats.get(statType).getScoringEfvAttributesForGene(geneIdx);
    }


    // Efo-related getter methods

    public Map<Integer, Set<Integer>> getMappingsForEfo(String efoTerm) {
        return efoIndex.getMappingsForEfo(efoTerm);
    }

    public Set<String> getEfos() {
        return efoIndex.getEfos();
    }

    public Multiset<Integer> getScoresAcrossAllEfos(StatisticsType statType) {
        return stats.get(statType).getScoresAcrossAllEfos();
    }

    /**
     * @param statType
     * @return Set of attributes or which experiment counts exist for statType
     */
    public Set<Attribute> getAllAttributes(StatisticsType statType) {
        Set<Attribute> attributes = new HashSet<Attribute>();
        Set<Integer> attrIndexes = stats.get(statType).getAttributes();
        for (Integer attrIndex : attrIndexes) {
            attributes.add(getAttributeForIndex(attrIndex));
        }
        return attributes;
    }

    /**
     * @param attr
     * @param exp
     * @return efo term which maps to attr and exp
     */
    public String getEfoTerm(Attribute attr, Experiment exp) {
        return efoIndex.getEfoTerm(getIndexForAttribute(attr), getIndexForExperiment(exp));
    }

    /**
     *
     * @param attributeIndex
     * @param statType
     * @return pValue/tStat rank -> Experiment index -> ConciseSet of gene indexes, corresponding to attributeIndex and statType
     */
    public SortedMap<PvalTstatRank, Map<Integer, ConciseSet>> getPvalsTStatRanksForAttribute(Integer attributeIndex, StatisticsType statType) {
        return stats.get(statType).getPvalsTStatRanksForAttribute(attributeIndex);
    }
}
