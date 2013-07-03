<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="network.js" />
        <g:javascript src="participants-in-sessions.js" />
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />
        
        <ol class="property-list">
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
            <span id="showInternal-label" class="property-label">
              <g:message code="network.showInternal.label" />
            </span>
            <span class="property-value" arial-labelledby="showInternal-label">
              <g:checkBox name="showInternal" value="${network.showInternal}" disabled="true" />
            </span>
          </li>
          <li>
           <span id="enabled-label" class="property-label">
             <g:message code="default.enabled.label" />
           </span>
           <span class="property-value" arial-labelledby="enabled-label">
             <g:checkBox name="enabled" value="${network.enabled}" disabled="true" />
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
                        <g:select from="${sessionStates}" class="session-state-select" name="session.state.id" optionKey="id" optionValue="description" value="${session.key?.state?.id}" />
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
                                      <g:message code="paper.label" />: ${participant.paper}
                                  </span>
                                  <g:if test="${participant.paper.coAuthors && !participant.paper.coAuthors.isEmpty()}">
                                      <span class="participant-paper-value">
                                          <g:message code="paper.coAuthors.label" />: <eca:formatText text="${participant.paper.coAuthors}" />
                                      </span>
                                  </g:if>
                              </g:if>
                          </li>
                      </g:each>
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
                        <li class="participant-paper-value"> </li>
                    </ul>
                </li>
            </ul>
          </li>
        </ol>

        <div class="buttons">
            <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                <g:message code="default.button.back.label" />
            </eca:link>
            <eca:ifUserHasAccess controller="${params.controller}" action="edit">
                <eca:link controller="${params.controller}" action="edit" id="${params.id}">
                    <g:message code="default.button.edit.label" />
                </eca:link>
            </eca:ifUserHasAccess>
        </div>
	</body>
</html>