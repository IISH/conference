<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>${page.toString()}</title>
    </head>
    <body>
        <form method="post" action="#">
            <fieldset class="form">
                <div class="fieldcontain ${hasErrors(bean: feeState, field: 'name', 'error')} required">
                    <label>
                        <g:message code="feestate.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" name="feeState.name" maxlength="50" required="required" value="${fieldValue(bean: feeState, field: 'name')}" />
                </div>

                <div class="fieldcontain ${hasErrors(bean: feeState, field: 'isDefaultFee', 'error')} ">
                    <label>
                        <g:message code="feestate.isdefaultfee.label" />
                    </label>
                    <g:checkBox name="feeState.isDefaultFee" value="${feeState?.isDefaultFee}" />
                </div>

                <div class="fieldcontain ${hasErrors(bean: feeState, field: 'enabled', 'error')} ">
                    <label>
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox name="feeState.enabled" value="${fee?.enabled}" />
                </div>

                <div class="columns copy">
                <g:each in="${feeState.feeAmounts}" var="amount" status="i">
                    <div class="column">
                        <input type="hidden" name="id_${i}" value="${amount?.id}" />

                        <div class="fieldcontain ${hasErrors(bean: amount, field: 'feeAmount', 'error')} required">
                            <label>
                                <g:message code="feeamount.feeamount.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:field type="number" name="feeAmount.feeAmount_${i}" min="0" required="required" value="${fieldValue(bean: amount, field: 'feeAmount')}" />
                        </div>

                        <div class="fieldcontain ${hasErrors(bean: amount, field: 'endDate', 'error')} required">
                            <label>
                                <g:message code="feeamount.enddate.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input name="feeAmount.endDate_${i}" value="${amount?.endDate}" class="datepicker" type="text" />
                        </div>

                        <div class="fieldcontain ${hasErrors(bean: amount, field: 'numDaysStart', 'error')} required">
                            <label>
                                <g:message code="feeamount.numdaysstart.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select from="${1..days}" name="feeAmount.numDaysStart_${i}" required="required" value="${fieldValue(bean: amount, field: 'numDaysStart')}" />
                        </div>

                        <div class="fieldcontain ${hasErrors(bean: amount, field: 'numDaysEnd', 'error')} required">
                            <label>
                                <g:message code="feeamount.numdaysend.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select from="${1..days}" name="feeAmount.numDaysStart_null" required="required" value="${fieldValue(bean: amount, field: 'numDaysEnd')}" />
                        </div>

                        <div class="fieldcontain ${hasErrors(bean: amount, field: 'enabled', 'error')} ">
                            <label for="enabled">
                                <g:message code="default.enabled.label" />
                            </label>
                            <g:checkBox name="feeAmount.enabled_${i}" value="${amount?.enabled}" />
                        </div>
                    </div>
                </g:each>
                    <div class="column hidden">
                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.feeamount.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:field type="number" name="feeAmount.feeAmount_null" min="0" required="required" />
                        </div>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.enddate.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input name="feeAmount.endDate_null" class="datepicker" type="text" />
                        </div>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.numdaysstart.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select from="${1..days}" name="feeAmount.numDaysStart_null" required="required" />
                        </div>

                        <div class="fieldcontain required">
                            <label>
                                <g:message code="feeamount.numdaysend.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select from="${1..days}" name="feeAmount.numDaysEnd_null" required="required" />
                        </div>

                        <div class="fieldcontain">
                            <label for="enabled">
                                <g:message code="default.enabled.label" />
                            </label>
                            <g:checkBox name="feeAmount.enabled_null" />
                        </div>
                    </div>
                    <div class="clear empty"></div>
                </div>
            </fieldset>
            <fieldset class="buttons">
                <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'feeamount.label')])}">
            </fieldset>
        </form>
    </body>
</html>
