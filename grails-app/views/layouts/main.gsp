<%@ page import="org.iisg.eca.domain.Setting;org.springframework.validation.FieldError" %>
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
                <g:if test="${params.event && params.date}">
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
              <!-- <dt class="esshc">Web</dt>

                <dd><a href="http://www.iisg.nl/esshc">ESSHC</A></dd>
                <dd>&nbsp;</dd>           -->

                <dt class="esshc">CMS</dt>

                <dd>&nbsp;</dd>

                <g:if test="${params.event && params.date}" >
                    <g:menu />
                </g:if>

                <!--<dd><a href="Participants.asp?year=12&partStat=-1&tp=name">Participants</a></dd>
                <dd><a href="Networks.asp">Networks</a></dd>
                <dd><a href="Sessions.asp">Sessions</a></dd>
                <dd><a href="Sessions2.asp">Sessions (v. 2)</a></dd>
                <dd><a href="Networks_Sessions.asp">Networks/Sessions</a></dd>
                <dd><a href="SessionDateTime.asp">SessionDateTime</a></dd>
                <dd><a href="Rooms.asp">Rooms</a></dd>
                <dd><a href="PlanSessions.asp">Plan Sessions</a></dd>
                <dd><a href="Email.asp">Send E-mail</a></dd>
                <dd><a href="EmailLog.asp">Verstuurde mailtjes</a></dd>
                <dd><a href="EditMenu.asp">Edit Menu</a></dd>
                <dd>&nbsp;</dd>
                <dd><a href="PaymentMade.asp">Payment Made Y/N</a></dd>
                <dd><a href="PaymentMade2.asp">Payment Made Y/N (v. 2)</a></dd>
                <dd><a href="programme_choose_year.asp">Programme</a></dd>
                <dd><a href="Concordance.asp">Concordance</a></dd>
                <dd><a href="ProgrammeWord_choose_year.asp">Programme Book</a></dd>
                <dd><a href="Letters.asp">Make Letter</a></dd>
                <dd><a href="ParticipantsList.asp">Participants List</a></dd>
                <dd>&nbsp;</dd>
                <dd><a href="overzicht_feerequest.asp">Fee Request</a></dd>
                <dd><a href="overzicht_invitationletter.asp">Invitation Letter</a></dd>
                <dd><a href="overzicht_reception.asp">Reception</a></dd>    -->
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
            ${grailsApplication.config.grails.serverURL} - Last updated: ${Setting.findByProperty(Setting.LAST_UPDATED).value}
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