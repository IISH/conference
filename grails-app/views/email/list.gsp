<%@ page import="org.iisg.eca.domain.EmailTemplate" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
        <ol id="email-templates">
        <g:each in="${EmailTemplate.findAllByShowInBackend(true)}" var="emailTemplate">
            <li>
                <eca:link action="send" id="${emailTemplate.id}" params="${[type: emailTemplate.action]}">
                    ${emailTemplate.description}
                </eca:link>
                <span>
                    <eca:formatText text="${emailTemplate.comment}" />
                </span>
            </li>
        </g:each>
        </ol>
    </body>
</html>