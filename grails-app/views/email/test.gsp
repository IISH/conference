<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <form method="post" action="#">
          <fieldset class="form">
            <div>
              <label class="property-label" for="from">
                <g:message code="email.from.label" />
              </label>
              <span class="property-value">
                <input id="from" name="from" id="from" value="${params.from}" type="text" />
              </span>
            </div>
            <div>
              <label class="property-label" for="to">
                <g:message code="email.to.label" />
              </label>
              <span class="property-value">
                <input id="to" name="to" value="${params.to}" type="text" />
              </span>
            </div>
            <div>
              <label class="property-label" for="subject">
                <g:message code="email.subject.label" />
              </label>
              <span class="property-value">
                <input id="subject" name="subject" value="${params.subject}" type="text" />
              </span>
            </div>
            <div>
              <label class="property-label" for="body">
                <g:message code="email.body.label" />
              </label>
              <span class="property-value">
                <textarea id="body" style="width:70%;" name="body" rows="20">${params.body}</textarea>
              </span>
            </div>            
          </fieldset>
          
          <fieldset class="buttons">
            <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
              <g:message code="default.button.cancel.label" />
            </eca:link>
            <input type="submit" name="btn_send" class="btn_send" value="${message(code: 'email.send.label')}" />
          </fieldset>
        </form>
        
        <g:if test="${statusInfo}">
          <div id="status-info">
            <h3><g:message code="email.info.label" /></h3>
            <eca:formatText text="${statusInfo}" />              
          </div>
        </g:if>
    </body>
</html>
