<%@ page import="org.iisg.eca.domain.ParticipantDate; org.iisg.eca.domain.ParticipantState" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <h3>${emailTemplate.description} - Select type of mail recipients</h3>

        <form method="post" action="#">
            <fieldset class="form">
                <div>
                    <label class="property-label">
                        <g:message code="participantState.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="state" from="${ParticipantState.list()}" optionKey="id" noSelection="${[null: 'All participant states']}" />
                    </span>
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="participantDate.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="participant" from="${participants}" optionKey="id" noSelection="${[null: 'All participants']}" />
                    </span>
                </div>
                <div>
                    <label class="property-label">
                        <g:message code="emailTemplate.comment.label" />
                    </label>
                    <span class="property-value">
                        <eca:formatText text="${emailTemplate.comment}" />
                    </span>
                </div>
            </fieldset>
            
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_send" class="btn_send" value="Send emails" />
            </fieldset>
        </form>
    </body>
</html>