<%@ page import="org.iisg.eca.domain.PaperState" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />
        <eca:navigation ids="${networkIds}" />
      
        <ol class="property-list">
          <li>
            <span id="network-label" class="property-label">
              <g:message code="network.label" />
            </span>
            <span class="property-value" arial-labelledby="network-label">
              <form name="network-form" action="#" method="get">
                <g:select id="network-select" name="networkId" from="${networks}" value="${params.id}" optionKey="id" optionValue="name" />
              </form>
            </span>
          </li>
          <li>
            <span id="sessions-label" class="property-label">
              <g:message code="session.multiple.label" />
            </span>
            <ul id="network-sessions" class="property-value" arial-labelledby="sessions-label">
                <g:each in="${sessions}" var="session">
                <g:if test="${session.value.size() > 0}">
                <li>
                    <span class="session">
                        <eca:link controller="session" action="show" id="${session.key.id}">
                            ${session.key.toString()}  (${session.key.state.toString()})
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
                                      <span class="paper-text"><g:message code="paper.label" />: ${participant.paper} (${participant.paper.state})</span>
                                      <input type="hidden" name="paper-id" class="paper-id" value="${participant.paper.id}" />
                                      <input type="hidden" name="paper-state-id" value="${participant.paper.state.id}" />
                                      <span class="ui-icon ui-icon-pencil edit-paper-icon"></span>
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
                </g:if>
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