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
              <label class="property-label" for="EmailTemplate.usedBy">
                <g:message code="emailtemplate.usedby.label" />
                <span class="required-indicator">*</span>
              </label>
              <input class="property-value" id="EmailTemplate.usedBy" maxlength="255" name="EmailTemplate.usedBy" value="${template?.usedBy}" type="text" required="required" />
            </div>
            <div class="${hasErrors(bean: template, field: 'subject', 'error')} required">
              <label class="property-label" for="EmailTemplate.subject">
                <g:message code="emailtemplate.subject.label" />
                <span class="required-indicator">*</span>
              </label>
              <input class="property-value" id="EmailTemplate.subject" maxlength="78" name="EmailTemplate.subject" value="${template?.subject}" type="text" required="required" />
            </div>
            <div>
                <label class="property-label"></label>
                <ul class="property-value">
                <g:each in="${EmailCode.list()}" var="emailCode">
                    <li>${emailCode.toString()}</li>
                </g:each>
                </ul>
            </div>
            <div class="${hasErrors(bean: template, field: 'body', 'error')} required">
              <label class="property-label" for="EmailTemplate.body">
                <g:message code="emailtemplate.body.label" />
                <span class="required-indicator">*</span>
              </label>
              <textarea class="property-value" id="EmailTemplate.body" style="width:70%;" name="EmailTemplate.body" required="required" rows="20">${template?.body}</textarea>
            </div>
            <div class="${hasErrors(bean: template, field: 'sender', 'error')} required">
              <label class="property-label" for="EmailTemplate.sender">
                <g:message code="emailtemplate.sender.label" />
                <span class="required-indicator">*</span>
              </label>
              <input class="property-value" id="EmailTemplate.sender" maxlength="30" name="EmailTemplate.sender" value="${template?.sender}" type="text" required="required" />
            </div>
            <div class="${hasErrors(bean: template, field: 'comment', 'error')} ">
              <label class="property-label" for="EmailTemplate.comment">
                <g:message code="emailtemplate.comment.label" />
              </label>
              <textarea class="property-value" id="EmailTemplate.comment" cols="40" name="EmailTemplate.comment" rows="5">${template?.comment}</textarea>
            </div>
            <div class="${hasErrors(bean: template, field: 'testAfterSave', 'error')} ">
              <label class="property-label" for="EmailTemplate.testAfterSave">
                <g:message code="emailtemplate.testaftersave.label" />
              </label>
              <input class="property-value" type="checkbox" id="EmailTemplate.testAfterSave" name="EmailTemplate.testAfterSave" value="${template?.testAfterSave}" />
            </div>
            <div class="${hasErrors(bean: template, field: 'testEmail', 'error')} ">
              <label class="property-label" for="EmailTemplate.testEmail">
                <g:message code="emailtemplate.testemail.label" />
              </label>
              <input class="property-value" id="EmailTemplate.testEmail" maxlength="255" name="EmailTemplate.testEmail" value="${sec.username()}" type="email" />
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
