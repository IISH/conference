<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
        <form method="post" action="#">
            <fieldset class="form">
                <div class="${hasErrors(bean: room, field: 'roomName', 'error')} required">
                    <label class="property-label" for="roomName">
                        <g:message code="room.roomName.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="roomName" maxlength="30" required="required" value="${fieldValue(bean: room, field: 'roomName')}" />
                </div>
                <div class="${hasErrors(bean: room, field: 'roomNumber', 'error')} required">
                    <label class="property-label" for="roomNumber">
                        <g:message code="room.roomNumber.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="text" name="roomNumber" maxlength="10" required="required" value="${fieldValue(bean: room, field: 'roomNumber')}" />
                </div>
                <div class="${hasErrors(bean: room, field: 'noOfSeats', 'error')} required">
                    <label class="property-label" for="noOfSeats">
                        <g:message code="room.noOfSeats.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input class="property-value" type="number" name="noOfSeats" required="required" value="${fieldValue(bean: room, field: 'noOfSeats')}"/>
                </div>
                <div class="${hasErrors(bean: room, field: 'comment', 'error')} ">
                    <label class="property-label" for="comment">
                        <g:message code="room.comment.label" />
                    </label>
                    <textarea class="property-value" cols="40" rows="5" name="comment">${fieldValue(bean: room, field: 'comment')}</textarea>
                </div>

                <div id="room-equipment" class="columns">
                    <g:each in="${equipment}" var="equip">
                        <fieldset class="form column">
                            <legend>${equip.equipment}</legend>
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
                        </fieldset>
                    </g:each>
                    <div class="clear empty" />
                </div>
           </fieldset>
            <fieldset class="buttons">
                <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <eca:link controller="${params.controller}" action="delete" id="${params.id}">
                    <g:message code="default.deleted.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
	</body>
</html>
