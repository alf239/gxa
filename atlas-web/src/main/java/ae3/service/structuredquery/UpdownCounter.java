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

/**
 * Heat map cell class
 * @author pashky
*/
public class UpdownCounter implements Comparable<UpdownCounter> {
    private int ups;
    private int downs;
    private int nones;
    private float mpvup;
    private float mpvdn;

    public UpdownCounter() {
        this(0, 0, 0, 1, 1);
    }

    public UpdownCounter(int ups, int downs, int nones, float mpvup, float mpvdn) {
        this.ups = ups;
        this.downs = downs;
        this.mpvup = mpvup;
        this.mpvdn = mpvdn;
        this.nones = nones;
    }

    public int getUps() {
        return ups;
    }

    public int getDowns() {
        return downs;
    }

    public int getNones() {
        return nones;
    }

    public float getMpvUp() {
        return mpvup;
    }

    public float getMpvDn() {
        return mpvdn;
    }

    public void add(boolean isUp, float pvalue) {
        if(isUp) {
            ++ups;
            mpvup = Math.min(mpvup, pvalue);
        } else {
            ++downs;
            mpvdn = Math.min(mpvdn, pvalue);
        }
    }

    public void addNo() {
        ++nones;
    }

    public boolean isZero() {
        return getUps() + getDowns() + getNones() == 0;
    }

    public int compareTo(UpdownCounter o) {
        if (getNoStudies() == o.getNoStudies() && getNoStudies() != 0)
            return - Float.valueOf(getMpvUp() + getMpvDn()).compareTo(o.getMpvUp() + o.getMpvDn());
        else if(getNoStudies() == o.getNoStudies() && getNoStudies() == 0)
            return - Integer.valueOf(getNones()).compareTo(o.getNones());
        else
            return - Integer.valueOf(getNoStudies()).compareTo(o.getNoStudies());

    }

    public int getNoStudies() {
        return getUps() + getDowns();
    }
}
