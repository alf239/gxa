package ae3.util;

import org.apache.solr.common.SolrDocument;
import org.apache.commons.lang.*;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

/**
 * @author pashky
 */
public class DecoratedSolrDocument {
    private SolrDocument solrDocument;
    private Map<String, List<String>> highlights;

    public DecoratedSolrDocument(SolrDocument solrDocument, Map<String, List<String>> highlights) {
        this.solrDocument = solrDocument;
        this.highlights = highlights;
    }

    // all this hacks to satisfy and trick JSP 2.0 EL resolver, we don't intent to implement Map in full
    public static abstract class MapEmulator<Target> implements Map<String, Target> {
        abstract Target mapValue(String key);

        public boolean isEmpty() { return false; }

        public boolean containsKey(Object key) {
            return mapValue(key != null ? key.toString() : null) != null;
        }


        public Target get(Object key) {
            return mapValue(key != null ? key.toString() : null);
        }

        public int size() { throw new NotImplementedException(); }
        public boolean containsValue(Object value) { throw new NotImplementedException(); }
        public Target put(String key, Target value) { throw new NotImplementedException(); }
        public Target remove(Object key) { throw new NotImplementedException(); }
        public void putAll(Map<? extends String, ? extends Target> t) { throw new NotImplementedException(); }
        public void clear() { throw new NotImplementedException(); }
        public Set<String> keySet() { throw new NotImplementedException(); }
        public Collection<Target> values() { throw new NotImplementedException(); }
        public Set<Map.Entry<String, Target>> entrySet() { throw new NotImplementedException(); }
    }

    public Map<String,String> getValue() {
        return new MapEmulator<String>() {
            public String mapValue(String key) {
                Collection fval = solrDocument.getFieldValues(key);
                if(fval != null)
                    return StringUtils.join(fval, ", ");
                return "";
            }
        };
    }

    public Map<String,String> getHtmlValue() {
        return new MapEmulator<String>() {
            public String mapValue(String key) {
                return StringEscapeUtils.escapeHtml(getValue().get(key));
            }
        };
    }

    public Map<String,String> getHilit() {
        return new MapEmulator<String>() {
            public String mapValue(String key) {
                List<String> val = highlights.get(key);
                if(val == null || val.size() == 0)
                    return StringEscapeUtils.escapeHtml(getValue().get(key));
                return org.apache.commons.lang.StringUtils.join(val, ", ");
            }
        };
    }

    public Map<String,Collection<String>> getValues() {
        return new MapEmulator<Collection<String>>() {
            public Collection<String> mapValue(String key) {
                @SuppressWarnings("unchecked")
                Collection<String> c = (Collection)solrDocument.getFieldValues(key);
                return c;
            }
        };
    }

    public Object getFieldValue(String key) {
        return solrDocument.getFieldValue(key);
    }

    public Collection getFieldValues(String key) {
        return solrDocument.getFieldValues(key);
    }

    public SolrDocument getOriginal() {
        return solrDocument;
    }
}
