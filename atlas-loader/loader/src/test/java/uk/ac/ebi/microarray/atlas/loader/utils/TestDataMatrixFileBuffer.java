package uk.ac.ebi.microarray.atlas.loader.utils;

import junit.framework.TestCase;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.microarray.atlas.loader.model.ExpressionValue;

import java.net.URL;
import java.util.Set;

/**
 * This tests the DataMatrixFileBuffer class.  This does not implement TestCase
 * because junit doesn't like running multithreaded tests and the init() method
 * requires it.
 *
 * @author Tony Burdett
 * @date 03-Sep-2009
 */
public class TestDataMatrixFileBuffer extends TestCase {
  private URL dataMatrixURL;
  private String assayRef;

  protected void setUp() throws Exception {
    dataMatrixURL = this.getClass().getClassLoader().getResource(
        "E-GEOD-3790-sample-processed-data.txt");
    assayRef = "HC52 CN B";
  }

  protected void tearDown() throws Exception {
    dataMatrixURL = null;
    assayRef = null;
  }

  public void testReadEVs() {
    try {
      DataMatrixFileBuffer buffer =
          DataMatrixFileBuffer.getDataMatrixFileBuffer(dataMatrixURL);

      long startTime = System.currentTimeMillis();
      Set<ExpressionValue> evs = buffer.readAssayExpressionValues(assayRef);
      long endTime = System.currentTimeMillis();

      long readOnceTime = endTime - startTime;

      System.out.println("Reading took: " + readOnceTime + "ms.");

//      System.out.println("First 100 expression values read...");
//      int i = 0;
//      for (ExpressionValue ev : evs) {
//        System.out
//            .println(ev.getDesignElementAccession() + ":" + ev.getValue());
//        i++;
//        if (i > 100) {
//          break;
//        }
//      }

      assertTrue("Read zero expression values", evs.size() > 0);
    }
    catch (ParseException e) {
      System.err.println(e.getErrorItem().getComment());
      e.printStackTrace();
      fail();
    }
  }

  public void testRepeatReads() {
    try {
      DataMatrixFileBuffer buffer =
          DataMatrixFileBuffer.getDataMatrixFileBuffer(dataMatrixURL);

      // repeat reads
      for (int i = 0; i < 100; i++) {
        long startTime = System.currentTimeMillis();
        buffer.readAssayExpressionValues(assayRef);
        long endTime = System.currentTimeMillis();

        long repeatTime = endTime - startTime;

        System.out.println(
            "Repeat read number " + i + " took: " + repeatTime + "ms.");
        assertTrue(
            "Repeat read number " + i +
                " took longer than 5ms, just to return reference?",
            repeatTime < 5);
      }
    }
    catch (ParseException e) {
      System.err.println(e.getErrorItem().getComment());
      e.printStackTrace();
      fail();
    }
  }
}
