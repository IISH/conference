<%@ page import="org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.User" %>
<html>
	<head>
		<meta name="layout" content="main">
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
                <div class="${hasErrors(bean: eventSession, field: 'comment', 'error')} ">
                    <label>
                        <g:message code="session.comment.label" />
                    </label>
                    <textarea cols="40" rows="5" name="Session.comment">
                        ${fieldValue(bean: eventSession, field: 'comment')}
                    </textarea>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'enabled', 'error')} ">
                    <label>
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox name="Session.enabled" checked="${eventSession.enabled}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'deleted', 'error')} ">
                    <label>
                        <g:message code="default.deleted.label" />
                    </label>
                    <g:checkBox name="Session.deleted" checked="${eventSession.deleted}" />
                </div>

                <div style="color:red; font-weight:bold;">TODO: Equipment for this session</div>

                <ol id="session-participants">
                <g:each in="${eventSession.sessionParticipants}" var="participant" status="i">
                    <li>
                        <input type="hidden" name="SessionParticipant_${i}.user.id" value="${participant.user.id}" />
                        <input type="hidden" name="SessionParticipant_${i}.type.id" value="${participant.type.id}" />

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
                    <li class="hidden">
                        <input type="hidden" name="SessionParticipant_null.user.id" />
                        <input type="hidden" name="SessionParticipant_null.type.id" />

                        <span class="property-label"> </span>
                        <span class="property-value"> </span>
                    </li>
                </ol>

                <div id="tabs" class="session">
                    <ul>
                        <g:each in="${types}" var="type">
                            <li><a href="#${type.toString().toLowerCase()}-tab">Add ${type}</a></li>
                        </g:each>
                    </ul>

                    <g:each in="${types}" var="type">
                        <div id="${type.toString().toLowerCase()}-tab">
                            <input type="hidden" name="${type.toString().toLowerCase()}-id" value="${type.id}" />
                            <g:select from="${participants}" name="${type}" optionKey="id" noSelection="${['null': 'Select a participant']}" />
                            <input type="button" name="add-${type.toString().toLowerCase()}" value="Add ${type}" />
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
