<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <ol class="property-list">
            <li>
                <span id="name-label" class="property-label">
                    <eca:fallbackMessage code="group.name.label" fbCode="group.name.label" />
                </span>
                <span class="property-value" arial-labelledby="name-label">
                    <eca:formatText text="${group.name}" />
                </span>
            </li>
            <li>
                <span id="pages-label" class="property-label">
                    <eca:fallbackMessage code="page.multiple.label" fbCode="group.pages.label" />
                </span>
                <ul class="property-value" arial-labelledby="pages-label">
                    <g:if test="${pages.size() > 0}">
                    <g:each in="${pages}" var="page" status="i">
                        <li>
                            <eca:formatText text="${page}" />
                        </li>
                    </g:each>
                    </g:if>
                    <g:else>
                        <li>-</li>
                    </g:else>
                </ul>
            </li>
            <li>
                <span id="users-label" class="property-label">
                    <eca:fallbackMessage code="user.multiple.label" fbCode="group.users.label" />
                </span>
                <ul class="property-value" arial-labelledby="users-label">
                    <g:if test="${users.size() > 0}">
                    <g:each in="${users}" var="user" status="i">
                        <li>
                            <eca:formatText text="${user}" />
                        </li>
                    </g:each>
                    </g:if>
                    <g:else>
                        <li>-</li>
                    </g:else>
                </ul>
            </li>
            <li>
                <span id="enabled-label" class="property-label">
                    <eca:fallbackMessage code="default.enabled.label" fbCode="group.enabled.label" />
                </span>
                <span class="property-value" arial-labelledby="enabled-label">
                    <g:checkBox name="enabled" value="${group.enabled}" disabled="true" />
                </span>
            </li>
        </ol>

        <div class="buttons">
            <eca:link previous="true">
                <g:message code="default.button.back.label" />
            </eca:link>
            <eca:ifUserHasAccess controller="${params.controller}" action="edit">
                <eca:link controller="${params.controller}" action="edit" id="${params.id}">
                    <g:message code="default.button.edit.label" />
                </eca:link>
            </eca:ifUserHasAccess>
        </div>
    </body>
</html>