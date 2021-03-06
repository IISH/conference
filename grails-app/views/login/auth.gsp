<html>
    <head>
        <meta name='layout' content='main'/>
        <title><g:message code="springSecurity.login.title" /></title>
    </head>
    <body>
        <div id='login'>
            <h1><g:message code="springSecurity.login.title" /></h1>

            <g:if test="${flash.message && flash.error}">
                <ul class="errors" role="alert">
                    <li>${flash.message}</li>
                </ul>
            </g:if>

            <g:elseif test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:elseif>

            <form action='${postUrl}' method='post' id='loginForm'>
                <fieldset class="form">
                    <div>
                        <label class="property-label" for='username'>
                            <g:message code="springSecurity.login.username.label" />
                        </label>
                        <span class="property-value">
                            <input type='text' name='j_username' id='username'/>
                        </span>
                    </div>
                    <div>
                        <label class="property-label" for='password'>
                            <g:message code="springSecurity.login.password.label" />
                        </label>
                        <span class="property-value">
                            <input type='password' name='j_password' id='password'/>
                        </span>
                    </div>
                </fieldset>
                
                <fieldset class="buttons">
                    <span class="value">
                        <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'/>
                    </span>
                </fieldset>
                
                <fieldset>
                    <g:message code="springSecurity.login.lost.password" />
                    <eca:link controller="login" action="forgotPassword"><g:message code="springSecurity.login.reset" /></eca:link>
                </fieldset>
            </form>
        </div>

        <script type='text/javascript' language="javascript">
            <!--
            (function() {
                document.forms['loginForm'].elements['j_username'].focus();
            })();
            // -->
        </script>
    </body>
</html>
