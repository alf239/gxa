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

package ae3.service.structuredquery;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * @author pashky
 */
public class PrefixNodeTest extends TestCase {
    private PrefixNode root;
    @Override
    protected void setUp() throws Exception {
        root = new PrefixNode();
    }

    @Override
    protected void tearDown() throws Exception {
        root = null;
    }

    @Test
    public void testAdd() {
        root.add("abc", 123);
        final String[] a = new String[1];
        final int[] c = new int[1];
        root.collect("", new PrefixNode.WalkResult() {
            public void put(String name, int count) {
                a[0] = name;
                c[0] = count;
            }
            public boolean enough() {
                return false;
            }
        });
        assertTrue(a[0].equals("abc"));
        assertTrue(c[0] == 123);
    }

    public void testWalk() {
        root.add("abc", 123);
        root.add("abb", 234);
        root.add("def", 567);
        root.add("defgh", 888);

        final List<String> a = new ArrayList<String>();
        final List<Integer> c = new ArrayList<Integer>();
        PrefixNode.WalkResult wr = new PrefixNode.WalkResult() {
            public void put(String name, int count) {
                a.add(name);
                c.add(count);
            }
            public boolean enough() {
                return false;
            }
        };

        a.clear(); c.clear();
        root.walk("abc", 0, "", wr);
        assertEquals(a.size(), 1);
        assertTrue(a.get(0).equals("abc"));
        assertTrue(c.get(0) == 123);

        a.clear(); c.clear();
        root.walk("abb", 0, "", wr);
        assertEquals(a.size(), 1);
        assertTrue(a.get(0).equals("abb"));
        assertTrue(c.get(0) == 234);

        a.clear(); c.clear();
        root.walk("ab", 0, "", wr);
        assertEquals(a.size(), 2);
        assertTrue(a.get(0).equals("abb"));
        assertTrue(c.get(0) == 234);
        assertTrue(a.get(1).equals("abc"));
        assertTrue(c.get(1) == 123);

        a.clear(); c.clear();
        root.walk("def", 0, "", wr);
        assertEquals(a.size(), 2);
        assertTrue(a.get(0).equals("def"));
        assertTrue(c.get(0) == 567);
        assertTrue(a.get(1).equals("defgh"));
        assertTrue(c.get(1) == 888);

        a.clear(); c.clear();
        root.walk("defgh", 0, "", wr);
        assertEquals(a.size(), 1);
        assertTrue(a.get(0).equals("defgh"));
        assertTrue(c.get(0) == 888);
    }
}
