<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="exp" type="ae3.model.AtlasExperiment" scope="request"/>
<jsp:useBean id="expSpecies" type="java.util.Collection<java.lang.String>" scope="request"/>
<jsp:useBean id="arrayDesigns" type="java.util.Collection<java.lang.String>" scope="request"/>

<div style="float:right;margin:0 20px;">
    <a href="${pageContext.request.contextPath}/experiment/${exp.accession}"
        class="geneName"
       style="vertical-align: baseline">${exp.accession}</a>

    <div style="border:1px black solid;padding:5px;">
        <table cellpadding="2" cellspacing="0" border="0">
            <tr>
                <td style="text-align:right;">Platform:</td>
                <td>
                    <c:forEach var="arrayDesign" items="${arrayDesigns}">
                        <a class="experimentLink" href="${pageContext.request.contextPath}/experiment/${exp.accession}?ad=${arrayDesign}">${arrayDesign}</a>&nbsp;
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <td style="text-align:right;">Organism:</td>
                <td>
                    <c:forEach var="species" items="${expSpecies}" varStatus="i">
                        ${species}${i.last ? "" : ", "}
                    </c:forEach>
                </td>
            </tr>

            <c:if test="${not empty exp.numSamples}">
                <tr>
                    <td style="text-align:right;">Samples:</td>
                    <td>${exp.numSamples}</td>
                </tr>
            </c:if>

            <tr>
                <td style="text-align:right;">ArrayExpress:</td>
                <td>
                    <a href="http://www.ebi.ac.uk/arrayexpress/experiments/${exp.accession}"
                       title="Experiment information and full data in ArrayExpress Archive"
                       class="external" rel="nofollow" target="_blank">${exp.accession}</a>
                </td>
            </tr>
        </table>
    </div>
    <ul style="padding-left:15px">
        <li><a href="${pageContext.request.contextPath}/experimentDesign/${exp.accession}"
               style="font-size:12px;font-weight:bold;">experiment design</a></li>
        <!--
        <li><a href="#" style="font-size:12px;font-weight:bold;">similiarity</a></li>
        -->
    </ul>
    <ul style="padding-left:15px">
        <c:if test="${empty experimentDesign}">
            <c:import url="gallery.jsp"></c:import>
        </c:if>
    </ul>
</div>


    
