<html>
<head>
	<meta name='layout' content='main'/>
	<title><g:message code="springSecurity.forgot.title" /></title>
</head>
<body>
    <div id='login'>
        <div class='fheader'><g:message code="springSecurity.forgot.header" /></div>

        <g:if test='${flash.message}'>
            <div class='login_message'>${flash.message}</div>
        </g:if>

        <g:form controller="login" action="newPassword" method='POST' id='loginForm' class='cssform' autocomplete='off'>
            <p>
                <label for='username'><g:message code="springSecurity.forgot.username.label" />:</label>
                <input type='text' class='text_' name='j_username' id='username'/>
            </p>

            <p>
                <input type='submit' id="submit" value='${message(code: "springSecurity.forgot.button")}'/>
            </p>
        </g:form>
    </div>
</body>
</html>
