<%@ page import="org.iisg.eca.domain.Setting; org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.User" %>
<html xmlns="http://www.w3.org/1999/html">
	<head>
        <meta name="layout" content="main">
        <g:javascript src="network-session.js" />
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
                        <g:message code="network.multiple.label" />                        
                    </label>
                    <ul class="property-value">
                        <g:each in="${eventSession.networks}" var="network" status="i">
                            <li>
                                <g:select name="Session_${i}.network.id" from="${networks}" optionKey="id" value="${network.id}" />
                                <span class="ui-icon ui-icon-circle-minus"></span>
                            </li>
                        </g:each>
                        <li class="add">
                            <span class="ui-icon ui-icon-circle-plus"></span>
                            <g:message code="default.add.label" args="[g.message(code: 'network.label')]" />
                        </li>
                        <li class="hidden">
                            <g:select name="Session_null.network.id" from="${networks}" optionKey="id" />
                            <span class="ui-icon ui-icon-circle-minus"></span>
                        </li>
                    </ul>
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
                    <ul class="session-participants property-value">
                        <g:each in="${participants}" var="participant" status="i">
                            <li>
                                <input type="hidden" name="user-id" class="user-id" value="${participant.participant.user.id}" />

                                <span class="participant-value">${participant.participant}</span>
                                <span class="participant-state-value">(${participant.participant.state})</span>

                                <ul>
                                <g:each in="${participant.types}" var="type">
                                    <li class="participant-type-value">
                                        <input type="hidden" name="type-id" class="type-id" value="${type.id}" />
                                        <span class="participant-type-val">${type}</span> <span class="ui-icon ui-icon-circle-minus"></span>
                                    </li>
                                </g:each>
                                </ul>

                                <g:if test="${participant.paper}">
                                    <span class="participant-paper-value">
                                        <g:message code="paper.label" />: ${participant.paper}
                                    </span>
                                </g:if>
                            </li>
                        </g:each>
                        <li class="hidden">
                            <input type="hidden" name="user-id" class="user-id" />

                            <span class="participant-value"> </span>
                            <span class="participant-state-value"> </span>

                            <ul>
                                <li class="participant-type-value">
                                    <input type="hidden" name="type-id" class="type-id" />
                                    <span class="participant-type-val"> </span> <span class="ui-icon ui-icon-circle-minus"></span>
                                </li>
                            </ul>

                            <span class="participant-paper-value"> </span>
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
                        <div id="${type.toString().toLowerCase()}-tab" class="form <g:if test="${type.type.equalsIgnoreCase('author')}">author</g:if>">
                            <input type="hidden" name="type-id" value="${type.id}" class="type-id" />
                            <input type="hidden" name="participant-id" class="participant-id" />

                            <div>
                                <label class="property-label">
                                    Paticipant
                                </label>
                                <input type="text" name="participant" class="select-participant property-value" />
                            </div>
                            <g:if test="${type.withPaper && (maxPapers == null || maxPapers > 1)}">
                                <div>
                                    <label class="property-label">
                                        Paper
                                    </label>
                                    <select class="paper-id property-value" name="paper-id"></select>
                                </div>
                            </g:if>
                            <div>
                                <input type="button" name="add-participant" class="property-value" value="${g.message(code: "default.add.label", args: [type])}" />
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
