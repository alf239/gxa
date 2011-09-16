/**
 * Checking the starting point
 *
 * We assume that the new updates are rolled on top of 2.0.9-compatible database.
 * In order to do that, we use this script. It will either
 * <ul>
 *     <li>
 *         be run as a new, unknown one -
 *         and in this case it will check that DB is indeed 2.0.9 as expected, or
 *     </li>
 *     <li>
 *         won't run as MigrationManager has already run it - and in this case we know we're in post-2.0.9 world
 *         and can rely on MigrationManager to proceed with further updates
 *     </li>
 * </ul>
 *
 * This script is also an example of migration using Groovy.
 *
 * For more example on SQL usage in Groovy, check out
 * <a href="http://groovy.codehaus.org/Tutorial+6+-+Groovy+SQL">Groovy Tutorial</a>
 */

import groovy.sql.Sql
import org.slf4j.LoggerFactory

def log = LoggerFactory.getLogger(getClass())

private def checkTableExists(sql, String tableName) {
    sql.execute("select * from " + tableName)
}

def sql = Sql.newInstance(connection)

log.debug("checkTableExists(a2_schemachanges)")
checkTableExists(sql, "a2_schemachanges")

log.debug("drop old table-tracking table")
sql.execute("drop table a2_schemachanges")