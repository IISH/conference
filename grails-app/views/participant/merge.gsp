<%@ page import="org.iisg.eca.domain.Setting" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
</head>

<body>
<form method="post" action="#">
    <div id="merge-columns" class="columns">
        <div class="column">
            <ol class="property-list">
                <li>
                    <span id="title-label-user-1" class="property-label">
                        <g:message code="title.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="title-label-user-1">
                        <g:fieldValue bean="${userA}" field="title"/>
                    </span>
                </li>
                <li>
                    <span id="firstName-label-user-1" class="property-label">
                        <g:message code="user.firstName.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="firstName-label-user-1">
                        <g:fieldValue bean="${userA}" field="firstName"/>
                    </span>
                </li>
                <li>
                    <span id="lastName-label-user-1" class="property-label">
                        <g:message code="user.lastName.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="lastName-label-user-1">
                        <g:fieldValue bean="${userA}" field="lastName"/>
                    </span>
                </li>
                <li>
                    <span id="gender-label-user-1" class="property-label">
                        <g:message code="user.gender.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="gender-label-user-1">
                        <g:fieldValue bean="${userA}" field="gender"/>
                    </span>
                </li>
                <li>
                    <span id="organisation-label-user-1" class="property-label">
                        <g:message code="user.organisation.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="organisation-label-user-1">
                        <g:fieldValue bean="${userA}" field="organisation"/>
                    </span>
                </li>
                <g:if test="${Setting.getSetting(Setting.SHOW_DEPARTMENT).booleanValue}">
                    <li>
                        <span id="department-label-user-1" class="property-label">
                            <g:message code="user.department.label"/>
                        </span>
                        <span class="property-value" aria-labelledby="department-label-user-1">
                            <g:fieldValue bean="${userA}" field="department"/>
                        </span>
                    </li>
                </g:if>
                <g:if test="${Setting.getSetting(Setting.SHOW_EDUCATION).booleanValue}">
                    <li>
                        <span id="education-label-user-1" class="property-label">
                            <g:message code="user.education.label"/>
                        </span>
                        <span class="property-value" aria-labelledby="education-label-user-1">
                            <g:fieldValue bean="${userA}" field="education"/>
                        </span>
                    </li>
                </g:if>
                <li>
                    <span id="email-label-user-1" class="property-label">
                        <g:message code="user.email.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="email-label-user-1">
                        <g:fieldValue bean="${userA}" field="email"/>
                    </span>
                </li>
                <li>
                    <span id="emailDiscontinued-label-user-1" class="property-label">
                        <g:message code="user.emailDiscontinued.label"/>
                    </span>
                    <span class="property-value" arial-labelledby="emailDiscontinued-label">
                        <g:checkBox name="emailDiscontinued" value="${userA.emailDiscontinued}" disabled="true"/>
                    </span>
                </li>
                <li>
                    <span id="address-label-user-1" class="property-label">
                        <g:message code="user.address.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="address-label-user-1">
                        <eca:formatText text="${userA.address}"/>
                    </span>
                </li>
                <li>
                    <span id="city-label-user-1" class="property-label">
                        <g:message code="user.city.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="city-label-user-1">
                        <g:fieldValue bean="${userA}" field="city"/>
                    </span>
                </li>
                <li>
                    <span id="country-label-user-1" class="property-label">
                        <g:message code="user.country.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="country-label-user-1">
                        <g:fieldValue bean="${userA}" field="country"/>
                    </span>
                </li>
                <li>
                    <span id="phone-label-user-1" class="property-label">
                        <g:message code="user.phone.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="phone-label-user-1">
                        <g:fieldValue bean="${userA}" field="phone"/>
                    </span>
                </li>
                <li>
                    <span id="mobile-label-user-1" class="property-label">
                        <g:message code="user.mobile.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="mobile-label-user-1">
                        <g:fieldValue bean="${userA}" field="mobile"/>
                    </span>
                </li>
                <li>
                    <span id="cv-label-user-1" class="property-label">
                        <g:message code="user.cv.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="cv-label-user-1">
                        <eca:formatText text="${userA.cv}"/>
                    </span>
                </li>
                <li>
                    <span id="extra-info-label-user-1" class="property-label">
                        <g:message code="user.extraInfo.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="extra-info-label-user-1">
                        <eca:formatText text="${userA.extraInfo}"/>
                    </span>
                </li>
            </ol>
        </div>

        <div class="column">
            <ol class="property-list">
                <li>
                    <span id="title-label-user-2" class="property-label">
                        <g:message code="title.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="title-label-user-2">
                        <g:fieldValue bean="${userB}" field="title"/>
                    </span>
                </li>
                <li>
                    <span id="firstName-label-user-2" class="property-label">
                        <g:message code="user.firstName.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="firstName-label-user-2">
                        <g:fieldValue bean="${userB}" field="firstName"/>
                    </span>
                </li>
                <li>
                    <span id="lastName-label-user-2" class="property-label">
                        <g:message code="user.lastName.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="lastName-label-user-2">
                        <g:fieldValue bean="${userB}" field="lastName"/>
                    </span>
                </li>
                <li>
                    <span id="gender-label-user-2" class="property-label">
                        <g:message code="user.gender.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="gender-label-user-2">
                        <g:fieldValue bean="${userB}" field="gender"/>
                    </span>
                </li>
                <li>
                    <span id="organisation-label-user-2" class="property-label">
                        <g:message code="user.organisation.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="organisation-label-user-2">
                        <g:fieldValue bean="${userB}" field="organisation"/>
                    </span>
                </li>
                <g:if test="${Setting.getSetting(Setting.SHOW_DEPARTMENT).booleanValue}">
                    <li>
                        <span id="department-label-user-2" class="property-label">
                            <g:message code="user.department.label"/>
                        </span>
                        <span class="property-value" aria-labelledby="department-label-user-2">
                            <g:fieldValue bean="${userB}" field="department"/>
                        </span>
                    </li>
                </g:if>
                <g:if test="${Setting.getSetting(Setting.SHOW_EDUCATION).booleanValue}">
                    <li>
                        <span id="education-label-user-2" class="property-label">
                            <g:message code="user.education.label"/>
                        </span>
                        <span class="property-value" aria-labelledby="education-label-user-2">
                            <g:fieldValue bean="${userB}" field="education"/>
                        </span>
                    </li>
                </g:if>
                <li>
                    <span id="email-label-user-2" class="property-label">
                        <g:message code="user.email.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="email-label-user-2">
                        <g:fieldValue bean="${userB}" field="email"/>
                    </span>
                </li>
                <li>
                    <span id="emailDiscontinued-label-user-2" class="property-label">
                        <g:message code="user.emailDiscontinued.label"/>
                    </span>
                    <span class="property-value" arial-labelledby="emailDiscontinued-label">
                        <g:checkBox name="emailDiscontinued" value="${userB.emailDiscontinued}" disabled="true"/>
                    </span>
                </li>
                <li>
                    <span id="address-label-user-2" class="property-label">
                        <g:message code="user.address.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="address-label-user-2">
                        <eca:formatText text="${userB.address}"/>
                    </span>
                </li>
                <li>
                    <span id="city-label-user-2" class="property-label">
                        <g:message code="user.city.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="city-label-user-2">
                        <g:fieldValue bean="${userB}" field="city"/>
                    </span>
                </li>
                <li>
                    <span id="country-label-user-2" class="property-label">
                        <g:message code="user.country.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="country-label-user-2">
                        <g:fieldValue bean="${userB}" field="country"/>
                    </span>
                </li>
                <li>
                    <span id="phone-label-user-2" class="property-label">
                        <g:message code="user.phone.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="phone-label-user-2">
                        <g:fieldValue bean="${userB}" field="phone"/>
                    </span>
                </li>
                <li>
                    <span id="mobile-label-user-2" class="property-label">
                        <g:message code="user.mobile.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="mobile-label-user-2">
                        <g:fieldValue bean="${userB}" field="mobile"/>
                    </span>
                </li>
                <li>
                    <span id="cv-label-user-2" class="property-label">
                        <g:message code="user.cv.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="cv-label-user-2">
                        <eca:formatText text="${userB.cv}"/>
                    </span>
                </li>
                <li>
                    <span id="extra-info-label-user-2" class="property-label">
                        <g:message code="user.extraInfo.label"/>
                    </span>
                    <span class="property-value" aria-labelledby="extra-info-label-user-2">
                        <eca:formatText text="${userB.extraInfo}"/>
                    </span>
                </li>
            </ol>
        </div>

        <div class="clear empty"></div>
    </div>

    <fieldset class="buttons">
        <input type="hidden" name="user" value="${userB.id}"/>

        <eca:link previous="true">
            <g:message code="default.button.cancel.label"/>
        </eca:link>
        <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.merge.label')}"/>
    </fieldset>
</form>
</body>
</html>
