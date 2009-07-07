<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ae3.service.AtlasGeneService" %>
<%@ page import="ae3.model.AtlasGene" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collection" %>
<%@ page import="ae3.service.structuredquery.*" %>
<%@ taglib uri="http://ebi.ac.uk/ae3/functions" prefix="u" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%

    //GenePropValueListHelper.Instance.treeAutocomplete("name",request.getParameter("start"),-1);

    //AtlasStructuredQueryService

    //ArrayList<AtlasGene> Genes = AtlasGeneService.getGenes(request.getParameter("start"));

    String Rec = request.getParameter("rec");
    String prefix = request.getParameter("start");

    if(null == prefix)
               prefix = "a";

    int RecordCount = 1000;

    if(null != Rec)
        RecordCount= 100000;

    AtlasStructuredQueryService service = ae3.service.ArrayExpressSearchService.instance().getStructQueryService();

    Collection<AutoCompleteItem> Genes = service.getGeneListHelper().autoCompleteValues(GeneProperties.GENE_PROPERTY_NAME,prefix,RecordCount,null);

    //AZ:2008-07-07 "0" means all numbers
    if(prefix.equals("0"))
    {
        for(int i=1;i!=10;i++)
        {
            Genes.addAll(service.getGeneListHelper().autoCompleteValues(GeneProperties.GENE_PROPERTY_NAME,String.valueOf(i),RecordCount,null));
        }
    }

    request.setAttribute("Genes",Genes);
%>

<style type="text/css">

    .alertNotice {
        padding: 50px 10px 10px 10px;
        text-align: center;
        font-weight: bold;
    }

    .alertNotice > p {
        margin: 10px;
    }

    .alertHeader {
        color: red;
    }

    #centeredMain {
        width:740px;
        margin: 0 auto;
        padding:50px 0;
        height: 100%;
    }

    .roundCorner {
        background-color: #EEF5F5;
    }

    a.Alphabet{
       margin:10px; 
    }

</style>


<jsp:include page="start_head.jsp" />
Gene Expression Atlas Summary for ${atlasGene.geneName} (${atlasGene.geneSpecies}) - Gene Expression Atlas
<jsp:include page="end_head.jsp" />

<meta name="Description" content="${atlasGene.geneName} (${atlasGene.geneSpecies}) - Gene Expression Atlas Summary"/>
<meta name="Keywords" content="ArrayExpress, Atlas, Microarray, Condition, Tissue Specific, Expression, Transcriptomics, Genomics, cDNA Arrays" />

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/scripts/jquery-1.3.2.min.js"></script>
<!--[if IE]><script language="javascript" type="text/javascript" src="scripts/excanvas.min.js"></script><![endif]-->

<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.autocomplete.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquerydefaultvalue.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.pagination.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/plots.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/feedback.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.tablesorter.min.js"></script>
<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.flot.atlas.js"></script>

<link rel="stylesheet" href="<%=request.getContextPath()%>/atlas.css" type="text/css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/geneView.css" type="text/css" />


<link rel="stylesheet" href="<%= request.getContextPath()%>/blue/style.css" type="text/css" media="print, projection, screen" />
<link rel="stylesheet" href="<%= request.getContextPath()%>/jquery.autocomplete.css" type="text/css" />
<link rel="stylesheet" href="<%= request.getContextPath()%>/structured-query.css" type="text/css" />
<jsp:include page='start_body_no_menus.jsp' />

<div class="contents" id="contents">
<div id="ae_pagecontainer">

<table style="width:100%;border-bottom:1px solid #dedede">
    <tr>
        <td align="left" valign="bottom">
            <a href="<%= request.getContextPath()%>/" title="Home"><img src="<%= request.getContextPath()%>/images/atlas-logo.png" alt="Gene Expression Atlas" title="Atlas Data Release ${f:escapeXml(service.stats.dataRelease)}: ${service.stats.numExperiments} experiments, ${service.stats.numAssays} assays, ${service.stats.numEfvs} conditions" border="0"></a>          
        </td>

        <td width="100%" valign="bottom" align="right">
            <a href="http://www.ebi.ac.uk/microarray/doc/atlas/index.html">about the project</a> |
            <a href="http://www.ebi.ac.uk/microarray/doc/atlas/faq.html">faq</a> |
            <a id="feedback_href" href="javascript:showFeedbackForm()">feedback</a> <span id="feedback_thanks" style="font-weight:bold;display:none">thanks!</span> |
            <a target="_blank" href="http://arrayexpress-atlas.blogspot.com">blog</a> |
            <a target="_blank" href="http://www.ebi.ac.uk/microarray/doc/atlas/api.html">web services api</a> |
            <a href="http://www.ebi.ac.uk/microarray/doc/atlas/help.html">help</a>
        </td>
        <td align="right" valign="bottom">
        </td>
    </tr>
</table>

    <div style="margin:100px; font-weight:bold; font-size:larger; text-align:center;">
 	<a class="alphabet"  href="index.htm?start=0" title="Array Expression Atlas Genes Starting With Digit">123</a>
        <a class="alphabet"  href="index.htm?start=a" title="Array Expression Atlas Genes Starting With A">A</a>
        <a class="alphabet"  href="index.htm?start=b" title="Array Expression Atlas Genes Starting With B">B</a>
        <a class="alphabet"  href="index.htm?start=c" title="Array Expression Atlas Genes Starting With C">C</a>
        <a class="alphabet"  href="index.htm?start=d" title="Array Expression Atlas Genes Starting With D">D</a>
        <a class="alphabet"  href="index.htm?start=e" title="Array Expression Atlas Genes Starting With E">E</a>
        <a class="alphabet"  href="index.htm?start=f" title="Array Expression Atlas Genes Starting With F">F</a>
        <a class="alphabet"  href="index.htm?start=g" title="Array Expression Atlas Genes Starting With G">G</a>
        <a class="alphabet"  href="index.htm?start=h" title="Array Expression Atlas Genes Starting With H">H</a>
        <a class="alphabet"  href="index.htm?start=i" title="Array Expression Atlas Genes Starting With I">I</a>
        <a class="alphabet"  href="index.htm?start=j" title="Array Expression Atlas Genes Starting With J">J</a>
        <a class="alphabet"  href="index.htm?start=k" title="Array Expression Atlas Genes Starting With K">K</a>
        <a class="alphabet"  href="index.htm?start=l" title="Array Expression Atlas Genes Starting With L">L</a>
        <a class="alphabet"  href="index.htm?start=m" title="Array Expression Atlas Genes Starting With M">M</a>
        <a class="alphabet"  href="index.htm?start=n" title="Array Expression Atlas Genes Starting With N">N</a>
        <a class="alphabet"  href="index.htm?start=o" title="Array Expression Atlas Genes Starting With O">O</a>
        <a class="alphabet"  href="index.htm?start=p" title="Array Expression Atlas Genes Starting With P">P</a>
        <a class="alphabet"  href="index.htm?start=q" title="Array Expression Atlas Genes Starting With Q">Q</a>
        <a class="alphabet"  href="index.htm?start=r" title="Array Expression Atlas Genes Starting With R">R</a>
        <a class="alphabet"  href="index.htm?start=s" title="Array Expression Atlas Genes Starting With S">S</a>
        <a class="alphabet"  href="index.htm?start=t" title="Array Expression Atlas Genes Starting With T">T</a>
        <a class="alphabet"  href="index.htm?start=u" title="Array Expression Atlas Genes Starting With U">U</a>
	<a class="alphabet"  href="index.htm?start=v" title="Array Expression Atlas Genes Starting With V">V</a>
        <a class="alphabet"  href="index.htm?start=w" title="Array Expression Atlas Genes Starting With W">W</a>
        <a class="alphabet"  href="index.htm?start=x" title="Array Expression Atlas Genes Starting With X">X</a>       
	<a class="alphabet"  href="index.htm?start=y" title="Array Expression Atlas Genes Starting With Y">Y</a>
	<a class="alphabet"  href="index.htm?start=z" title="Array Expression Atlas Genes Starting With Z">Z</a>	
    </div>



    <c:forEach var="gene" items="${Genes}">
         <a href="<%=request.getContextPath()%>/gene/${gene.id}" title="Gene Expression Atlas Data For ${gene.value}" target="_self">${gene.value}</a>&nbsp;
    </c:forEach>

    <%
        String s = request.getRequestURI();

        if(null == prefix)
               prefix = "a";
        
        String NextURL = "index.htm?start="+prefix+"&rec=1000" ;

        boolean more = ((Genes.size() > 999) & (Rec==null));

        request.setAttribute("more",more);
    %>

    <c:if test="${more}">
            <a href="<%= NextURL %>">more&gt;&gt;</a>        
    </c:if>

</div>

<!-- end page contents here -->
<jsp:include page='end_body.jsp' />
