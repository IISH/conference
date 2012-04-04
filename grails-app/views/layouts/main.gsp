<%@ page import="org.iisg.eca.Page; org.iisg.eca.DynamicPage; org.springframework.validation.FieldError" %>
<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="ECA" /></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui-1.8.18.custom.css')}" type="text/css">
        <g:javascript library="jquery" />
        <g:javascript library="application" />
		<g:layoutHead/>
        <r:layoutResources />
        <script src="${resource(dir: 'js', file: 'jquery-ui.js')}" type="text/javascript"></script>
	</head>
	<body>
		<div id="header" role="banner">
            <a href="${createLink(uri: '/')}">
                <img src="${resource(dir: 'images', file: 'grails_logo.png')}" alt="Grails"/>
            </a>
        </div>
        <div id="nav" role="navigation">
            <div class="left">
                <ul>
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label" /></a></li>
                </ul>
            </div>
            <div class="right">
                <ul>
                    <sec:ifLoggedIn>
                        <li>
                            <div class="loggedin">
                                <g:message code="springSecurity.loggedin.welcome" args="${[sec.loggedInUserInfo(field: 'fullName')]}" />
                                <eca:roles />
                                <g:img dir="images/skin" file="sorted_desc.gif" />
                            </div>
                        </li>
                    </sec:ifLoggedIn>

                    <sec:ifNotLoggedIn>
                        <li>
                            <g:localeSelect name="lang" />
                        </li>
                    </sec:ifNotLoggedIn>
                </ul>
            </div>
            <div class="clear"></div>
        </div>
        <div id="content" role="main">
            <g:if test="${page?.class == Page}">
                <h1>${page.toString()}</h1>
            </g:if>
		    <g:elseif test="${page?.class == DynamicPage}">
                <h1>${page.page.toString()}</h1>
		    </g:elseif>

            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:layoutBody />

            <g:hasErrors>
                <ul class="errors" role="alert">
                    <g:eachError var="error">
                        <li <g:if test="${error in FieldError}">data-field-id="${error.field}"</g:if>>
                            <g:message error="${error}" />
                        </li>
                    </g:eachError>
                </ul>
			</g:hasErrors>
        </div>
		<div id="footer" role="contentinfo"></div>
        <div id="user_menu">
            <ul>
                <li><g:link controller="user" action="show"><g:message code="menu.personalpage" /></g:link></li>
                <li><a href="/"><g:message code="menu.usersoverview" /></a></li>
                <li><g:link controller="logout"><g:message code="menu.logout" /></g:link></li>
            </ul>
        </div>
        <r:layoutResources />
	</body>
</html>