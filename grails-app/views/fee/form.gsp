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
                    <span class="property-value">
                        <input type="text" name="feeState.name" maxlength="50" required="required" value="${fieldValue(bean: feeState, field: 'name')}" />
                    </span>
                </div>

                <div class="${hasErrors(bean: feeState, field: 'isDefaultFee', 'error')} ">
                    <label class="property-label">
                        <g:message code="feeState.isDefaultFee.label" />
                    </label>
                    <span class="property-value">
                        <g:checkBox name="feeState.isDefaultFee" value="${feeState?.isDefaultFee}" />
                    </span>
                </div>

                <div class="${hasErrors(bean: feeState, field: 'enabled', 'error')} ">
                    <label class="property-label">
                        <g:message code="default.enabled.label" />
                    </label>
                    <span class="property-value">
                        <g:checkBox name="feeState.enabled" value="${fee?.enabled}" />
                    </span>
                </div>

                <div class="columns copy">
                    <g:each in="${feeState.feeAmounts}" var="amount" status="i">
                        <fieldset class="form column">
                            <span class="remove-item">
                                <span class="ui-icon ui-icon-circle-minus"></span>
                                <g:message code="default.delete.label" args="[message(code: 'feeAmount.label').toLowerCase()]" />
                            </span>

                            <input type="hidden" name="feeAmount_${i}.id" value="${amount.id}" />

                            <div class="${hasErrors(bean: amount, field: 'feeAmount', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.feeAmount.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <g:field type="number" name="feeAmount_${i}.feeAmount" min="0" required="required" value="${fieldValue(bean: amount, field: 'feeAmount')}" />
                                </span>
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'endDate', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.endDate.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <eca:dateField name="feeAmount_${i}.endDate" date="${amount?.endDate}" /> 
                                </span>
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'numDaysStart', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.numDaysStart.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <g:select from="${1..days}" name="feeAmount_${i}.numDaysStart" required="required" value="${fieldValue(bean: amount, field: 'numDaysStart')}" />
                                </span>
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'numDaysEnd', 'error')} required">
                                <label class="property-label">
                                    <g:message code="feeAmount.numDaysEnd.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <g:select from="${1..days}" name="feeAmount_${i}.numDaysEnd" required="required" value="${fieldValue(bean: amount, field: 'numDaysEnd')}" />
                                </span>
                            </div>

                            <div class="${hasErrors(bean: amount, field: 'enabled', 'error')} ">
                                <label class="property-label">
                                    <g:message code="default.enabled.label" />
                                </label>
                                <span class="property-value">
                                    <g:checkBox name="feeAmount_${i}.enabled" value="${amount?.enabled}" />
                                </span>
                            </div>
                        </fieldset>
                    </g:each>
                    
                    <fieldset class="form column hidden">
                        <span class="remove-item">
                            <span class="ui-icon ui-icon-circle-minus"></span>
                            <g:message code="default.delete.label" args="[message(code: 'feeAmount.label').toLowerCase()]" />
                        </span>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.feeAmount.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <span class="property-value">
                                <g:field type="number" name="feeAmount_null.feeAmount" min="0" required="required" />
                            </span>
                        </div>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.endDate.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <span class="property-value">
                                <eca:dateField name="feeAmount_null.endDate" /> 
                            </span>
                        </div>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.numDaysStart.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <span class="property-value">
                                <g:select from="${1..days}" name="feeAmount_null.numDaysStart" required="required" />
                            </span>
                        </div>

                        <div class="required">
                            <label class="property-label">
                                <g:message code="feeAmount.numDaysEnd.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <span class="property-value">
                                <g:select from="${1..days}" name="feeAmount_null.numDaysEnd" required="required" />
                            </span>
                        </div>

                        <div>
                            <label class="property-label">
                                <g:message code="default.enabled.label" />
                            </label>
                            <span class="property-value">
                                <g:checkBox name="feeAmount_null.enabled" />
                            </span>
                        </div>
                    </fieldset>
                    <div class="clear empty"></div>
                </div>
            </fieldset>
            
            <fieldset class="buttons">
                <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <g:if test="${params.action != 'create'}">
                    <eca:link action="delete" id="${params.id}" class="btn_delete">
                        <g:message code="default.button.delete.label" />
                    </eca:link>
                </g:if>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <g:if test="${params.action != 'create'}">
                  <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
                </g:if>
                <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'feeAmount.label')])}">
            </fieldset>
        </form>
    </body>
</html>
