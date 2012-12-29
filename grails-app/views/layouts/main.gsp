<%@ page import="org.springframework.context.i18n.LocaleContextHolder; java.text.SimpleDateFormat; org.iisg.eca.domain.Setting;org.springframework.validation.FieldError" %>
<!doctype html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

        <meta http-equiv="cache-control" content="max-age=0" />
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="expires" content="-1" />
        <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
        <meta http-equiv="pragma" content="no-cache" />
        
        <title>
            <g:layoutTitle default="${(curPage) ? message(code: curPage.titleCode, args: [message(code: curPage.titleArg)], default: curPage.titleDefault) : 'ECA'}" />
        </title>
        
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'default.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.css')}" type="text/css">
        
        <g:javascript library="jquery" plugin="jquery" />
        <r:layoutResources />
        <g:javascript src="jquery.cookie.js" />
        <g:javascript src="jquery-ui.js" />
        <g:layoutHead />
        <g:javascript src="application.js" />
    </head>
    <body>
        <div id="header" role="banner">
            <div id="event-header">
                <a id="event-logo" href="${grailsApplication.config.grails.serverURL}" name="top">
                    ${grailsApplication.config.grails.serverURL}
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
                            <g:img class="${(curLang == 'nl') ? 'selected' : ''}" dir="images/flags" file="nl.png" />
                        </a></li>
                        <li><a href="?lang=en">
                            <g:img class="${(curLang == 'en') ? 'selected' : ''}" dir="images/flags" file="us.png" />
                        </a></li>
                    </ul>
                </div>
                
                <sec:ifLoggedIn>
                    <div id="loggedin">
                        <g:message code="springSecurity.loggedin.welcome" args="${[sec.loggedInUserInfo(field: 'fullName')]}" />
                        <span class="roles">
                            <eca:roles />
                        </span>
                        <g:img dir="images/skin" file="sorted_desc.gif" />
                    </div>
                </sec:ifLoggedIn>
            </div>
        </div>
      
        <div id="container">
            <div id="nav" role="navigation">
                <dl id="menu">
                    <g:if test="${params.event && params.date}">
                        <dt>Web</dt>
                        <dd><a target="_blank" href="${Setting.getByEvent(Setting.findAllByProperty(Setting.WEB_ADDRESS)).value}">${curDate.event.shortName}</a></dd>

                        <dd>&nbsp;</dd>

                        <dt>CMS</dt>
                        <eca:menu />
                    </g:if>
                </dl>
            </div>

            <div id="content" role="main">
                <g:if test="${curPage}">
                    <h1><g:message code="${curPage.titleCode}" args="${[message(code: curPage.titleArg)]}" default="${curPage.titleDefault}" /></h1>
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
                    <a href="#top"><g:message code="default.back.to.top" /> <span class="ui-icon ui-icon-triangle-1-n" /></a>
                </span>
            </div>
          
            <div class="clear"></div>
        </div>

        <div id="footer" role="contentinfo">
            ${grailsApplication.config.grails.serverURL} -
            <g:message code="default.last.updated" />:
            <g:formatDate date="${new SimpleDateFormat("yy-MM-dd").parse(Setting.findByProperty(Setting.LAST_UPDATED).value)}" format="MMMMM yyyy" />
        </div>

        <div id="usermenu">
            <ul>
                <li><eca:link controller="user" action="show"><g:message code="menu.personalpage" /></eca:link></li>
                <li><eca:link controller="logout"><g:message code="menu.logout" /></eca:link></li>
            </ul>
        </div>

        <r:layoutResources />
    </body>
</html>