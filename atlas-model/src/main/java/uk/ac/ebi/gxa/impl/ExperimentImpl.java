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

package uk.ac.ebi.gxa.impl;

import uk.ac.ebi.gxa.*;

import java.util.*;

import uk.ac.ebi.gxa.exceptions.LogUtil;

public abstract class ExperimentImpl implements Experiment {
    private final String accession;
    private final long id;

    private String description;
    private String articleAbstract;
    private String performer;
    private String lab;

    private Date loadDate;
    private Date releaseDate;
    private Long pubmedId;

    private List<Asset> assets = new ArrayList<Asset>();

    protected ExperimentImpl(String accession, long id) {
        this.accession = accession;
        this.id = id;
    }

    public String getAccession() {
        return accession;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAbstract() {
        return articleAbstract;
    }

    public void setAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }


    public Date getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getPubmedId() {
        return pubmedId;
    }

    public void setPubmedIdString(String pubmedIdString) {
        if (pubmedIdString != null) {
            try {
                final long pubmedId = Long.parseLong(pubmedIdString);
                setPubmedId(pubmedId);
            } catch (NumberFormatException e) {
                LogUtil.logUnexpected("Couldn't parse " + pubmedIdString + " as long", e);
            }
        }
    }

    public void setPubmedId(Long pubmedId) {
        this.pubmedId = pubmedId;
    }

    public void addAssets(List<Asset> assets) {
        this.assets.addAll(assets);
    }

    // TODO: lazy collection
    public List<Asset> getAssets() {
        return Collections.unmodifiableList(assets);
    }

    /*
    //Collection<Assay> getAssays();
    //Collection<Sample> getSamples();
    */

    @Override
    public String toString() {
        return "Experiment{" +
                "accession='" + getAccession() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", performer='" + getPerformer() + '\'' +
                ", lab='" + getLab() + '\'' +
                '}';
    }
}
