<%@ page import="org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>${page.toString()}</title>
    </head>
    <body>
        <div id="tabs">
            <ul>
                <li><a href="#personal-tab">Personal information</a></li>
                <li><a href="#event-tab">Event information</a></li>
            </ul>
            <form action="#" method="post">
                <fieldset id="personal-tab">
                    <h3>Registration info</h3>
                    <ol class="property-list">
                        <li>
                            <span id="id-label" class="property-label">#</span>
                            <span class="property-value" arial-labelledby="id-label">
                                <g:fieldValue bean="${participant.user}" field="id" />
                            </span>
                        </li>
                        <li>
                            <span id="date-added-label" class="property-label">
                                Date added
                            </span>
                            <span class="property-value" arial-labelledby="date-added-label">
                                <g:fieldValue bean="${participant.user}" field="dateAdded" />
                            </span>
                        </li>
                        <li>
                            <span id="event-date-added-label" class="property-label">
                                Event date added
                            </span>
                            <span class="property-value" arial-labelledby="event-date-added-label">
                                <g:fieldValue bean="${participant}" field="dateAdded" />
                            </span>
                        </li>
                    </ol>
                    <div class="${hasErrors(bean: participant, field: 'state', 'error')} required">
                        <label>
                            Current state
                        </label>
                        <g:select name="ParticipantDate.state" from="${ParticipantState.list()}" optionKey="id" optionValue="state" value="${participant.state}" />
                    </div>
                    <div class="${hasErrors(bean: participant, field: 'feeState', 'error')} required">
                        <label>
                            Current fee state
                        </label>
                        <g:select name="ParticipantDate.feeState" from="${FeeState.list()}" optionKey="id" optionValue="name" value="${participant.feeState}" />
                    </div>

                    <h3>Personal info</h3>
                    <div class="${hasErrors(bean: participant.user, field: 'title', 'error')} required">
                        <label>
                            Title
                        </label>
                        <input type="text" name="User.title" maxlength="50" value="${fieldValue(bean: participant.user, field: 'title')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'firstName', 'error')} required">
                        <label>
                            First name
                            <span class="required-indicator">*</span>
                        </label>
                        <input type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: participant.user, field: 'firstName')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'lastName', 'error')} required">
                        <label>
                            Last name
                            <span class="required-indicator">*</span>
                        </label>
                        <input type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: participant.user, field: 'lastName')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'gender', 'error')} required">
                        <label>
                            Gender
                        </label>
                        <g:select from="['M','F']" name="User.gender" value="${fieldValue(bean: participant.user, field: 'gender')}"  />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'organisation', 'error')} required">
                        <label>
                            Organisation
                        </label>
                        <input type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: participant.user, field: 'organisation')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'department', 'error')} required">
                        <label>
                            Department
                        </label>
                        <input type="text" name="User.department" maxlength="50" value="${fieldValue(bean: participant.user, field: 'department')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'email', 'error')} required">
                        <label>
                            E-mail
                            <span class="required-indicator">*</span>
                        </label>
                        <input type="email" name="User.email" required="required" maxlength="50" value="${fieldValue(bean: participant.user, field: 'email')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'address', 'error')} required">
                        <label>
                            Address
                        </label>
                        <textarea name="User.address">
                            ${fieldValue(bean: participant.user, field: 'address')}
                        </textarea>
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'city', 'error')} required">
                        <label>
                            City
                            <span class="required-indicator">*</span>
                        </label>
                        <input type="text" name="User.city" required="required" maxlength="50" value="${fieldValue(bean: participant.user, field: 'city')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'country', 'error')} required">
                        <label>
                            Country
                            <span class="required-indicator">*</span>
                        </label>
                        <g:select name="User.country" from="${Country.list(sort: 'nameEnglish')}" required="required" optionKey="id" optionValue="nameEnglish" value="${participant.user.country}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'phone', 'error')}">
                        <label>
                            Phone
                        </label>
                        <input type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: participant.user, field: 'phone')}" />
                    </div>
                    <div class="${hasErrors(bean: participant.user, field: 'mobile', 'error')}">
                        <label>
                            Mobile
                        </label>
                        <input type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: participant.user, field: 'mobile')}" />
                    </div>

                    <h3>Inventation letter</h3>
                    <div class="${hasErrors(bean: participant, field: 'invitationLetter', 'error')}">
                        <label>
                            Inventation letter requested
                        </label>
                        <g:checkBox name="ParticipantDate.invitationLetter" checked="${fieldValue(bean: participant, field: 'invitationLetter')}" />
                    </div>
                    <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                        <label>
                            Inventation letter sent
                        </label>
                        <g:checkBox name="ParticipantDate.invitationLetterSent" checked="${fieldValue(bean: participant, field: 'invitationLetterSent')}" />
                    </div>

                    <h3>Extra</h3>
                    <g:each in="${Extra.list()}" var="extra">
                        <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                            <label>
                                ${extra.toString()}
                            </label>
                            <g:checkBox name="ParticipantDate.extras" checked="${participant.extras.find { it == extra }}" value="${extra.id}" />
                        </div>
                    </g:each>

                    <h3>Fee request</h3>
                    <div class="${hasErrors(bean: participant, field: 'lowerFeeRequested', 'error')}">
                        <label>
                            Lower fee requested
                        </label>
                        <g:checkBox name="ParticipantDate.lowerFeeRequested" checked="${fieldValue(bean: participant, field: 'lowerFeeRequested')}" />
                    </div>
                    <div class="${hasErrors(bean: participant, field: 'lowerFeeAnswered', 'error')}">
                        <label>
                            Lower fee answered
                        </label>
                        <g:checkBox name="ParticipantDate.lowerFeeAnswered" checked="${fieldValue(bean: participant, field: 'lowerFeeAnswered')}" />
                    </div>

                    <h3>Attendance</h3>
                    <span style="font-weight:bold; color:red;">TODO</span>
                </fieldset>

                <fieldset id="event-tab">

                </fieldset>
            </form>
        </div>
    </body>
</html>
