<%@ page import="org.iisg.eca.domain.Page; org.iisg.eca.domain.Group; org.iisg.eca.domain.Role; org.iisg.eca.domain.Country; org.iisg.eca.domain.Title" %>
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
              <li>
                <g:message error="${error}" />
              </li>
            </g:eachError>
          </ul>
        </g:hasErrors>

        <form action="#" method="post">
            <fieldset class="form">
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
                <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.email.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="email" name="User.email" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'email')}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'enabled', 'error')}">
                    <label class="property-label">
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox class="property-value" name="User.enabled" checked="${user.enabled}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'roles', 'error')}">
                    <label class="property-label">
                        <g:message code="role.multiple.label" />
                    </label>
                    <g:select class="property-value" name="User.roles" from="${allRoles}" multiple="true" optionKey="id" optionValue="role" value="${roles}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'groups', 'error')}">
                    <label class="property-label">
                        <g:message code="group.multiple.label" />
                    </label>
                    <g:select class="property-value" name="User.groups" from="${Group.list()}" multiple="true" optionKey="id" optionValue="name" value="${groups}" />
                </div>
                <div class="${hasErrors(bean: user, field: 'pages', 'error')}">
                    <label class="property-label">
                        <g:message code="page.multiple.label" />
                    </label>
                    <ul class="property-value">
                        <g:each in="${pages}" var="page" status="i">
                            <li>
                                <input type="hidden" name="Page_${i}.id" id="Page_${i}.id">

                                <label class="property-label">
                                    <g:select name="Page_${i}.page.id" from="${allPages}" optionKey="id" value="${page.page.id}" />
                                </label>

                                <label class="property-label">
                                    <g:message code="default.access.denied" />
                                    <g:checkBox name="Page_${i}.denied" checked="${page.denied}" />
                                </label>

                                <span class="ui-icon ui-icon-circle-minus"></span>
                            </li>
                        </g:each>

                        <li class="add">
                            <span class="ui-icon ui-icon-circle-plus"></span>
                            <g:message code="default.add.label" args="[g.message(code: 'page.label').toLowerCase()]"  />
                            <input type="hidden" class="to-be-deleted" name="Page.to-be-deleted">
                        </li>

                        <li class="hidden">
                            <input type="hidden" name="Page_null.id" id="Page_null.id">

                            <label class="property-label">
                                <g:select name="Page_null.page.id" from="${allPages}" optionKey="id" />
                            </label>

                            <label class="property-label">
                                <g:message code="default.access.denied" />
                                <g:checkBox name="Page_null.denied" />
                            </label>

                            <span class="ui-icon ui-icon-circle-minus"></span>
                        </li>
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
