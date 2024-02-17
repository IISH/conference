<%@ page import="org.iisg.eca.domain.AgeRange; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order; org.iisg.eca.domain.PaperReview; org.iisg.eca.domain.ReviewCriteria" %>
<div id="sessions-tab" class="columns">
    <g:if test="${participant}">
        <div id="selected-days">
            <span class="header">
                <g:message code="participantDay.is.present.label" />
            </span>

            <g:if test="${daysPresent.size() == 0}">
                <span class="not-found">
                    <g:message code="participantDay.not.found.label" />
                </span>
            </g:if>

            <ol class="list-days-present">
                <g:each in="${daysPresent}" var="day">
                    <li><eca:formatText text="${day.toString()}" /></li>
                </g:each>
            </ol>

            <span class="link change-present-days">
                <a><g:message code="default.change.label" args="${[g.message(code: 'participantDay.multiple.label')]}" /></a>
            </span>
        </div>
    </g:if>

    <g:each in="${sessions}" var="participantSessionInfo">
        <div class="participant-session column">
            <div>
                <ol class="property-list">
                    <li>
                        <span id="session-id-label" class="property-label">#</span>
                        <span class="property-value" arial-labelledby="session-id-label">${participantSessionInfo.session.id}</span>
                    </li>
                    <li>
                        <span id="code-label" class="property-label">
                            <g:message code="session.code.label" />
                        </span>
                        <span class="property-value" arial-labelledby="code-label">
                            <eca:formatText text="${participantSessionInfo.session.code}" />
                        </span>
                    </li>
                    <li>
                        <span id="name-label" class="property-label">
                            <g:message code="session.name.label" />
                        </span>
                        <span class="property-value" arial-labelledby="name-label">
                            <eca:link controller="session" action="show" id="${participantSessionInfo.session.id}">
                                <eca:formatText text="${participantSessionInfo.session.name}" />
                            </eca:link>
                        </span>
                    </li>
                    <li>
                        <span id="state-label" class="property-label">
                            <g:message code="session.state.label" />
                        </span>
                        <span class="property-value" arial-labelledby="state-label">
                            <eca:formatText text="${participantSessionInfo.session.state}" />
                        </span>
                    </li>
                    <li>
                        <span id="type-label" class="property-label">
                            <g:message code="participantType.function.label" />
                        </span>
                        <span class="property-value" arial-labelledby="type-label">
                            <ol>
                                <g:each in="${participantSessionInfo.types}" var="type">
                                    <li>${type.toString()}</li>
                                </g:each>
                            </ol>
                        </span>
                    </li>
                    <li>
                        <span id="planned-date-label" class="property-label">
                            <g:message code="session.planned.label" />
                        </span>
                        <span class="property-value" arial-labelledby="planned-date-label">
                            <g:set var="sessionDateTime" value="${participantSessionInfo.session.getPlannedDateTime()}" />
                            <g:if test="${sessionDateTime}">
                                <eca:formatText text="${sessionDateTime.day.toString()}" />
                                (<eca:formatText text="${sessionDateTime.period.toString()}" />)
                            </g:if>
                            <g:else>
                                <g:message code="session.not.planned.label" />
                            </g:else>
                        </span>
                    </li>
                </ol>
            </div>
        </div>
    </g:each>
</div>