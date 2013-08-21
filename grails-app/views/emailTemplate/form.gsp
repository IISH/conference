<%@ page import="org.iisg.eca.domain.EmailCode" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <g:hasErrors bean="${template}">
          <ul class="errors" role="alert">
            <g:eachError bean="${template}" var="error">
              <li>
                <g:message error="${error}" />
              </li>
            </g:eachError>
          </ul>
        </g:hasErrors>
        
        <form method="post" action="#">
          <fieldset class="form">
            <div class="${hasErrors(bean: template, field: 'usedBy', 'error')} required">
              <label class="property-label" for="emailTemplate.description">
                <g:message code="emailTemplate.description.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="emailTemplate.description" maxlength="255" name="emailTemplate.description" value="${template?.description}" type="text" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: template, field: 'subject', 'error')} required">
              <label class="property-label" for="emailTemplate.subject">
                <g:message code="emailTemplate.subject.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="emailTemplate.subject" maxlength="78" name="emailTemplate.subject" value="${template?.subject}" type="text" required="required" />
              </span>
            </div>
            <div>
                <label class="property-label">
                  &nbsp;
                </label>
                <ul class="property-value">
                  <li>[SenderName] : Value of the field &quot;<g:message code="emailTemplate.sender.label" />&quot;</li>
                  <g:each in="${EmailCode.list()}" var="emailCode">
                      <li>${emailCode.toString()}</li>
                  </g:each>
                </ul>
            </div>
            <div class="${hasErrors(bean: template, field: 'body', 'error')} required">
              <label class="property-label" for="emailTemplate.body">
                <g:message code="emailTemplate.body.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <textarea id="emailTemplate.body" style="width:70%;" name="emailTemplate.body" required="required" rows="20">${template?.body}</textarea>
              </span>
            </div>
            <div class="${hasErrors(bean: template, field: 'sender', 'error')} required">
              <label class="property-label" for="emailTemplate.sender">
                <g:message code="emailTemplate.sender.label" />
                <span class="required-indicator">*</span>
              </label>
              <span class="property-value">
                <input id="emailTemplate.sender" maxlength="30" name="emailTemplate.sender" value="${template?.sender}" type="text" required="required" />
              </span>
            </div>
            <div class="${hasErrors(bean: template, field: 'comment', 'error')} ">
              <label class="property-label" for="emailTemplate.comment">
                <g:message code="emailTemplate.comment.label" />
              </label>
              <span class="property-value">
                <textarea id="emailTemplate.comment" cols="40" name="emailTemplate.comment" rows="5">${template?.comment}</textarea>
              </span>
            </div>
            <div class="${hasErrors(bean: template, field: 'sortOrder', 'error')} ">
              <label class="property-label" for="emailTemplate.sortOrder">
                <g:message code="emailTemplate.sortOrder.label" />
              </label>
              <span class="property-value">
                <input type="number" id="emailTemplate.sortOrder" name="emailTemplate.sortOrder" value="${template?.sortOrder}" />
              </span>
            </div>
            <div class="${hasErrors(bean: template, field: 'testAfterSave', 'error')} ">
              <label class="property-label" for="emailTemplate.testAfterSave">
                <g:message code="emailTemplate.testAfterSave.label" />
              </label>
              <span class="property-value">
                <input type="checkbox" id="emailTemplate.testAfterSave" name="emailTemplate.testAfterSave" value="${template?.testAfterSave}" />
              </span>
            </div>
            <div class="${hasErrors(bean: template, field: 'testEmail', 'error')} ">
              <label class="property-label" for="emailTemplate.testEmail">
                <g:message code="emailTemplate.testEmail.label" />
              </label>
              <span class="property-value">
                <input id="emailTemplate.testEmail" maxlength="255" name="emailTemplate.testEmail" value="${sec.username()}" type="email" />
              </span>
            </div>
          </fieldset>
          
          <fieldset class="buttons">
            <eca:link previous="true">
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
