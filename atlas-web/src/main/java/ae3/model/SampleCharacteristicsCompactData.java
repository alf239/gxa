package ae3.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rpetry
 * Date: Oct 22, 2010
 * Time: 9:23:56 AM
 * Class representing mapping between sample characteristic values and assays, for a single sample characteristic
 */
public class SampleCharacteristicsCompactData {
    // sample characteristic name
    private String name;
    // unique values corresponding to this class' sample characteristic
    private List<String> uniqueScvs = new ArrayList<String>();
    // Map sample index -> position of this class' sample characteristic' value in uniqueScvs
    // N.B. Assumption: a given sample will have at most one value per sample characteristic
    private Map<Integer, Integer> sampleIndexToScvPos = new HashMap<Integer, Integer>();
    // This array is indexed by assayIndex and contains a position of an scv in uniqueScvs that the corresponding assay contains
    private int[] assayScvs;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @param name        sample characteristic name
     * @param numOfAssays Number of assays in the array design for which this data is being assembled
     */
    public SampleCharacteristicsCompactData(String name, Integer numOfAssays) {
        this.name = name;
        this.assayScvs = new int[numOfAssays];
        uniqueScvs.add(""); // Make sure that an empty value always has pos 0 in scvs, hence corresponds to an default initialised value 0 in int array, assayScvs
    }

    /**
     * @return sample characteristic name
     */
    public String getName() {
        return name;
    }

    /**
     * @return unique values corresponding to this class' sample characteristic
     */
    public List<String> getScvs() {
        return uniqueScvs;
    }

    /**
     * @return RLE-encoded List of scv positions in uniqueScvs - when this list is decoded, contents of each (assay)
     *         index in this list determines what scv is contained in that assay
     */
    public List<String> getAssayEfvsRLE() {
        List<String> assayScvsRLE = new ArrayList<String>();
        Integer cnt = 0;
        int prev = assayScvs[0];
        for (int efvPos : assayScvs) {
            if (efvPos != prev) {
                assayScvsRLE.add(prev + ":" + cnt);
                cnt = 0;
                prev = efvPos;
            }
            cnt++;
        }
        if (cnt > 0) {
            assayScvsRLE.add(prev + ":" + cnt);
        }

        return assayScvsRLE;
    }

    /**
     * Add scv from a Sample at sampleIndex
     *
     * @param scv
     * @param sampleIndex
     */
    public void addScv(String scv, Integer sampleIndex) {
        if (!uniqueScvs.contains(scv)) {
            uniqueScvs.add(scv);
        }

        if (sampleIndexToScvPos.get(sampleIndex) != null) {
            String errMsg = "Sample Index: " + sampleIndex + " already contains an scv: " + sampleIndexToScvPos.get(sampleIndex) + " for sc: " + name + " (trying to insert a new scv: " + scv + ")";
            log.error(errMsg);
            throw new RuntimeException(errMsg);
        }
        sampleIndexToScvPos.put(sampleIndex, uniqueScvs.indexOf(scv));
    }

    /**
     * Add mapping between sampleIndex and assayIndex - if this class contains any scvs from that sampleIndex
     *
     * @param sampleIndex
     * @param assayIndex
     */
    public void addMapping(Integer sampleIndex, Integer assayIndex) {
        Integer scvPos = sampleIndexToScvPos.get(sampleIndex);
        if (scvPos != null) {
            assayScvs[assayIndex] = scvPos;
        }
    }

}