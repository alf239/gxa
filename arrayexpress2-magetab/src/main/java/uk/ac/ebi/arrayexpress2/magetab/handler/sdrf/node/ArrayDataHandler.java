package uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.node;

import org.mged.magetab.error.ErrorCode;
import org.mged.magetab.error.ErrorItem;
import org.mged.magetab.error.ErrorItemFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ArrayDataNode;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.exception.UnmatchedTagException;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.AbstractSDRFHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.SDRFDatatypeHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.node.attribute.ArrayDesignFileHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.node.attribute.ArrayDesignHandler;
import uk.ac.ebi.arrayexpress2.magetab.handler.sdrf.node.attribute.ArrayDesignRefHandler;

/**
 * A handler that handles array data nodes in the SDRF graph
 * <p/>
 * Tag: Array Data File <br/>Allowed Attributes: Array Design File/Ref, Comment
 *
 * @author Tony Burdett
 * @date 22-Jan-2009
 */
public class ArrayDataHandler extends AbstractSDRFHandler
    implements SDRFDatatypeHandler {
  public ArrayDataHandler() {
    setTag("arraydatafile");
  }

  public int assess() {
    for (int i = 1; i < values.length;) {
      if (headers[i].startsWith("comment")) {
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
        // this is either the end, or a bad column name
        return i;
      }
      i++;
    }

    // iterated over every column, so must have reached the end
    return values.length;
  }

  protected void readValues() throws ParseException {
    // find the SourceNode to modify
    ArrayDataNode adf;

    if (headers[0].equals(tag)) {
      // first row, so lookup or make a new source
      synchronized (investigation.SDRF) {
        adf = investigation.SDRF.lookupNode(values[0], ArrayDataNode.class);
        if (adf == null) {
          adf = new ArrayDataNode();
          adf.setNodeType(headers[0]);
          adf.setNodeName(values[0]);
          investigation.SDRF.storeNode(adf);
          addNextNodeForCompilation(adf);
        }
      }
      // now do the rest
      boolean emptyHeaderSkipped = false;
      for (int i = 1; i < values.length;) {
        if (headers[i].startsWith("comment")) {
          String type = headers[i].substring(headers[i].lastIndexOf("[") + 1,
                                             headers[i].lastIndexOf("]"));
          adf.comments.put(type, values[i]);
        }
        else if (headers[i].startsWith("arraydesign")) {
          ArrayDesignHandler handler;
          if (headers[i].endsWith("file")) {
            handler = new ArrayDesignFileHandler();
          }
          else {
            handler = new ArrayDesignRefHandler();
          }
          i += handleAttribute(adf, handler, headers, values, i);
        }
        else if (headers[i].equals("")) {
          // skip the case where the header is an empty string
          emptyHeaderSkipped = true;
        }
        else {
          // got to something we don't recognise
          // this is either the end, or a bad column name
          // check the name of the next node and put it in this one
          // but be wary of Factor Value nodes
          int j = i;
          while (j < headers.length &&
              (headers[j].startsWith("factorvalue") ||
                  headers[j].startsWith("unit") ||
                  headers[j].startsWith("termsourceref") ||
                  headers[j].startsWith("comment"))) {
            // loop over every factor value column
            j++;
          }
          i = j;

          if (i < headers.length) {
            // update the child node
            updateChildNode(adf, i);
            break;
          }
        }
        i++;
      }

      // iterated over every column, so must have reached the end
      // update node in SDRF
      investigation.SDRF.updateNode(adf);

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

  public String getDatatypeName() {
    return "rawData";
  }
}