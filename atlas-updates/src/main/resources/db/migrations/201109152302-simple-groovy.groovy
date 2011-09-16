/**
 * This script is an example of migration using Groovy
 *
 * For more example on SQL usage in Groovy, check out
 * <a href="http://groovy.codehaus.org/Tutorial+6+-+Groovy+SQL">Groovy Tutorial</a>
 */

import groovy.sql.Sql
import org.codehaus.groovy.reflection.ReflectionUtils
import org.slf4j.LoggerFactory

def log = LoggerFactory.getLogger(getClass())

def getResource = {def resource -> ReflectionUtils.getCallingClass(0).getResourceAsStream(resource)}

def properties = new Properties()
properties.load(getResource("atlas.properties"))
properties.load(getResource("Curated.properties"))

def sql = Sql.newInstance(connection: connection)

sql.execute("alter table a2_property add displayname varchar2(512)")

for (p in properties) {
    def matcher = p.key =~ /factor\.curatedname\.(\w+)/
    if (matcher.matches()) {
        def factor = matcher[0][1]
        log.debug("update a2_property set displayname=${p.value} where name=${factor}")
        sql.execute("update a2_property set displayname=? where name=?", [p.value, factor])
    }
}
