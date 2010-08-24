package uk.ac.ebi.arrayexpress2.magetab.handler.idf.impl;

import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.handler.idf.AbstractIDFHandler;

/**
 * A handler implementation that will handle Normalization Term Source REF
 * fields in the IDF.
 * <p/>
 * Tag: Normalization Term Source Ref
 *
 * @author Tony Burdett
 * @date 21-Jan-2009
 */
public class NormalizationTermSourceRefHandler extends AbstractIDFHandler {
  public NormalizationTermSourceRefHandler() {
    setTag("normalizationtermsourceref");
  }

  public void readValue(String value) throws ParseException {
    // read
    investigation.IDF.normalizationTermSourceREF.add(value);
  }
}
