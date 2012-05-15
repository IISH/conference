<html>
	<head>
		<meta name="layout" content="main">
        <g:javascript src="plan.js" />
	</head>
	<body>
        <div id="sessions-unscheduled">
        <g:each in="${sessionsUnscheduled}" var="session">
            <div class="session-block">
                <input type="hidden" name="session-id" value="${session.id}">
                ${session.code}
            </div>
        </g:each>
        </div>

        <table id="legenda-schedule">
            <tr>
                <td style="background-color:#f30;">Not possible</td>
                <td style="background-color:#ccc;">
                    <input type="hidden" name="equipment-combo-id" value="-1">
                    No/default equipment (-)
                </td>
                <g:each in="${equipment}" var="equip" status="i">
                    <td class="equipment-combos">
                        <input type="hidden" name="equipment-ids" value="${equip.collect { it.id }.join(',')}">
                        <input type="hidden" name="equipment-combo-id" value="${i}">
                        ${equip.collect { it.equipment }.join(' & ').encodeAsHTML()} (${i})
                    </td>
                </g:each>
                <td style="background-color:#ff0;">Best choice</td>
            </tr>
        </table>

        <table id="schedule">
            <tr>
                <td>&nbsp;</td>
                <g:each in="${dateTimes}" var="dateTime">
                    <td>${dateTime.index} <br /> ${formatDate(date: dateTime.day.day, format: 'EE')} <br /> ${dateTime.period}</td>
                </g:each>
            </tr>

            <g:each in="${schedule}" var="timeSlot" status="i">
                <g:if test="${i%dateTimes.size() == 0}">
                    <tr>
                        <td class="room-indicator">
                            <input type="hidden" name="room-id" value="${timeSlot.room.id}" />
                            ${timeSlot.room.roomNumber}
                        </td>
                </g:if>

                <td class="time-slot">
                    <input type="hidden" name="date-time-id" value="${timeSlot.sessionDateTime.id}" />
                    <input type="hidden" name="room-id" value="${timeSlot.room.id}" />
                    <input type="hidden" name="equipment-combo-id" value="${timeSlot.equipmentCombinationIndex(equipment)}">

                    <g:if test="${timeSlot.session}">
                        <div class="session-block">
                            <input type="hidden" name="session-id" value="${timeSlot.session.id}">
                            ${timeSlot.session.code}
                        </div>
                    </g:if>

                    <div class="equipment-indicator">
                        <g:if test="${timeSlot.equipmentCombinationIndex(equipment) >= 0}">
                            (${timeSlot.equipmentCombinationIndex(equipment)})
                        </g:if>
                        <g:else>
                            (-)
                        </g:else>
                    </div>
                </td>

                <g:if test="${i%dateTimes.size() == (dateTimes.size()-1)}">
                    </tr>
                </g:if>
            </g:each>
        </table>

        <table id="legenda-rooms">
            <tr>
                <g:each in="${rooms}" var="room">
                    <td>${room.roomNumber}: ${room.roomName}</td>
                </g:each>
            </tr>
        </table>

        <div id="loading">LOADING...</div>

        <div id="session-info" class="info">
            <ol class="property-list">
                <li>
                    <span id="code-label" class="property-label">
                        <g:message code="session.code.label" />
                    </span>
                    <span class="property-value" arial-labelledby="code-label"> </span>
                </li>
                <li>
                    <span id="name-label" class="property-label">
                        <g:message code="session.name.label" />
                    </span>
                    <span class="property-value" arial-labelledby="name-label"> </span>
                </li>
                <li>
                    <span id="commnent-label" class="property-label">
                        <g:message code="session.comment.label" />
                    </span>
                    <span class="property-value" arial-labelledby="commnent-label"> </span>
                </li>
                <li>
                    <span id="participants-label" class="property-label">
                        <g:message code="session.sessionparticipants.label" />
                    </span>
                    <span class="property-value" arial-labelledby="participants-label"> </span>
                </li>
                <li>
                    <span id="equipment-label" class="property-label">
                        <g:message code="equipment.label" />
                    </span>
                    <span class="property-value" arial-labelledby="equipment-label"> </span>
                </li>
            </ol>
        </div>

        <div id="room-info" class="info">
            <ol class="property-list">
                <li>
                    <span id="roomnumnber-label" class="property-label">
                        <g:message code="room.roomnumber.label" />
                    </span>
                    <span class="property-value" arial-labelledby="roomnumnber-label"> </span>
                </li>
                <li>
                    <span id="roomname-label" class="property-label">
                        <g:message code="room.roomname.label" />
                    </span>
                    <span class="property-value" arial-labelledby="roomname-label"> </span>
                </li>
                <li>
                    <span id="noofseats-label" class="property-label">
                        <g:message code="room.noofseats.label" />
                    </span>
                    <span class="property-value" arial-labelledby="noofseats-label"> </span>
                </li>
                <li>
                    <span id="roomcomment-label" class="property-label">
                        <g:message code="room.comment.label" />
                    </span>
                    <span class="property-value" arial-labelledby="roomcomment-label"> </span>
                </li>
            </ol>
        </div>
    </body>
</html>