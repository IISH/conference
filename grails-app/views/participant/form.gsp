<%@ page import="org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <g:javascript src="participant.js" />
    </head>
    <body>
        <eca:navigation prev="${eca.createLink(action: params.action, id: params.int('id')-1)}" next="${eca.createLink(action: params.action, id: params.int('id')+1)}" />

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
                                <g:select class="property-value" from="${Title.list()}" name="User.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${[null:' ']}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.firstName.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input class="property-value" type="text" name="User.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.lastName.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input class="property-value" type="text" name="User.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.gender.label" />
                                </label>
                                <g:select class="property-value" from="['M','F']" name="User.gender" value="${user.gender?.toString()}" noSelection="${[null:' ']}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.organisation.label" />
                                </label>
                                <input class="property-value" type="text" name="User.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.department.label" />
                                </label>
                                <input class="property-value" type="text" name="User.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.email.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input class="property-value" type="email" name="User.email" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'email')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.address.label" />
                                </label>
                                <textarea class="property-value" name="User.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.city.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input class="property-value" type="text" name="User.city" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.country.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <g:select class="property-value" name="User.country.id" from="${Country.list(sort: 'nameEnglish')}" required="required" optionKey="id" optionValue="nameEnglish" value="${user.country.id}" noSelection="${[null:' ']}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.phone.label" />
                                </label>
                                <input class="property-value" type="text" name="User.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.mobile.label" />
                                </label>
                                <input class="property-value" type="text" name="User.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                            </div>
                            <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.extraInfo.label" />
                                </label>
                                <textarea class="property-value" name="User.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
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
                                    <g:checkBox class="property-value" name="ParticipantDate.invitationLetter" checked="${participant.invitationLetter}" />
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.invitationLetterSent.label" />
                                    </label>
                                    <g:checkBox class="property-value" name="ParticipantDate.invitationLetterSent" checked="${participant.invitationLetterSent}" />
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
                                        <g:checkBox class="property-value" name="ParticipantDate.extras" checked="${participant.extras.find { it == extra }}" value="${extra.id}" />
                                    </div>
                                </g:each>
                            </fieldset> 

                            <fieldset class="form"> 
                                <legend><g:message code="participantDate.lowerFee.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'lowerFeeRequested', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.lowerFeeRequested.label" />
                                    </label>
                                    <g:checkBox class="property-value" name="ParticipantDate.lowerFeeRequested" checked="${participant.lowerFeeRequested}" />
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'lowerFeeAnswered', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.lowerFeeAnswered.label" />
                                    </label>
                                    <g:checkBox class="property-value" name="ParticipantDate.lowerFeeAnswered" checked="${participant.lowerFeeAnswered}" />
                                </div>
                            </fieldset> 

                            <fieldset class="form"> 
                                <legend><g:message code="participantDate.attendance.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'state', 'error')} required">
                                    <label class="property-label">
                                        <g:message code="participantDate.state.label" />
                                    </label>
                                    <g:select class="property-value" name="ParticipantDate.state.id" from="${ParticipantState.list()}" optionKey="id" optionValue="state" value="${participant.state.id}" />
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'feeState', 'error')} required">
                                    <label class="property-label">
                                        <g:message code="participantDate.feeState.label" />
                                    </label>
                                    <g:select class="property-value" name="ParticipantDate.feeState.id" from="${FeeState.list()}" optionKey="id" optionValue="name" value="${participant.feeState.id}" />
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
                            <g:message code="participantData.would.like.to.add.message" args="[curDate.toString()]" />
                            <input type="checkbox" name="add-to-date" value="add" />
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
                                    <input class="property-value" type="text" name="Paper_${i}.title" maxlength="500" required="required" value="${fieldValue(bean: paper, field: 'title')}" />
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'abstr', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.abstr.label" />
                                        <span class="required-indicator">*</span>
                                    </label>
                                    <textarea class="property-value" name="Paper_${i}.abstr" required="required" cols="40" rows="5">${fieldValue(bean: paper, field: 'abstr')}</textarea>
                                </div>
                                <div>
                                    <label class="property-label">
                                        <g:message code="paper.file.label" />
                                    </label>
                                    <div class="property-value">
                                        <g:if test="${paper.file}">
                                        <span>
                                            <a target="_blank" href="${eca.createLink(action: 'downloadPaper', id: paper.id)}">${paper.fileName}</a> - ${paper.getReadableFileSize()}
                                        </span>
                                        <span class="paper ui-icon ui-icon-circle-minus"></span>
                                        </g:if>
                                        <g:else>-</g:else>
                                    </div>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'file', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.file.upload.label" />
                                    </label>
                                    <input class="property-value" type="file" name="Paper_${i}.file" />
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'coAuthors', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.coAuthors.label" />
                                    </label>
                                    <input class="property-value" type="text" name="Paper_${i}.coAuthors" maxlength="500" value="${fieldValue(bean: paper, field: 'coAuthors')}" />
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'state', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.state.label" />
                                    </label>
                                    <g:select class="property-value" from="${paperStates}" name="Paper_${i}.state.id" optionKey="id" optionValue="description" value="${paper.state.id}" />
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'comment', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.comment.label" />
                                    </label>
                                    <textarea class="property-value" name="Paper_${i}.comment" cols="40" rows="5">${fieldValue(bean: paper, field: 'comment')}</textarea>
                                </div>
                            </fieldset>

                            <fieldset class="form">
                                <legend><g:message code="paper.networks.sessions.info.label" /></legend>
                                <div class="${hasErrors(bean: paper, field: 'networkProposal', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.networkProposal.label" />
                                    </label>
                                    <div class="property-value">
                                        <g:select from="${networks}" name="Paper_${i}.networkProposal.id" optionKey="id" optionValue="name" value="${paper.networkProposal?.id}" noSelection="${['': ' ']}" />
                                        <input type="button" id="btn_network" name="btn_network" value="${g.message(code: "default.goto")}" />
                                    </div>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'sessionProposal', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.sessionProposal.label" />
                                    </label>
                                    <input class="property-value" type="text" name="Paper_${i}.sessionProposal" maxlength="500" value="${fieldValue(bean: paper, field: 'sessionProposal')}" />
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'proposalDescription', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.proposalDescription.label" />
                                    </label>
                                    <textarea class="property-value" name="Paper_${i}.proposalDescription" cols="40" rows="5">${fieldValue(bean: paper, field: 'proposalDescription')}</textarea>
                                </div>
                                <div class="${hasErrors(bean: paper, field: 'session', 'error')}">
                                    <label class="property-label">
                                        <g:message code="session.label" />
                                    </label>
                                    <div class="property-value">
                                        <span>
                                            <g:if test="${paper.session}">
                                                <eca:link controller="session" action="show" id="${paper.session.id}">
                                                    ${fieldValue(bean: paper, field: 'session')}
                                                </eca:link>
                                            </g:if>
                                            <g:else>-</g:else>
                                        </span>
                                    </div>
                                </div>
                            </fieldset>

                            <fieldset class="form">
                                <legend><g:message code="equipment.label" /></legend>
                                <g:each in="${equipmentList}" var="equipment">
                                    <div class="${hasErrors(bean: paper, field: 'equipment', 'error')}">
                                        <label class="property-label">
                                            ${equipment.equipment}
                                        </label>
                                        <g:checkBox class="property-value" name="Paper_${i}.equipment" value="${equipment.id}" checked="${paper.equipment.find { it == equipment }}" />
                                    </div>
                                </g:each>
                                <div class="${hasErrors(bean: paper, field: 'equipmentComment', 'error')}">
                                    <label class="property-label">
                                        <g:message code="paper.equipmentComment.label" />
                                    </label>
                                    <textarea class="property-value" name="Paper_${i}.equipmentComment" cols="40" rows="5">${fieldValue(bean: paper, field: 'equipmentComment')}</textarea>
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
                                <input class="property-value" type="text" name="Paper_null.title" maxlength="500" required="required" />
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.abstr.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <textarea class="property-value" name="Paper_null.abstr" required="required" cols="40" rows="5"></textarea>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.file.label" />
                                </label>
                                <div class="property-value"> - </div>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.file.upload.label" />
                                </label>
                                <input class="property-value" type="file" name="Paper_null.file" />
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.coAuthors.label" />
                                </label>
                                <input class="property-value" type="text" name="Paper_null.coAuthors" maxlength="500" />
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.state.label" />
                                </label>
                                <g:select class="property-value" from="${paperStates}" name="Paper_null.state.id" optionKey="id" optionValue="description" value="${paperStates.find { it.id == 1 }}" />
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.comment.label" />
                                </label>
                                <textarea class="property-value" name="Paper_null.comment" cols="40" rows="5"></textarea>
                            </div>
                        </fieldset>

                        <fieldset class="form">
                            <legend><g:message code="paper.networks.sessions.info.label" /></legend>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.networkProposal.label" />
                                </label>
                                <div class="property-value">
                                    <g:select from="${networks}" name="Paper_null.networkProposal" optionKey="id" optionValue="name" noSelection="${['': ' ']}" />
                                    <input type="button" id="btn_network" name="btn_network" value="${g.message(code: "default.goto")}" />
                                </div>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.sessionProposal.label" />
                                </label>
                                <input class="property-value" type="text" name="Paper_null.sessionProposal" maxlength="500" />
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.proposalDescription.label" />
                                </label>
                                <textarea class="property-value" name="Paper_null.proposalDescription" cols="40" rows="5"></textarea>
                            </div>
                            <div>
                                <label class="property-label">
                                    <g:message code="session.label" />
                                </label>
                                <div class="property-value">
                                  <span>-</span>
                                  <input type="button" class="btn_session" name="btn_session" value="${g.message(code: "default.goto")}" />
                                </div>
                            </div>
                        </fieldset>

                        <fieldset class="form">
                            <legend><g:message code="equipment.label" /></legend>
                            <g:each in="${equipmentList}" var="equipment">
                                <div>
                                    <label class="property-label">
                                        ${equipment.equipment}
                                    </label>
                                    <input class="property-value" type="checkbox" name="Paper_null.equipment" />
                                </div>
                            </g:each>
                            <div>
                                <label class="property-label">
                                    <g:message code="paper.equipmentComment.label" />
                                </label>
                                <textarea class="property-value" name="Paper_null.equipmentComment" cols="40" rows="5"></textarea>
                            </div>
                        </fieldset>
                    </div>
                    
                    <div class="clear empty"></div>
                </g:if>
                <g:else>
                    <div style="font-weight:bold; color:red;">User did not sign up for this event date</div>
                </g:else>
                </div>
            </div>
          
            <fieldset class="buttons">
                <eca:link previous="true">
                    <g:message code="default.button.cancel.label" />
                </eca:link>
                <eca:link action="delete" id="${params.id}">
                    <g:message code="default.deleted.label" />
                </eca:link>
                <input type="submit" name="btn_save" class="btn_save" value="${message(code: 'default.button.save.label')}" />
                <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'paper.label').toLowerCase()])}" />
            </fieldset>
        </form>
    </body>
</html>
