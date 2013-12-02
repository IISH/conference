<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <g:each in="${feeStates}" var="feeState">
            <h3>${feeState.toString()}</h3>

            <g:if test="${ feeState.isDefaultFee }">
                <span class="action"><g:message code="feeState.isDefaultFee.label" /></span>
            </g:if>

            <span class="action">
                <eca:ifUserHasAccess controller="${params.controller}" action="edit">
                    <eca:link controller="fee" action="edit" id="${feeState.id}">
                        <g:message code="default.edit.label" args="[g.message(code: 'feeState.label')]" />
                    </eca:link>
                </eca:ifUserHasAccess>
            </span>

            <table class="fee-list">
                <thead>
                    <tr>
                        <td class="id hidden"></td>
                        <td><g:message code="feeAmount.numDays.label" /></td>
                        <td><g:message code="feeAmount.endDate.label" /></td>
                        <td><g:message code="feeAmount.feeAmount.label" /></td>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${feeAmounts.get(feeState)}" var="feeAmount">
                        <tr>
                            <td class="id hidden">${feeAmount.id}</td>
                            <td><g:fieldValue bean="${feeAmount}" field="numDays" /></td>
                            <td><g:fieldValue bean="${feeAmount}" field="endDate" /></td>
                            <td><eca:getAmount amount="${feeAmount.feeAmount}" /></td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </g:each>

        <div class="buttons">
            <eca:link previous="true">
                <g:message code="default.button.back.label" />
            </eca:link>
            <eca:ifUserHasAccess controller="${params.controller}" action="create">
                <eca:link controller="${params.controller}" action="create">
                    <g:message code="default.button.create.label" />
                </eca:link>
            </eca:ifUserHasAccess>
        </div>
    </body>
</html>
