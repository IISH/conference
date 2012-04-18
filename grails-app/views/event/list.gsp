<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title>${page.toString()}</title>
	</head>
	<body>
        <div id="events">
            <g:each in="${events}" var="evt">
                <div class="event">
                    <h2>
                        ${evt}
                    </h2>

                    <span class="action">
                        <eca:link controller="event" action="edit" id="${allEvents.get(evt).id}">
                            <g:message code="default.edit.label" args="[message(code: 'event.label').toLowerCase()]" />
                        </eca:link>
                    </span>

                    <ul>
                        <g:each in="${datesByEvent.get(evt)}" var="eventDate">
                            <li>
                                <eca:link event="${eventDate.event.url}" date="${eventDate.url}">
                                    ${eventDate.yearCode}
                                </eca:link>
                            </li>
                        </g:each>

                        <sec:ifAnyGranted roles="superAdmin">
                            <li>
                                <span class="action">
                                    <eca:link controller="eventDate" action="create" id="${allEvents.get(evt).id}">
                                        <g:message code="default.add.label" args="[message(code: 'eventdate.label').toLowerCase()]" />
                                    </eca:link>
                                </span>
                            </li>
                        </sec:ifAnyGranted>
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
