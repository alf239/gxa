package uk.ac.ebi.gxa.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.SQLException;

/**
 * @author alf
 */
class OracleScriptSplitter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    void parse(Reader reader, SqlStatementExecutor executor) throws SQLException, IOException {
        StringBuilder sqlBuffer = null;
        try {
            boolean plsqlMode = false;
            LineNumberReader lineReader = new LineNumberReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (sqlBuffer == null) {
                    sqlBuffer = new StringBuilder();
                }

                line = line.trim();

                if (line.length() < 1) {
                    // Do nothing, it's an empty line.
                } else if (line.startsWith("--") || line.startsWith("#") || line.startsWith("//")) {
                    logger.debug(line);
                } else if (line.matches("[/.]")) {
                    /*
                   http://download.oracle.com/docs/cd/B19306_01/server.102/b14357/ch4.htm#i1039663
                   Terminate PL/SQL subprograms by entering a period (.) by itself on a new line.
                   You can also terminate and execute a PL/SQL subprogram by entering a slash (/) by itself on a new line.
                    */
                    executor.executeStatement(sqlBuffer.toString());
                    plsqlMode = false;
                    sqlBuffer = null;
                } else if (!plsqlMode && line.toLowerCase().matches("(" +
                        "begin|" +
                        "declare|" +
                        "create(\\s+or\\s+replace)?" +
                        "\\s" +
                        "(function|library|package(\\s+body)?|procedure|trigger|type)" +
                        "\\s+" +
                        "(\\S+|\"[^\"]+\")" +
                        ")" +
                        "(\\s+.*|$)")) {
                    plsqlMode = true;
                    sqlBuffer.append(line);
                    sqlBuffer.append("\n");
                } else if (!plsqlMode && line.endsWith(";")) {
                    sqlBuffer.append(line.substring(0, line.lastIndexOf(";")));
                    executor.executeStatement(sqlBuffer.toString());
                    sqlBuffer = null;
                } else {
                    sqlBuffer.append(line);
                    sqlBuffer.append("\n");
                }
            }

            // Check to see if we have an unexecuted statement in command.
            if (sqlBuffer != null && sqlBuffer.length() > 0) {
                logger.info("Last statement in script is missing a terminating delimiter, executing anyway.");
                executor.executeStatement(sqlBuffer.toString());
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
            logger.error("Error executing: " + sqlBuffer, e);
            throw e;
        } catch (IOException e) {
            e.fillInStackTrace();
            logger.error("Error executing: " + sqlBuffer, e);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error running script.  Cause: " + e, e);
        }
    }
}
