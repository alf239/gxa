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

package uk.ac.ebi.gxa.web.tags.resourcebundle.wro4j;

import org.apache.commons.digester.annotations.rules.ObjectCreate;
import org.apache.commons.digester.annotations.rules.SetNext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
@ObjectCreate(pattern = "groups")
public class Wro4jGroups {
    private final Map<String, Wro4jGroup> groups = new HashMap<String, Wro4jGroup>();

    @SetNext
    public void addGroup(Wro4jGroup group) {
        groups.put(group.getName(), group);
    }

    public Wro4jGroup findGroup(String name) {
        return groups.get(name);
    }
}
