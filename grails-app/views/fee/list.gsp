<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>${page.toString()}</title>
    </head>
    <body>
        <g:each in="${feeStates}" var="feeState">
            <h3>${feeState.toString()}</h3>

            <g:if test="${ feeState.isDefaultFee }">
                <span class="action"><g:message code="feestate.isdefaultfee.label" /></span>
            </g:if>

            <span class="action">
                <eca:link controller="fee" action="edit" id="${feeState.id}">
                    <g:message code="default.edit.label" args="[message(code: 'feestate.label').toLowerCase()]" />
                </eca:link>
            </span>

            <table>
                <thead>
                    <tr>
                        <td class="id hidden"></td>
                        <td><g:message code="feeamount.numdays.label" /></td>
                        <td><g:message code="feeamount.enddate.label" /></td>
                        <td><g:message code="feeamount.feeamount.label" /></td>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${feeAmounts.get(feeState)}" var="feeAmount">
                        <tr>
                            <td class="id hidden">${feeAmount.id}</td>
                            <td><g:fieldValue bean="${feeAmount}" field="numDays" /></td>
                            <td><g:fieldValue bean="${feeAmount}" field="endDate" /></td>
                            <td><g:fieldValue bean="${feeAmount}" field="feeAmount" /></td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </g:each>

        <div class="buttons">
            <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                <g:message code="default.button.back.label" />
            </eca:link>
            <eca:link controller="${params.controller}" action="create">
                <g:message code="default.button.create.label" />
            </eca:link>
        </div>
    </body>
</html>
