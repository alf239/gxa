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

package ae3.service.structuredquery;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class GeneAutoCompleteItemRankTest {

    @Test
    public void rankTest() {
        AtlasGenePropertyService.GeneAutoCompleteItemRank geneRank =
                new AtlasGenePropertyService.GeneAutoCompleteItemRank(Arrays.asList("human", "homo", "mus", "rattus"));

        Rank r1 = geneRank.getRank(newGeneItem("gene1", null));
        Rank r2 = geneRank.getRank(newGeneItem("gene2", null));
        assertEquals(r1, r2);
        assertTrue(r1.isMax());

        Rank r3 = geneRank.getRank(newGeneItem("gene1", "human"));
        Rank r4 = geneRank.getRank(newGeneItem("gene1", "mus rattus"));
        assertGreater(r3, r4);
        assertTrue(r3.isMax());
    }

    private static void assertGreater(Rank r1, Rank r2) {
        assertTrue(r1.compareTo(r2) > 0);
    }

    private static GeneAutoCompleteItem newGeneItem(String value, String species) {
        return new GeneAutoCompleteItem("property", value, 0L, species, value, Collections.<String>emptyList());
    }
}
