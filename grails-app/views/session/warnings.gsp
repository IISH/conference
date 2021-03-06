<%@ page import="org.iisg.eca.domain.Setting; org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Order" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
</head>
<body>
    <input type="hidden" name="id" value="${params.id}" />
    <eca:navigation ids="${sessionIds}" />

    <ol class="property-list">
        <li>
            <span id="session-label" class="property-label">
                <g:message code="session.label" />
            </span>
            <span class="property-value" arial-labelledby="session-label">
                <form name="session-form" action="#" method="get">
                    <g:select id="session-select" name="sessionId" from="${sessions}" value="${params.id}" optionKey="id" />
                    <input type="button" id="btn_session" name="btn_session" value="${g.message(code: "default.goto")}" />
                </form>
            </span>
        </li>
        <g:if test="${Setting.getSetting(Setting.SHOW_SESSION_EXTRA_INFO).booleanValue}">
            <li>
                <span id="extra-info-label" class="property-label">
                    <g:message code="session.extraInfo.label" />
                </span>
                <span class="property-value" arial-labelledby="extra-info-label">
                    <eca:formatText text="${session.extraInfo}" />
                </span>
            </li>
        </g:if>
        <li>
            <span id="comment-label" class="property-label">
                <g:message code="session.comment.label" />
            </span>
            <span class="property-value" arial-labelledby="comment-label">
                <eca:formatText text="${session.comment}" />
            </span>
        </li>
        <li>
            <span id="participants-label" class="property-label">
                <g:message code="session.sessionParticipants.label" />
            </span>
            <ul class="session-participants property-value" arial-labelledby="participants-label">
                <g:each in="${participants}" var="participant" status="i">
                    <li>
                        <span class="participant-value">
                            <eca:link controller="participant" action="show" id="${participant.participant?.user?.id}">
                                ${participant.participant}
                            </eca:link>
                        </span>
                        <span class="participant-state-value">(${participant.participant?.state})</span>
                        <span class="participant-country-value">
                            <g:if test="${participant.participant?.user?.country}">
                                (${participant.participant.user.country})
                            </g:if>
                            <g:else>
                                (<g:message code="user.no.country.label" />)
                            </g:else>
                        </span>

                        <ul>
                            <g:each in="${participant.types}" var="type">
                                <li class="participant-type-value">
                                    <span class="participant-type-val">${type}</span>
                                </li>
                            </g:each>

                            <g:if test="${!participant.participant?.findOrder()}">
                                <li class="participant-warning">
                                    <span><g:message code="participantDate.noPaymentId.label" /></span>
                                </li>
                            </g:if>
                            <g:elseif test="${participant.participant?.findOrder()?.payed != Order.ORDER_PAYED}">
                                <li class="participant-warning">
                                    <span><g:message code="participantDate.paymentIdNoPayment.label" /></span>
                                </li>
                            </g:elseif>
                        </ul>

                        <g:if test="${participant.paper}">
                            <span class="participant-paper-value">
                                <span class="paper-text">
                                    <span class="l"><g:message code="paper.authorOf.label" />:</span>
                                    <span class="v">${participant.paper} (${participant.paper.state})</span>
                                </span>
                            </span>
                            <g:if test="${participant.paper.coAuthors && !participant.paper.coAuthors.isEmpty()}">
                                <span class="participant-paper-coauthor-value">
                                    <span class="l"><g:message code="paper.allCoAuthors.label" />:</span>
                                    <span class="v"><eca:formatText text="${participant.paper.coAuthors}" /></span>
                                </span>
                            </g:if>
                        </g:if>

                        <g:if test="${participant.paperCoAuthoring}">
                            <span class="participant-paper-coauthoring-value">
                                <span class="paper-text">
                                    <span class="l"><g:message code="paper.coAuthorOf.label" />:</span>
                                    <span class="v">${participant.paperCoAuthoring} (${participant.paperCoAuthoring.state})</span>
                                </span>
                            </span>
                            <g:if test="${participant.paperCoAuthoring.coAuthors && !participant.paperCoAuthoring.coAuthors.isEmpty()}">
                                <span class="participant-paper-coauthoring-coauthor-value">
                                    <span class="l"><g:message code="paper.allCoAuthors.label" />:</span>
                                    <span class="v"><eca:formatText text="${participant.paperCoAuthoring.coAuthors}" /></span>
                                </span>
                            </g:if>
                        </g:if>
                    </li>
                </g:each>
                <g:if test="${!session.getAllParticipantTypes().contains(ParticipantType.get(ParticipantType.AUTHOR))}">
                    <li class="participant-warning">
                        <span><g:message code="session.noAuthorsFound.label" /></span>
                    </li>
                </g:if>
            </ul>
        </li>
    </ol>
</body>
</html>