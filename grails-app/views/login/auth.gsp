<html>
<head>
	<meta name='layout' content='main'/>
	<title><g:message code="springSecurity.login.title" /></title>
</head>
<body>
    <div id='login'>
        <h1><g:message code="springSecurity.login.title" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <form action='${postUrl}' method='post' id='loginForm' autocomplete='off'>
            <fieldset class="form">
                <div class="fieldcontain">
                    <label for='username'><g:message code="springSecurity.login.username.label" />:</label>
                    <input type='text' class='text_' name='j_username' id='username'/>
                </div>
                <div class="fieldcontain">
                    <label for='password'><g:message code="springSecurity.login.password.label" />:</label>
                    <input type='password' class='text_' name='j_password' id='password'/>
                </div>
            </fieldset>
            <fieldset class="buttons">
                <input type='submit' value='${message(code: "springSecurity.login.button")}'/>
                <eca:link controller="login" action="forgotPassword"><g:message code="springSecurity.login.reset" /></eca:link>
            </fieldset>
        </form>
    </div>
</body>
</html>
