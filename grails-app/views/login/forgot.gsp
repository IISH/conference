<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.forgot.title" /></title>
</head>
<body>
    <div id='login'>
        <h1><g:message code="springSecurity.forgot.header" /></h1>

        <g:if test='${flash.message}'>
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <g:form controller="login" action="newPassword" method="POST" id="loginForm" autocomplete="off">
            <fieldset class="form">
                <div>
                    <label for='username' class="property-label">
                        <g:message code="springSecurity.forgot.username.label" />
                    </label>
                    <span class="property-value">
                        <input type='text' name='j_username' id='username'/>
                    </span>
                </div>
            </fieldset>
            
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.back.label" />
                </eca:link>
                <input type='submit' id="submit" value='${message(code: "springSecurity.forgot.button")}'/>
            </fieldset>
        </g:form>
    </div>
</body>
</html>
