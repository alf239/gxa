<%String svnBuildString = "$Rev: 4866 $ $Date: 2008-06-20 11:57:28 +0100 (Fri, 20 Jun 2008) $";%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://ebi.ac.uk/ae3/functions" prefix="u" %>
<%@ page import="ae3.service.ArrayExpressSearchService" %>
<%@ page import="ae3.service.AtlasStructuredQuery" %>
<%@ page import="ae3.util.StructuredQueryHelper" %>
<%@ page import="ae3.service.AtlasStructuredQueryResult" %>
<%@ page buffer="0kb" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    request.setAttribute("service", ArrayExpressSearchService.instance());

    AtlasStructuredQuery atlasQuery = null;
    if(request.getParameterNames().hasMoreElements())
         atlasQuery = StructuredQueryHelper.parseRequest(request);
    request.setAttribute("query", atlasQuery);
%>

<jsp:include page="start_head.jsp"></jsp:include>
ArrayExpress Atlas Preview
<jsp:include page="end_head.jsp"></jsp:include>

    <link rel="stylesheet" href="blue/style.css" type="text/css" media="print, projection, screen" />
    <link rel="stylesheet" href="jquery.autocomplete.css" type="text/css"/>

    <script type="text/javascript" src="jquery.min.js"></script>
    <script type="text/javascript" src="jquery.cookie.js"></script>
    <script type="text/javascript" src="jquery.tablesorter.min.js"></script>
    <script type="text/javascript" src="jquery-impromptu.1.5.js"></script>
    <script type="text/javascript" src="jquery.autocomplete.js"></script>
    <script type="text/javascript" src="jquerydefaultvalue.js"></script>
    <script type="text/javascript" src="structured-query.js"></script>

    <script type="text/javascript">
        $("#atlasTable").tablesorter({
            sortAppend: [[6,0]],
            headers: {
                6: { sorter : 'digit' },
                7: { sorter : false },
                8: { sorter : false }
            }
        });

        $(document).ready(function()
            {
                $("#gene").defaultvalue("(all genes)");
                $("#gene").autocomplete("autocomplete.jsp", {
                        minChars:1,
                        matchCase: true,
                        matchSubset: false,
                        multiple: true,
                        multipleSeparator: " ",                    
                        extraParams: {type:"gene"},
                        formatItem:function(row) {return row[0] + " (" + row[1] + ")";}
                });
            }
        );

        var options = {
            expressions : [
                    <c:forEach var="i" items="${service.geneExpressionOptions}">
                    [ '<c:out escapeXml="false" value="${u:escapeJS(i[0])}"/>', '<c:out escapeXml="false" value="${u:escapeJS(i[1])}"/>' ],
                    </c:forEach>
            ],
            factors : [
                <c:forEach var="i" items="${service.experimentalFactorOptions}">
                '<c:out escapeXml="false" value="${u:escapeJS(i)}"/>',
                </c:forEach>
            ],
            species : [
                <c:forEach var="i" items="${service.allAvailableAtlasSpecies}">
                '<c:out escapeXml="false" value="${u:escapeJS(i)}"/>',
                </c:forEach>
            ]
        };

        var lastquery;
        <c:if test="${!empty query}">
        lastquery = {
            gene : '<c:out  escapeXml="false" value="${u:escapeJS(query.gene)}"/>',
            species : [<c:forEach var="i" items="${query.species}">'<c:out escapeXml="false" value="${u:escapeJS(i)}"/>',</c:forEach>],
            conditions : [
                <c:forEach var="c" items="${query.conditions}">
                { factor: '<c:out escapeXml="false" value="${u:escapeJS(c.factor)}"/>',
                  expression: '<c:out escapeXml="false" value="${u:escapeJS(c.expression)}"/>',
                  values: [<c:forEach var="v" items="${c.factorValues}">'<c:out escapeXml="false" value="${u:escapeJS(v)}"/>',</c:forEach>]
                },</c:forEach>
            ]
        };
        </c:if>
    </script>

    <style type="text/css">
        .label {
            font-size: 10px;
        }

        .atlasHelp {
            display: none;
        }

        /* tables */
        table.squery {
            font-family:arial;
            background-color: #CDCDCD;
            margin:10px 0pt 15px;
            font-size: 8pt;
            text-align: left;
        }
        table.squery thead tr th, table.squery tfoot tr th {
            background-color: #e6EEEE;
            border: 1px solid #FFF;
            font-size: 8pt;
            padding: 4px;
        }
        table.squery tbody td {
            color: #3D3D3D;
            padding: 4px;
            background-color: #FFF;
            vertical-align: top;
        }

        .prefix { padding-right:5px;}
        .speciesSelect { width: 120px; margin-right:5px; }
        
        div.value select, div.value input.value { width:150px; }
        div.value div.buttons { float: right; }
        div.value div.input { float: left; }
        div.value { width: 250px; margin-bottom:2px; }
        #conditions td { vertical-align: top; }
        #conditions td.factorvalue { padding-bottom: 10px; padding-left: 5px; }
        #conditions td.andbuttons {  padding-bottom:10px; padding-left: 10px}
        div.countup, div.countdn { width:15%;float:left;text-align:center; }
        div.gradel { width:7%;float:left; }
        div.expref { clear:both;text-align:left;width:100%;background-color:#f0f0f0; }
        table.squery td.counter, th.counter {text-align:center;width:120px; }
        th.factor { text-align:center;font-weight:normal; }
        th.factor em { font-weight:bold;font-style:normal;}
        th.gene { vertical-align:middle;text-align:center; }
        div.exps { clear:both;margin-top:2px; }
        ul.upexp, ul.dnexp { list-style:none; float: left;width:50%; padding:0px; margin:0px;list-style-position:outside;  }
        ul.upexp li, ul.dnexp li { padding:0px; margin:0px;font-size:7pt; }
        ul.upexp li { text-align:left; }
        ul.dnexp li { text-align:right; }
        ul.upexp li { background-color: #fff0f0; }
        ul.dnexp li { background-color: #f0f0ff; }
    </style>

<jsp:include page="start_body_no_menus.jsp"></jsp:include>

<jsp:include page="end_menu.jsp"></jsp:include>
<table width="100%" style="position:relative;top:-10px;border-bottom:thin solid lightgray">
    <tr>
        <td align="left" valign="bottom" width="55">
            <a href="index.jsp"><img border="0" src="atlasbeta.jpg" width="50" height="25"/></a>
        </td>

        <td align="left">
            <a href="http://www.ebi.ac.uk/microarray/doc/atlas/index.html">about the project</a> |
            <a href="http://www.ebi.ac.uk/microarray/doc/atlas/faq.html">faq</a> |
            <a id="feedback_href" href="javascript:showFeedbackForm()">feedback</a> <span id="feedback_thanks" style="font-weight:bold;display:none">thanks!</span> |
            <a target="_blank" href="http://arrayexpress-atlas.blogspot.com">blog</a> |
            <a target="_blank" href="http://www.ebi.ac.uk/microarray/doc/atlas/api.html">web services api</a> (<b>new!</b>) |
            <a href="http://www.ebi.ac.uk/microarray/doc/atlas/help.html">help</a>
        </td>
        <td align="right">
            <a href="http://www.ebi.ac.uk/microarray"><img border="0" height="20" title="EBI ArrayExpress" src="aelogo.png"/></a>
        </td>
    </tr>
</table>
<div style="margin-bottom:50px">

    <form name="atlasform" action="qrs" onsubmit="renumberAll();">
        <table>
            <tr valign="top">
                <td>
                    <label class="label" for="gene">Genes</label>
                </td>

                <td>
                    <label class="label" for="species">Organisms</label>
                </td>

                <td style="padding-left:5px">
                    <label class="label" for="conditions">Conditions</label>
                </td>
            </tr>
            <tr valign="top">
                <td>
                    <input type="text" name="gene" id="gene" style="width:150px" value="${query.gene}"/>
                </td>
                <td>
                    <table id="species" cellpadding="0" cellspacing="0">
                        <tbody></tbody>
                    </table>
                </td>
                <td style="padding-left:5px">
                    <table id="conditions" cellpadding="0" cellspacing="0">
                        <tbody></tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td colspan="3" align="left">
                    <input type="submit" value="Search Atlas">
                </td>
            </tr>
        </table>
    </form>

    <script type="text/javascript">
        initQuery();
    </script>

    <c:if test="${!empty query}">
        <div style="margin:10px auto;width:150px;text-align:center;clear:both" id="loading_display">Searching... <img src="indicator.gif" alt="Loading..."/></div>
        <c:set var="timeStart" value="${u:currentTime()}"/>
        <%
            response.flushBuffer();
            AtlasStructuredQueryResult atlasResult = ArrayExpressSearchService.instance().doStructuredAtlasQuery(atlasQuery);
            request.setAttribute("result", atlasResult);
        %>
        <script type="text/javascript">$("#loading_display").hide()</script>

        <c:if test="${result.size > 0}">
            <p>
                <c:if test="${result.size == 200}">More than </c:if><c:out value="${result.size}" /> matching gene(s) found:
            </p>
            <table class="squery">
                <thead>
                    <tr>
                        <th colspan="2" rowspan="2" class="gene">Gene</th>
                        <c:forEach var="c" items="${result.conditions}">
                            <th colspan="${f:length(c.factorValues)}" class="factor">
                                <c:out value="${c.expression.description}" escapeXml="true"/> in<br/> <em><c:out value="${c.factor}" escapeXml="true"/></em>
                            </th>
                        </c:forEach>
                        <th rowspan="2">&nbsp;</th>
                    </tr>
                    <tr>
                        <c:forEach var="c" items="${result.conditions}">
                            <c:forEach var="v" items="${c.factorValues}">
                                <th class="counter"><c:out value="${v}" escapeXml="true"/></th>
                            </c:forEach>
                        </c:forEach>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${result.results}">
                        <tr>
                            <td>
                                <c:url var="urlGeneAnnotation" value="http://www.ebi.ac.uk/ebisearch/search.ebi">
                                    <c:param name="db" value="genomes"/>
                                    <c:param name="t" value="${row.gene.geneIdentifier}"/>
                                </c:url>
                                <a title="Show gene annotation" target="_blank" href="${urlGeneAnnotation}"><c:out value="${row.gene.geneIdentifier}" escapeXml="true"/></a>
                            </td>
                            <c:set var="geneName" value="${f:split(row.gene.geneName,';')}"/>
                            <td><a href="gene?gid=${f:escapeXml(row.gene.geneIdentifier)}" title="${f:join(geneName, ', ')}"><c:out value="${f:substring(geneName[0],0,20)}${f:length(geneName[0]) > 20 || f:length(geneName) > 1 ? '...' : ''}" escapeXml="true"/><c:if test="${empty row.gene.geneName}">(none)</c:if></a></td>
                            <c:forEach var="ud" items="${row.counters}">
                                <td class="counter">
                                    <c:set var="upc" value="${ud.ups != 0 ? (ud.mpvUp > 0.05 ? 0.05 : ud.mpvUp) * 240 / 0.05 : 240}"/>
                                    <c:set var="dnc" value="${ud.downs != 0 ? (ud.mpvDn > 0.05 ? 0.05 : ud.mpvDn) * 240 / 0.05 : 240}"/>
                                    <div>
                                        <c:forEach var="g" items="${u:gradient(240,upc,upc,dnc,dnc,255,12,ud.ups,ud.downs)}" varStatus="s">
                                            <c:if test="${s.first}"><div class="countup" title="${ud.ups != 0 ? 'p-value is ' : ''}${ud.ups != 0 ? ud.mpvUp : ''}" style="background-color:${g};color:${upc > 200 ? 'black' : 'white'}">${ud.ups == 0 ? '-' : ud.ups}</div></c:if>
                                            <c:if test="${s.last}"><div class="countdn" title="${ud.downs != 0 ? 'p-value is ' : ''}${ud.downs != 0 ? ud.mpvDn : ''}" style="background-color:${g};color:${dnc > 200 ? 'black' : 'white'}">${ud.downs == 0 ? '-' : ud.downs}</div></c:if>
                                            <c:if test="${!s.first && !s.last}"><div class="gradel" style="background-color:${g};color:${g}">.</div></c:if>
                                        </c:forEach>
                                    </div>
                                </td>
                            </c:forEach>
                            <td>
                                <c:url var="urlExps" value="/sexpt">
                                    <c:param name="gene" value="${row.gene.geneId}"/>
                                    <c:forEach var="c" varStatus="s" items="${result.conditions}">
                                        <c:param name="ef${s.index}" value="${c.factor}"/>
                                        <c:forEach var="v" items="${c.factorValues}"><c:param name="fv${s.index}" value="${v}"/></c:forEach>
                                    </c:forEach>
                                </c:url>
                                <span onclick="loadExperiments(this,'${urlExps}','${u:escapeJS(row.gene.geneIdentifier)}');"><img src="expandopen.gif" alt="&gt;" title="Show experiments" width="11" height="11" style="border:0px;"/></span>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty result}">
            No results found!
        </c:if>
        <c:set var="timeFinish" value="${u:currentTime()}"/>
        <div>
            Processing time: <c:out value="${(timeFinish - timeStart) / 1000.0}"/> secs.
        </div>        
    </c:if>

</div>

<jsp:include page="end_body.jsp"></jsp:include>

