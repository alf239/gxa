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

package uk.ac.ebi.gxa.requesthandlers.base.result;

import uk.ac.ebi.gxa.requesthandlers.base.restutil.RestOut;
import uk.ac.ebi.gxa.requesthandlers.base.restutil.RestOut;

import java.io.StringWriter;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;

/**
 * @author pashky
*/
public class ErrorResult {
    private String error;

    public ErrorResult(String error) {
        this.error = error;
    }

    private static String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        pw.flush();
        sw.flush();
        String[] strings = sw.getBuffer().toString().split("(\r?\n)+");
        return StringUtils.join(strings, '\n', 0, strings.length > 10 ? 10 : strings.length)
                + (strings.length > 10 ? "\n..." : "");
    }


    public ErrorResult(Throwable e) {
        this.error = "Exception occured: " + exceptionToString(e);
    }

    public @RestOut
    String getError() {
        return error;
    }
}
