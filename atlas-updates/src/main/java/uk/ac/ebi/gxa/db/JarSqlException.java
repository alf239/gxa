/*
 * Copyright 2008-2011 Microarray Informatics Team, EMBL-European Bioinformatics Institute
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

package uk.ac.ebi.gxa.db;

import com.carbonfive.db.jdbc.DatabaseType;
import com.carbonfive.db.migration.AbstractMigration;
import com.carbonfive.db.migration.MigrationException;
import com.google.common.io.Closeables;
import org.apache.commons.lang.Validate;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author alf
 */
public class JarSqlException extends AbstractMigration {
    private final Resource resource;

    public JarSqlException(String version, Resource resource) {
        super(version, resource.getFilename());
        this.resource = resource;
    }

    @Override
    public void migrate(DatabaseType dbType, Connection connection) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(resource.getInputStream());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".sql")) {
                    OracleScriptRunner scriptRunner = new OracleScriptRunner();
                    scriptRunner.execute(connection, new InputStreamReader(zis, "UTF-8"));
                    Validate.isTrue(!connection.isClosed(), "JDBC Connection should not be closed.");
                } else {
                    if (!entry.getName().matches("/?META-INF/.*"))
                        throw new MigrationException("Unexpected jar entry: " + entry.getName());
                }
            }
        } catch (FileNotFoundException e) {
            throw new MigrationException("Script not found.", e);
        } catch (IOException e) {
            throw new MigrationException("Error while reading script input stream.", e);
        } catch (SQLException e) {
            throw new MigrationException("SQL Error while executing the script.", e);
        } finally {
            Closeables.closeQuietly(zis);
        }
    }
}
