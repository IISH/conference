<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <asset:javascript src="plan.js" />
        <asset:javascript src="participants-in-sessions.js" />
    </head>
    <body>
        <div id="unschedule-session"> 
            <span><g:message code="session.unschedule.label" /></span>
        </div>
        
        <div id="sessions-unscheduled">
            <g:each in="${sessionsUnscheduled}" var="session">
            <div class="session-block">
                <a name="${session.code}"></a>
                <input type="hidden" name="session-id" value="${session.id}" />
                ${session.code}
            </div>
            </g:each>
        </div>
        
        <table id="legenda-schedule">
            <tr class="equipment">
                <td style="background-color:#f30;">Not possible</td>
                <td style="background-color:#ccc;">
                    <input type="hidden" name="equipment-combo-code" value="">
                    No/default equipment
                </td>
                <g:each in="${equipment}" var="equip" status="i">
                    <td class="equipment-combos">
                        <input type="hidden" name="equipment-ids" value="${equip*.id.join(',')}">
                        <input type="hidden" name="equipment-combo-code" value="${equip*.code.join('')}">
                        ${equip.collect { it.equipment }.join(' & ').encodeAsHTML()}
                    </td>
                </g:each>
                <td style="background-color:#ff0;">Best choice</td>
            </tr>
            <tr class="codes">
                <td style="background-color:#f30;"> </td>
                <td style="background-color:#ccc;">
                    <input type="hidden" name="equipment-combo-code" value="">
                    (-)
                </td>
                <g:each in="${equipment}" var="equip" status="i">
                   <td class="equipment-combos">
                       <input type="hidden" name="equipment-ids" value="${equip*.id.join(',')}">
                       <input type="hidden" name="equipment-combo-code" value="${equip*.code.join('')}">
                       (${equip*.code.join('')})
                   </td>
               </g:each>
               <td style="background-color:#ff0;"> </td>
            </tr>
        </table>

        <div id="conflicts-searching">
            <g:message code="session.conflicts.searching.label" />
        </div>

        <div id="no-conflicts">
            <g:message code="session.no.conflicts.label" />
        </div>

        <div id="conflicts">
            <span>
                <g:message code="session.conflicts.found.label" />
            </span>
            <ul> </ul>
        </div>

        <table id="schedule">
            <tr>
                <td>&nbsp;</td>
                <g:each in="${dateTimes}" var="dateTime">
                    <td>${dateTime.indexNumber} <br /> ${formatDate(date: dateTime.day.day, format: 'EE')} <br /> ${dateTime.period}</td>
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
                    <input type="hidden" name="equipment-combo-code" value="${timeSlot.equipment*.code.join('')}">
                    <input type="hidden" name="plan-message" value="(${timeSlot.room.roomNumber}: ${timeSlot.room.roomName}) - (${timeSlot.sessionDateTime.indexNumber}: ${formatDate(date: timeSlot.sessionDateTime.day.day, format: 'EE')} ${timeSlot.sessionDateTime.period})">

                    <g:if test="${timeSlot.session}">
                        <div class="session-block">
                            <a name="${timeSlot.session.code}"></a>
                            <input type="hidden" name="session-id" value="${timeSlot.session.id}" />
                            ${timeSlot.session.code}
                        </div>
                    </g:if>

                    <div class="equipment-indicator">
                        <g:if test="${timeSlot.equipmentCombinationIndex(equipment) >= 0}">
                           (${timeSlot.equipment*.code.join('')})
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

        <div id="loading">
            <div class="loading-helper">
                <span class="loading-text"><g:message code="default.please.wait" /></span>
            </div>
        </div>

        <div id="session-info-container">
            <div class="pointer"></div>
            <div id="session-info" class="info">
                <ol class="property-list">
                    <li>
                        <span id="code-label" class="property-label">
                            <g:message code="session.code.label" />
                        </span>
                        <span class="property-value" aria-labelledby="code-label"> </span>
                    </li>
                    <li>
                        <span id="name-label" class="property-label">
                            <g:message code="session.name.label" />
                        </span>
                        <span class="property-value" aria-labelledby="name-label"> </span>
                    </li>
                    <li>
                        <span id="state-label" class="property-label">
                            <g:message code="session.state.label" />
                        </span>
                        <span class="property-value" aria-labelledby="state-label"> </span>
                    </li>
                    <li>
                        <span id="commnent-label" class="property-label">
                            <g:message code="session.comment.label" />
                        </span>
                        <span class="property-value" aria-labelledby="commnent-label"> </span>
                    </li>
                    <li>
                        <span id="participants-label" class="property-label">
                            <g:message code="session.sessionParticipants.label" />
                        </span>
                        <ul class="session-participants property-value" arial-labelledby="participants-label">
                            <li class="hidden">
                                <span class="participant-value"> </span>
                                <span class="participant-state-value"> </span>

                                <ul>
                                    <li class="participant-type-value">
                                        <span class="participant-type-val"> </span>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <span id="equipment-label" class="property-label">
                            <g:message code="equipment.label" />
                        </span>
                        <ul class="property-value" aria-labelledby="equipment-label"> </ul>
                    </li>
                    <li>
                        <span id="problems-label" class="property-label">
                            <g:message code="session.problems.label" />
                        </span>
                        <ul class="property-value" aria-labelledby="problems-label"> </ul>
                    </li>
                </ol>
            </div>
        </div>

        <div id="room-info-container">
            <div class="pointer"></div>
            <div id="room-info" class="info">
                <ol class="property-list">
                    <li>
                        <span id="roomnumnber-label" class="property-label">
                            <g:message code="room.roomNumber.label" />
                        </span>
                        <span class="property-value" aria-labelledby="roomnumnber-label"> </span>
                    </li>
                    <li>
                        <span id="roomname-label" class="property-label">
                            <g:message code="room.roomName.label" />
                        </span>
                        <span class="property-value" aria-labelledby="roomname-label"> </span>
                    </li>
                    <li>
                        <span id="noofseats-label" class="property-label">
                            <g:message code="room.noOfSeats.label" />
                        </span>
                        <span class="property-value" aria-labelledby="noofseats-label"> </span>
                    </li>
                    <li>
                        <span id="roomcomment-label" class="property-label">
                            <g:message code="room.comment.label" />
                        </span>
                        <span class="property-value" aria-labelledby="roomcomment-label"> </span>
                    </li>
                </ol>
            </div>
        </div>
    </body>
</html>