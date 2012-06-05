<%@ page import="org.iisg.eca.domain.EmailTemplate" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
        <ul id="email-templates">
        <g:each in="${EmailTemplate.list(usedInternal: true)}" var="emailTemplate">
            <li>
                <eca:link mapping="email" params="${[type: emailTemplate.action]}" id="${emailTemplate.id}">
                    ${emailTemplate.description}
                </eca:link>
            </li>
        </g:each>
        </ul>
    </body>
</html>