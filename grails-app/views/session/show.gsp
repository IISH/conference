<%@ page import="org.iisg.eca.domain.Setting; org.iisg.eca.domain.PaperState" %>
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
          <g:if test="${Setting.getSetting(Setting.SHOW_SESSION_TYPES, curDate?.event).value == '1'}">
            <li>
                <span id="type-label" class="property-label">
                    <g:message code="session.type.label" />
                </span>
                <span class="property-value" arial-labelledby="type-label">
                    <eca:formatText text="${eventSession.type}" />
                    <eca:formatText text="${eventSession.differentType}" />
                </span>
            </li>
          </g:if>
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