<%@ page import="org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="participant.js" />
    </head>
    <body>
        <g:set var="networks" value="${Network.list()}" />
        <g:set var="paperStates" value="${PaperState.list()}" />
        <g:set var="equipmentList" value="${Equipment.list()}" />

        <input type="hidden" name="id" value="${params.id}" />
        <input type="hidden" name="user.id" value="${user.id}">

        <eca:navigation ids="${participantIds}" index="${params.index}" />

        <g:hasErrors model="[participant: participant, user: user]">
            <ul class="errors" role="alert">
                <g:eachError model="[participant: participant, user: user]" var="error">
                    <li><g:message error="${error}" /></li>
                </g:eachError>
             </ul>
        </g:hasErrors>

        <form id="participant-form" action="#" method="post" enctype="multipart/form-data">
            <div id="tabs">
                <ul>
                    <li><a href="#personal-tab"><g:message code="participantDate.personal.info.label" /></a></li>
                    <li><a href="#papers-tab"><g:message code="paper.multiple.label" /></a></li>
                    <li><a href="#sessions-tab"><g:message code="session.multiple.label" /></a></li>
                    <li><a href="#payments-tab"><g:message code="payment.multiple.label" /></a></li>
                    <li><a href="#emails-tab"><g:message code="email.multiple.label" /></a></li>
                </ul>

                <g:render template="tabs/personal" />
                <g:render template="tabs/papers" />
                <g:render template="tabs/sessions" />
                <g:render template="tabs/payments" />
                <g:render template="tabs/emails" />
            </div>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <eca:link action="delete" id="${params.id}" class="btn_delete">
                    <g:message code="default.deleted.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
                <g:if test="${participant}">
                    <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'paper.label').toLowerCase()])}" />
                    <input type="button" name="btn_add_order" class="btn_add_order" value="${message(code: 'default.add.label', args: [message(code: 'order.label').toLowerCase()])}" />
                </g:if>
            </fieldset>
        </form>

        <g:render template="tabs/modals" />
    </body>
</html>
