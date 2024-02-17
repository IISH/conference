<%@ page import="org.iisg.eca.domain.AgeRange; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order; org.iisg.eca.domain.PaperReview; org.iisg.eca.domain.ReviewCriteria" %>
<div id="payments-tab" class="columns">
    <g:set var="amounts" value="${[]}" />
    <g:set var="hidePayedButton" value="${false}" />

    <g:each in="${orders}" var="order">
        <div class="participant-order column">
            <div>
                <ol class="property-list">
                    <li>
                        <span id="order-id-label" class="property-label">#</span>
                        <span class="property-value" arial-labelledby="order-id-label">${order.id}</span>
                    </li>
                    <li>
                        <span id="order-code-label" class="property-label">
                            <g:message code="order.code.label" />
                        </span>
                        <span class="property-value" arial-labelledby="order-code-label">
                            <eca:formatText text="${order.orderCode}" />
                        </span>
                    </li>
                    <li>
                        <span id="amount-label" class="property-label">
                            <g:message code="order.amount.label" />
                        </span>
                        <span class="property-value" arial-labelledby="amount-label">
                            <eca:formatText text="${order.getAmountAsBigDecimal()}" />
                        </span>
                    </li>

                    <g:if test="${order.payed == Order.ORDER_REFUND_OGONE || order.payed == Order.ORDER_REFUND_BANK}">
                        <li>
                            <span id="refunded=amount-label" class="property-label">
                                <g:message code="order.refunded.amount.label" />
                            </span>
                            <span class="property-value" arial-labelledby="amount-label">
                                <eca:formatText text="${order.getRefundedAmountAsBigDecimal()}" />
                            </span>
                        </li>

                        <li>
                            <span id="refunded-label" class="property-label">
                                <g:message code="order.refunded.at.label" />
                            </span>
                            <span class="property-value" arial-labelledby="refunded-label">
                                <g:formatDate date="${order.refundedAt}" formatName="default.date.time.format" />
                            </span>
                        </li>
                    </g:if>

                    <li>
                        <span id="status-label" class="property-label">
                            <g:message code="order.status.label" />
                        </span>

                        <g:set var="classStatus" value="red" />
                        <g:if test="${order.payed == Order.ORDER_PAYED}">
                            <g:set var="classStatus" value="green" />
                        </g:if>
                        <g:elseif test="${order.paymentMethod != Order.ORDER_OGONE_PAYMENT ||
                                order.payed == Order.ORDER_REFUND_OGONE || order.payed == Order.ORDER_REFUND_BANK}">
                            <g:set var="classStatus" value="orange" />
                        </g:elseif>

                        <span class="property-value bold ${classStatus}" arial-labelledby="status-label">
                            ${order.getStatusText()}

                            <g:if test="${  (order.paymentMethod != Order.ORDER_OGONE_PAYMENT) &&
                                    (order.payed == Order.ORDER_NOT_PAYED) &&
                                    !hidePayedButton && !amounts.contains(order.amount)}">
                                <span class="inline-button order-set-payed">
                                    <g:message code="order.set.payed.label" />
                                </span>
                                <g:set var="amounts" value="${amounts + [order.amount]}" />
                            </g:if>

                            <g:if test="${order.payed == Order.ORDER_PAYED}">
                                <g:set var="hidePayedButton" value="${true}" />

                                <g:if test="${order.amount > 0}">
                                    <sec:ifAnyGranted roles="admin,superAdmin">
                                        <g:set var="refundBtnClass" value="inline-button order-refund-payment"/>
                                        <g:if test="${Setting.getSetting(Setting.SHOW_REFUND_DIALOG).booleanValue}">
                                            <g:set var="refundBtnClass" value="${refundBtnClass} refund-dialog"/>
                                        </g:if>
                                        <span class="${refundBtnClass}" data-order-id="${order.id}"
                                              data-amount="${order.standardAmountToBeRefunded}">
                                            <g:message code="order.refund.payment.label" />
                                        </span>
                                    </sec:ifAnyGranted>
                                </g:if>
                            </g:if>
                        </span>
                    </li>
                    <li>
                        <span id="method-label" class="property-label">
                            <g:message code="order.method.label" />
                        </span>
                        <span class="property-value" arial-labelledby="method-label">
                            ${order.getPaymentMethodText()}
                        </span>
                    </li>
                    <li>
                        <span id="created-label" class="property-label">
                            <g:message code="order.created.at.label" />
                        </span>
                        <span class="property-value" arial-labelledby="created-label">
                            <g:formatDate date="${order.createdAt}" formatName="default.date.time.format" />
                        </span>
                    </li>
                    <li>
                        <span id="updated-label" class="property-label">
                            <g:message code="order.updated.at.label" />
                        </span>
                        <span class="property-value" arial-labelledby="updated-label">
                            <g:formatDate date="${order.updatedAtPayWay}" formatName="default.date.time.format" />
                        </span>
                    </li>
                    <li>
                        <span id="description-label" class="property-label">
                            <g:message code="order.description.label" />
                        </span>
                        <span class="property-value" arial-labelledby="description-label">
                            <eca:formatText text="${order.description}" />
                        </span>
                    </li>
                </ol>
            </div>
        </div>
    </g:each>
</div>