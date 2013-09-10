<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title><g:message code="event.multiple.label" /></title>
    </head>
    <body>
        <h1><g:message code="event.multiple.label" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <div id="events">
            <g:each in="${events}" var="evt">
                <div class="event">
                    <h2>
                        ${evt.toString()}
                    </h2>

                    <ul>
                        <g:each in="${dates.get(evt)}" var="eventDate">
                        <li>
                            <eca:link event="${evt.url}" date="${eventDate.url}">${eventDate.yearCode}</eca:link>
                        </li>
                        </g:each>
                     </ul>
                </div>
            </g:each>
        </div>

        <sec:ifAnyGranted roles="superAdmin">
            <div class="buttons">
                <eca:link controller="event" action="create">
                    <g:message code="default.add.label" args="[message(code: 'event.label')]" />
                </eca:link>
            </div>
        </sec:ifAnyGranted>
	</body>
</html>
