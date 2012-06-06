<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
	</head>
	<body>
        <h3>${emailTemplate.description}</h3>

        <form method="post" action="#">
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_send" class="btn_send" value="Send emails" />
            </fieldset>
        </form>
    </body>
</html>