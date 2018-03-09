<%@ page import="org.iisg.eca.domain.Order; org.iisg.eca.utils.PaymentStatistic" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
<div class="title-menu-link">
    <span class="ui-icon ui-icon-carat-1-e"></span>
    <a href="#statistics"><g:message code="payment.go.to.statistics.label" /></a>
</div>

<g:set var="back" value="${params.back ? "&back=${params.back}" : ''}"/>
<ul class="payments-filters">
    <li><a href="?filter=all${back}"><g:message code="payment.show.all" /></a></li>
    <li><a href="?filter=notPayed${back}"><g:message code="payment.show.not.payed" /></a></li>
    <li><a href="?filter=notCompleted${back}"><g:message code="payment.show.not.completed" /></a></li>
</ul>

<div class="tbl_container">
    <input type="hidden" name="url" value="${eca.createLink(controller: 'participant', action: 'show', id: 0)}" />

    <div class="menu">
        <ul>
            <li><a href="">Open link</a></li>
            <li><a href="" target="_blank">Open link in new tab</a></li>
        </ul>
    </div>

    <table class="clear">
        <thead class="no-filters">
            <tr>
                <th class="counter"></th>
                <th class="id hidden"></th>
                <th><g:message code="user.lastName.label" /></th>
                <th><g:message code="user.firstName.label" /></th>
                <th><g:message code="payment.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantState.label" /></th>
            </tr>
        </thead>

        <tbody>
        <g:each in="${paymentsList}" var="row" status="i">
            <tr>
                <td class="counter">${i+1}</td>
                <td class="id hidden">${row.get('user_id')}</td>
                <td>${row.get('lastname')}</td>
                <td>${row.get('firstname')}</td>

                <g:if test="${row.get('payed') == null || row.get('payed') != Order.ORDER_PAYED}">
                    <td class="warning">
                        <g:if test="${row.get('payed') == null}">
                            <g:message code="payment.no.attempt.label" />
                        </g:if>
                        <g:elseif test="${row.get('payed') == Order.ORDER_REFUND_OGONE || row.get('payed') == Order.ORDER_REFUND_BANK}">
                            <g:message code="payment.refund.label" />
                        </g:elseif>
                        <g:else>
                            <g:message code="payment.not.completed.label" />
                        </g:else>
                    </td>
                </g:if>
                <g:else>
                    <td>
                        <g:if test="${row.get('amount') == 0}">
                            <g:message code="order.free.label" />
                        </g:if>
                        <g:elseif test="${row.get('payment_method') == Order.ORDER_BANK_PAYMENT}">
                            <g:message code="order.method.bank.label" />
                        </g:elseif>
                        <g:elseif test="${row.get('payment_method') == Order.ORDER_CASH_PAYMENT}">
                            <g:message code="order.method.cash.label" />
                        </g:elseif>
                        <g:else>
                            <g:message code="order.method.ogone.label" />
                        </g:else>
                    </td>
                </g:else>

                <td>
                    <g:if test="${row.get('payed') == 1}">
                        <eca:getAmount amount="${row.get('amount') - row.get('refunded_amount')}" cents="${true}" />
                    </g:if>
                </td>

                <td class="warning">
                    <g:if test="${row.get('participant_state_id') != 1 && row.get('participant_state_id') != 2}">
                        ${row.get('participant_state')}
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<br /><br />

<a name="statistics"></a>
<div class="tbl_container">
    <h3><g:message code="payment.by.payment.method.label" /></h3>

    <table class="payment-statistics">
        <thead>
            <tr>
                <th>&nbsp;</th>
                <th colspan="2"><g:message code="payment.unconfirmed.label" /></th>
                <th colspan="2"><g:message code="payment.confirmed.label" /></th>
                <th colspan="2"><g:message code="payment.refunded.label" /></th>
            </tr>
            <tr class="subheader">
                <th>&nbsp;</th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="left"><g:message code="order.free.label" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(3L, paymentMethod).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(3L, paymentMethod).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(3L, paymentMethod).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).refundedAmount}" cents="${true}" /></td>
            </tr>
            <tr>
                <td class="left"><g:message code="order.method.ogone.label" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(0L, paymentMethod).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(0L, paymentMethod).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(0L, paymentMethod).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(0L, paymentMethod).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(0L, paymentMethod).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(0L, paymentMethod).refundedAmount}" cents="${true}" /></td>
            </tr>
            <tr>
                <td class="left"><g:message code="order.method.bank.label" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(1L, paymentMethod).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(1L, paymentMethod).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(1L, paymentMethod).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(1L, paymentMethod).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(1L, paymentMethod).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(1L, paymentMethod).refundedAmount}" cents="${true}" /></td>
            </tr>
            <tr>
                <td class="left"><g:message code="order.method.cash.label" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(2L, paymentMethod).refundedAmount}" cents="${true}" /></td>
            </tr>
            <tr class="tbl_totals">
                <td class="left"><g:message code="default.total" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(paymentMethod).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(paymentMethod).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(paymentMethod).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(paymentMethod).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(paymentMethod).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(paymentMethod).refundedAmount}" cents="${true}" /></td>
            </tr>
        </tbody>
    </table>
</div>

<br /><br />

<div class="tbl_container">
    <h3><g:message code="payment.by.amount.label" /></h3>

    <table class="payment-statistics">
        <thead>
            <tr>
                <th>&nbsp;</th>
                <th colspan="2"><g:message code="payment.unconfirmed.label" /></th>
                <th colspan="2"><g:message code="payment.confirmed.label" /></th>
                <th colspan="2"><g:message code="payment.refunded.label" /></th>
            </tr>
            <tr class="subheader">
                <th>&nbsp;</th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${paymentAmountsList}" var="row">
                <tr>
                    <td class="left"><eca:getAmount amount="${row.get('amount')}" cents="${true}" /></td>

                    <td>${PaymentStatistic.getPaymentStatistic(row.get('amount'), paymentAmount).unConfirmedNoParticipants}</td>
                    <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(row.get('amount'), paymentAmount).unConfirmedAmount}" cents="${true}" /></td>

                    <td>${PaymentStatistic.getPaymentStatistic(row.get('amount'), paymentAmount).confirmedNoParticipants}</td>
                    <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(row.get('amount'), paymentAmount).confirmedAmount}" cents="${true}" /></td>

                    <td>${PaymentStatistic.getPaymentStatistic(row.get('amount'), paymentAmount).refundedNoParticipants}</td>
                    <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(row.get('amount'), paymentAmount).refundedAmount}" cents="${true}" /></td>
                </tr>
            </g:each>

            <tr class="tbl_totals">
                <td class="left"><g:message code="default.total" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(paymentAmount).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(paymentAmount).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(paymentAmount).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(paymentAmount).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(paymentAmount).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(paymentAmount).refundedAmount}" cents="${true}" /></td>
            </tr>
        </tbody>
    </table>
</div>

<br /><br />

<div class="tbl_container">
    <h3><g:message code="payment.by.participant.state.label" /></h3>

    <table class="payment-statistics">
        <thead>
            <tr>
                <th>&nbsp;</th>
                <th colspan="2"><g:message code="payment.unconfirmed.label" /></th>
                <th colspan="2"><g:message code="payment.confirmed.label" /></th>
                <th colspan="2"><g:message code="payment.refunded.label" /></th>
            </tr>
            <tr class="subheader">
                <th>&nbsp;</th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
                <th><g:message code="participantDate.total.label" /></th>
                <th><g:message code="order.amount.label" /></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="left">Participant data checked, Participant (1, 2)</td>

                <td>${PaymentStatistic.getPaymentStatistic(1L, participantState).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(1L, participantState).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(1L, participantState).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(1L, participantState).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(1L, participantState).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(1L, participantState).refundedAmount}" cents="${true}" /></td>
            </tr>
            <tr>
                <td class="left"><g:message code="participantDate.other.state.label" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(0L, participantState).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(0L, participantState).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(0L, participantState).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(0L, participantState).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getPaymentStatistic(0L, participantState).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getPaymentStatistic(0L, participantState).refundedAmount}" cents="${true}" /></td>
            </tr>
            <tr class="tbl_totals">
                <td class="left"><g:message code="default.total" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(participantState).unConfirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(participantState).unConfirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(participantState).confirmedNoParticipants}</td>
                <td class="line"><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(participantState).confirmedAmount}" cents="${true}" /></td>

                <td>${PaymentStatistic.getTotalPaymentStatistic(participantState).refundedNoParticipants}</td>
                <td><eca:getAmount amount="${PaymentStatistic.getTotalPaymentStatistic(participantState).refundedAmount}" cents="${true}" /></td>
            </tr>
        </tbody>
    </table>
</div>

<br /><br />

<div class="tbl_container">
    <h3><g:message code="payment.total.participants.label" /></h3>

    <table class="payment-statistics">
        <thead>
            <tr class="subheader">
                <th>&nbsp;</th>
                <th><g:message code="participantDate.total.label" /></th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
                <th>&nbsp;</th>
            </tr>
        </thead>
        <tbody class="line">
            <tr>
                <td class="left"><g:message code="payment.number.payed.label" /></td>
                <td>${participantsTotalPayed.get('no_participants')}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td class="left"><g:message code="payment.number.not.completed.label" /></td>
                <td>${participantsTotalNotCompleted.get('no_participants')}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td class="left"><g:message code="payment.number.no.attempt.label" /></td>
                <td>${participantsTotalNoAttempt.get('no_participants')}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr>
                <td class="left"><g:message code="payment.number.refunded.label" /></td>
                <td>${participantsTotalRefunded.get('no_participants')}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <tr class="tbl_totals">
                <td class="left"><g:message code="default.total" /></td>
                <td>${participantsTotal.get('no_participants')}</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
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