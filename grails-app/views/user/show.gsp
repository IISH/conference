<%@ page import="org.iisg.eca.User" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'user.label')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
        <h1><g:message code="default.show.label" args="[entityName]" /></h1>
        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>
        <ol class="property-list user">
            <g:if test="${userInstance?.email}">
                <li class="fieldcontain">
                    <span id="email-label" class="property-label"><g:message code="user.email.label" /></span>
                    <span class="property-value" aria-labelledby="email-label"><g:fieldValue bean="${userInstance}" field="email"/></span>
                </li>
            </g:if>

            <g:if test="${userInstance?.fullName}">
                <li class="fieldcontain">
                    <span id="fullName-label" class="property-label"><g:message code="user.fullName.label" /></span>
                    <span class="property-value" aria-labelledby="fullName-label"><g:fieldValue bean="${userInstance}" field="fullName"/></span>
                </li>
            </g:if>

            <g:if test="${userInstance?.institute}">
                <li class="fieldcontain">
                    <span id="institute-label" class="property-label"><g:message code="user.institute.label" /></span>
                    <span class="property-value" aria-labelledby="institute-label"><g:fieldValue bean="${userInstance}" field="institute"/></span>
                </li>
            </g:if>

            <g:if test="${userInstance?.country}">
                <li class="fieldcontain">
                    <span id="country-label" class="property-label"><g:message code="user.country.label" /></span>
                    <span class="property-value" aria-labelledby="country-label"><g:fieldValue bean="${userInstance}" field="country"/></span>
                </li>
            </g:if>

            <g:if test="${userInstance?.language}">
                <li class="fieldcontain">
                    <span id="language-label" class="property-label"><g:message code="user.language.label" /></span>
                    <span class="property-value" aria-labelledby="language-label"><g:fieldValue bean="${userInstance}" field="language"/></span>
                </li>
            </g:if>
        </ol>

        <g:form>
            <fieldset class="buttons">
                <g:hiddenField name="id" value="${userInstance?.id}" />
                <g:link class="edit" action="edit"><g:message code="default.button.edit.label" /></g:link>
            </fieldset>
        </g:form>
	</body>
</html>
