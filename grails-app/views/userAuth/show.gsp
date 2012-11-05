<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <ol class="property-list">
            <li>
                <span id="firstName-label" class="property-label">
                    <g:message code="user.firstName.label" />
                </span>
                <span class="property-value" aria-labelledby="firstName-label">
                    <g:fieldValue bean="${user}" field="firstName"/>
                </span>
            </li>
            <li>
               <span id="lastName-label" class="property-label">
                   <g:message code="user.lastName.label" />
               </span>
               <span class="property-value" aria-labelledby="lastName-label">
                   <g:fieldValue bean="${user}" field="lastName"/>
               </span>
            </li>
            <li>
                <span id="email-label" class="property-label">
                    <g:message code="user.email.label" />
                </span>
                <span class="property-value" aria-labelledby="email-label">
                    <g:fieldValue bean="${user}" field="email"/>
                </span>
            </li>
            <li>
                <span id="city-label" class="property-label">
                    <g:message code="user.city.label" />
                </span>
                <span class="property-value" aria-labelledby="city-label">
                    <g:fieldValue bean="${user}" field="city"/>
                </span>
            </li>
            <li>
                <span id="country-label" class="property-label">
                    <g:message code="user.country.label" />
                </span>
                <span class="property-value" aria-labelledby="country-label">
                    <g:fieldValue bean="${user}" field="country"/>
                </span>
            </li>
            <li>
                <span id="enabled-label" class="property-label">
                    <g:message code="default.enabled.label" />
                </span>
                <span class="property-value" aria-labelledby="enabled-label">
                    <g:checkBox name="deleted" value="${user.enabled}" disabled="true" />
                </span>
            </li>
            <li>
                <span id="roles-label" class="property-label">
                    <g:message code="role.multiple.label" />
                </span>
                <ul class="property-value" aria-labelledby="roles-label">
                    <g:each in="${roles}" var="role">
                        <li>${role}</li>
                    </g:each>
                </ul>
            </li>
            <li>
                <span id="groups-label" class="property-label">
                    <g:message code="group.multiple.label" />
                </span>
                <ul class="property-value" aria-labelledby="groups-label">
                    <g:each in="${groups}" var="group">
                        <li>${group}</li>
                    </g:each>
                </ul>
            </li>
            <li>
                <span id="pages-label" class="property-label">
                    <g:message code="page.multiple.label" />
                </span>
                <ul class="property-value" aria-labelledby="pages-label">
                    <g:each in="${pages}" var="page">
                        <li>${page}</li>
                    </g:each>
                </ul>
            </li>
        </ol>

        <div class="buttons">
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.back.label" />
                </eca:link>
                <eca:ifUserHasAccess controller="${params.controller}" action="edit">
                    <eca:link action="edit" id="${params.id}">
                        <g:message code="default.button.edit.label" />
                    </eca:link>
                </eca:ifUserHasAccess>
            </fieldset>
        </div>
    </body>
</html>
