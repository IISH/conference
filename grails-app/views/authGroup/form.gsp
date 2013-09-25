<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <form method="post" action="#">
            <fieldset class="form">
                <div class="${hasErrors(bean: group, field: 'name', 'error')} required">
                    <label class="property-label" for="Group.name">
                        <g:message code="group.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input id="Group.name" maxlength="50" name="Group.name" value="${group?.name}" required="required" type="text" />
                    </span>
                </div>
                <div>
                    <label class="property-label" for="Group.pages">
                        <g:message code="group.pagesInGroup.label" />
                    </label>
                    <span class="property-value">
                        <g:select id="Group.pages" name="Group.pages" class="moveSelectBox primary" optionKey="id" optionValue="titleWithAction" from="${pages}" multiple="multiple" size="10" />
                        <input type="button" class="moveItems" value="${g.message(code: 'default.remove.selected.label', args: [g.message(code: 'group.pagesInGroup.label')]).encodeAsHTML()}" />
                    </span>
                </div>
                <div>
                    <label class="property-label" for="Group.availablePages">
                        <g:message code="group.pagesNotInGroup.label" />
                    </label>
                    <span class="property-value">
                        <g:select id="Group.availablePages" name="Group.availablePages" class="moveSelectBox" optionKey="id" optionValue="titleWithAction" from="${pagesNotInGroup}" multiple="multiple" size="10" />
                        <input type="button" class="moveItems" value="${g.message(code: 'default.add.selected.label', args: [g.message(code: 'group.pagesInGroup.label')]).encodeAsHTML()}" />
                    </span>
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="user.multiple.label" />
                    </label>
                    <ul class="property-value">
                        <g:each in="${users}" var="user" status="i">
                            <li>
                                <eca:usersAutoComplete name="User_${i}.id" labelValue="${user.lastName + ', ' + user.firstName}" idValue="${user.id}" queryName="allUsers" required="required" />
                                <span class="ui-icon ui-icon-circle-minus"></span>
                            </li>
                        </g:each>

                        <li class="add">
                            <span class="ui-icon ui-icon-circle-plus"></span>
                            <g:message code="default.add.label" args="[g.message(code: 'user.label').toLowerCase()]" />
                            <input type="hidden" name="User.to-be-deleted" class="to-be-deleted" />
                        </li>

                        <li class="hidden">
                            <eca:usersAutoComplete name="User_null.id" labelValue="" idValue="" queryName="allUsers" required="required" />
                            <span class="ui-icon ui-icon-circle-minus"></span>
                        </li>
                    </ul>
                </div>
                <div class="${hasErrors(bean: group, field: 'enabled', 'error')} ">
                    <label class="property-label" for="Group.enabled">
                        <g:message code="default.enabled.label" />
                    </label>
                    <span class="property-value">
                        <g:checkBox id="Group.enabled" name="Group.enabled" value="${group?.enabled}" />
                    </span>
                </div>
            </fieldset>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>

                <g:if test="${params.action != 'create'}">
                    <eca:link controller="${params.controller}" action="delete" id="${params.id}" class="btn_delete">
                        <g:message code="default.deleted.label" />
                    </eca:link>
                    
                    <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                </g:if>
                
                <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
            </fieldset>
        </form>
    </body>
</html>
