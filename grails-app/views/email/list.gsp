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
                    <eca:link action="send" id="${emailTemplate.id}">
                        ${emailTemplate.description}
                    </eca:link>
                </eca:ifUserHasAccess>

                <eca:ifUserHasNoAccess controller="email" action="send">
                        ${emailTemplate.description}
                </eca:ifUserHasNoAccess>
				<div class="email-template-description">
	                <g:if test="${emailTemplate.comment}">
		                <span class="italic"><eca:formatText text="${emailTemplate.comment}" /></span> <br />
	                </g:if>
		            <span class="bold"><g:message code="emailTemplate.subject.label" />:</span>
	                ${emailTemplate.subject}
				</div>
            </li>
        </g:each>
        </ol>
    </body>
</html>