package uk.ac.ebi.gxa.netcdf.generator.helper;

import uk.ac.ebi.gxa.dao.AtlasDAOTestCase;
import uk.ac.ebi.microarray.atlas.model.Assay;
import uk.ac.ebi.microarray.atlas.model.Experiment;
import uk.ac.ebi.microarray.atlas.model.Sample;
import uk.ac.ebi.gxa.netcdf.generator.helper.DataSlice;
import uk.ac.ebi.gxa.netcdf.generator.helper.DataSlicer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Javadocs go here.
 *
 * @author Junit Generation Plugin for Maven, written by Tony Burdett
 * @date 07-10-2009
 */
public class TestDataSlicer extends AtlasDAOTestCase {
  private DataSlicer dataSlicer;

  private Experiment experiment;


  public void setUp() throws Exception {
    super.setUp();

    dataSlicer = new DataSlicer(getAtlasDAO());
    experiment = getAtlasDAO().getExperimentByAccession("E-ABCD-1234");
  }

  public void tearDown() throws Exception {
    super.tearDown();

    dataSlicer = null;
    experiment = null;
  }

  public void testSliceExperiment() {
    try {
      // slice up experiment
      Set<DataSlice> slices = dataSlicer.sliceExperiment(experiment);

      // check that the set is non-null and that there are not 0 slices
      assertNotNull("Set of dataslices is null", slices);
      assertNotSame("Should be at least one dataslice", slices.size(), 0);

      // todo - do profiling on slices, check against dataset, rather than just dump to system out
      for (DataSlice slice : slices) {
        System.out.println(
            "Next slice... exp:" + slice.getExperiment().getAccession() +
                " array:" + slice.getArrayDesign().getAccession());

        System.out.println("\tAssays...");
        for (Assay ass : slice.getAssays()) {
          System.out.println("\t\t" + ass.toString());
        }
        System.out.println("\tSamples...");
        for (Sample sample : slice.getSamples()) {
          System.out.println("\t\t" + sample.toString());
        }
        System.out.println("\tDesign Element ids");
        for (int designElementID : slice.getDesignElements().keySet()) {
          System.out.println("\t\t" + designElementID);
        }
        System.out.println("\tAssay -> Sample indexes");
        for (Map.Entry<Assay, List<Sample>> entry : slice
            .getSampleMappings().entrySet()) {
          System.out.println("\t\tassay = " + entry.getKey().getAccession());
          for (Sample sample : entry.getValue()) {
            System.out.println("\t\t\t -> sample =  " + sample.getAccession());
          }
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
}
