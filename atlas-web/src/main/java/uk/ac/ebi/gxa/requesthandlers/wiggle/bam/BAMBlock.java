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

package uk.ac.ebi.gxa.requesthandlers.wiggle.bam;

public class BAMBlock {
    public final byte[] buffer;
    public final int from;
    public final int to;

    BAMBlock(int length, int from, int to) throws BAMException {
        if (from < 0 || from > to || to > length) {
            throw new BAMException("Invalid subregion [" + from + "," + to + ") in the array of length " + length);
        }
        this.buffer = new byte[length];
        this.from = from;
        this.to = to;
    }
}
