<%@ page import="org.iisg.eca.domain.Network; org.iisg.eca.domain.FeeState" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
        <ul id="export">
            <li>
                <h3>Program book export</h3>
                <ol>
                    <li>
                        <eca:link action="days">Create XML Export of Days for program book</eca:link>
                    </li>
                    <li>
                        <eca:link action="concordance">Create XML Export of Concordance for program book</eca:link>
                    </li>
                    <li>
                        <eca:link action="sessions">Create XML Export of Sessions for program book</eca:link>
                    </li>
                    <li>
                        <eca:link action="sessionsOrderedByCode">Create XML Export of Sessions (ordered by code) for program book</eca:link>
                    </li>
                    <li>
                        <eca:link action="programAtAGlance">Create Excel Export of Program at a glance</eca:link>
                    </li>
                </ol>
            </li>

            <li>
                <h3>Badges export</h3>
                <ol>
                    <li>
                        <eca:link action="badgesPayed">Create Excel Export of participants for badges (Payed)</eca:link>
                    </li>
                    <li>
                        <eca:link action="badgesNotPayed">Create Excel Export of participants for badges (Not payed online or no attempt)</eca:link>
                    </li>
                    <li>
                        <eca:link action="badgesUnconfirmed">Create Excel Export of participants for badges (Unconfirmed bank transfers and on-site payments)</eca:link>
                    </li>
                </ol>
            </li>

            <li>
                <h3>Participants export</h3>
                <ol>
                    <li>
                        <eca:link action="participantsWithParticipantState012999">Create Excel Export of participants with participant state new, checked, participant and not finished</eca:link>
                    </li>
                    <li>
                        <eca:link action="participantsWithParticipantState012">Create Excel Export of participants with participant state new, checked and participant</eca:link>
                    </li>
                    <li>
                        <eca:link action="participantsWithParticipantState12">Create Excel Export of participants with participant state checked and participant</eca:link>
                    </li>
                    <li>
                        <a href="#" class="participants-export-open">Create Excel Export of participants on fee state</a>
                    </li>
                </ol>
            </li>

            <li>
                <h3>Network export</h3>
                <ol>
                    <li>
                        <a href="#" class="network-export-open" data-submit="participantsInNetwork">Create Excel Export of participants in a network</a>
                    </li>
                    <li>
                        <a href="#" class="network-export-open" data-submit="sessionPapersInNetwork">Create Excel Export of session papers in a network</a>
                    </li>
                    <li>
                        <a href="#" class="network-export-open" data-submit="sessionPapersInNetworkAccepted">Create Excel Export of accepted session papers in a network</a>
                    </li>
                    <li>
                        <a href="#" class="network-export-open" data-submit="individualPapersInNetwork">Create Excel Export of individual paper proposals in a network</a>
                    </li>
                    <li>
                        <a href="#" class="network-export-open" data-submit="allPapersInNetwork">Create Excel Export of all paper proposals in a network</a>
                    </li>
                </ol>
            </li>
        </ul>

        <div id="participants-export-dialog">
            <form method="post" action="participants">
                <fieldset class="form dialog">
                    <div>
                        <label class="property-label">
                            <g:message code="participantDate.feeState.label" />
                        </label>
                        <span class="property-value">
                            <g:select name="feeStateId" from="${FeeState.sortedFeeStates.list()}" optionKey="id" optionValue="name" noSelection="${['null': 'Filter on fee state']}" />
                        </span>
                    </div>
                </fieldset>
                <fieldset class="buttons">
                    <input type="submit" name="export" value="${g.message(code: 'default.export.menu.label')}" />
                </fieldset>
            </form>
        </div>

        <div id="network-export-dialog">
            <form method="post" action="">
                <fieldset class="form dialog">
                    <div>
                        <label class="property-label">
                            <g:message code="network.label" />
                        </label>
                        <span class="property-value">
                            <g:select name="networkId" from="${Network.where { showOnline == true }}" optionKey="id" optionValue="name" />
                        </span>
                    </div>
                </fieldset>
                <fieldset class="buttons">
                    <input type="submit" name="export" value="${g.message(code: 'default.export.menu.label')}" />
                </fieldset>
            </form>
        </div>
    </body>
</html>
