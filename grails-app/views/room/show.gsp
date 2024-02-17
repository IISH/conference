<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <eca:navigation ids="${roomIds}" />

        <ol class="property-list">
          <li>
            <span id="id-label" class="property-label">#</span>
            <span class="property-value" arial-labelledby="id-label">
              <eca:formatText text="${room.id}" />
            </span>
          </li>
          <li>
            <span id="roomName-label" class="property-label">
              <eca:fallbackMessage code="room.roomName.label" fbCode="room.roomName.label" />
            </span>
            <span class="property-value" arial-labelledby="roomName-label">
              <eca:formatText text="${room.roomName}" />
            </span>
          </li>
          <li>
            <span id="roomNumber-label" class="property-label">
              <eca:fallbackMessage code="room.roomNumber.label" fbCode="room.roomNumber.label" />
            </span>
            <span class="property-value" arial-labelledby="roomNumber-label">
              <eca:formatText text="${room.roomNumber}" />
            </span>
          </li>
          <li>
            <span id="noOfSeats-label" class="property-label">
              <eca:fallbackMessage code="room.noOfSeats.label" fbCode="room.noOfSeats.label" />
            </span>
            <span class="property-value" arial-labelledby="noOfSeats-label">
              <eca:formatText text="${room.noOfSeats}" />
            </span>
          </li>
          <li>
            <span id="comment-label" class="property-label">
              <eca:fallbackMessage code="room.comment.label" fbCode="room.comment.label" />
            </span>
            <span class="property-value" arial-labelledby="comment-label">
              <eca:formatText text="${room.comment}" />
            </span>
          </li>

            <div id="room-equipment" class="columns">
               <g:each in="${equipment}" var="equip">
                   <fieldset class="form column">
                       <legend>${equip.equipment}</legend>
                       <g:each in="${timeSlots}" var="timeSlot">
                           <div>
                               <label>
                                   <g:checkBox name="timeslot" value="${equip.id}_${timeSlot.id}" checked="${room.roomSessionDateTimeEquipment.find { (it.equipment == equip) && (it.sessionDateTime == timeSlot) }}" disabled="true" />
                                   ${timeSlot.toString()}
                               </label>
                           </div>
                       </g:each>
                   </fieldset>
               </g:each>
               <div class="clear empty" />
           </div>
        </ol>

        <div class="buttons">
          <eca:link previous="true">
            <g:message code="default.button.back.label" />
          </eca:link>
          <eca:ifUserHasAccess controller="${params.controller}" action="edit">
            <eca:link controller="${params.controller}" action="edit" id="${params.id}">
              <g:message code="default.button.edit.label" />
            </eca:link>
          </eca:ifUserHasAccess>
        </div>
    </body>
</html>
