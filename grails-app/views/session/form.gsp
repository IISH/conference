<%@ page import="org.iisg.eca.domain.Setting; org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.User" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="session.js" />
        <g:javascript src="participants-in-sessions.js" />
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />
        <g:set var="maxPapers" value="${Setting.getByEvent(Setting.findAllByProperty(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION)).value?.toInteger()}" />
        
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
                    <label class="property-label">
                        #
                    </label>
                    <span class="property-value">
                        ${eventSession.id}
                    </span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'code', 'error')} required">
                    <label class="property-label">
                        <g:message code="session.code.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" maxlength="10" name="Session.code" value="${fieldValue(bean: eventSession, field: 'code')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'name', 'error')} required">
                    <label class="property-label">
                        <g:message code="session.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="text" name="Session.name" required="required" value="${fieldValue(bean: eventSession, field: 'name')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'abstr', 'error')}">
                    <label class="property-label">
                        <g:message code="session.abstr.label" />
                    </label>
                    <span class="property-value">
                        <textarea cols="40" rows="5" name="Session.abstr">${fieldValue(bean: eventSession, field: 'abstr')}</textarea>
                    </span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'comment', 'error')}">
                    <label class="property-label">
                        <g:message code="session.comment.label" />
                    </label>
                    <span class="property-value">
                        <textarea cols="40" rows="5" name="Session.comment">${fieldValue(bean: eventSession, field: 'comment')}</textarea>
                    </span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'enabled', 'error')}">
                    <label class="property-label">
                        <g:message code="default.enabled.label" />
                    </label>
                    <span class="property-value">
                        <g:checkBox name="Session.enabled" checked="${eventSession.enabled}" />
                    </span>
                </div>
                <div>
                    <label class="property-label">                      
                        <g:message code="network.multiple.label" />                        
                    </label>
                    <ul class="property-value">
                        <input type="hidden" name="Session.to-be-deleted" class="to-be-deleted" />

                        <g:each in="${eventSession.networks}" var="network" status="i">
                            <li>
                                <g:select name="Session_${i}.networks.id" from="${networks}" optionKey="id" value="${network.id}" />
                                <span class="ui-icon ui-icon-circle-minus"></span>
                            </li>
                        </g:each>

                        <li class="add">
                            <span class="ui-icon ui-icon-circle-plus"></span>
                            <g:message code="default.add.label" args="[g.message(code: 'network.label').toLowerCase()]" />
                        </li>

                        <li class="hidden">
                            <g:select name="Session_null.networks.id" from="${networks}" optionKey="id" />
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
                        <g:message code="session.sessionParticipants.label" />
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
                                        <input type="hidden" name="set-index" value="false" />
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
                                    <input type="hidden" name="set-index" value="false" />
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
                                    <g:message code="participantDate.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="participant" class="select-participant" />
                                </span>
                            </div>
                            <g:if test="${type.withPaper && (maxPapers == null || maxPapers > 1)}">
                                <div>
                                    <label class="property-label">
                                        <g:message code="paper.label" />
                                    </label>
                                    <span class="property-value">
                                        <select class="paper-id" name="paper-id"></select>
                                    </span>
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
                <eca:link controller="${params.controller}" action="delete" id="${params.id}" class="btn_delete">
                    <g:message code="default.deleted.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${g.message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
    </body>
</html>
