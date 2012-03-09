<%@ page import="org.iisg.eca.Conference" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'conference.label')}" />
		<title><g:message code="conference.list" args="[entityName]" /></title>
	</head>
	<body>
        <h1><g:message code="conference.events" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <eca:listConferences conferenceList="${conferenceInstanceList}" dateList="${dateInstanceList}" />
	</body>
</html>
