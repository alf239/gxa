package uk.ac.ebi.arrayexpress2.magetab.handler.idf.impl;

import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.handler.idf.AbstractIDFHandler;

/**
 * A handler implementation that will handle Protocol Software fields in the
 * IDF.
 * <p/>
 * Tag: Protocol Software
 *
 * @author Tony Burdett
 * @date 21-Jan-2009
 */
public class ProtocolSoftwareHandler extends AbstractIDFHandler {
  public ProtocolSoftwareHandler() {
    setTag("protocolsoftware");
  }

  public void readValue(String value) throws ParseException {
    // read
    investigation.IDF.protocolSoftware.add(value);
  }
}
