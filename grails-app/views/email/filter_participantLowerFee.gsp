<%@ page import="org.iisg.eca.domain.ParticipantDate" %>
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
                        <g:message code="participantDate.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="participant" from="${ParticipantDate.lowerFeeNotAnswered(curDate).list()}" optionKey="id" noSelection="${[null: 'All participants']}" />
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
                <input type="submit" name="btn_send" class="btn_send" value="${message(code: 'email.send.multiple.label')}" />
            </fieldset>
        </form>
    </body>
</html>