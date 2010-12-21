package uk.ac.ebi.arrayexpress2.magetab.handler.idf.impl;

import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.handler.idf.AbstractIDFHandler;

/**
 * A handler implementation that will handle Person Roles fields in the IDF.
 * <p/>
 * Tag: Person Roles
 *
 * @author Tony Burdett
 * @date 21-Jan-2009
 */
public class PersonRolesHandler extends AbstractIDFHandler {
  public PersonRolesHandler() {
    setTag("personroles");
  }

  public void readValue(String value) throws ParseException {
    // read
    investigation.IDF.personRoles.add(value);
  }
}