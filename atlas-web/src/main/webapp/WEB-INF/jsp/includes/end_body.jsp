<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ebi.ac.uk/ae3/functions" prefix="u" %>
<div style="height:57px;">&nbsp;</div>
<table class="footerpane" id="footerpane" summary="The main footer pane of the page" style="position:fixed; bottom: 0px; z-index:10;">
    <tr>
      <td class="footerrow" width="90%">
            
            <iframe src="http://www.ebi.ac.uk/inc/foot.html" name="foot" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  height="22px" width="100%"  style="z-index:2;"></iframe>
      </td>
      <td style="text-align:right;width:10%;white-space:nowrap;padding-right:10px;" id="divFooter">

      </td>
        <script type="text/javascript">
           document.getElementById("divFooter").innerHTML = "Gene Expression Atlas ${u:getProp('atlas.software.version')} Build <c:out value="${u:getProp('atlas.buildNumber')}"/>";
        </script>
    </tr>
</table>

<script src="http://www.ebi.ac.uk/inc/js/footer.js" type="text/javascript"></script>

${atlas.googleanalytics.script}
</body>
</html>