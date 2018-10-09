<%@ page import="org.iisg.eca.domain.PaperState" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="network.js" />
        <g:javascript src="participants-in-sessions.js" />
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />
        
        <div class="title-menu-link toggle-session-participants">
            <span class="ui-icon ui-icon-person"></span>
            <a><g:message code="default.toggle.label" args="${[g.message(code: 'session.sessionParticipants.label').toLowerCase()]}" /></a>
        </div>
        
        <ol class="property-list">
          <li>
            <span id="id-label" class="property-label">#</span>
            <span class="property-value" arial-labelledby="id-label">${network.id}</span>
          </li>
          <li>
            <span id="name-label" class="property-label">
              <g:message code="network.name.label" />
            </span>
            <span class="property-value" arial-labelledby="name-label">
              <eca:formatText text="${network.name}" />
            </span>
          </li>
          <li>
            <span id="comment-label" class="property-label">
              <g:message code="network.comment.label" />
            </span>
            <span class="property-value" arial-labelledby="comment-label">
              <eca:formatText text="${network.comment}" />
            </span>
          </li>
	      <li>
		    <span id="long-description-label" class="property-label">
		       <g:message code="network.longDescription.label" />
		    </span>
		    <span class="property-value" arial-labelledby="long-description-label">
		       <eca:formatText text="${network.longDescription}" />
		    </span>
	      </li>
          <li>
            <span id="url-label" class="property-label">
              <g:message code="network.url.label" />
            </span>
            <span class="property-value" arial-labelledby="url-label">
              <eca:formatText text="${network.url}" />
            </span>
          </li>
          <li>
            <span id="email-label" class="property-label">
              <g:message code="network.email.label" />
            </span>
            <span class="property-value" arial-labelledby="email-label">
              <eca:formatText text="${network.email}" />
            </span>
          </li>
          <li>
            <span id="showOnline-label" class="property-label">
              <g:message code="network.showOnline.label" />
            </span>
            <span class="property-value" arial-labelledby="showOnline-label">
              <g:checkBox name="showOnline" value="${network.showOnline}" disabled="true" />
            </span>
          </li>
          <li>
            <span id="chairs-label" class="property-label">
              <g:message code="network.chairs.label" />
            </span>
            <ul class="property-value" arial-labelledby="chairs-label">
              <g:each in="${network.chairs}" var="chair">
                <li>
                  <eca:link controller="participant" action="show" id="${chair.chair.id}">
                    ${chair.encodeAsHTML()}
                  </eca:link>
                </li>
              </g:each>
            </ul>
          </li>

          <li>
            <span id="sessions-label" class="property-label">
              <g:message code="session.multiple.label" />
            </span>
            <ul id="network-sessions" class="property-value" arial-labelledby="sessions-label">
                <g:each in="${sessions}" var="session">
                <li>
                    <span class="session">
                        <eca:link controller="session" action="show" id="${session.key.id}">
                            ${session.key.toString()}
                        </eca:link>
                        
                        &nbsp;
                        
                        <input type="hidden" name="session-id" value="${session.key.id}" />
                        <span>
                            <eca:radioSelect name="session-state-select-${session.key.id}" class="session-state-select" value="${session.key?.state?.id}" values="${sessionStates}" labelName="shortDescription" />
                        </span>                        
                    </span>
                    
                    <ul class="session-participants" class="property-value">
                      <g:each in="${session.value}" var="participant" status="i">
                          <li>
                              <span class="participant-value">
                                  <eca:link controller="participant" action="show" id="${participant?.participant?.user?.id}">
                                      ${participant?.participant}
                                  </eca:link>
                              </span>
                              <span class="participant-state-value">(${participant?.participant?.state})</span>

                              <ul>
                              <g:each in="${participant.types}" var="type">
                                  <li class="participant-type-value">
                                      <span class="participant-type-val">${type}</span>
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
                          <span class="participant-value">
                              <eca:link controller="participant" action="show" id="*id*"> </eca:link>
                          </span>
                          <span class="participant-state-value"> </span>

                          <ul>
                              <li class="participant-type-value">
                                  <span class="participant-type-val"> </span>
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
                </li>
                </g:each>
            </ul>
          </li>
          <li>
            <label id="not-participants" class="property-label">
                <g:message code="network.not.in.participants.label" />
            </label>
            <span class="loading property-value" arial-labelledby="not-participants">
                <input type="hidden" name="network-id" value="${params.id}" />
                <span class="ui-icon ui-icon-refresh"></span>
                <g:message code="default.load.label" args="[g.message(code: 'participantDate.multiple.label').toLowerCase()]" />
            </span>
            <ul id="not-in-session" class="property-value" arial-labelledby="not-participants">
                <li>
                    <a class="participant" href="#"> </a>
                    <ul class="participants">
                        <li class="participant-paper-value">
                            <span class="paper-text"></span>
                            <input type="hidden" name="paper-id" class="paper-id" value="" />
                            <input type="hidden" name="paper-state-id" value="" />
                            <span class="ui-icon ui-icon-pencil edit-paper-icon"></span>
                        </li>
                    </ul>
                </li>
            </ul>
          </li>
        </ol>

        <div class="buttons">
            <eca:link previous="true">
                <g:message code="default.button.back.label" />
            </eca:link>
            <eca:ifUserHasAccess controller="${params.controller}" action="edit">
                <eca:link controller="${params.controller}" action="edit" id="${params.id}">
                    <g:message code="default.button.edit.label" />
                </eca:link>
            </eca:ifUserHasAccess>
        </div>
        
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