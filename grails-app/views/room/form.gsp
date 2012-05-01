<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
        <form method="post" action="#">
            <fieldset class="form">
                <div class="fieldcontain ${hasErrors(bean: room, field: 'roomName', 'error')} required">
                    <label for="roomName">
                        <g:message code="room.roomname.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" name="roomName" maxlength="30" required="required" value="${fieldValue(bean: room, field: 'roomName')}" />
                </div>
                <div class="fieldcontain ${hasErrors(bean: room, field: 'roomNumber', 'error')} required">
                    <label for="roomNumber">
                        <g:message code="room.roomnumber.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" name="roomNumber" maxlength="10" required="required" value="${fieldValue(bean: room, field: 'roomNumber')}" />
                </div>
                <div class="fieldcontain ${hasErrors(bean: room, field: 'noOfSeats', 'error')} required">
                    <label for="noOfSeats">
                        <g:message code="room.noofseats.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="number" name="noOfSeats" required="required" value="${fieldValue(bean: room, field: 'noOfSeats')}"/>
                </div>
                <div class="fieldcontain ${hasErrors(bean: room, field: 'comment', 'error')} ">
                    <label for="comment">
                        <g:message code="room.comment.label" />
                    </label>
                    <textarea cols="40" rows="5" name="comment">${fieldValue(bean: room, field: 'comment')}</textarea>
                </div>

                <div class="columns">
                    <g:each in="${equipment}" var="equip">
                        <div class="column">
                            <h3>${equip.equipment}</h3>
                            <div class="bold">
                                <label>
                                    <input type="checkbox" class="check-all" />
                                    <g:message code="default.button.selectall.label" />
                                </label>
                            </div>
                            <g:each in="${timeSlots}" var="timeSlot">
                                <div>
                                    <label>
                                        <g:checkBox name="timeslot" value="${equip.id}_${timeSlot.id}" checked="${room.roomSessionDateTimeEquipment.find { (it.equipment == equip) && (it.sessionDateTime == timeSlot) }}" />
                                        ${timeSlot.toString()}
                                    </label>
                                </div>
                            </g:each>
                        </div>
                    </g:each>
                    <div class="clear empty" />
                </div>
           </fieldset>
            <fieldset class="buttons">
                <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
	</body>
</html>
