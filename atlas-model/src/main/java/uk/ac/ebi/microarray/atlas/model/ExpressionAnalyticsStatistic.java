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
 * http://ostolop.github.com/gxa/
 */

package uk.ac.ebi.microarray.atlas.model;

/**
 * Created by IntelliJ IDEA.
 * User: Andrey
 * Date: Jan 13, 2010
 * Time: 11:07:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExpressionAnalyticsStatistic {
    private double pvalue;
    private double tstat;

    public double getPvalue(){
        return pvalue;
    }
    public void setPvalue(double pvalue){
        this.pvalue = pvalue;
    }

    public double getTstat(){
        return tstat;
    }
    public void setTstat(double tstat){
        this.tstat = tstat;
    }

}
