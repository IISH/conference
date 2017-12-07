<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <h3>${info}</h3>

        <div class="tbl_container">
            <g:if test="${controller && action}">
                <input type="hidden" name="url" value="${eca.createLink(controller: controller, action: action, id: 0)}" />
            </g:if>

            <div class="menu">
                <ul>
                    <li><a href="">Open link</a></li>
                    <li><a href="" target="_blank">Open link in new tab</a></li>
                </ul>
            </div>

            <g:if test="${export}">
                <div class="tbl_toolbar right">
                    <span>
                        <g:message code="default.export.data" />
                    </span>
                    <select class="export-data">
                        <option value="-1"> </option>
                        <option value="export=0&amp;format=csv&amp;sep=,">CSV (,)</option>
                        <option value="export=0&amp;format=csv&amp;sep=;">CSV (;)</option>
                        <option value="export=0&amp;format=csv&amp;sep=tab">CSV (tab)</option>
                        <option value="export=0&amp;format=xls">XLS</option>
                        <option value="export=0&amp;format=xml">XML</option>
                    </select>
                </div>
            </g:if>

            <table class="clear">
                <thead class="no-filters">
                    <tr>
                        <th class="counter"></th>
                        <g:each in="${headers}" var="header" status="i">
                            <g:if test="${i == 0}">
                                <th class="id hidden"></th>
                            </g:if>

                            <th>${header}</th>
                        </g:each>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${data}" var="row" status="i">
                        <tr>
                            <td class="counter">${i+1}</td>
                            <g:each in="${row.values()}" var="column" status="j">
                                <g:if test="${j == 0}">
                                    <td class="id hidden">
                                </g:if>
                                <g:else>
                                    <td>
                                </g:else>
                                    ${column}
                                </td>
                            </g:each>
                        </tr>
                    </g:each>
                </tbody>
             </table>
        </div>

        <div class="buttons">
            <eca:link previous="true">
                <g:message code="default.button.back.label" />
            </eca:link>
        </div>
    </body>
</html>
