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

package ae3.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;

import static uk.ac.ebi.gxa.exceptions.LogUtil.createUnexpected;

/**
 * Helper functions for parsing and managing structured query
 *
 * @author pashky
 */
public class HtmlHelper {

    /**
     * Encode staring with URL encdoing (%xx's)
     *
     * @param str url
     * @return encoded str
     */
    public static String escapeURL(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * @param identifier
     * @return identifier, first url-encoded, then all percent characters url-encoded again
     *         Both encodings are needed to make urlrewrite rules work with gene identifiers containing e.g. '+'
     *         and ':' characters in gene/<geneid> and experiment/<expacc>/<geneid> types of urls.
     */
    public static String urlRewriteEncode(String identifier) {
        return escapeURL(identifier).replaceAll("\\%", "%25");
    }

    public static boolean isIn(Collection set, Object element) {
        return set.contains(element);
    }

    public static String truncateLine(String line, int num) {
        if (line.length() > num)
            return line.substring(0, num) + "...";
        else
            return line;
    }

    public static Comparable maxProperty(Iterable it, String prop) {
        Method method = null;
        Comparable r = null;
        for (Object o : it) {
            if (method == null) {
                try {
                    method = o.getClass().getMethod(prop, (Class[]) null);
                } catch (Exception e) {
                    throw createUnexpected("Cannot obtain method", e);
                }
            }
            try {
                Comparable v = (Comparable) method.invoke(o, (Object[]) null);
                if (r == null || r.compareTo(v) < 0) {
                    r = v;
                }
            } catch (Exception e) {
                throw createUnexpected("Cannot invoke method", e);
            }
        }
        return r;
    }
}

