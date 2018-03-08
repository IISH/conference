<%@ page import="org.iisg.eca.domain.AgeRange; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order; org.iisg.eca.domain.PaperReview; org.iisg.eca.domain.ReviewCriteria" %>
<g:if test="${participant}">
    <div id="edit-days" class="info">
        <input type="hidden" name="user-id" value="${user.id}" />
        <form method="post" action="#">
            <fieldset class="form">
                <g:each in="${Day.list()}" var="day">
                    <div>
                        <span class="property-label">
                            <g:checkBox name="day" value="${day.id}" id="edit-day-${day.id}" checked="${user.daysPresent.find { it.day.id == day.id }}" />
                        </span>
                        <label class="property-value" for="edit-day-${day.id}">
                            ${day.toString()}
                        </label>
                    </div>
                </g:each>
            </fieldset>
        </form>
    </div>

    <div id="refund-payment" class="info">
        <form method="post" action="#">
            <fieldset class="form">
                <div>
                    <label class="property-label">
                        <g:message code="order.refunded.amount.label" />
                    </label>
                    <span class="property-value">
                        <g:field type="text" name="amount" required="required" value="" />
                    </span>
                </div>
            </fieldset>
        </form>
    </div>

    <div id="new-order" class="info">
        <form method="post" action="../newOrder" id="new-order-form">
            <fieldset class="form">
                <input type="hidden" name="participantId" value="${participant.id}" />
                <div>
                    <label class="property-label">
                        <g:message code="order.amount.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <g:field type="text" name="amount" required="required" value="" />
                    </span>
                </div>
                <div>
                    <span class="property-label"></span>
                    <span class="property-value">
                        <g:radio id="bank-method" name="method" value="${Order.ORDER_BANK_PAYMENT}" checked="${true}" />
                        <label class="property-value" for="bank-method">
                            <g:message code="order.method.bank.label" />
                        </label>
                    </span>
                </div>
                <div>
                    <span class="property-label"></span>
                    <span class="property-value">
                        <g:radio id="cash-method" name="method" value="${Order.ORDER_CASH_PAYMENT}" />
                        <label class="property-value" for="cash-method">
                            <g:message code="order.method.cash.label" />
                        </label>
                    </span>
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="order.description.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="text" name="description" value="${curDate.getShortNameAndYear() + ' payment'}" maxlength="100" />
                    </span>
                </div>
                <div>
                    <span class="property-label"></span>
                    <span class="property-value">
                        <g:radio id="status-not-payed" name="status" value="${Order.ORDER_NOT_PAYED}" checked="${true}" />
                        <label class="property-value" for="status-not-payed">
                            <g:message code="order.status.not.payed.label" />
                        </label>
                    </span>
                </div>
                <div>
                    <span class="property-label"></span>
                    <span class="property-value">
                        <g:radio id="status-payed" name="status" value="${Order.ORDER_PAYED}" />
                        <label class="property-value" for="status-payed">
                            <g:message code="order.status.payed.label" />
                        </label>
                    </span>
                </div>
            </fieldset>
        </form>
    </div>

    <div id="paper-review" class="info">
        <form method="post" action="#">
            <fieldset class="form">
                <input type="hidden" name="id" value="" />
                <g:each in="${ReviewCriteria.list()}" var="criteria">
                    <div>
                        <label class="property-label">
                            ${criteria.name}
                        </label>
                        <span class="property-value">
                            <g:field type="text" name="criteria_${criteria.id}" value="" />
                        </span>
                    </div>
                </g:each>
                <div>
                    <label class="property-label">
                        <g:message code="paper.review.label" />
                    </label>
                    <span class="property-value">
                        <textarea name="review" cols="40" rows="5"></textarea>
                    </span>
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="paper.reviewComments.label" />
                    </label>
                    <span class="property-value">
                        <textarea name="reviewComments" cols="40" rows="5"></textarea>
                    </span>
                </div>
                <div>
                    <span class="property-label">
                        <g:checkBox name="reviewAward"/>
                    </span>
                    <label class="property-value">
                        <g:message code="paper.reviewAward.label" />
                    </label>
                </div>
            </fieldset>
        </form>
    </div>
</g:if>