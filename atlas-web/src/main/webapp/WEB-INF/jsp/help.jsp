<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ebi.ac.uk/ae3/functions" prefix="u" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  ~ Copyright 2008-2010 Microarray Informatics Team, EMBL-European Bioinformatics Institute
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  ~
  ~ For further details of the Gene Expression Atlas project, including source code,
  ~ downloads and documentation, please see:
  ~
  ~ http://gxa.github.com/gxa
  --%>

<jsp:useBean id="atlasProperties" type="uk.ac.ebi.gxa.properties.AtlasProperties" scope="application"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="eng">
    <head>
<u:htmlTemplate file="look/help.head.html" />
<%-- <c:out value="${f:substringAfter(pageContext.request.requestURI, '/help/')}"/> - Gene Expression Atlas Help --%>

<c:import url="/WEB-INF/jsp/includes/query-includes.jsp" />

<style type="text/css">
    /* TODO: display contents appropriately */

    #ae_pagecontainer  .toc {
        position:absolute;
        top:0;
        right: 0;
        width: 30%;
        background-color:white;
    }


    .toc ul {
        margin-left: 0;
        padding-left: 1.5em;
        text-indent: -0.5em;
    }

    #ae_pagecontainer .toc td {
        vertical-align:top;
        padding:0 0 0 20px;
        margin:0;
    }

    #toctitle h2 { margin-top:0; }

</style>

<script type="text/javascript">
    $(document).ready(function () {
        var toc = $('.toc');
        $('.toc ~ hr').hide();

        var parentY = toc.offset().top;
        $(window).scroll(function () {
            var scrolltop = $(window).scrollTop();
            var offset = scrolltop - parentY + 10;
            if(offset <0)
                offset =  0;
            toc.animate({top:offset+"px"},{duration:500,queue:false});
        });
    });
</script>

<style type="text/css">
    @media print {
        body, .contents, .header, .contentsarea, .head {
            position: relative;
        }
    }
    </style>
</head>

<body onLoad="if(navigator.userAgent.indexOf('MSIE') != -1) {document.getElementById('head').allowTransparency = true;}">
	<div class="headerdiv" id="headerdiv" style="position:absolute; z-index: 1;">
		<iframe src="http://www.ebi.ac.uk/inc/head.html" name="head" id="head" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  width="100%" style="position:absolute; z-index: 1; height: 57px;"></iframe>
	</div>


<div class="contents" id="contents">
<div id="ae_pagecontainer">

<!-- location bar -->
<table style="border-bottom:1px solid #DEDEDE;margin:0 0 10px 0;width:100%;height:30px;">
    <tr>
        <td class="atlastable" align="left" valign="bottom" width="55" style="padding-right:10px;">
            <a href="${pageContext.request.contextPath}/" title="Gene Expression Atlas Homepage"><img border="0" width="55" src="${pageContext.request.contextPath}/images/atlas-logo.png" alt="Gene Expression Atlas"/></a>
        </td>
        <td class="atlastable" align="right" valign="bottom">
            <a href="${pageContext.request.contextPath}">home</a> |
            <a href="${pageContext.request.contextPath}/help/AboutAtlas">about the project</a> |
            <a href="${pageContext.request.contextPath}/help/AtlasFaq">faq</a> |
            <a id="feedback_href" href="javascript:showFeedbackForm()">feedback</a><span id="feedback_thanks" style="font-weight:bold;display:none">thanks!</span> |
            <a href="http://arrayexpress-atlas.blogspot.com">blog</a> |
	    <a href="${pageContext.request.contextPath}/help/AtlasDasSource">das</a> |
            <a href="${pageContext.request.contextPath}/help/AtlasApis">api</a> <b>new</b> |
            <a href="${pageContext.request.contextPath}/help">help</a>
        </td>
    </tr>
</table>
<div style="position:relative;width:100%;">
<div style="padding-bottom:50px;width:70%">
    <u:renderWiki/>
</div>
</div>

</div><!-- /id="ae_pagecontainer" -->
</div><!-- /id="contents" -->

<u:htmlTemplate file="look/footer.html" />
</body></html>
