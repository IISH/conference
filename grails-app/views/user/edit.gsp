<%@ page import="org.iisg.eca.domain.Country; org.iisg.eca.domain.Title" %>
<!doctype html>
<html>
	<head>
        <meta name="layout" content="main">
        <title><g:message code="default.edit.label" args="${[message(code: 'user.label')]}" /></title>
	</head>
	<body>
        <h1><g:message code="default.edit.label" args="${[message(code: 'user.label')]}" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <g:hasErrors bean="${user}">
            <ul class="errors" role="alert">
                <g:eachError bean="${user}" var="error">
                    <li><g:message error="${error}" /></li>
                </g:eachError>
            </ul>
        </g:hasErrors>

        <form action="#" method="post">
            <fieldset class="form">
                <legend><g:message code="user.personalInfo.label" /></legend>
                <div class="${hasErrors(bean: user, field: 'title', 'error')} required">
                    <label class="property-label">
                        <g:message code="title.label" />
                    </label>
                    <g:select class="property-value" from="${Title.list()}" name="User.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${[null:' ']}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.firstName.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.lastName.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.gender.label" />
                    </label>
                    <g:select class="property-value" from="['M','F']" name="User.gender" value="${user.gender?.toString()}" noSelection="${[null:' ']}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.organisation.label" />
                    </label>
                    <input class="property-value" type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.department.label" />
                    </label>
                    <input class="property-value" type="text" name="User.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.email.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="email" name="User.email" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'email')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.address.label" />
                    </label>
                    <textarea class="property-value" name="User.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                </div>
                <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.city.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="User.city" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.country.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <g:select class="property-value" name="User.country.id" from="${Country.list(sort: 'nameEnglish')}" required="required" optionKey="id" value="${user.country.id}" noSelection="${[null:' ']}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                    <label class="property-label">
                        <g:message code="user.phone.label" />
                    </label>
                    <input class="property-value" type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                    <label class="property-label">
                        <g:message code="user.mobile.label" />
                    </label>
                    <input class="property-value" type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                    <label class="property-label">
                        <g:message code="user.extraInfo.label" />
                    </label>
                    <textarea class="property-value" name="User.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
                </div>

                <legend><g:message code="user.changePassword.label" /></legend>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.password.label" />
                    </label>
                    <input type="password" class="property-value" name="User.password" />
                </div>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.newPassword.label" />
                    </label>
                    <input type="password" class="property-value" name="User.newPassword" />
                </div>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.newPasswordAgain.label" />
                    </label>
                    <input type="password" class="property-value" name="User.newPasswordAgain" />
                </div>
            </fieldset>
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
	</body>
</html>
