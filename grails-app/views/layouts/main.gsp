<%@ page import="java.text.SimpleDateFormat; org.iisg.eca.domain.Setting;org.springframework.validation.FieldError" %>
<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        
        <title>
            <g:layoutTitle default="${(curPage) ? curPage.toString() : 'ECA' }" />
        </title>
        
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'default.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.css')}" type="text/css">
        
        <g:javascript library="jquery" plugin="jquery" />
        <r:layoutResources />
        <g:javascript src="jquery-ui.js"  />
        <g:layoutHead/>
        <g:javascript src="application.js" />
    </head>
    <body>
        <div id="header" role="banner">
            <div id="event-header">
                <a id="event-logo" href="${grailsApplication.config.grails.serverURL}" name="top">
                    ${grailsApplication.config.grails.serverURL}
                </a>
                
                <a href="http://www.iisg.nl/" class="hosted">Hosted by IISH</a>
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
                    Select language:
                    <ul>
                        <li><a href="?lang=nl"><g:img dir="images/flags" file="nl.png" /></a></li>
                        <li><a href="?lang=en"><g:img dir="images/flags" file="us.png" /></a></Li>
                    </ul>
                </div>

                <sec:ifLoggedIn>
                    <div id="loggedin">
                        <g:message code="springSecurity.loggedin.welcome" args="${[sec.loggedInUserInfo(field: 'fullName')]}" />
                        <eca:roles />
                        <g:img dir="images/skin" file="sorted_desc.gif" />
                    </div>
                </sec:ifLoggedIn>
            </div>
        </div>

        <div id="nav" role="navigation">
            <dl id="menu">
                <dt>Web</dt>
                <dd><a href="http://www.iisg.nl/esshc">ESSHC</a></dd>

                <dd>&nbsp;</dd>

                <g:if test="${params.event && params.date}">
                    <dt class="esshc">CMS</dt>
                    <eca:menu />
                </g:if>
            </dl>
        </div>

        <div id="content" role="main">
            <g:if test="${curPage}">
                <h1>${curPage.toString()}</h1>
            </g:if>

            <g:if test="${flash.message && curPage}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:layoutBody />
            
            <span class="top right">
                <a href="#top">Back to top</a>
            </span>
        </div>

        <div id="footer" class="clear" role="contentinfo">
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