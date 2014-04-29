<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <g:hasErrors bean="${room}">
          <ul class="errors" role="alert">
            <g:eachError bean="${room}" var="error">
              <li>
                <g:message error="${error}" />
              </li>
            </g:eachError>
          </ul>
        </g:hasErrors>

        <form method="post" action="#">
            <fieldset class="form">
                <div>
                    <label class="property-label">
                        #
                    </label>
                    <span class="property-value">
                        ${room.id}
                    </span>
                </div>
                <div class="${hasErrors(bean: room, field: 'roomName', 'error')} required">
                    <label class="property-label" for="roomName">
                        <g:message code="room.roomName.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="text" name="roomName" maxlength="30" required="required" value="${fieldValue(bean: room, field: 'roomName')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: room, field: 'roomNumber', 'error')} required">
                    <label class="property-label" for="roomNumber">
                        <g:message code="room.roomNumber.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="text" name="roomNumber" maxlength="10" required="required" value="${fieldValue(bean: room, field: 'roomNumber')}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: room, field: 'noOfSeats', 'error')} required">
                    <label class="property-label" for="noOfSeats">
                        <g:message code="room.noOfSeats.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <span class="property-value">
                        <input type="number" name="noOfSeats" required="required" value="${fieldValue(bean: room, field: 'noOfSeats')}"/>
                    </span>
                </div>
                <div class="${hasErrors(bean: room, field: 'comment', 'error')} ">
                    <label class="property-label" for="comment">
                        <g:message code="room.comment.label" />
                    </label>
                    <span class="property-value">
                        <textarea cols="40" rows="5" name="comment">${fieldValue(bean: room, field: 'comment')}</textarea>
                    </span>
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
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <g:if test="${params.action != 'create'}">
                    <eca:link controller="${params.controller}" action="delete" id="${params.id}" class="btn_delete">
                        <g:message code="default.deleted.label" />
                    </eca:link>
                </g:if>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <g:if test="${params.action != 'create'}">
                  <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
                </g:if>
            </fieldset>
        </form>
    </body>
</html>
