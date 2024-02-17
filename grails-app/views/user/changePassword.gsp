<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title><g:message code="user.changePassword.label" /></title>
    </head>
    <body>
        <h1><g:message code="user.changePassword.label" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <g:hasErrors bean="${user}">
            <ul class="errors" role="alert">
                <g:eachError bean="${user}" var="error">
                    <li><g:message error="${error}" /></li>
                </g:eachError>
            </ul>
        </g:hasErrors>

        <form action="#" method="post" autocomplete="off">
            <fieldset class="form">
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.password.label" />
                    </label>
                    <span class="property-value">
                        <input type="password" name="User.password" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.newPassword.label" />
                    </label>
                    <span class="property-value">
                        <input type="password" name="User.newPassword" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.newPasswordAgain.label" />
                    </label>
                    <span class="property-value">
                        <input type="password" name="User.newPasswordAgain" />
                    </span>
                </div>
            </fieldset>
            
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
            </fieldset>
        </form>
    </body>
</html>
