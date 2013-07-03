<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <h3>${info}</h3>

        <div class="tbl_container">
            <input type="hidden" name="url" value="${eca.createLink(controller: controller, action: action, id: 0)}" />
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
            <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                <g:message code="default.button.back.label" />
            </eca:link>
        </div>
    </body>
</html>