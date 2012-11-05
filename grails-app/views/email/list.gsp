<%@ page import="org.iisg.eca.domain.EmailTemplate" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <ol id="email-templates">
        <g:each in="${templates}" var="emailTemplate">
            <li>
                <eca:ifUserHasAccess controller="email" action="send">
                    <eca:link action="send" id="${emailTemplate.id}" params="${[type: emailTemplate.action]}">
                        ${emailTemplate.description}
                    </eca:link>
                </eca:ifUserHasAccess>

                <eca:ifUserHasNoAccess controller="email" action="send">
                        ${emailTemplate.description}
                </eca:ifUserHasNoAccess>

                <span>
                    <eca:formatText text="${emailTemplate.comment}" />
                </span>
            </li>
        </g:each>
        </ol>
    </body>
</html>