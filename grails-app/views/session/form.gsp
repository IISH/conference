<%@ page import="org.iisg.eca.domain.Setting; org.iisg.eca.domain.ParticipantType; org.iisg.eca.domain.User; org.iisg.eca.domain.PaperState" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <asset:javascript src="session.js" />
        <asset:javascript src="participants-in-sessions.js" />
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />
        <g:set var="maxPapers" value="${Setting.getSetting(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION).value?.toInteger()}" />
        
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
                <g:if test="${Setting.getSetting(Setting.SHOW_SESSION_EXTRA_INFO).booleanValue}">
                    <div class="${hasErrors(bean: eventSession, field: 'extraInfo', 'error')}">
                        <label class="property-label">
                            <g:message code="session.extraInfo.label" />
                        </label>
                        <span class="property-value">
                            <textarea cols="40" rows="5" name="Session.extraInfo">${fieldValue(bean: eventSession, field: 'extraInfo')}</textarea>
                        </span>
                    </div>
                </g:if>
                <div class="${hasErrors(bean: eventSession, field: 'comment', 'error')}">
                    <label class="property-label">
                        <g:message code="session.comment.label" />
                    </label>
                    <span class="property-value">
                        <textarea cols="40" rows="5" name="Session.comment">${fieldValue(bean: eventSession, field: 'comment')}</textarea>
                    </span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'state', 'error')}">
                    <label class="property-label">
                        <g:message code="session.state.label" />
                    </label>
                    <span class="property-value">
                        <g:select from="${sessionStates}" name="Session.state.id" optionKey="id" optionValue="description" value="${eventSession.state.id}" />
                    </span>
                </div>
                <g:if test="${Setting.getSetting(Setting.SHOW_SESSION_TYPES, curDate?.event).value == '1'}">
                    <div class="${hasErrors(bean: eventSession, field: 'type', 'error')}">
                        <label class="property-label">
                            <g:message code="session.type.label" />
                        </label>
                        <span class="property-value">
                            <g:select from="${sessionTypes}" name="Session.type.id" optionKey="id" optionValue="type" value="${eventSession.type?.id}" noSelection="${['': ' ']}" />
                            <g:if test="${Setting.getSetting(Setting.SHOW_OPTIONAL_SESSION_TYPE, curDate?.event).value == '1'}">
                                <input type="text" name="Session.differentType" value="${fieldValue(bean: eventSession, field: 'differentType')}" />
                            </g:if>
                        </span>
                    </div>
                </g:if>
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

                                <eca:link controller="participant" action="show" id="${participant?.participant?.user?.id}">
                                    ${participant?.participant}
                                </eca:link>
                                <span class="participant-state-value">(${participant.participant.state})</span>

                                <ul>
                                <g:each in="${participant.types}" var="type">
                                    <li class="participant-type-value">
                                        <input type="hidden" name="set-index" value="false" />
                                        <input type="hidden" name="type-id" class="type-id" value="${type.id}" />
                                        <span class="participant-type-val">${type}</span>
                                        <g:if test="${type.id != ParticipantType.CO_AUTHOR}">
                                            <span class="ui-icon ui-icon-circle-minus"></span>
                                        </g:if>
                                    </li>
                                </g:each>
                                </ul>

                                <g:if test="${participant.paper}">
                                    <span class="participant-paper-value">
                                        <span class="paper-text">
                                            <span class="l"><g:message code="paper.authorOf.label" />:</span>
                                            <span class="v">${participant.paper} (${participant.paper.state})</span>
                                        </span>
                                        <input type="hidden" name="paper-id" class="paper-id" value="${participant.paper.id}" />
                                        <input type="hidden" name="paper-state-id" value="${participant.paper.state.id}" />
                                        <span class="ui-icon ui-icon-pencil edit-paper-icon"></span>
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
                                        <input type="hidden" name="paper-id" class="paper-id" value="${participant.paperCoAuthoring.id}" />
                                        <input type="hidden" name="paper-state-id" value="${participant.paperCoAuthoring.state.id}" />
                                        <span class="ui-icon ui-icon-pencil edit-paper-icon"></span>
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
                        <li class="hidden">
                            <input type="hidden" name="user-id" class="user-id" />

                            <span class="participant-value">
                                <eca:link controller="participant" action="show" id="*id*"></eca:link>
                            </span>
                            <span class="participant-state-value"> </span>

                            <ul>
                                <li class="participant-type-value">
                                    <input type="hidden" name="set-index" value="false" />
                                    <input type="hidden" name="type-id" class="type-id" />
                                    <span class="participant-type-val"> </span>
                                    <span class="ui-icon ui-icon-circle-minus"></span>
                                </li>
                            </ul>

                            <span class="participant-paper-value">
                                <span class="paper-text">
                                    <span class="l"><g:message code="paper.authorOf.label" />:</span>
                                    <span class="v"></span>
                                </span>
                                <input type="hidden" name="paper-id" class="paper-id" value="" />
                                <input type="hidden" name="paper-state-id" value="" />
                                <span class="ui-icon ui-icon-pencil edit-paper-icon"></span>
                            </span>
                            <span class="participant-paper-coauthor-value">
                                <span class="l"><g:message code="paper.allCoAuthors.label" />:</span>
                                <span class="v"> </span>
                            </span>

                            <span class="participant-paper-coauthoring-value">
                                <span class="paper-text">
                                    <span class="l"><g:message code="paper.coAuthorOf.label" />:</span>
                                    <span class="v"></span>
                                </span>
                                <input type="hidden" name="paper-id" class="paper-id" value="" />
                                <input type="hidden" name="paper-state-id" value="" />
                                <span class="ui-icon ui-icon-pencil edit-paper-icon"></span>
                            </span>
                            <span class="participant-paper-coauthoring-coauthor-value">
                                <span class="l"><g:message code="paper.allCoAuthors.label" />:</span>
                                <span class="v"> </span>
                            </span>
                        </li>
                    </ul>
                </div>
                
                <div id="tabs">
                    <ol>
                        <g:each in="${types}" var="type">
                            <g:if test="${type.id != ParticipantType.CO_AUTHOR}">
                                <li><a href="#${type.toString().toLowerCase()}-tab"><g:message code="default.add.label" args="[type]" /></a></li>
                            </g:if>
                        </g:each>
                    </ol>

                    <g:each in="${types}" var="type">
                        <g:if test="${type.id != ParticipantType.CO_AUTHOR}">
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
                                <div class="buttons">
                                    <input type="button" name="add-participant" class="property-value" value="${g.message(code: "default.add.label", args: [type])}" />
                                </div>
                            </div>
                        </g:if>
                    </g:each>
                  </div>
                  <div id="loading">
                      <div class="loading-helper">
                          <span class="loading-text"><g:message code="default.please.wait" /></span>
                      </div>
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
                <g:if test="${params.action != 'create'}">
                  <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
                </g:if>
            </fieldset>
        </form>
        
        <div id="edit-paper" class="info">
          <input type="hidden" name="paper-id" value="" />
          <form method="post" action="#">
            <fieldset class="form">
              <div class="participant-paper-value"></div>
              <g:each in="${PaperState.list()}" var="paperState">
                <div>
                  <span class="property-label">
                    <g:radio name="paper-state" value="${paperState.id}" id="edit-paper-${paperState.id}" />
                  </span>
                  <label class="property-value" for="edit-paper-${paperState.id}">
                    ${paperState.toString()}                    
                  </label>
                </div>
              </g:each>
            </fieldset>
          </form>
        </div>
    </body>
</html>
