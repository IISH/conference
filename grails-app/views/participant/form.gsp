<%@ page import="org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country" %>
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
                <li><a href="#personal-tab"><g:message code="participantdate.personalinfo.label" /></a></li>
                <li><a href="#papers-tab"><g:message code="paper.multiple.label" /></a></li>
            </ul>
                <fieldset id="personal-tab" class="columns">
                    <div class="column">
                        <h3><g:message code="participantdate.registrationinfo.label" /></h3>
                        <ol class="property-list">
                            <li>
                                <span id="id-label" class="property-label">#</span>
                                <span class="property-value" arial-labelledby="id-label">
                                    <g:fieldValue bean="${user}" field="id" />
                                </span>
                            </li>
                            <li>
                                <span id="date-added-label" class="property-label">
                                    <g:message code="user.dateadded.label" />
                                </span>
                                <span class="property-value" arial-labelledby="date-added-label">
                                    <g:formatDate date="${user.dateAdded}" />
                                </span>
                            </li>
                            <g:if test="${participant}">
                                <li>
                                    <span id="event-date-added-label" class="property-label">
                                        <g:message code="participantdate.dateadded.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="event-date-added-label">
                                        <g:formatDate date="${participant.dateAdded}" />
                                    </span>
                                </li>
                            </g:if>
                        </ol>

                        <h3><g:message code="participantdate.personalinfo.label" /></h3>
                        <div class="${hasErrors(bean: user, field: 'title', 'error')} required">
                            <label>
                                <g:message code="title.label" />
                            </label>
                            <g:select from="${Title.list()}" name="User.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${[null:' ']}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                            <label>
                                <g:message code="user.firstname.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                            <label>
                                <g:message code="user.lastname.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                            <label>
                                <g:message code="user.gender.label" />
                            </label>
                            <g:select from="['M','F']" name="User.gender" value="${user.gender?.toString()}" noSelection="${[null:' ']}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                            <label>
                                <g:message code="user.organisation.label" />
                            </label>
                            <input type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                            <label>
                                <g:message code="user.department.label" />
                            </label>
                            <input type="text" name="User.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                            <label>
                                <g:message code="user.email.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="email" name="User.email" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'email')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                            <label>
                                <g:message code="user.address.label" />
                            </label>
                            <textarea name="User.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                        </div>
                        <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                            <label>
                                <g:message code="user.city.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <input type="text" name="User.city" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                            <label>
                                <g:message code="user.country.label" />
                                <span class="required-indicator">*</span>
                            </label>
                            <g:select name="User.country.id" from="${Country.list(sort: 'nameEnglish')}" required="required" optionKey="id" optionValue="nameEnglish" value="${user.country.id}" noSelection="${[null:' ']}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                            <label>
                                <g:message code="user.phone.label" />
                            </label>
                            <input type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                            <label>
                                <g:message code="user.mobile.label" />
                            </label>
                            <input type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                        </div>
                        <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                            <label>
                                <g:message code="user.extrainfo.label" />
                            </label>
                            <textarea name="User.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
                        </div>
                    </div>

                    <g:if test="${participant}">
                        <div class="column">
                            <h3><g:message code="participantdate.inventationletter.label" /></h3>
                            <div class="${hasErrors(bean: participant, field: 'invitationLetter', 'error')}">
                                <label>
                                    <g:message code="participantdate.invitationletter.label" />
                                </label>
                                <g:checkBox name="ParticipantDate.invitationLetter" checked="${participant.invitationLetter}" />
                            </div>
                            <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                <label>
                                    <g:message code="participantdate.invitationlettersent.label" />
                                </label>
                                <g:checkBox name="ParticipantDate.invitationLetterSent" checked="${participant.invitationLetterSent}" />
                            </div>

                            <h3><g:message code="participantdate.extra.label" /></h3>
                            <g:each in="${Extra.list()}" var="extra">
                                <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                    <label>
                                        ${extra.toString()}
                                    </label>
                                    <g:checkBox name="ParticipantDate.extras" checked="${participant.extras.find { it == extra }}" value="${extra.id}" />
                                </div>
                            </g:each>

                            <h3><g:message code="participantdate.lowerfee.label" /></h3>
                            <div class="${hasErrors(bean: participant, field: 'lowerFeeRequested', 'error')}">
                                <label>
                                    <g:message code="participantdate.lowerfeerequested.label" />
                                </label>
                                <g:checkBox name="ParticipantDate.lowerFeeRequested" checked="${participant.lowerFeeRequested}" />
                            </div>
                            <div class="${hasErrors(bean: participant, field: 'lowerFeeAnswered', 'error')}">
                                <label>
                                    <g:message code="participantdate.lowerfeeanswered.label" />
                                </label>
                                <g:checkBox name="ParticipantDate.lowerFeeAnswered" checked="${participant.lowerFeeAnswered}" />
                            </div>

                            <h3><g:message code="participantdate.attendance.label" /></h3>
                            <div class="${hasErrors(bean: participant, field: 'state', 'error')} required">
                                <label>
                                    <g:message code="participantdate.state.label" />
                                </label>
                                <g:select name="ParticipantDate.state.id" from="${ParticipantState.list()}" optionKey="id" optionValue="state" value="${participant.state.id}" />
                            </div>
                            <div class="${hasErrors(bean: participant, field: 'feeState', 'error')} required">
                                <label>
                                    <g:message code="participantdate.feestate.label" />
                                </label>
                                <g:select name="ParticipantDate.feeState.id" from="${FeeState.list()}" optionKey="id" optionValue="name" value="${participant.feeState.id}" />
                            </div>

                            <div style="font-weight:bold; color:red;">TODO (Present / Not present at)</div>

                            <h3><g:message code="participantdate.volunteering.label" /></h3>
                            <div class="${hasErrors(bean: participant, field: 'participantVolunteering', 'error')} ">
                                <ul class="inline">
                                <g:each in="${participant.participantVolunteering}" var="instance" status="i">
                                    <li>
                                        <input type="hidden" name="ParticipantVolunteering_${i}.id" value="${instance.id}" />
                                        <label>
                                            <g:message code="participantdate.volunteering.select.label" />
                                            <g:select from="${volunteering}" name="ParticipantVolunteering_${i}.volunteering.id" optionKey="id" optionValue="description" value="${instance.volunteering.id}" />
                                        </label>
                                        <label>
                                            <g:message code="network.label" />
                                            <g:select from="${networks}" name="ParticipantVolunteering_${i}.network.id" optionKey="id" optionValue="name" value="${instance.network.id}" />
                                        </label>
                                        <span class="ui-icon ui-icon-circle-minus"></span>
                                    </li>
                                </g:each>
                                    <li class="add">
                                        <span class="ui-icon ui-icon-circle-plus"></span>
                                        <g:message code="default.add.label" args="[g.message(code: 'participantdate.volunteering.add.label')]" />
                                    </li>
                                    <li class="hidden">
                                        <input type="hidden" name="ParticipantVolunteering_null.id" />
                                        <label>
                                            <g:message code="participantdate.volunteering.select.label" />
                                            <g:select from="${volunteering}" name="ParticipantVolunteering_null.volunteering.id" optionKey="id" optionValue="description" />
                                        </label>
                                        <label>
                                            <g:message code="network.label" />
                                            <g:select from="${networks}" name="ParticipantVolunteering_null.network.id" optionKey="id" optionValue="name" />
                                        </label>
                                        <span class="ui-icon ui-icon-circle-minus"></span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </g:if>
                    <g:else>
                        <div>
                            <g:message code="participantdata.would.like.to.add.message" args="[curDate.toString()]" />
                            <input type="checkbox" name="add-to-date" value="add" />
                        </div>
                    </g:else>
                </fieldset>

                <fieldset id="papers-tab" class="columns">
                <g:if test="${participant}">
                    <g:if test="${participant.user.papers.isEmpty()}">
                        <div style="font-weight:bold; color:red;">TODO: NO PAPERS!</div>
                    </g:if>
                    <g:each in="${participant.user.papers}" var="paper" status="i">
                        <div class="column">
                            <input type="hidden" name="Paper_${i}.id" value="${paper.id}" />

                            <h3><g:message code="paper.label" /></h3>
                            <div class="${hasErrors(bean: paper, field: 'title', 'error')}">
                                <label>
                                    <g:message code="paper.title.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input type="text" name="Paper_${i}.title" maxlength="500" required="required" value="${fieldValue(bean: paper, field: 'title')}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'abstr', 'error')}">
                                <label>
                                    <g:message code="paper.abstr.label" />
                                </label>
                                <textarea name="Paper_${i}.abstr" cols="40" rows="5">${fieldValue(bean: paper, field: 'abstr')}</textarea>
                            </div>
                            <g:if test="${paper.file}">
                            <div>
                                <label>
                                    <g:message code="paper.file.label" />
                                </label>
                                <span>
                                    <a target="_blank" href="${eca.createLink(action: 'downloadPaper', id: paper.id)}">${paper.fileName}</a> - ${paper.getReadableFileSize()}
                                </span>
                                <span class="paper ui-icon ui-icon-circle-minus"></span>
                            </div>
                            </g:if>
                            <div class="${hasErrors(bean: paper, field: 'file', 'error')}">
                                <label>
                                    <g:message code="paper.file.upload.label" />
                                </label>
                                <input type="file" name="Paper_${i}.file" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'coAuthors', 'error')}">
                                <label>
                                    <g:message code="paper.coauthors.label" />
                                </label>
                                <input type="text" name="Paper_${i}.coAuthors" maxlength="500" value="${fieldValue(bean: paper, field: 'coAuthors')}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'state', 'error')}">
                                <label>
                                    <g:message code="paper.state.label" />
                                </label>
                                <g:select from="${paperStates}" name="Paper_${i}.state.id" optionKey="id" optionValue="description" value="${paper.state.id}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'comment', 'error')}">
                                <label>
                                    <g:message code="paper.comment.label" />
                                </label>
                                <textarea name="Paper_${i}.comment" cols="40" rows="5">${fieldValue(bean: paper, field: 'comment')}</textarea>
                            </div>

                            <h3><g:message code="paper.networkssessionsinfo.label" /></h3>
                            <div class="${hasErrors(bean: paper, field: 'networkProposal', 'error')}">
                                <label>
                                    <g:message code="paper.networkproposal.label" />
                                </label>
                                <g:select from="${networks}" name="Paper_${i}.networkProposal" optionKey="id" optionValue="name" value="${paper.networkProposal}" noSelection="${[null: ' ']}" />
                                <input type="button" id="btn_network" name="btn_network" value="${g.message(code: "default.goto")}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'sessionProposal', 'error')}">
                                <label>
                                    <g:message code="paper.sessionproposal.label" />
                                </label>
                                <input type="text" name="Paper_${i}.sessionProposal" maxlength="500" value="${fieldValue(bean: paper, field: 'sessionProposal')}" />
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'proposalDescription', 'error')}">
                                <label>
                                    <g:message code="paper.proposaldescription.label" />
                                </label>
                                <textarea name="Paper_${i}.proposalDescription" cols="40" rows="5">${fieldValue(bean: paper, field: 'proposalDescription')}</textarea>
                            </div>
                            <div class="${hasErrors(bean: paper, field: 'session', 'error')}">
                                <label>
                                    <g:message code="session.label" />
                                </label>
                                <span>
                                    <g:if test="${paper.session}">${fieldValue(bean: paper, field: 'session')}</g:if>
                                    <g:else>-</g:else>
                                </span>
                                <input type="hidden" name="session_id" value="${paper.session?.id}" />
                                <input type="button" id="btn_session" name="btn_session" value="${g.message(code: "default.goto")}" />
                            </div>

                            <h3><g:message code="equipment.label" /></h3>
                            <g:each in="${equipmentList}" var="equipment">
                                <div class="${hasErrors(bean: paper, field: 'equipment', 'error')}">
                                    <label>
                                        ${equipment.equipment}
                                    </label>
                                    <g:checkBox name="Paper_${i}.equipment" value="${equipment.id}" checked="${paper.equipment.find { it == equipment }}" />
                                </div>
                            </g:each>
                            <div class="${hasErrors(bean: paper, field: 'equipmentComment', 'error')}">
                                <label>
                                    <g:message code="paper.equipmentcomment.label" />
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
