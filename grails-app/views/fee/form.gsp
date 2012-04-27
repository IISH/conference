<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>${page.toString()}</title>
    </head>
    <body>
        <g:hasErrors bean="${feeState}">
            <ul class="errors" role="alert">
                <g:eachError bean="${feeState}" var="error">
                    <li><g:message error="${error}" /></li>
                </g:eachError>
             </ul>
        </g:hasErrors>

        <form method="post" action="#">
            <fieldset class="form">
                <div class="${hasErrors(bean: feeState, field: 'name', 'error')} required">
                    <label>
                        <g:message code="feestate.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" name="FeeState.name" maxlength="50" required="required" value="${fieldValue(bean: feeState, field: 'name')}" />
                </div>

                <div class="${hasErrors(bean: feeState, field: 'isDefaultFee', 'error')} ">
                    <label>
                        <g:message code="feestate.isdefaultfee.label" />
                    </label>
                    <g:checkBox name="FeeState.isDefaultFee" value="${feeState?.isDefaultFee}" />
                </div>

                <div class="${hasErrors(bean: feeState, field: 'enabled', 'error')} ">
                    <label>
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox name="FeeState.enabled" value="${fee?.enabled}" />
                </div>

                <div class="columns copy">
                    <g:each in="${feeState.feeAmounts}" var="amount" status="i">
                        <div class="column">
                            <span class="ui-icon ui-icon-circle-minus"></span>
                            <input type="hidden" name="FeeAmount_${i}.id" value="${amount?.id}" />

                            <div class="fieldcontain ${hasErrors(bean: amount, field: 'feeAmount', 'error')} required">
                                <label>
                                    <g:message code="feeamount.feeamount.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:field type="number" name="FeeAmount_${i}.feeAmount" min="0" required="required" value="${fieldValue(bean: amount, field: 'feeAmount')}" />
                            </div>

                            <div class="fieldcontain ${hasErrors(bean: amount, field: 'endDate', 'error')} required">
                                <label>
                                    <g:message code="feeamount.enddate.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input name="FeeAmount_${i}.endDate" value="${amount?.endDate}" class="datepicker" type="text" />
                            </div>

                            <div class="fieldcontain ${hasErrors(bean: amount, field: 'numDaysStart', 'error')} required">
                                <label>
                                    <g:message code="feeamount.numdaysstart.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:select from="${1..days}" name="FeeAmount_${i}.numDaysStart" required="required" value="${fieldValue(bean: amount, field: 'numDaysStart')}" />
                            </div>

                            <div class="fieldcontain ${hasErrors(bean: amount, field: 'numDaysEnd', 'error')} required">
                                <label>
                                    <g:message code="feeamount.numdaysend.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:select from="${1..days}" name="FeeAmount_${i}.numDaysStart" required="required" value="${fieldValue(bean: amount, field: 'numDaysEnd')}" />
                            </div>

                            <div class="fieldcontain ${hasErrors(bean: amount, field: 'enabled', 'error')} ">
                                <label for="enabled">
                                    <g:message code="default.enabled.label" />
                                </label>
                                <g:checkBox name="FeeAmount_${i}.enabled" value="${amount?.enabled}" />
                            </div>
                        </div>
                    </g:each>
                    <div class="column hidden">
                        <span class="ui-icon ui-icon-circle-minus"></span>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.feeamount.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:field type="number" name="FeeAmount_null.feeAmount" min="0" required="required" />
                        </div>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.enddate.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input name="FeeAmount_null.endDate" class="datepicker" type="text" />
                        </div>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.numdaysstart.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select from="${1..days}" name="FeeAmount_null.numDaysStart" required="required" />
                        </div>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.numdaysend.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select from="${1..days}" name="FeeAmount_null.numDaysEnd" required="required" />
                        </div>

                        <div class="fieldcontain">
                            <label for="enabled">
                                <g:message code="default.enabled.label" />
                            </label>
                            <g:checkBox name="FeeAmount_null.enabled" />
                        </div>
                    </div>
                    <div class="clear empty"></div>
                </div>
            </fieldset>
            <fieldset class="buttons">
                <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <eca:link action="delete">
                    <g:message code="default.button.delete.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'feeamount.label')])}">
            </fieldset>
        </form>
    </body>
</html>
