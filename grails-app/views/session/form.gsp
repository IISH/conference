<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
        <g:hasErrors bean="${eventSession}">
            <ul class="errors" role="alert">
                <g:eachError bean="${eventSession}" var="error">
                    <li>
                        <g:message error="${error}" />
                    </li>
                </g:eachError>
            </ul>
        </g:hasErrors>

        <form method="post" action="#">
            <fieldset class="form">
                <div>
                    <label>#</label>
                    <span>${eventSession.id}</span>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'code', 'error')} required">
                    <label>
                        <g:message code="session.code.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" maxlength="10" name="Session.code" value="${fieldValue(bean: eventSession, field: 'code')}" required="required" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'name', 'error')} required">
                    <label>
                        <g:message code="session.name.label" />
                        <span class="required-indicator">*</span>
                    </label>
                    <input type="text" name="Session.name" required="required" value="${fieldValue(bean: eventSession, field: 'name')}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'comment', 'error')} ">
                    <label>
                        <g:message code="session.comment.label" />
                    </label>
                    <textarea cols="40" rows="5" name="Session.comment">
                        ${fieldValue(bean: eventSession, field: 'comment')}
                    </textarea>
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'enabled', 'error')} ">
                    <label>
                        <g:message code="default.enabled.label" />
                    </label>
                    <g:checkBox name="Session.enabled" checked="${eventSession.enabled}" />
                </div>
                <div class="${hasErrors(bean: eventSession, field: 'deleted', 'error')} ">
                    <label>
                        <g:message code="default.deleted.label" />
                    </label>
                    <g:checkBox name="Session.deleted" checked="${eventSession.deleted}" />
                </div>

                <div style="color:red; font-weight:bold;">TODO: Equipment for this session</div>
                <div style="color:red; font-weight:bold;">TODO: Participants for this session</div>

                <div id="tabs">
                    <ul>
                        <li><a href="#chair-tab">Add Chair</a></li>
                        <li><a href="#organiser-tab">Add Organiser</a></li>
                        <li><a href="#author-tab">Add Author</a></li>
                        <li><a href="#co-author-tab">Add Co-Author</a></li>
                        <li><a href="#discussant-tab">Add Discussant</a></li>
                    </ul>

                    <div id="chair-tab">
                        <div>
                            <label>
                                <g:message code="default.deleted.label" />
                            </label>
                            <g:checkBox name="Session.deleted" checked="${eventSession.deleted}" />
                        </div>
                    </div>
                    <div id="organiser-tab">

                    </div>
                    <div id="author-tab">

                    </div>
                    <div id="co-author-tab">

                    </div>
                    <div id="discussant-tab">

                    </div>
                </div>
            </fieldset>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
    </body>
</html>
