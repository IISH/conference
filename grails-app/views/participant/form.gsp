<%@ page import="org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="participant.js" />
    </head>
    <body>
        <eca:navigation ids="${participantIds}" />

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
                </ul>
              
                <div id="personal-tab" class="columns">
                    <div class="column">
                        <div>
                            <h3><g:message code="participantDate.registration.info.label" /></h3>
                            <ol class="property-list">
                                <li>
                                    <span id="id-label" class="property-label">#</span>
                                    <span class="property-value" arial-labelledby="id-label">
                                        <g:fieldValue bean="${user}" field="id" />
                                    </span>
                                </li>
                                <li>
                                    <span id="date-added-label" class="property-label">
                                        <g:message code="user.dateAdded.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="date-added-label">
                                        <g:formatDate date="${user.dateAdded}" />
                                    </span>
                                </li>
                                <g:if test="${participant}">
                                    <li>
                                        <span id="event-date-added-label" class="property-label">
                                            <g:message code="participantDate.dateAdded.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="event-date-added-label">
                                            <g:formatDate date="${participant.dateAdded}" />
                                        </span>
                                    </li>
                                </g:if>
                            </ol>
                        </div>

                        <fieldset class="form"> 
                            <legend><g:message code="participantDate.personal.info.label" /></legend>
                            <div class="${hasErrors(bean: user, field: 'title', 'error')} required">
                                <label class="property-label">
                                    <g:message code="title.label" />
                                </label>
                                <span class="property-value">
                                    <g:select from="${Title.list()}" name="User.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${['':' ']}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.firstName.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.lastName.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.gender.label" />
                                </label>
                                <span class="property-value">
                                    <g:select from="['M','F']" name="User.gender" value="${user.gender?.toString()}" noSelection="${['': ' ']}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.organisation.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.department.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.email.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="email" name="User.email" required="required" maxlength="100" value="${fieldValue(bean: user, field: 'email')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.address.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="User.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.city.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.city" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.country.label" />
                                </label>
                                <span class="property-value">
                                    <g:select name="User.country.id" from="${Country.list(sort: 'nameEnglish')}" optionKey="id" value="${user.country?.id}" noSelection="${[null:' ']}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.phone.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.mobile.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.extraInfo.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="User.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
                                </span>
                            </div>
                        </fieldset>
                    </div>

                    <div class="column">
                        <g:if test="${participant}">
                            <fieldset class="form"> 
                                <legend><g:message code="participantDate.inventationLetter.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'invitationLetter', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.invitationLetter.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.invitationLetter" checked="${participant.invitationLetter}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.invitationLetterSent.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.invitationLetterSent" checked="${participant.invitationLetterSent}" />
                                    </span>
                                </div>
                            </fieldset>

                            <fieldset class="form">
                                <g:each in="${Extra.list()}" var="extra" status="i">
                                    <g:if test="${i==0}">
                                        <legend><g:message code="participantDate.extra.label" /></legend>
                                    </g:if>

                                    <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                        <label class="property-label">
                                            ${extra.toString()}
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="ParticipantDate.extras" checked="${participant.extras.find { it == extra }}" value="${extra.id}" />
                                        </span>
                                    </div>
                                </g:each>
                            </fieldset> 

                            <fieldset class="form"> 
                                <legend><g:message code="participantDate.lowerFee.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'lowerFeeRequested', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.lowerFeeRequested.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.lowerFeeRequested" checked="${participant.lowerFeeRequested}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'lowerFeeAnswered', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.lowerFeeAnswered.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.lowerFeeAnswered" checked="${participant.lowerFeeAnswered}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'lowerFeeText', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.lowerFeeText.label" />
                                    </label>
                                    <span class="property-value">
                                        <textarea name="ParticipantDate.lowerFeeText" cols="40" rows="5">${participant.lowerFeeText}</textarea>
                                    </span>
                                </div>
                            </fieldset>

                            <fieldset class="form">
                                <legend><g:message code="participantDate.student.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'student', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.studentRequested.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.student" checked="${participant.student}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'studentConfirmed', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.studentConfirmed.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.studentConfirmed" checked="${participant.studentConfirmed}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'award', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.award.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:checkBox name="ParticipantDate.award" checked="${participant.award}" />
                                    </span>
                                </div>
                            </fieldset>

                            <fieldset class="form"> 
                                <legend><g:message code="participantDate.attendance.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'state', 'error')} required">
                                    <label class="property-label">
                                        <g:message code="participantDate.state.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:select name="ParticipantDate.state.id" from="${ParticipantState.list()}" optionKey="id" optionValue="state" value="${participant.state.id}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'feeState', 'error')} required">
                                    <label class="property-label">
                                        <g:message code="participantDate.feeState.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:select name="ParticipantDate.feeState.id" from="${FeeState.list()}" optionKey="id" optionValue="name" value="${participant.feeState.id}" />
                                    </span>                                
                                </div>
                                <div class="${hasErrors(bean: user, field: 'dateTimesNotPresent', 'error')}">
                                    <table id="participant-present">
                                        <g:each in="${SessionDateTime.tableList}" var="dateTimeRow">
                                            <tr>
                                            <g:each in="${dateTimeRow}" var="sessionDateTime" status="i">
                                                <g:if test="${i==0}">
                                                    <td><g:formatDate date="${sessionDateTime.day.day}" formatName="default.date.day.format" /></td>
                                                </g:if>

                                                <td>
                                                    <label>
                                                        <g:checkBox name="present" value="${sessionDateTime.id}" checked="${!user.dateTimesNotPresent.find { it.id == sessionDateTime.id }}" />
                                                        ${sessionDateTime.period}
                                                    </label>
                                                </td>
                                            </g:each>
                                            </tr>
                                        </g:each>
                                    </table>
                                </div>
                            </fieldset>

                            <fieldset class="form"> 
                                <legend><g:message code="participantDate.volunteering.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'participantVolunteering', 'error')} ">
                                    <ul>
                                    <g:each in="${participant.participantVolunteering}" var="instance" status="i">
                                        <li>
                                            <input type="hidden" name="ParticipantVolunteering_${i}.id" value="${instance.id}" />
                                            <label>
                                                <g:message code="participantDate.volunteering.select.label" />
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
                                            <g:message code="default.add.label" args="[g.message(code: 'participantDate.volunteering.add.label')]" />
                                        </li>
                                        <li class="hidden">
                                            <input type="hidden" name="ParticipantVolunteering_null.id" />
                                            <label>
                                                <g:message code="participantDate.volunteering.select.label" />
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
                            </fieldset>
                        </g:if>
                        <g:else>
                            <div>
                                <g:message code="participantDate.not.yet.registered.message" args="[curDate.toString()]" /> <br />
                                <g:message code="participantDate.would.like.to.add.message" /> <input type="checkbox" name="add-to-date" value="add" />
                            </div>
                        </g:else>
                    </div>
                  
                    <div class="clear empty"></div>
                </div>

                <div id="papers-tab" class="columns copy">
                    <input type="hidden" name="max-papers" value="${Setting.getByEvent(Setting.findAllByProperty(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION)).value}" />
                    <input type="hidden" name="to-be-deleted" class="to-be-deleted" />

                    <g:if test="${participant}">
                    <g:each in="${papers}" var="paper" status="i">
                        <div class="column">
                            <input type="hidden" name="Paper_${i}.id" value="${paper.id}" />

                            <span class="remove-item">
                                <span class="ui-icon ui-icon-circle-minus"></span>
                                <g:message code="default.delete.label" args="[message(code: 'paper.label').toLowerCase()]" />
                            </span>

                            <fieldset class="form">
                                <legend><g:message code="paper.label" /></legend>
                                <div class="${hasErrors(bean: paper, field: 'title', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.title.label" />
                                        <span class="required-indicator">*</span>
                                    </label>
                                    <span class="property-value">
                                        <input type="text" name="Paper_${i}.title" maxlength="500" required="required" value="${fieldValue(bean: paper, field: 'title')}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'abstr', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.abstr.label" />
                                        <span class="required-indicator">*</span>
                                    </label>
                                    <span class="property-value">
                                        <textarea name="Paper_${i}.abstr" required="required" cols="40" rows="5">${fieldValue(bean: paper, field: 'abstr')}</textarea>
                                    </span>
                                </div>
                                <div>
                                    <label class="property-label">
                                        <g:message code="paper.file.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:if test="${paper.file}">
                                        <span>
                                            <a target="_blank" href="${eca.createLink(action: 'downloadPaper', id: paper.id)}">${paper.fileName}</a> - ${paper.getReadableFileSize()}
                                        </span>
                                        <span class="paper no-del ui-icon ui-icon-circle-minus"></span>
                                        </g:if>
                                        <g:else>-</g:else>
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'file', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.file.upload.label" />
                                    </label>
                                    <span class="property-value">
                                        <input type="file" name="Paper_${i}.file" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'coAuthors', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.coAuthors.label" />
                                    </label>
                                    <span class="property-value">
                                        <input type="text" name="Paper_${i}.coAuthors" maxlength="500" value="${fieldValue(bean: paper, field: 'coAuthors')}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'state', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.state.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:select from="${paperStates}" name="Paper_${i}.state.id" optionKey="id" optionValue="description" value="${paper.state.id}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'comment', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.comment.label" />
                                    </label>
                                    <span class="property-value">
                                        <textarea name="Paper_${i}.comment" cols="40" rows="5">${fieldValue(bean: paper, field: 'comment')}</textarea>
                                    </span>
                                </div>
                            </fieldset>

                            <fieldset class="form">
                                <legend><g:message code="paper.networks.sessions.info.label" /></legend>
                                <div class="${hasErrors(bean: paper, field: 'networkProposal', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.networkProposal.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:select from="${networks}" name="Paper_${i}.networkProposal.id" optionKey="id" optionValue="name" value="${paper.networkProposal?.id}" noSelection="${['': ' ']}" />
                                        <input type="button" id="btn_network" name="btn_network" value="${g.message(code: "default.goto")}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'sessionProposal', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.sessionProposal.label" />
                                    </label>
                                    <span class="property-value">
                                        <input type="text" name="Paper_${i}.sessionProposal" maxlength="500" value="${fieldValue(bean: paper, field: 'sessionProposal')}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'proposalDescription', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.proposalDescription.label" />
                                    </label>
                                    <span class="property-value">
                                        <textarea name="Paper_${i}.proposalDescription" cols="40" rows="5">${fieldValue(bean: paper, field: 'proposalDescription')}</textarea>
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'session', 'error')}">
                                    <label class="property-label">
                                        <g:message code="session.label" />
                                    </label>
                                    <span class="property-value">
                                        <span>
                                            <g:if test="${paper.session}">
                                                <eca:link controller="session" action="show" id="${paper.session.id}">
                                                    ${fieldValue(bean: paper, field: 'session')}
                                                </eca:link>
                                            </g:if>
                                            <g:else>-</g:else>
                                        </span>
                                    </span>
                                </div>
                            </fieldset>
                            
                            <fieldset class="form">
                                <legend><g:message code="equipment.label" /></legend>
                                <g:each in="${equipmentList}" var="equipment">
                                    <div class="${hasErrors(bean: paper, field: 'equipment', 'error')}">
                                        <label class="property-label">
                                            ${equipment.equipment}
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="Paper_${i}.equipment" value="${equipment.id}" checked="${paper.equipment.find { it == equipment }}" />
                                        </span>
                                    </div>
                                </g:each>
                                <div class="${hasErrors(bean: paper, field: 'equipmentComment', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.equipmentComment.label" />
                                    </label>
                                    <span class="property-value">
                                        <textarea name="Paper_${i}.equipmentComment" cols="40" rows="5">${fieldValue(bean: paper, field: 'equipmentComment')}</textarea>
                                    </span>
                                </div>
                            </fieldset>
                        </div>
                    </g:each>

                    <div class="column hidden">
                        <span class="remove-item">
                            <span class="ui-icon ui-icon-circle-minus"></span>
                            <g:message code="default.delete.label" args="[message(code: 'paper.label').toLowerCase()]" />
                        </span>

                        <fieldset class="form">
                            <legend><g:message code="paper.label" /></legend>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.title.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="text" name="Paper_null.title" maxlength="500" required="required" />
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.abstr.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <textarea name="Paper_null.abstr" required="required" cols="40" rows="5"></textarea>
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.file.label" />
                                </label>
                                <span class="property-value"> - </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.file.upload.label" />
                                </label>
                                <span class="property-value">
                                    <input type="file" name="Paper_null.file" />
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.coAuthors.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="Paper_null.coAuthors" maxlength="500" />
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.state.label" />
                                </label>
                                <span class="property-value">
                                    <g:select from="${paperStates}" name="Paper_null.state.id" optionKey="id" optionValue="description" value="${paperStates.find { it.id == 1 }}" />
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.comment.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="Paper_null.comment" cols="40" rows="5"></textarea>
                                </span>
                            </div>
                        </fieldset>

                        <fieldset class="form">
                            <legend><g:message code="paper.networks.sessions.info.label" /></legend>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.networkProposal.label" />
                                </label>
                                <span class="property-value">
                                    <g:select from="${networks}" name="Paper_null.networkProposal" optionKey="id" optionValue="name" noSelection="${['': ' ']}" />
                                    <input type="button" id="btn_network" name="btn_network" value="${g.message(code: "default.goto")}" />
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.sessionProposal.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="Paper_null.sessionProposal" maxlength="500" />
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.proposalDescription.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="Paper_null.proposalDescription" cols="40" rows="5"></textarea>
                                </span>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="session.label" />
                                </label>
                                <span class="property-value">
                                  <span>-</span>
                                  <input type="button" class="btn_session" name="btn_session" value="${g.message(code: "default.goto")}" />
                                </span>
                            </div>
                        </fieldset>

                        <fieldset class="form">
                            <legend><g:message code="equipment.label" /></legend>
                            <g:each in="${equipmentList}" var="equipment">
                                <div>
                                    <label class="property-label">
                                        ${equipment.equipment}
                                    </label>
                                    <span class="property-value">
                                        <input type="checkbox" name="Paper_null.equipment" />
                                    </span>
                                </div>
                            </g:each>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.equipmentComment.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="Paper_null.equipmentComment" cols="40" rows="5"></textarea>
                                </span>
                            </div>
                        </fieldset>
                    </div>
                    
                    <div class="clear empty"></div>
                </g:if>
                </div>
            </div>
          
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <eca:link action="delete" id="${params.id}" class="btn_delete">
                    <g:message code="default.deleted.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'paper.label').toLowerCase()])}" />
            </fieldset>
        </form>
    </body>
</html>
