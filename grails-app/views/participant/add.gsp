<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <g:hasErrors bean="${participant?.user}">
          <ul class="errors" role="alert">
            <g:eachError bean="${participant?.user}" var="error">
              <li>
                <g:message error="${error}" />
              </li>
            </g:eachError>
          </ul>
        </g:hasErrors>

        <form method="post" action="#">
            <fieldset class="form">
                <div class="required">
                    <label class="property-label">
                        <g:message code="user.email.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="email" name="email" required="required" value="${params.email}" />
                    </span>
                </div>
            </fieldset>
          
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>                
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.create.label')}" />
            </fieldset>
        </form>
    </body>
</html>
