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

package uk.ac.ebi.gxa.requesthandlers.query;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pashky
 */
public class TableHeaderServlet extends HttpServlet {
    private static int stoi(String str, int def) {
        try {
            return Integer.valueOf(str);
        } catch(Exception e) {
            return def;
        }
    }
    private static Color stoc(String str, Color def) {
        try {
            return Color.decode("#" + str);
        } catch(Exception e) {
            return def;
        }
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int stepWidth = stoi(req.getParameter("s"), 26);
        int maxHeight = stoi(req.getParameter("h"), -1);
        int fontSize = stoi(req.getParameter("fs"), 11);
        int lineHeight = stoi(req.getParameter("lh"), 15);
        Color textColor = stoc(req.getParameter("tc"), Color.BLACK);
        Color lineColor = stoc(req.getParameter("lc"), Color.BLACK);

        String[] texts = null;
        Integer[] depths = null;
        Boolean[] isExpandables = null;
        String sessionName = req.getParameter("st");
        if(sessionName != null) {
            HttpSession session = req.getSession(false);
            if(session != null) {
                Iterable iterable = (Iterable)session.getAttribute(sessionName);
                String textMethodName = req.getParameter("mt");
                String depthMethodName = req.getParameter("md");
                String isExpandableMethodName = req.getParameter("ie");
                Method getTextMethod = null;
                Method getDepthMethod = null;
                Method isExpandableMethod = null;
                List<String> textl = new ArrayList<String>();
                List<Integer> depthl = new ArrayList<Integer>();
                List<Boolean> isExpandable1 = new ArrayList<Boolean>();
                for(Object o : iterable) {
                    if(getTextMethod == null) {
                        Class klass= o.getClass();
                        try {
                            getTextMethod = klass.getMethod(textMethodName, (Class[]) null);
                        } catch(NoSuchMethodException e) {
                            break;
                        }
                        if(depthMethodName != null) {
                            try {
                                getDepthMethod = klass.getMethod(depthMethodName, (Class[]) null);
                            } catch (NoSuchMethodException e) {
                                // does not apply to sessionName "resultEfvs" hence OK
                            }
                        }

                        if (isExpandableMethodName != null) {
                            try {
                                isExpandableMethod = klass.getMethod(isExpandableMethodName, (Class[]) null);
                            } catch (NoSuchMethodException e) {
                                 // does not apply to sessionName "resultEfvs" hence OK
                            }
                        }
                    }
                    try {
                        textl.add((String) getTextMethod.invoke(o, (Object[]) null));
                        if (getDepthMethod != null) { // null for efvs; not null for efos
                            depthl.add((Integer) getDepthMethod.invoke(o, (Object[]) null));
                        }
                        if (isExpandableMethod != null) { // null for efvs; not null for efos
                            isExpandable1.add((Boolean) isExpandableMethod.invoke(o, (Object[]) null));
                        }

                    } catch(IllegalAccessException e) {
                        break;
                    } catch(InvocationTargetException e) {
                        break;
                    }
                }
                if(!textl.isEmpty())
                    texts = textl.toArray(new String[textl.size()]);
                if(!depthl.isEmpty())
                    depths = depthl.toArray(new Integer[depthl.size()]);
                if (!isExpandable1.isEmpty())
                    isExpandables = isExpandable1.toArray(new Boolean[isExpandable1.size()]);
            }
        } else {
            texts = req.getParameterValues("t");
            String[] depthsStrs = req.getParameterValues("d");
            if(depthsStrs != null) {
                depths = new Integer[texts.length];
                for(int i = 0; i < depths.length; ++i) {
                    depths[i] = stoi(depthsStrs[i], 0);
                }
            }
        }

        if(texts == null) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        res.setContentType("image/png");
        WritableRenderedImage img;
        if (depths == null) {
            img = DiagonalTextRenderer.drawTableHeader(texts,
                    stepWidth, maxHeight, fontSize, lineHeight,
                    textColor, lineColor);
        } else {
            int depthStep = stoi(req.getParameter("tds"), 4);
            int treeXShift = stoi(req.getParameter("tsx"), 7);
            int treeYShift = stoi(req.getParameter("tsy"), 5);
            Color treeColor = stoc(req.getParameter("tlc"), Color.black);

            img = DiagonalTextRenderer.drawTableTreeHeader(texts, depths, isExpandables, stepWidth, maxHeight, fontSize, lineHeight,
                    depthStep, treeXShift, treeYShift, textColor, lineColor, treeColor);
        }
        ImageIO.write(img, "png", res.getOutputStream());
    }
}
