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

package uk.ac.ebi.gxa.web.thirdparty.wro4j.tag;

/**
 * @author Olga Melnichuk
 */
public enum WebResourceType {

    CSS("css") {
        @Override
        public String toHtml(String src) {
            return "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + src + "\"/>";
        }
    },

    JS("js") {
        @Override
        public String toHtml(String src) {
            return "<script type=\"text/javascript\" src=\"" + src + "\"></script>";
        }
    };

    private String ext;

    WebResourceType(String ext) {
        this.ext = ext;
    }

    public String ext() {
        return ext;
    }

    public abstract String toHtml(String src);
}
