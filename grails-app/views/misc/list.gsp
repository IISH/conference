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
                <thead>
                    <tr>
                        <th class="counter"></th>
                        <g:each in="${headers}" var="header" status="i">
                            <g:if test="${i == 0}">
                                <th class="id">
                            </g:if>
                            <g:else>
                                <th>
                            </g:else>

                                ${header}
                            </th>
                        </g:each>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${data}" var="row">
                        <tr>
                            <td class="counter">${i}</td>
                            <g:each in="${row.values()}" var="column" status="i">
                                <g:if test="${i == 0}">
                                    <td class="id">
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