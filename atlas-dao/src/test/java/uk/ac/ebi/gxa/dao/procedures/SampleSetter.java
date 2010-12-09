/*
 * Copyright 2008-2010 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * For further details of the Gene Expression Atlas project, including source code,
 * downloads and documentation, please see:
 *
 * http://gxa.github.com/gxa
 */

package uk.ac.ebi.gxa.dao.procedures;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A simple test class designed for setting up a stored procedure in the
 * in-memory hypersonic database.  This stored procedure ultimately mimics the
 * A2_SAMPLESET procedure that should be found in the "real" atlas2 database
 *
 * @author Tony Burdett
 * @date 08-Oct-2009
 */
public class SampleSetter {
    public static void call(Connection conn,
                            String experimentAccession,
                            String sampleAccession,
                            Object assays, Object properties,
                            String channel) throws SQLException {
        // this mimics the stored procedure A2_SAMPLESET in the actual DB
        Statement stmt = conn.createStatement();

        // create an sampleid - no oracle id generators here!
        long sampleid = (long) System.currentTimeMillis();

        stmt.executeQuery(
                "INSERT INTO A2_SAMPLE(sampleid, accession, channel) " +
                        "values (" + sampleid + ", '" + sampleAccession +
                        "', '" + channel + "');");
    }
}