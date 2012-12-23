<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title><g:message code="default.welcome.label" /></title>
    </head>
    <body>
        <h1><g:message code="default.welcome.label" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
    
    
    </body>
</html>