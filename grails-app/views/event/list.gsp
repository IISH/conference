<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="event.list" /></title>
	</head>
	<body>
        <h1><g:message code="event.list" /></h1>

        <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
        </g:if>

        <div id="events">
            <g:each in="${events}" var="event">
                <div class="event">
                    <h2>
                        <g:link controller="event" action="show" id="${allEvents.get(event).id}">
                            ${event}
                        </g:link>
                    </h2>


                    <span class="action">
                        <g:link controller="event" action="edit" id="${allEvents.get(event).id}">
                            <g:message code="default.edit.label" args="[message(code: 'event.label').toLowerCase()]" />
                        </g:link>
                    </span>

                    <ul>
                        <g:each in="${datesByEvent.get(event)}" var="date">
                            <li>
                                <g:link controller="eventDate" action="show" id="${date.id}">
                                    ${date.yearCode}
                                </g:link>
                            </li>
                        </g:each>

                        <sec:ifAnyGranted roles="superAdmin">
                            <li>
                                <span class="action">
                                    <g:link controller="eventDate" action="create" id="${allEvents.get(event).id}">
                                        <g:message code="default.add.label" args="[message(code: 'eventdate.label').toLowerCase()]" />
                                    </g:link>
                                </span>
                            </li>
                        </sec:ifAnyGranted>
                     </ul>
                </div>
            </g:each>
        </div>

        <sec:ifAnyGranted roles="superAdmin">
            <div class="buttons">
                <g:link controller="event" action="create">
                    <g:message code="default.add.label" args="[message(code: 'event.label')]" />
                </g:link>
            </div>
        </sec:ifAnyGranted>
	</body>
</html>
