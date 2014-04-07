<%@ page import="org.iisg.eca.domain.EventDate; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.ParticipantDate" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />

        <h3>${emailTemplate.description} - <g:message code="email.select.label" /></h3>

        <form method="post" action="#">
            <fieldset class="form">

                <g:if test="${filterMap.participant}">
                    <div>
                        <label class="property-label">
                            <g:message code="participantDate.label" />
                        </label>
                        <span class="property-value">
                            <eca:usersAutoComplete name="participant" queryName="${queryName}" placeholder="${placeholder}" />
                        </span>
                    </div>
                </g:if>

                <g:if test="${filterMap.participantState}">
                    <div>
                        <label class="property-label">
                            <g:message code="participantState.label" />
                        </label>
                        <span class="property-value">
                            <g:select name="participantState" from="${ParticipantState.list()}" optionKey="id" noSelection="${[null: 'All participant states']}" />
                        </span>
                    </div>
                </g:if>

                <g:if test="${filterMap.paperState}">
                    <div>
                        <label class="property-label">
                            <g:message code="paper.state.label" />
                        </label>
                        <span class="property-value">
                            <g:select name="paperState" from="${PaperState.list()}" optionKey="id" noSelection="${[null: 'All paper states']}" />
                        </span>
                    </div>
                </g:if>

                <g:if test="${filterMap.eventDates}">
                    <div>
                        <label class="property-label">
                            <g:message code="eventDate.participants.label" />
                        </label>
                        <span class="property-value">
                            <g:select name="eventDates" from="${EventDate.findAllByEvent(curDate.event)}"
                                      optionKey="id" optionValue="${{it.getDateAndLaterText()}}" />
                        </span>
                    </div>
                </g:if>

                <div>
                    <label class="property-label">
                        <g:message code="emailTemplate.comment.label" />
                    </label>
                    <span class="property-value">
                        <eca:formatText text="${emailTemplate.comment}" />
                    </span>
                </div>
            </fieldset>

            <fieldset id="email-preview-container">
                <div id="preview-options" class="tbl_toolbar">
                    <div>
                        <eca:link controller="emailTemplate" action="edit" id="${emailTemplate.id}" target="_blank">
                            <span class="ui-icon ui-icon-newwin"></span>
                            <span><g:message code="email.edit.template.label" /></span>
                        </eca:link>
                    </div>
                    <div class="refresh link">
                        <span class="ui-icon ui-icon-refresh"></span>
                        <span><g:message code="email.refresh.preview.label" /></span>
                    </div>
                </div>

                <ol id="email-preview" class="property-list">
                    <li>
                        <span id="from-label" class="property-label">
                            <g:message code="email.from.label" />
                        </span>
                        <span class="property-value" arial-labelledby="from-label">
                            ${from}
                        </span>
                    </li>
                    <li>
                        <span id="to-label" class="property-label">
                            <g:message code="email.to.label" />
                        </span>
                        <span class="property-value" arial-labelledby="to-label">
                            ${to}
                        </span>
                    </li>
                    <li>
                        <span id="subject-label" class="property-label">
                            <g:message code="email.subject.label" />
                        </span>
                        <span class="property-value" arial-labelledby="subject-label">
                            <eca:formatText text="${preview.subject}" />
                        </span>
                    </li>
                    <li>
                        <span id="body-label" class="property-label">
                            <g:message code="email.body.label" />
                        </span>
                        <span class="property-value" arial-labelledby="body-label">
                            <eca:formatText text="${preview.body}" />
                        </span>
                    </li>
                </ol>
            </fieldset>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_send" class="btn_send" value="${g.message(code: 'email.send.multiple.label')}" />
            </fieldset>
        </form>
    </body>
</html>