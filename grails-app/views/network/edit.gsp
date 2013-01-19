<%@ page import="org.iisg.eca.domain.Session; org.iisg.eca.domain.User" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="network.js" />
        <g:javascript src="participants-in-sessions.js" />
    </head>
    <body>
        <input type="hidden" name="id" value="${params.id}" />

        <g:hasErrors bean="${network}">
          <ul class="errors" role="alert">
            <g:eachError bean="${network}" var="error">
              <li>
                <g:message error="${error}" />
              </li>
            </g:eachError>
          </ul>
        </g:hasErrors>

        <form method="post" action="#">
          <fieldset class="form">
            <div class="${hasErrors(bean: network, field: 'name', 'error')} required">
              <label class="property-label" for="Network.name">
                <g:message code="network.name.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="Network.name" maxlength="30" name="Network.name" value="${network.name}" type="text" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: network, field: 'comment', 'error')} ">
              <label class="property-label" for="Network.comment">
                <g:message code="network.comment.label" />
              </label>
              <span class="property-value">
                <textarea id="Network.comment" cols="40" name="Network.comment" rows="5">${network.comment}</textarea>
              </span>
            </div>
            <div class="${hasErrors(bean: network, field: 'url', 'error')} required">
              <label class="property-label" for="Network.url">
                <g:message code="network.url.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="Network.url" maxlength="255" name="Network.url" value="${network.url}" type="url" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: network, field: 'showOnline', 'error')} ">
              <label class="property-label" for="Network.showOnline">
                <g:message code="network.showOnline.label" />
              </label>
              <span class="property-value">
                <g:checkBox id="Network.showOnline" name="Network.showOnline" value="${network.showOnline}" />
              </span>
            </div>
            <div class="${hasErrors(bean: network, field: 'showInternal', 'error')} ">
              <label class="property-label" for="Network.showInternal">
                <g:message code="network.showInternal.label" />
              </label>
              <span class="property-value">
                <g:checkBox id="Network.showInternal" name="Network.showInternal" value="${network.showInternal}" />
              </span>
            </div>
              <div class="${hasErrors(bean: network, field: 'enabled', 'error')} ">
                <label class="property-label" for="Network.enabled">
                  <g:message code="default.enabled.label" />
                </label>
                <span class="property-value">
                  <g:checkBox id="Network.enabled" name="Network.enabled" value="${network.enabled}" />
                </span>
              </div>
            <div class="${hasErrors(bean: network, field: 'chairs', 'error')} ">
              <label class="property-label" for="Network.chairs">
                <g:message code="network.chairs.label" />
              </label>
              <ul class="property-value">
                <g:each in="${network.chairs.findAll { !it.deleted }}" var="instance" status="i">
                  <li>
                    <input type="hidden" name="NetworkChair_${i}.id" value="${instance.id}" />
                    <label class="property-label">
                      <g:message code="user.multiple.label" />
                      <g:select id="chair" name="NetworkChair_${i}.chair.id" value="${instance.chair.id}" optionKey="id" class="property-value" from="${User.list()}" required="required" />
                    </label>
                    <label class="property-label">
                      <g:message code="networkChair.isMainChair.label" />
                      <g:checkBox id="NetworkChair_${i}.isMainChair" name="NetworkChair_${i}.isMainChair" value="${instance.isMainChair}" class="property-value" />
                    </label>
                    <span class="ui-icon ui-icon-circle-minus"></span>
                  </li>
                </g:each>
                <li class="add">
                  <span class="ui-icon ui-icon-circle-plus"></span>
                  <g:message code="default.add.label" args="[g.message(code: 'network.chairs.label').toLowerCase()]" />
                </li>
                <li class="hidden">
                  <input type="hidden" name="NetworkChair_null.id" />
                  <label class="property-label">
                    <g:message code="user.multiple.label" />
                    <g:select id="chair" name="NetworkChair_${i}.chair.id" optionKey="id" class="property-value" from="${User.list()}" required="required" />
                  </label>
                  <label class="property-label">
                    <g:message code="networkChair.isMainChair.label" />
                    <g:checkBox id="NetworkChair_${i}.isMainChair" name="NetworkChair_${i}.isMainChair" class="property-value" />
                  </label>
                  <span class="ui-icon ui-icon-circle-minus"></span>
                </li>
              </ul>
              </div>
              <div>
                <label class="property-label">
                    <g:message code="default.add.label" args="[g.message(code: 'session.multiple.label').toLowerCase()]" />
                </label>
                <ul class="property-value">
                    <li>
                        <g:select name="Network_${i}.sessions.id" from="${Session.list()}" optionKey="id" value="${session.id}" />
                        <span class="ui-icon ui-icon-circle-plus add-session"></span>
                    </li>
                    <li>
                        <label class="property-label">
                            <g:message code="session.code.label" />
                            <input type="text" maxlength="10" name="Session.code" class="session-code" />
                        </label>

                        <label class="property-label">
                            <g:message code="session.name.label" />
                            <input type="text" name="Session.name" class="session-name" />
                        </label>

                        <span class="ui-icon ui-icon-circle-plus add-new-session"></span>
                    </li>
                </ul>
              </div>
              <div>
                  <label class="property-label">
                      <g:message code="session.multiple.label" />
                  </label>
                  <ul id="network-sessions" class="property-value">
                    <g:each in="${sessions}" var="session">
                    <li>
                        <input type="hidden" name="set-index" value="false" />
                        <input type="hidden" name="session-id" value="${session.key.id}" class="session-id" />
                        <span class="session">${session.key.toString()}</span>
                        <span class="ui-icon ui-icon-circle-minus remove-session"></span>

                        <ul class="session-participants" class="property-value">
                          <g:each in="${session.value}" var="participant" status="i">
                              <li>
                                  <span class="participant-value">${participant.participant}</span>
                                  <span class="participant-state-value">(${participant.participant.state})</span>

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
                                  </g:if>
                              </li>
                          </g:each>
                        </ul>
                    </li>
                    </g:each>
                    <li class="hidden">
                      <input type="hidden" name="set-index" value="false" />
                      <input type="hidden" name="session-id" class="session-id" />
                      <span class="session"> </span>
                      <span class="ui-icon ui-icon-circle-minus remove-session"></span>

                      <ul class="session-participants" class="property-value">
                        <li>
                            <span class="participant-value"> </span>
                            <span class="participant-state-value"> </span>

                            <ul>
                                <li class="participant-type-value">
                                    <span class="participant-type-val"> </span>
                                </li>
                            </ul>

                            <span class="participant-paper-value"> </span>
                        </li>
                      </ul>
                    </li>
                  </ul>
              </div>
              <div>
                <label class="property-label">
                    <g:message code="network.not.in.participants.label" />
                </label>
                <span class="loading property-value">
                    <input type="hidden" name="network-id" value="${params.id}" />
                    <span class="ui-icon ui-icon-refresh"></span>
                    <g:message code="default.load.label" args="[g.message(code: 'participantDate.multiple.label').toLowerCase()]" />
                </span>
                <ul id="not-in-session" class="property-value">
                    <li>
                        <a class="participant" href="#"> </a>
                        <ul class="participants">
                            <li class="participant-paper-value"> </li>
                        </ul>
                    </li>
                </ul>
              </div>
          </fieldset>

          <fieldset class="buttons">
            <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                <g:message code="default.button.cancel.label" />
            </eca:link>
            <eca:link action="delete" id="${params.id}" class="btn_delete">
                <g:message code="default.deleted.label" />
            </eca:link>
            <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
          </fieldset>
    </form>
    </body>
</html>
