<%@ page import="org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.User" %>
<html>
	<head>
		<meta name="layout" content="main">
        <g:javascript src="session.js" />
	</head>
	<body>
        <g:hasErrors bean="${eventSession}">
            <ul class="errors" role="alert">
                <g:eachError bean="${eventSession}" var="error">
                    <li>
                        <g:message error="${error}" />
                    </li>
                </g:eachError>
            </ul>
        </g:hasErrors>

        <form method="post" action="#">
            <fieldset class="form">
                <div>
                    <label>#</label>
                    <span>${eventSession.id}</span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'code', 'error')} required">
                    <label>
                        <g:message code="session.code.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" maxlength="10" name="Session.code" value="${fieldValue(bean: eventSession, field: 'code')}" required="required" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'name', 'error')} required">
                    <label>
                        <g:message code="session.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" name="Session.name" required="required" value="${fieldValue(bean: eventSession, field: 'name')}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'comment', 'error')}">
                    <label>
                        <g:message code="session.comment.label" />
                    </label>
                    <textarea cols="40" rows="5" name="Session.comment">${fieldValue(bean: eventSession, field: 'comment')}</textarea>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'enabled', 'error')}">
                    <label>
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox name="Session.enabled" checked="${eventSession.enabled}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'deleted', 'error')}">
                    <label>
                        <g:message code="default.deleted.label" />
                    </label>
                    <g:checkBox name="Session.deleted" checked="${eventSession.deleted}" />
                </div>

                <ol id="session-equipment">
                <g:if test="${equipment.isEmpty()}">
                    <li>
                        <span class="property-label">
                            Equipment
                        </span>
                        <span class="property-value">-</span>
                    </li>
                </g:if>
                <g:each in="${equipment}" var="equip" status="i">
                    <li>
                        <g:if test="${i == 0}">
                            <span class="property-label">
                                Equipment
                            </span>
                        </g:if>
                        <g:else>
                            <span class="property-label"> </span>
                        </g:else>
                        <span class="property-value">${equip[0]} (${equip[1]})</span>
                    </li>
                </g:each>
                </ol>

                <ol id="session-participants">
                <g:if test="${eventSession.sessionParticipants.isEmpty()}">
                    <li>
                        <input type="hidden" name="user-id" class="user-id" />
                        <input type="hidden" name="type-id" class="type-id" />

                        <span class="property-label">
                            Participants in session
                        </span>
                        <span class="property-value">-</span>
                    </li>
                </g:if>
                <g:each in="${eventSession.sessionParticipants}" var="participant" status="i">
                    <li>
                        <input type="hidden" name="user-id" class="user-id" value="${participant.user.id}" />
                        <input type="hidden" name="type-id" class="type-id" value="${participant.type.id}" />

                        <g:if test="${i == 0}">
                            <span class="property-label">
                                Participants in session
                            </span>
                        </g:if>
                        <g:else>
                            <span class="property-label"> </span>
                        </g:else>
                        <span class="property-value">${participant.encodeAsHTML()}</span>
                    </li>
                </g:each>
                </ol>

                <div id="tabs">
                    <ul>
                        <g:each in="${types}" var="type">
                            <li><a href="#${type.toString().toLowerCase()}-tab">Add ${type}</a></li>
                        </g:each>
                    </ul>

                    <g:each in="${types}" var="type">
                        <div id="${type.toString().toLowerCase()}-tab" <g:if test="${type.type.equalsIgnoreCase('author')}">class="author"</g:if>>
                            <input type="hidden" name="type-id" value="${type.id}" class="type-id" />
                            <input type="hidden" name="participant-id" class="participant-id" />

                            <div>
                                <label>
                                    Paticipant
                                </label>
                                <input type="text" name="participant" class="select-participant" />
                            </div>
                            <g:if test="${type.type.equalsIgnoreCase('author')}">
                                <div>
                                    <label>
                                        Paper
                                    </label>
                                    <select class="paper" name="paper"></select>
                                </div>
                            </g:if>
                            <div>
                                <input type="button" name="add-participant" value="Add ${type}" />
                            </div>
                        </div>
                    </g:each>
                </div>
            </fieldset>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
    </body>
</html>
