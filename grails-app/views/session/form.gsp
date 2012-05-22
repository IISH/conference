<%@ page import="org.iisg.eca.domain.Setting; org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.User" %>
<html>
	<head>
        <meta name="layout" content="main">
        <g:javascript src="session.js" />
	</head>
	<body>
        <g:set var="maxPapers" value="${Setting.getByProperty(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION).value?.toInteger()}" />

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
                    <label class="property-label">#</label>
                    <span class="property-value">${eventSession.id}</span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'code', 'error')} required">
                    <label class="property-label">
                        <g:message code="session.code.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" maxlength="10" name="Session.code" value="${fieldValue(bean: eventSession, field: 'code')}" required="required" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'name', 'error')} required">
                    <label class="property-label">
                        <g:message code="session.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="Session.name" required="required" value="${fieldValue(bean: eventSession, field: 'name')}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'comment', 'error')}">
                    <label class="property-label">
                        <g:message code="session.comment.label" />
                    </label>
                    <textarea class="property-value" cols="40" rows="5" name="Session.comment">${fieldValue(bean: eventSession, field: 'comment')}</textarea>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'enabled', 'error')}">
                    <label class="property-label">
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox class="property-value" name="Session.enabled" checked="${eventSession.enabled}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'deleted', 'error')}">
                    <label class="property-label">
                        <g:message code="default.deleted.label" />
                    </label>
                    <g:checkBox class="property-value" name="Session.deleted" checked="${eventSession.deleted}" />
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="equipment.multiple.label" />
                    </label>
                    <ul id="session-equipment" class="property-value">
                        <g:each in="${equipment}" var="equip" status="i">
                            <li>${equip[0]} (${equip[1]})</li>
                        </g:each>
                    </ul>
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="session.sessionparticipants.label" />
                    </label>
                    <ul id="session-participants" class="property-value">
                        <g:each in="${participants}" var="participant" status="i">
                            <li>
                                <input type="hidden" name="user-id" class="user-id" value="${participant[0].user.id}" />
                                <input type="hidden" name="type-id" class="type-id" value="${participant[0].type.id}" />
                                <input type="hidden" name="paper-ids" class="paper-ids" value="${participant[1]*.id.join(',')}" />
                                
                                <g:if test="${participant[0].type.withPaper}">
                                    <span>(Paper(s): ${participant[1]*.title.join(', ')})</span>
                                </g:if>
                                <g:else>
                                    <span>${participant[0].encodeAsHTML()}</span>
                                </g:else>
                                
                                <span class="ui-icon ui-icon-circle-minus"></span>
                            </li>
                        </g:each>
                        <li class="hidden">
                            <input type="hidden" name="user-id" class="user-id" />
                            <input type="hidden" name="type-id" class="type-id" />
                            <input type="hidden" name="paper-ids" class="paper-ids" />
                            
                            <span></span>
                            
                            <span class="ui-icon ui-icon-circle-minus"></span>
                        </li>
                    </ul>
                </div>
                
                <div id="tabs">
                    <ul>
                        <g:each in="${types}" var="type">
                            <li><a href="#${type.toString().toLowerCase()}-tab"><g:message code="default.add.label" args="[type]" /></a></li>
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
                            <g:if test="${type.withPaper && (maxPapers == null || maxPapers > 1)}">
                                <div>
                                    <label>
                                        Paper
                                    </label>
                                    <select class="paper-id" name="paper-id"></select>
                                </div>
                            </g:if>
                            <div>
                                <input type="button" name="add-participant" value="${g.message(code: "default.add.label", args: [type])}" />
                            </div>
                        </div>
                    </g:each>
                </div>
            </fieldset>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${g.message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
    </body>
</html>
