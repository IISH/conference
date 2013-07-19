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
            <span id="id-label" class="property-label">#</span>
            <span class="property-value" arial-labelledby="id-label">${eventSession.id}</span>
          </li>
          <li>
            <span id="code-label" class="property-label">
              <g:message code="session.code.label" />
            </span>
            <span class="property-value" arial-labelledby="code-label">
              <eca:formatText text="${eventSession.code}" />
            </span>
          </li>
          <li>
            <span id="name-label" class="property-label">
              <g:message code="session.name.label" />
            </span>
            <span class="property-value" arial-labelledby="name-label">
              <eca:formatText text="${eventSession.name}" />
            </span>
          </li>          
          <li>
            <span id="abstr-label" class="property-label">
              <g:message code="session.abstr.label" />
            </span>
            <span class="property-value" arial-labelledby="abstr-label">
              <eca:formatText text="${eventSession.abstr}" />
            </span>
          </li>
          <li>
            <span id="comment-label" class="property-label">
              <g:message code="session.comment.label" />
            </span>
            <span class="property-value" arial-labelledby="comment-label">
              <eca:formatText text="${eventSession.comment}" />
            </span>
          </li>
          <li>
            <span id="state-label" class="property-label">
              <g:message code="session.state.label" />
            </span>
            <span class="property-value" arial-labelledby="state-label">
              <eca:formatText text="${eventSession.state}" />
            </span>
          </li>
          <li>
            <span id="enabled-label" class="property-label">
              <g:message code="default.enabled.label" />
            </span>
            <span class="property-value" arial-labelledby="enabled-label">
              <g:checkBox name="enabled" value="${eventSession.enabled}" disabled="true" />
            </span>
          </li>
          <li>
            <span id="networks-label" class="property-label">
              <g:message code="network.multiple.label"  />
            </span>
            <ul class="property-value" arial-labelledby="networks-label">
              <g:each in="${networks}" var="network">
                <li>${network.encodeAsHTML()}</li>
              </g:each>
            </ul>
          </li>
          <li>
            <span id="equipment-label" class="property-label">
              <g:message code="equipment.multiple.label" />
            </span>
            <ul class="property-value" arial-labelledby="equipment-label">
              <g:each in="${equipment}" var="equip">
                 <li>${equip[0]} (${equip[1]})</li>
              </g:each>
            </ul>
          </li>
          <li>
            <span id="participants-label" class="property-label">
              <g:message code="session.sessionParticipants.label" />
            </span>
            <ul class="session-participants property-value" arial-labelledby="participants-label">
                <g:each in="${participants}" var="participant" status="i">
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
                                <g:message code="paper.label" />: ${participant.paper} (${participant.paper.state})
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