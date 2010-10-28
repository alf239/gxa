<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div style="float:right;margin:0 20px;">
    <a href="http://www.ebi.ac.uk/arrayexpress/experiments/${exp.accession}" target="_blank"
       title="Experiment information and full data in ArrayExpress Archive" class="geneName"
       style="vertical-align: baseline">${exp.accession}</a>

    <div style="border:1px black solid;padding:5px;">
        <table cellpadding="2" cellspacing="0" border="0">
            <tr>
                <td>Platform:</td>
                <td>
                    <c:forEach var="arrayDesign" items="${arrayDesigns}">
                        <a href="${pageContext.request.contextPath}/experiment/${exp.accession}?ad=${arrayDesign}">${arrayDesign}</a>&nbsp;
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <td>Organism:</td>
                <td>${exp.organism}</td>
            </tr>
            <tr>
                <td>Samples:</td>
                <td>${exp.numSamples}</td>
            </tr>
            <tr>
                <td>Individuals:</td>
                <td>${exp.numIndividuals}</td>
            </tr>
            <tr>
                <td>Study type:</td>
                <td>${exp.studyType}</td>
            </tr>
        </table>
    </div>
    <ul style="padding-left:15px">
        <li><a href="${pageContext.request.contextPath}/experimentDesign/${exp.accession}"
               style="font-size:12px;font-weight:bold;">experiment design</a></li>
        <li><a href="${pageContext.request.contextPath}/experiment/${exp.accession}"
               style="font-size:12px;font-weight:bold;">experiment analysis</a></li>
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


    
