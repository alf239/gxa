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

package ae3.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Andrey
 * Date: Jul 6, 2009
 * Time: 11:39:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class URLUtil {
    public static String getGeneURL(HttpServletRequest request, String GeneId)
    {
        //return "lol.txt";
        return(request.getContextPath()+"/gene/"+GeneId);
    }

    public static String getDasUrl(HttpServletRequest request)
    {
       return request.getRequestURL().toString();
//       return "http://" + AtlasProperties.getProperty("atlas.host") + request.getContextPath() + "/das/" + AtlasProperties.getProperty("atlas.dasdsn");
    }
}

