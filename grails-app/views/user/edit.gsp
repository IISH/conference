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
                    <span class="property-value">
                        <g:select from="${Title.list()}" name="User.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${[null:' ']}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.firstName.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.lastName.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.gender.label" />
                    </label>
                    <span class="property-value">
                        <g:select from="['M','F']" name="User.gender" value="${user.gender?.toString()}" noSelection="${[null:' ']}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.organisation.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.department.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.email.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="email" name="User.email" required="required" maxlength="100" value="${fieldValue(bean: user, field: 'email')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.address.label" />
                    </label>
                    <span class="property-value">
                        <textarea name="User.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.city.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.city" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.country.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="User.country.id" from="${Country.list(sort: 'nameEnglish')}" optionKey="id" value="${user.country?.id}" noSelection="${[null:' ']}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                    <label class="property-label">
                        <g:message code="user.phone.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                    <label class="property-label">
                        <g:message code="user.mobile.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                    <label class="property-label">
                        <g:message code="user.extraInfo.label" />
                    </label>
                    <span class="property-value">
                        <textarea name="User.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
                    </span>
                </div>

                <legend><g:message code="user.changePassword.label" /></legend>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.password.label" />
                    </label>
                    <span class="property-value">
                        <input type="password" name="User.password" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.newPassword.label" />
                    </label>
                    <span class="property-value">
                        <input type="password" name="User.newPassword" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'password', 'error')}">
                    <label class="property-label">
                        <g:message code="user.newPasswordAgain.label" />
                    </label>
                    <span class="property-value">
                        <input type="password" name="User.newPasswordAgain" />
                    </span>
                </div>
            </fieldset>
            
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <g:if test="${params.action != 'create'}">
                  <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
                </g:if>
            </fieldset>
        </form>
    </body>
</html>
