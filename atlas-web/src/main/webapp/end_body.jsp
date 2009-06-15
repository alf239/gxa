<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ebi.ac.uk/ae3/functions" prefix="u" %>
<span style="position:fixed;bottom:6px;right:0px;padding-right:10px;z-index:20;">Gene Expression Atlas ${u:getProp('atlas.software.version')} Build <c:out value="${u:getProp('atlas.buildNumber')}"/></span>

<table class="footerpane" id="footerpane" summary="The main footer pane of the page" style="position:fixed; bottom: 0px; z-index:10;">
    <tr>
      <td colspan ="4" class="footerrow">
        <div class="footerdiv" id="footerdiv">
            <iframe src="http://www.ebi.ac.uk/inc/foot.html" name="foot" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  height="22px" width="100%"  style="z-index:2;"></iframe>
        </div>
      </td>
    </tr>
</table>

<script src="http://www.ebi.ac.uk/inc/js/footer.js" type="text/javascript"></script>

${atlas.googleanalytics.script}
</body>
</html>