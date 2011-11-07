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

package uk.ac.ebi.gxa.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.microarray.atlas.model.PropertyName;
import uk.ac.ebi.microarray.atlas.model.PropertyValue;
import uk.ac.ebi.microarray.atlas.model.Sample;

import java.util.List;

/**
 * @author Robert Petryszak
 */
public class SampleDAO extends AbstractDAO<Sample> {
    public static final String NAME_COL = "accession";

    public static final Logger log = LoggerFactory.getLogger(SampleDAO.class);

    public SampleDAO(SessionFactory sessionFactory) {
        super(sessionFactory, Sample.class);
    }

    long getTotalCount() {
        return (Long) template.find("select count(a) FROM Sample a").get(0);
    }

    @SuppressWarnings("unchecked")
    public List<Sample> getSamplesByPropertyValue(String propertyValue) {
        return template.find("select s from Experiment e left join e.samples s left join s.properties p where p.propertyValue.value = ? ", propertyValue);
    }

    @Override
    public void save(Sample object) {
        super.save(object);
        template.flush();
    }

    /**
     * @return Name of the column for hibernate to match searched objects against - c.f. super.getByName()
     */
    @Override
    public String getNameColumn() {
        return NAME_COL;
    }

    public void deleteProperties(PropertyName property, PropertyValue propertyValue) {
        template.bulkUpdate("delete from SampleProperty sp " +
                "where sp.property = ? and sp.propertyValue = ?", property, propertyValue);
        template.flush();
        template.clear();
    }
}
