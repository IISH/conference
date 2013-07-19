<%@ page import="org.iisg.eca.domain.Day" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <g:hasErrors bean="${eventDate}">
          <ul class="errors" role="alert">
            <g:eachError bean="${eventDate}" var="error">
              <li>
                <g:message error="${error}" />
              </li>
            </g:eachError>
          </ul>
        </g:hasErrors>
    
        <form method="post" action="#">           
          <fieldset class="form">
            <div class="${hasErrors(bean: eventDate, field: 'yearCode', 'error')} required">
              <label class="property-label" for="EventDate.yearCode">
                <eca:fallbackMessage code="eventDate.yearCode.label" fbCode="eventDate.yearCode.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                 <input id="EventDate.yearCode" maxlength="20" name="EventDate.yearCode" value="${eventDate?.yearCode}" type="text" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'startDate', 'error')} ">
              <label class="property-label" for="EventDate.startDate">
                <eca:fallbackMessage code="eventDate.startDate.label" fbCode="eventDate.startDate.label" />
              </label>
              <span class="property-value">
                <input id="EventDate.startDate" placeholder="${g.message(code: 'default.date.form.format').toLowerCase()}" name="EventDate.startDate" value="${g.formatDate(formatName: 'default.date.form.format', date: eventDate?.startDate)}" type="text" />
              </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'endDate', 'error')} ">
              <label class="property-label" for="EventDate.endDate">
                <eca:fallbackMessage code="eventDate.endDate.label" fbCode="eventDate.endDate.label" />
              </label>
              <span class="property-value">
                <input id="EventDate.endDate" placeholder="${g.message(code: 'default.date.form.format').toLowerCase()}" name="EventDate.endDate" value="${g.formatDate(formatName: 'default.date.form.format', date: eventDate?.endDate)}" type="text" />
              </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'days', 'error')} ">
              <label class="property-label">
                <eca:fallbackMessage code="day.multiple.label" fbCode="eventDate.days.label" />
              </label>
              <ul class="property-value">
                <g:each in="${Day.findAllByDate(eventDate)}" var="instance" status="i">
                    <li>
                      <input type="hidden" name="Day_${i}.id" value="${instance.id}" />
                      <label class="property-label">
                        <eca:fallbackMessage code="day.dayNumber.label" fbCode="day.dayNumber.label" />
                        <g:field id="Day_${i}.dayNumber" name="Day_${i}.dayNumber" value="${instance?.dayNumber}" type="number" required="required" />
                      </label>
                      <label class="property-label">
                        <eca:fallbackMessage code="day.day.label" fbCode="day.day.label" />
                        <input id="Day_${i}.day" placeholder="${g.message(code: 'default.date.form.format').toLowerCase()}" name="Day_${i}.day" value="${g.formatDate(formatName: 'default.date.form.format', date: instance?.day)}" required="required" class="datepicker" type="text" />
                      </label>
                      <span class="ui-icon ui-icon-circle-minus"></span>
                    </li>
                </g:each>
                <li class="add">
                  <span class="ui-icon ui-icon-circle-plus"></span>
                  <g:message code="default.add.label" args="[eca.fallbackMessage(code: 'day.multiple.label', fbCode: 'eventDate.days.label').toLowerCase()]" />
                  <input type="hidden" name="Day.to-be-deleted" class="to-be-deleted" />
                </li>
                <li class="hidden">
                  <input type="hidden" name="Day_null.id" />
                  <label class="property-label">
                    <eca:fallbackMessage code="day.dayNumber.label" fbCode="day.dayNumber.label" />
                    <g:field id="Day_${i}.dayNumber" name="Day_${i}.dayNumber" value="${instance?.dayNumber}" class="property-value" type="number" required="required" />
                  </label>
                  <label class="property-label">
                    <eca:fallbackMessage code="day.day.label" fbCode="day.day.label" />
                    <input id="Day_${i}.day" placeholder="${g.message(code: 'default.date.form.format').toLowerCase()}" name="Day_${i}.day" value="${g.formatDate(formatName: 'default.date.form.format', date: instance?.day)}" class="property-value datepicker" required="required" type="text" />
                  </label>
                  <span class="ui-icon ui-icon-circle-minus"></span>
                </li>
              </ul>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'dateAsText', 'error')} required">
              <label class="property-label" for="EventDate.dateAsText">
                <eca:fallbackMessage code="eventDate.dateAsText.label" fbCode="eventDate.dateAsText.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="EventDate.dateAsText" maxlength="30" name="EventDate.dateAsText" value="${eventDate?.dateAsText}" class="property-value" type="text" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'description', 'error')} required">
              <label class="property-label" for="EventDate.description">
                <eca:fallbackMessage code="eventDate.description.label" fbCode="eventDate.description.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="EventDate.description" maxlength="255" name="EventDate.description" value="${eventDate?.description}" class="property-value" type="text" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'longDescription', 'error')} ">
              <label class="property-label" for="EventDate.longDescription">
                <eca:fallbackMessage code="eventDate.longDescription.label" fbCode="eventDate.longDescription.label" />
              </label>
              <span class="property-value">
                <textarea id="EventDate.longDescription" cols="40" name="EventDate.longDescription" rows="5">${eventDate?.longDescription}</textarea>
              </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'createStatistics', 'error')} ">
                <label class="property-label" for="EventDate.createStatistics">
                  <eca:fallbackMessage code="eventDate.createStatistics.label" fbCode="eventDate.createStatistics.label" />
                </label>
                <span class="property-value">
                  <g:checkBox id="EventDate.createStatistics" name="EventDate.createStatistics" value="${eventDate?.createStatistics}" />
                </span>
            </div>
            <div class="${hasErrors(bean: eventDate, field: 'enabled', 'error')} ">
              <label class="property-label" for="EventDate.enabled">
                <eca:fallbackMessage code="default.enabled.label" fbCode="eventDate.enabled.label" />
              </label>
              <span class="property-value">
                <g:checkBox id="EventDate.enabled" name="EventDate.enabled" value="${eventDate?.enabled}" />
              </span>
            </div>
          </fieldset>
          
          <fieldset class="buttons">
            <eca:link controller="${params.prevController}" action="${params.prevAction}" id="${params.prevId}">
              <g:message code="default.button.cancel.label" />
            </eca:link>
            <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            <g:if test="${params.action != 'create'}">
              <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
            </g:if>
          </fieldset>
        </form>
    </body>
</html>
