<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'fullName', 'error')} required">
	<label for="fullName">
		<g:message code="user.fullName.label" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="fullName" maxlength="30" required="" value="${userInstance?.fullName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'institute', 'error')} required">
	<label for="institute">
		<g:message code="user.institute.label" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="institute" maxlength="50" required="" value="${userInstance?.institute}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'language', 'error')} required">
	<label for="language">
		<g:message code="user.language.label" />
		<span class="required-indicator">*</span>
	</label>
    <g:localeSelect name="language" required="" value="${userInstance?.language}" />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'password', 'error')}">
	<label for="password">
		<g:message code="user.password.label" />
	</label>
	<g:field type="password" name="password" value="" />
</div>

<div class="fieldcontain">
	<label for="encryptedPasswordAgain">
		<g:message code="user.encryptedPasswordAgain.label" />
	</label>
	<g:field type="password" name="encryptedPasswordAgain" value="" />
</div>