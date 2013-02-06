<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title><g:message code="user.label" /></title>
    </head>
    <body>
        <h1><g:message code="user.label" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <ol class="property-list">
            <li>
                <span id="title-label" class="property-label">
                    <g:message code="title.label" />
                </span>
                <span class="property-value" aria-labelledby="title-label">
                    <g:fieldValue bean="${user}" field="title" />
                </span>
            </li>
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
               <span id="gender-label" class="property-label">
                   <g:message code="user.gender.label" />
               </span>
               <span class="property-value" aria-labelledby="gender-label">
                   <g:fieldValue bean="${user}" field="gender" />
               </span>
            </li>
            <li>
                <span id="organisation-label" class="property-label">
                    <g:message code="user.organisation.label" />
                </span>
                <span class="property-value" aria-labelledby="organisation-label">
                    <g:fieldValue bean="${user}" field="organisation"/>
                </span>
            </li>
            <li>
                <span id="department-label" class="property-label">
                    <g:message code="user.department.label" />
                </span>
                <span class="property-value" aria-labelledby="department-label">
                    <g:fieldValue bean="${user}" field="department"/>
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
                <span id="address-label" class="property-label">
                    <g:message code="user.address.label" />
                </span>
                <span class="property-value" aria-labelledby="address-label">
                    <eca:formatText text="${user.address}" />
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
                <span id="phone-label" class="property-label">
                    <g:message code="user.phone.label" />
                </span>
                <span class="property-value" aria-labelledby="phone-label">
                    <g:fieldValue bean="${user}" field="phone"/>
                </span>
            </li>
            <li>
                <span id="mobile-label" class="property-label">
                    <g:message code="user.mobile.label" />
                </span>
                <span class="property-value" aria-labelledby="mobile-label">
                    <g:fieldValue bean="${user}" field="mobile"/>
                </span>
            </li>
            <li>
                <span id="extra-info-label" class="property-label">
                    <g:message code="user.extraInfo.label" />
                </span>
                <span class="property-value" aria-labelledby="extra-info-label">
                    <eca:formatText text="${user.extraInfo}" />
                </span>
            </li>
        </ol>

        <div class="buttons">
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.back.label" />
                </eca:link>
                <eca:link action="edit">
                    <g:message code="default.button.edit.label" />
                </eca:link>
            </fieldset>
        </div>
    </body>
</html>
