<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
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
                    <label class="property-label">
                        <g:message code="feeState.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="feeState.name" maxlength="50" required="required" value="${fieldValue(bean: feeState, field: 'name')}" />
                </div>

                <div class="${hasErrors(bean: feeState, field: 'isDefaultFee', 'error')} ">
                    <label class="property-label">
                        <g:message code="feeState.isDefaultFee.label" />
                    </label>
                    <g:checkBox class="property-value"  name="feeState.isDefaultFee" value="${feeState?.isDefaultFee}" />
                </div>

                <div class="${hasErrors(bean: feeState, field: 'enabled', 'error')} ">
                    <label class="property-label">
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox class="property-value"  name="feeState.enabled" value="${fee?.enabled}" />
                </div>

                <div class="columns copy">
                    <g:each in="${feeState.feeAmounts}" var="amount" status="i">
                        <fieldset class="form column">
                            <span class="ui-icon ui-icon-circle-minus"></span>
                            <input type="hidden" name="feeAmount_${i}.id" value="${amount?.id}" />

                            <div class="${hasErrors(bean: amount, field: 'feeAmount', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.feeAmount.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:field class="property-value" type="number" name="feeAmount_${i}.feeAmount" min="0" required="required" value="${fieldValue(bean: amount, field: 'feeAmount')}" />
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'endDate', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.endDate.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <eca:dateField class="property-value" name="feeAmount_${i}.endDate" date="${amount?.endDate}" /> 
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'numDaysStart', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.numDaysStart.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:select class="property-value" from="${1..days}" name="feeAmount_${i}.numDaysStart" required="required" value="${fieldValue(bean: amount, field: 'numDaysStart')}" />
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'numDaysEnd', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.numDaysEnd.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:selec class="property-value"t from="${1..days}" name="feeAmount_${i}.numDaysStart" required="required" value="${fieldValue(bean: amount, field: 'numDaysEnd')}" />
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'enabled', 'error')} ">
                                <label class="property-label" for="enabled">
                                    <g:message code="default.enabled.label" />
                                </label>
                                <g:checkBox class="property-value" name="feeAmount_${i}.enabled" value="${amount?.enabled}" />
                            </div>
                        </fieldset>
                    </g:each>
                    <fieldset class="form column hidden">
                        <span class="ui-icon ui-icon-circle-minus"></span>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.feeAmount.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:field class="property-value" type="number" name="feeAmount_null.feeAmount" min="0" required="required" />
                        </div>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.endDate.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <eca:dateField class="property-value" name="feeAmount_null.endDate" /> 
                        </div>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.numDaysStart.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select class="property-value" from="${1..days}" name="feeAmount_null.numDaysStart" required="required" />
                        </div>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.numDaysEnd.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select class="property-value" from="${1..days}" name="feeAmount_null.numDaysEnd" required="required" />
                        </div>

                        <div>
                            <label class="property-label" for="enabled">
                                <g:message code="default.enabled.label" />
                            </label>
                            <g:checkBox class="property-value" name="feeAmount_null.enabled" />
                        </div>
                    </fieldset>
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
                <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'feeAmount.label')])}">
            </fieldset>
        </form>
    </body>
</html>
