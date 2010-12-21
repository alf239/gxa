package uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.node;

import org.mged.magetab.error.ErrorCode;
import org.mged.magetab.error.ErrorItem;
import org.mged.magetab.error.ErrorItemFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SourceNode;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.exception.UnmatchedTagException;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.AbstractSDRFHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.SDRFBiomaterialHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.node.attribute.*;

/**
 * A handler that handles Source nodes in the SDRF graph
 * <p/>
 * Tag: Source Name <br/>Allowed attributes: Characteristics, Provider, Material
 * Type, Description, Comment, Array Design File/Ref
 *
 * @author Tony Burdett
 * @date 22-Jan-2009
 */
public class SourceHandler extends AbstractSDRFHandler
    implements SDRFBiomaterialHandler {
  public SourceHandler() {
    setTag("sourcename");
  }

  public int assess() {
    for (int i = 1; i < values.length;) {
      if (headers[i].startsWith("characteristics")) {
        CharacteristicsHandler handler = new CharacteristicsHandler();
        i += assessAttribute(handler, headers, values, i);
      }
      else if (headers[i].equals("provider")) {
        ProviderHandler handler = new ProviderHandler();
        i += assessAttribute(handler, headers, values, i);
      }
      else if (headers[i].equals("materialtype")) {
        MaterialTypeHandler handler = new MaterialTypeHandler();
        i += assessAttribute(handler, headers, values, i);
      }
      else if (headers[i].equals("description")) {
        // don't need to do anything here
      }
      else if (headers[i].startsWith("comment")) {
        // don't need to do anything here
      }
      else if (headers[i].startsWith("arraydesign")) {
        ArrayDesignHandler handler;
        if (headers[i].endsWith("file")) {
          handler = new ArrayDesignFileHandler();
        }
        else {
          handler = new ArrayDesignRefHandler();
        }
        i += assessAttribute(handler, headers, values, i);
      }
      else if (headers[i].equals("")) {
        // skip the case where the header is an empty string
      }
      else {
        // got to something we don't recognise
        // this is either the end, or a non-handled column name
        return i;
      }
      i++;
    }

    // iterated over every column, so must have reached the end
    return values.length;
  }

  public void readValues() throws ParseException {
    // find the SourceNode to modify
    SourceNode source;

    if (headers[0].equals(tag)) {
      getLog().debug("Next source: " + values[0]);

      // first values, so lookup or make a new sourceNode
      synchronized (investigation.SDRF) {
        source = investigation.SDRF.lookupNode(values[0], SourceNode.class);
        if (source == null) {
          source = new SourceNode();
          source.setNodeType(headers[0]);
          source.setNodeName(values[0]);
          investigation.SDRF.storeNode(source);
          addNextNodeForCompilation(source);
        }
      }

      // now do the rest
      boolean emptyHeaderSkipped = false;
      for (int i = 1; i < values.length;) {
        if (headers[i].startsWith("characteristics")) {
          CharacteristicsHandler handler = new CharacteristicsHandler();
          i += handleAttribute(source, handler, headers, values, i);
        }
        else if (headers[i].equals("provider")) {
          ProviderHandler handler = new ProviderHandler();
          i += handleAttribute(source, handler, headers, values, i);
        }
        else if (headers[i].equals("materialtype")) {
          MaterialTypeHandler handler = new MaterialTypeHandler();
          i += handleAttribute(source, handler, headers, values, i);
        }
        else if (headers[i].equals("description")) {
          source.description = values[i];
        }
        else if (headers[i].startsWith("comment")) {
          String type =
              headers[i].substring(headers[i].lastIndexOf("[") + 1,
                                   headers[i].lastIndexOf("]"));
          source.comments.put(type, values[i]);
        }
        else if (headers[i].startsWith("arraydesign")) {
          ArrayDesignHandler handler;
          if (headers[i].endsWith("file")) {
            handler = new ArrayDesignFileHandler();
          }
          else {
            handler = new ArrayDesignRefHandler();
          }
          i += handleAttribute(source, handler, headers, values, i);
        }
        else if (headers[i].equals("")) {
          // skip the case where the header is an empty string
          emptyHeaderSkipped = true;
        }
        else {
          // got to something we don't recognise
          // this is either the end, or a bad column name
          // update the child node
          updateChildNode(source, i);
          break;
        }
        i++;
      }
      // iterated over every column, so must have reached the end
      // update node in SDRF
      investigation.SDRF.updateNode(source);

      // throw exception if we had to skip something
      if (emptyHeaderSkipped) {
        String message =
            "One or more columns with empty headers were detected " +
                "and skipped";

        ErrorItem error =
            ErrorItemFactory.getErrorItemFactory(getClass().getClassLoader())
                .generateErrorItem(
                    message,
                    ErrorCode.UNKNOWN_SDRF_HEADING,
                    this.getClass());

        throw new UnmatchedTagException(error, false, message);
      }
    }
    else {
      String message =
          "This handler starts at tag: " + tag + ", not " + headers[0];

      ErrorItem error =
          ErrorItemFactory.getErrorItemFactory(getClass().getClassLoader())
              .generateErrorItem(
                  message,
                  ErrorCode.UNKNOWN_SDRF_HEADING,
                  this.getClass());

      throw new UnmatchedTagException(error, false, message);
    }
  }

  public String getMaterialName() {
    return "source";
  }
}