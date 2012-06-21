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

                    <sec:ifAnyGranted roles="admin">
                        <span class="action">
                            <eca:link controller="event" action="edit" id="${evt.id}">
                                <g:message code="default.edit.label" args="[message(code: 'event.label').toLowerCase()]" />
                            </eca:link>
                        </span>
                    </sec:ifAnyGranted>

                    <ul>
                        <g:each in="${dates.get(evt)}" var="eventDate">
                            <li>
                                <eca:link event="${evt.url}" date="${eventDate.url}">${eventDate.yearCode}</eca:link>
                            </li>
                        </g:each>

                        <sec:ifAnyGranted roles="admin">
                            <li>
                                <span class="action">
                                    <eca:link controller="eventDate" action="create" id="${evt.id}">
                                        <g:message code="default.add.label" args="[message(code: 'eventDate.label').toLowerCase()]" />
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
