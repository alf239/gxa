package uk.ac.ebi.gxa.loader;

import uk.ac.ebi.gxa.loader.dao.LoaderDAO;
import uk.ac.ebi.gxa.utils.Pair;
import uk.ac.ebi.microarray.atlas.model.*;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static uk.ac.ebi.microarray.atlas.model.PropertyName.createProperty;

public class MockFactory {
    public static LoaderDAO createLoaderDAO() {
        return new MockLoaderDAO();
    }

    static class MockLoaderDAO extends LoaderDAO {
        public MockLoaderDAO() {
            super(null, null, null, null, null);
        }

        private Map<String, Organism> os = newHashMap();
        private Map<String, PropertyName> ps = newHashMap();
        private Map<String, PropertyValue> pvs = newHashMap();
        private Map<Pair<String, String>, Property> properties = newHashMap();
        private Map<String, ArrayDesign> ads = newHashMap();

        @Override
        public Property getOrCreatePropertyValue(String name, String value) {
            Property result = properties.get(Pair.create(name, value));
            if (result == null) {
                PropertyName p = ps.get(name);
                if (p == null) {
                    ps.put(name, p = createProperty(name));
                }
                PropertyValue pv = pvs.get(name);
                if (pv == null) {
                    pvs.put(name, pv = new PropertyValue(value));
                }
                properties.put(Pair.create(name, value), result = new Property(p, pv));
            }
            return result;
        }

        @Override
        public ArrayDesign getArrayDesignShallow(String accession) {
            ArrayDesign ad = ads.get(accession);
            if (ad == null) {
                ads.put(accession, ad = new ArrayDesign(accession));
            }
            return ad;
        }

        @Override
        public Organism getOrCreateOrganism(String name) {
            Organism o = os.get(name);
            if (o == null) {
                os.put(name, o = new Organism(null, name));
            }
            return o;
        }
    }
}
