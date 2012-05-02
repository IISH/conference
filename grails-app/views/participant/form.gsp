<%@ page import="org.iisg.eca.domain.Network; org.iisg.eca.domain.Title; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
    </head>
    <body>
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
                <li><a href="#personal-tab">Personal information</a></li>
                <li><a href="#papers-tab">Papers</a></li>
            </ul>
                <fieldset id="personal-tab" class="columns">
                    <div class="column">
                        <h3>Registration info</h3>
                        <ol class="property-list">
                            <li>
                                <span id="id-label" class="property-label">#</span>
                                <span class="property-value" arial-labelledby="id-label">
                                    <g:fieldValue bean="${user}" field="id" />
                                </span>
                            </li>
                            <li>
                                <span id="date-added-label" class="property-label">
                                    Date added
                                </span>
                                <span class="property-value" arial-labelledby="date-added-label">
                                    <g:formatDate date="${user.dateAdded}" />
                                </span>
                            </li>
                            <g:if test="${participant}">
                                <li>
                                    <span id="event-date-added-label" class="property-label">
                                        Event date added
                                    </span>
                                    <span class="property-value" arial-labelledby="event-date-added-label">
                                        <g:formatDate date="${participant.dateAdded}" />
                                    </span>
                                </li>
                            </g:if>
                        </ol>

                        <h3>Personal info</h3>
                        <div class="${hasErrors(bean: user, field: 'title', 'error')} required">
                            <label>
                                Title
                            </label>
                            <g:select from="${Title.list()}" name="User.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${[null:' ']}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                            <label>
                                First name
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                            <label>
                                Last name
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                            <label>
                                Gender
                            </label>
                            <g:select from="['M','F']" name="User.gender" value="${user.gender?.toString()}" noSelection="${[null:' ']}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                            <label>
                                Organisation
                            </label>
                            <input type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                            <label>
                                Department
                            </label>
                            <input type="text" name="User.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                            <label>
                                E-mail
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="email" name="User.email" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'email')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                            <label>
                                Address
                            </label>
                            <textarea name="User.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                        </div>
                        <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                            <label>
                                City
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="text" name="User.city" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                            <label>
                                Country
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select name="User.country.id" from="${Country.list(sort: 'nameEnglish')}" required="required" optionKey="id" optionValue="nameEnglish" value="${user.country.id}" noSelection="${[null:' ']}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                            <label>
                                Phone
                            </label>
                            <input type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                            <label>
                                Mobile
                            </label>
                            <input type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                            <label>
                                Extra information
                            </label>
                            <textarea name="User.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
                        </div>
                    </div>

                    <g:if test="${participant}">
                        <div class="column">
                            <h3>Inventation letter</h3>
                            <div class="${hasErrors(bean: participant, field: 'invitationLetter', 'error')}">
                                <label>
                                    Inventation letter requested
                                </label>
                                <g:checkBox name="ParticipantDate.invitationLetter" checked="${participant.invitationLetter}" />
                            </div>
                            <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                <label>
                                    Inventation letter sent
                                </label>
                                <g:checkBox name="ParticipantDate.invitationLetterSent" checked="${participant.invitationLetterSent}" />
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
                                <g:checkBox name="ParticipantDate.lowerFeeRequested" checked="${participant.lowerFeeRequested}" />
                            </div>
                            <div class="${hasErrors(bean: participant, field: 'lowerFeeAnswered', 'error')}">
                                <label>
                                    Lower fee answered
                                </label>
                                <g:checkBox name="ParticipantDate.lowerFeeAnswered" checked="${participant.lowerFeeAnswered}" />
                            </div>

                            <h3>Attendance</h3>
                            <div class="${hasErrors(bean: participant, field: 'state', 'error')} required">
                                <label>
                                    Current state
                                </label>
                                <g:select name="ParticipantDate.state.id" from="${ParticipantState.list()}" optionKey="id" optionValue="state" value="${participant.state.id}" />
                            </div>
                            <div class="${hasErrors(bean: participant, field: 'feeState', 'error')} required">
                                <label>
                                    Current fee state
                                </label>
                                <g:select name="ParticipantDate.feeState.id" from="${FeeState.list()}" optionKey="id" optionValue="name" value="${participant.feeState.id}" />
                            </div>
                            <div style="font-weight:bold; color:red;">TODO (Present / Not present at)</div>
                            <div style="font-weight:bold; color:red;">TODO (Participant would like to be / would like to get)</div>
                        </div>
                    </g:if>
                    <g:else>
                        <div style="font-weight:bold; color:red;">TODO (User did not sign up for this event date)</div>
                    </g:else>
                </fieldset>

                <fieldset id="papers-tab" class="columns">
                <g:if test="${participant}">
                    <g:if test="${participant.user.papers.isEmpty()}">
                        <div style="font-weight:bold; color:red;">NO PAPERS!</div>
                    </g:if>
                    <g:each in="${participant.user.papers}" var="paper" status="i">
                        <div class="column">
                            <input type="hidden" name="Paper_${i}.id" value="${paper.id}" />

                            <h3>Attendance information</h3>
                            <div class="${hasErrors(bean: paper, field: 'title', 'error')}">
                                <label>
                                    Paper title
                                    <span class="required-indicator">*</span>
                                </label>
                                <input type="text" name="Paper_${i}.title" maxlength="500" required="required" value="${fieldValue(bean: paper, field: 'title')}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'abstr', 'error')}">
                                <label>
                                    Abstract
                                </label>
                                <textarea name="Paper_${i}.abstr" cols="40" rows="5">${fieldValue(bean: paper, field: 'abstr')}</textarea>
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'file', 'error')}">
                                <label>
                                    Uploaded paper
                                </label>
                                <input type="file" name="Paper_${i}.file" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'coAuthors', 'error')}">
                                <label>
                                    Co-authors
                                </label>
                                <input type="text" name="Paper_${i}.coAuthors" maxlength="500" value="${fieldValue(bean: paper, field: 'coAuthors')}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'state', 'error')}">
                                <label>
                                    Paper state
                                </label>
                                <g:select from="${PaperState.list()}" name="Paper_${i}.state.id" optionKey="id" optionValue="description" value="${paper.state.id}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'comment', 'error')}">
                                <label>
                                    Additional comments
                                </label>
                                <textarea name="Paper_${i}.comment" cols="40" rows="5">${fieldValue(bean: paper, field: 'comment')}</textarea>
                            </div>

                            <h3>Networks & Sessions information</h3>
                            <div class="${hasErrors(bean: paper, field: 'networkProposal', 'error')}">
                                <label>
                                    Network proposal
                                </label>
                                <g:select from="${Network.list()}" name="Paper_${i}.networkProposal" optionKey="id" optionValue="name" value="${paper.networkProposal}" noSelection="${[null: ' ']}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'sessionProposal', 'error')}">
                                <label>
                                    Session proposal
                                </label>
                                <input type="text" name="Paper_${i}.sessionProposal" maxlength="500" value="${fieldValue(bean: paper, field: 'sessionProposal')}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'proposalDescription', 'error')}">
                                <label>
                                    Session proposal description
                                </label>
                                <textarea name="Paper_${i}.proposalDescription" cols="40" rows="5">${fieldValue(bean: paper, field: 'proposalDescription')}</textarea>
                            </div>
                            <span style="font-weight:bold; color:red;">TODO (Sessions and proposals)</span>

                            <h3>Audiovisual equipment</h3>
                            <g:each in="${Equipment.list()}" var="equipment">
                                <div class="${hasErrors(bean: paper, field: 'equipment', 'error')}">
                                    <label>
                                        ${equipment.equipment}
                                    </label>
                                    <g:checkBox name="Paper_${i}.equipment" value="${equipment.id}" checked="${paper.equipment.find { it == equipment }}" />
                                </div>
                            </g:each>
                            <div class="${hasErrors(bean: paper, field: 'equipmentComment', 'error')}">
                                <label>
                                    Equipment comments
                                </label>
                                <textarea name="Paper_${i}.equipmentComment" cols="40" rows="5">${fieldValue(bean: paper, field: 'equipmentComment')}</textarea>
                            </div>
                        </div>
                    </g:each>
                </g:if>
                <g:else>
                    <div style="font-weight:bold; color:red;">TODO (User did not sign up for this event date)</div>
                </g:else>
                </fieldset>
            </div>

            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
            </fieldset>
        </form>
    </body>
</html>
