<%@ page import="org.iisg.eca.domain.AgeRange; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order; org.iisg.eca.domain.PaperReview; org.iisg.eca.domain.ReviewCriteria" %>
<div id="emails-tab">
    <h3><g:message code="email.not.yet.sent.label" /></h3>

    <div id="emails-not-sent">
        <g:if test="${emailsNotSent.size() == 0}">
            <span><g:message code="default.search.empty.message" /></span>
        </g:if>
        <g:each in="${emailsNotSent}" var="email" >
            <span class="emailHeader"><a href="#">
                ${email.subject} (<g:message code="email.date.created.label" />: <g:formatDate date="${email.dateTimeCreated}" formatName="default.date.time.format" />)
            </a></span>
            <ol class="property-list email-content">
                <input type="hidden" name="email-id" value="${email.id}" />
                <li>
                    <span id="original-sent-label" class="property-label">
                        <g:message code="email.original.sent.label" />
                    </span>
                    <span class="property-value" arial-labelledby="original-sent-label">
                        <span class="inline-button resend-email">
                            <g:message code="email.send.now.label" />
                        </span>
                    </span>
                </li>
                <li>
                    <span id="copies-sent-label" class="property-label">
                        <g:message code="email.copies.sent.label" />
                    </span>
                    <span class="property-value" arial-labelledby="copies-sent-label"></span>
                </li>
                <li>
                    <span id="from-label" class="property-label">
                        <g:message code="email.from.label" />
                    </span>
                    <span class="property-value" arial-labelledby="from-label"></span>
                </li>
                <li>
                    <span id="subject-label" class="property-label">
                        <g:message code="email.subject.label" />
                    </span>
                    <span class="property-value" arial-labelledby="subject-label"></span>
                </li>
                <li>
                    <span id="body-label" class="property-label">
                        <g:message code="email.body.label" />
                    </span>
                    <span class="property-value" arial-labelledby="body-label"></span>
                </li>
            </ol>
        </g:each>
    </div>

    <h3><g:message code="email.sent.label" /></h3>

    <div id="emails-sent">
        <g:if test="${emailsSent.size() == 0}">
            <span><g:message code="default.search.empty.message" /></span>
        </g:if>
        <g:each in="${emailsSent}" var="email" >
            <span class="emailHeader"><a href="#">
                ${email.subject} (<g:message code="email.date.sent.label" />: <g:formatDate date="${email.dateTimeSent}" formatName="default.date.time.format" />)
            </a></span>
            <ol class="property-list email-content">
                <input type="hidden" name="email-id" value="${email.id}" />
                <li>
                    <span id="original-sent-label" class="property-label">
                        <g:message code="email.original.sent.label" />
                    </span>
                    <span class="property-value" arial-labelledby="original-sent-label">
                        <span class="inline-button resend-email">
                            <g:message code="email.resend.label" />
                        </span>
                    </span>
                </li>
                <li>
                    <span id="copies-sent-label" class="property-label">
                        <g:message code="email.copies.sent.label" />
                    </span>
                    <span class="property-value" arial-labelledby="copies-sent-label"></span>
                </li>
                <li>
                    <span id="from-label" class="property-label">
                        <g:message code="email.from.label" />
                    </span>
                    <span class="property-value" arial-labelledby="from-label"></span>
                </li>
                <li>
                    <span id="subject-label" class="property-label">
                        <g:message code="email.subject.label" />
                    </span>
                    <span class="property-value" arial-labelledby="subject-label"></span>
                </li>
                <li>
                    <span id="body-label" class="property-label">
                        <g:message code="email.body.label" />
                    </span>
                    <span class="property-value" arial-labelledby="body-label"></span>
                </li>
            </ol>
        </g:each>
    </div>

    <input type="hidden" name="user-id" value="${user.id}" />
    <div id="resend-registration-email-conainer">
        <span class="inline-button resend-registration-email">
            <g:message code="email.resend.registration.label" />
        </span>
    </div>
</div>