<%@ page import="org.springframework.context.i18n.LocaleContextHolder; java.text.SimpleDateFormat; org.iisg.eca.domain.Setting;org.springframework.validation.FieldError" %>
<!doctype html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="robots" content="noindex,nofollow">

        <meta http-equiv="cache-control" content="max-age=0" />
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="expires" content="-1" />
        <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
        <meta http-equiv="pragma" content="no-cache" />

        <title>
            <g:layoutTitle default="${(curPage) ? curPage.getTitle(curLocale) : 'ECA'}" />
        </title>

        <link rel="stylesheet" href="${eca.createLink(controller: 'css', action: 'css', noPreviousInfo: true)}" type="text/css">
        <asset:stylesheet src="easy-autocomplete.css"/>
        <asset:stylesheet src="jquery-ui.min.css"/>

        <asset:javascript src="application.js" />

        <g:layoutHead />
    </head>
    <body>
        <div id="header" role="banner">
            <div id="event-header">
                <a id="event-logo" href="${grailsApplication.config.grails.serverURL}" name="top">
                    ${Setting.getSetting(Setting.APPLICATION_TITLE, curDate?.event).value}
                </a>
                <a href="http://socialhistory.org/" target="_blank" class="hosted">Hosted by IISH</a>
            </div>

            <div id="banner-container">
                <div id="banner"></div>
            </div>

            <div id="banner-menu">
                <g:if test="${curDate}">
                    <div id="event-switcher">
                        <eca:eventSwitcher date="${curDate}" />
                    </div>
                </g:if>

                <div id="locales">
                    Language:
                    <ul>
                        <li><a href="?lang=nl">
                            <asset:image class="${(curLang == 'nl') ? 'selected' : ''}" src="flags/nl.png" />
                        </a></li>
                        <li><a href="?lang=en">
                            <asset:image class="${(curLang == 'en') ? 'selected' : ''}" src="flags/us.png" />
                        </a></li>
                    </ul>
                </div>

                <sec:ifLoggedIn>
                    <div id="loggedin">
                        <g:message code="springSecurity.loggedin.welcome" args="${[eca.fullName()]}" />
                        <span class="roles">
                            <eca:roles />
                        </span>
                        <asset:image src="skin/sorted_desc.png" />
                    </div>
                </sec:ifLoggedIn>
            </div>
        </div>

        <div id="container">
            <div id="nav" role="navigation">
                <dl id="menu">
                    <g:if test="${curDate}">
                        <dt>Web</dt>
                        <dd><a target="_blank" href="${Setting.getSetting(Setting.WEB_ADDRESS, curDate?.event).value}">${curDate?.event?.shortName}</a></dd>

                        <g:if test="${Setting.getSetting(Setting.ONLINE_PROGRAMME_URL, curDate?.event)?.value}">
                            <dd>
                                <a target="_blank" href="${Setting.getSetting(Setting.ONLINE_PROGRAMME_URL, curDate?.event).value}">
                                    <g:message code="default.online.programme.label" />
                                </a>
                            </dd>
                        </g:if>

                        <dd>&nbsp;</dd>

                        <dt>CMS</dt>
                        <dd>
                            <input type="text" name="menu-filter" id="menu-filter" placeholder="${g.message(code: 'menu.filter.label')}" />
                        </dd>
                        <eca:menu locale="${curLocale}" />
                    </g:if>
                </dl>
            </div>

            <div id="content" role="main">
                <g:if test="${curPage}">
                    <h1>${curPage.getTitle(curLocale)}</h1>
                </g:if>

                <g:if test="${flash.message && flash.error && curPage}">
                    <ul class="errors" role="alert">
                        <li>${flash.message}</li>
                    </ul>
                </g:if>

                <g:elseif test="${flash.message && curPage}">
                    <div class="message" role="status">${flash.message}</div>
                </g:elseif>

                <g:layoutBody />

                <span class="top right">
                    <a href="#top"><g:message code="default.back.to.top" /> <span class="ui-icon ui-icon-triangle-1-n"></span></a>
                </span>
            </div>
        </div>

        <div id="footer" role="contentinfo">
            ${grailsApplication.config.grails.serverURL} -
            <g:message code="default.last.updated" />:
            <g:formatDate date="${Setting.findByProperty(Setting.LAST_UPDATED).getDateValue()}" format="MMMMM yyyy" />
        </div>

        <div class="menu" id="usermenu">
            <ul>
                <li><eca:link controller="user" action="show"><g:message code="menu.personalpage" /></eca:link></li>
                <li><eca:link controller="user" action="changePassword"><g:message code="user.changePassword.label" /></eca:link></li>
                <li><eca:link controller="logout"><g:message code="menu.logout" /></eca:link></li>
            </ul>
        </div>
    </body>
</html>