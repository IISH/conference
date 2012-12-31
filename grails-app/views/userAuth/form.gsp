<%@ page import="org.iisg.eca.domain.Page; org.iisg.eca.domain.Group; org.iisg.eca.domain.Role; org.iisg.eca.domain.Country; org.iisg.eca.domain.Title" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
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
                <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.email.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="email" name="User.email" required="required" maxlength="100" value="${fieldValue(bean: user, field: 'email')}" />
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
                <div class="${hasErrors(bean: user, field: 'enabled', 'error')}">
                    <label class="property-label">
                        <g:message code="default.enabled.label" />
                    </label>
                    <span class="property-value">
                        <g:checkBox name="User.enabled" checked="${user.enabled}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'roles', 'error')}">
                    <label class="property-label">
                        <g:message code="role.multiple.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="User.roles" from="${allRoles}" multiple="true" optionKey="id" optionValue="role" value="${roles}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'groups', 'error')}">
                    <label class="property-label">
                        <g:message code="group.multiple.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="User.groups" from="${allGroups}" multiple="true" optionKey="id" optionValue="name" value="${groups}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'pages', 'error')}">
                    <label class="property-label">
                        <g:message code="page.multiple.label" />
                    </label>
                    <ul class="property-value">
                        <g:each in="${pages}" var="page" status="i">
                            <li>
                              <input type="hidden" name="Page_${i}.id" id="Page_${i}.id" value="${page.page?.id}">

                                <label class="property-label">
                                    <g:select name="Page_${i}.page.id" from="${allPages}" optionKey="id" value="${page.page?.id}" />
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
