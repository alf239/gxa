package uk.ac.ebi.arrayexpress2.magetab.handler.idf.impl;

import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.handler.idf.AbstractIDFHandler;

/**
 * A handler implementation that will handle Publication DOI Handler fields in
 * the IDF.
 * <p/>
 * Tag: Publication DOI
 *
 * @author Tony Burdett
 * @date 21-Jan-2009
 */
public class PublicationDoiHandler extends AbstractIDFHandler {
  public PublicationDoiHandler() {
    setTag("publicationdoi");
  }

  public void readValue(String value) throws ParseException {
    // read
    investigation.IDF.publicationDOI.add(value);
  }
}