<%@ page import="org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order" %>
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

                <div id="personal-tab" class="columns">
                    <div class="column">
                        <div>
                            <h3><g:message code="participantDate.registration.info.label" /></h3>
                            <ol class="property-list">
                                <li>
                                    <span id="id-label" class="property-label">#</span>
                                    <span class="property-value" arial-labelledby="id-label">
                                        ${user.id}

                                        <g:set var="link" value="${Setting.getSetting(Setting.CHANGE_USER).value}" />
                                        <g:if test="${link}">
                                            <span class="inline-link">
                                                <a href="${link.replace('[EMAIL]', user.email)}" target="_blank">
                                                    <g:message code="participantDate.show.personal.page.message" />
                                                </a>
                                            </span>
                                        </g:if>
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
                                    <g:select from="${Title.list()}" name="user.title" optionKey="title" optionValue="title" value="${user.title}" noSelection="${['':' ']}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'firstName', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.firstName.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.firstName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'firstName')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'lastName', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.lastName.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.lastName" required="required" maxlength="50" value="${fieldValue(bean: user, field: 'lastName')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'gender', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.gender.label" />
                                </label>
                                <span class="property-value">
                                    <g:select from="['M','F']" name="user.gender" value="${user.gender?.toString()}" noSelection="${['': ' ']}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'organisation', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.organisation.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.organisation" maxlength="50" value="${fieldValue(bean: user, field: 'organisation')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.department.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.email.label" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <span class="property-value">
                                    <input type="email" name="user.email" required="required" maxlength="100" value="${fieldValue(bean: user, field: 'email')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'emailDiscontinued', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.emailDiscontinued.label" />
                                </label>
                                <span class="property-value">
                                    <g:checkBox name="user.emailDiscontinued" value="${user.emailDiscontinued}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'address', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.address.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="user.address" cols="40" rows="5">${fieldValue(bean: user, field: 'address')}</textarea>
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'city', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.city.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.city" maxlength="50" value="${fieldValue(bean: user, field: 'city')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                                <label class="property-label">
                                    <g:message code="user.country.label" />
                                </label>
                                <span class="property-value">
                                    <g:select name="user.country.id" from="${Country.list(sort: 'nameEnglish')}" optionKey="id" value="${user.country?.id}" noSelection="${[null:' ']}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'phone', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.phone.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.phone" maxlength="50" value="${fieldValue(bean: user, field: 'phone')}" />
                                </span>
                            </div>
                            <div class="${hasErrors(bean: user, field: 'mobile', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.mobile.label" />
                                </label>
                                <span class="property-value">
                                    <input type="text" name="user.mobile" maxlength="50" value="${fieldValue(bean: user, field: 'mobile')}" />
                                </span>
                            </div>
                            <g:if test="${Setting.getSetting(Setting.SHOW_CV, curDate?.event).value == '1'}">
                                <div class="${hasErrors(bean: user, field: 'cv', 'error')}">
                                    <label class="property-label">
                                        <g:message code="user.cv.label" />
                                    </label>
                                    <span class="property-value">
                                        <textarea name="user.cv" cols="40" rows="5">${fieldValue(bean: user, field: 'cv')}</textarea>
                                    </span>
                                </div>
                            </g:if>
                            <div class="${hasErrors(bean: user, field: 'extraInfo', 'error')}">
                                <label class="property-label">
                                    <g:message code="user.extraInfo.label" />
                                </label>
                                <span class="property-value">
                                    <textarea name="user.extraInfo" cols="40" rows="5">${fieldValue(bean: user, field: 'extraInfo')}</textarea>
                                </span>
                            </div>
                        </fieldset>
                    </div>

                    <div class="column">
                        <g:if test="${participant}">
                            <g:if test="${Setting.getSetting(Setting.SHOW_INVITATION_LETTER, curDate?.event).value == '1'}">
                                <fieldset class="form">
                                    <legend><g:message code="participantDate.invitationLetter.label" /></legend>
                                    <div class="${hasErrors(bean: participant, field: 'invitationLetter', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.invitationLetterRequested.label" />
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.invitationLetter" checked="${participant.invitationLetter}" />
                                        </span>
                                    </div>
                                    <div class="${hasErrors(bean: participant, field: 'invitationLetterSent', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.invitationLetterSent.label" />
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.invitationLetterSent" checked="${participant.invitationLetterSent}" />
                                        </span>
                                    </div>
                                </fieldset>
                            </g:if>

                            <fieldset class="form">
                                <g:each in="${Extra.list()}" var="extra" status="i">
                                    <g:if test="${i==0}">
                                        <legend><g:message code="participantDate.extra.label" /></legend>
                                    </g:if>

                                    <div class="${hasErrors(bean: participant, field: 'extras', 'error')}">
                                        <label class="property-label">
                                            ${extra.toString()}
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.extras" checked="${participant.extras.find { it == extra }}" value="${extra.id}" />
                                        </span>
                                    </div>
                                </g:each>
                            </fieldset>

                            <g:if test="${(Setting.getSetting(Setting.SHOW_LOWER_FEE, curDate?.event).value == '1') || (Setting.getSetting(Setting.SHOW_STUDENT, curDate?.event).value == '1')}">
                                <fieldset class="form">
                                    <legend><g:message code="participantDate.lowerFee.label" /></legend>
                                    <div class="${hasErrors(bean: participant, field: 'lowerFeeRequested', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.lowerFeeRequested.label" />
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.lowerFeeRequested" checked="${participant.lowerFeeRequested}" />
                                        </span>
                                    </div>
                                    <div class="${hasErrors(bean: participant, field: 'lowerFeeAnswered', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.lowerFeeAnswered.label" />
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.lowerFeeAnswered" checked="${participant.lowerFeeAnswered}" />
                                        </span>
                                    </div>
                                    <div class="${hasErrors(bean: participant, field: 'lowerFeeText', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.lowerFeeText.label" />
                                        </label>
                                        <span class="property-value">
                                            <textarea name="participantDate.lowerFeeText" cols="40" rows="5">${participant.lowerFeeText}</textarea>
                                        </span>
                                    </div>
                                </fieldset>
                            </g:if>

                             <g:if test="${Setting.getSetting(Setting.SHOW_STUDENT, curDate?.event).value == '1'}">
                                <fieldset class="form">
                                    <legend><g:message code="participantDate.student.label" /></legend>
                                    <div class="${hasErrors(bean: participant, field: 'student', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.studentRequested.label" />
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.student" checked="${participant.student}" />
                                        </span>
                                    </div>
                                    <div class="${hasErrors(bean: participant, field: 'studentConfirmed', 'error')}">
                                        <label class="property-label">
                                            <g:message code="participantDate.studentConfirmed.label" />
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="participantDate.studentConfirmed" checked="${participant.studentConfirmed}" />
                                        </span>
                                    </div>
                                    <g:if test="${Setting.getSetting(Setting.SHOW_AWARD, curDate?.event).value == '1'}">
                                        <div class="${hasErrors(bean: participant, field: 'award', 'error')}">
                                            <label class="property-label">
                                                <g:message code="participantDate.award.label" />
                                            </label>
                                            <span class="property-value">
                                                <g:checkBox name="participantDate.award" checked="${participant.award}" />
                                            </span>
                                        </div>
                                    </g:if>
                                </fieldset>
                            </g:if>

                            <fieldset class="form">
                                <legend><g:message code="participantDate.attendance.label" /></legend>
                                <div class="${hasErrors(bean: participant, field: 'state', 'error')} required">
                                    <label class="property-label">
                                        <g:message code="participantDate.state.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:select name="participantDate.state.id" from="${ParticipantState.list()}" optionKey="id" optionValue="state" value="${participant.state.id}" />
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: participant, field: 'feeState', 'error')} required">
                                    <label class="property-label">
                                        <g:message code="participantDate.feeState.label" />
                                    </label>
                                    <span class="property-value">
                                        <g:select name="participantDate.feeState.id" from="${FeeState.sortedFeeStates.list()}" optionKey="id" optionValue="name" value="${participant.feeState.id}" />
                                    </span>
                                </div>
                                <g:if test="${Setting.getSetting(Setting.SHOW_ACCOMPANYING_PERSONS, curDate?.event).value == '1'}">
                                    <div class="${hasErrors(bean: participant, field: 'accompanyingPersons', 'error')} ">
                                        <label class="property-label">
                                            <g:message code="participantDate.accompanyingPersons.label" />
                                        </label>
                                        <ul class="property-value">
	                                        <g:set var="accompanyingPersons" value="${participant.accompanyingPersons ? participant.accompanyingPersons.sort() : []}" />
                                            <g:each in="${accompanyingPersons}" var="instance">
                                                <li>
                                                    <input type="text" name="participantDate.accompanyingPersons" value="${instance}" />
                                                    <span class="ui-icon ui-icon-circle-minus"></span>
                                                </li>
                                            </g:each>
                                            <li class="add">
                                                <span class="ui-icon ui-icon-circle-plus"></span>
                                                <g:message code="default.add.label" args="[g.message(code: 'participantDate.accompanyingPersons.person.label')]" />
                                            </li>
                                            <li class="hidden">
                                                <input type="text" name="participantDate.accompanyingPersons" />
                                                <span class="ui-icon ui-icon-circle-minus"></span>
                                            </li>
                                        </ul>
                                    </div>
                                </g:if>
                                <div class="${hasErrors(bean: participant, field: 'extraInfo', 'error')}">
                                    <label class="property-label">
                                        <g:message code="participantDate.extraInfo.label" />
                                    </label>
                                    <span class="property-value">
                                        <textarea name="participantDate.extraInfo" cols="40" rows="5">${fieldValue(bean: participant, field: 'extraInfo')}</textarea>
                                    </span>
                                </div>
                                <div class="${hasErrors(bean: user, field: 'dateTimesNotPresent', 'error')}">
                                    <table id="participant-present">
                                        <g:each in="${SessionDateTime.getTableList(curDate)}" var="dateTimeRow">
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

                            <g:if test="${(Setting.getSetting(Setting.SHOW_CHAIR_DISCUSSANT_POOL, curDate?.event).value == '1') || (Setting.getSetting(Setting.SHOW_LANGUAGE_COACH_PUPIL, curDate?.event).value == '1')}">
                                <fieldset class="form">
                                    <legend><g:message code="participantDate.volunteering.label" /></legend>
                                    <div class="${hasErrors(bean: participant, field: 'participantVolunteering', 'error')} ">
                                        <ul>
                                        <g:set var="volunteering" value="${Volunteering.list()}" />
                                        <g:each in="${participantVolunteering}" var="instance" status="i">
                                            <li>
                                                <input type="hidden" name="ParticipantVolunteering_${i}.id" value="${instance.id}" />
                                                <label>
                                                    <g:message code="participantDate.volunteering.select.label" />
                                                    <g:select from="${volunteering}" name="ParticipantVolunteering_${i}.volunteering.id" optionKey="id" optionValue="description" value="${instance?.volunteering?.id}" noSelection="[null:'']" />
                                                </label>
                                                <label>
                                                    <g:message code="network.label" />
                                                    <g:select from="${networks}" name="ParticipantVolunteering_${i}.network.id" optionKey="id" optionValue="name" value="${instance?.network?.id}" noSelection="[null:'']" />
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
                                                    <g:select from="${volunteering}" name="ParticipantVolunteering_null.volunteering.id" optionKey="id" optionValue="description" noSelection="[null:'']" />
                                                </label>
                                                <label>
                                                    <g:message code="network.label" />
                                                    <g:select from="${networks}" name="ParticipantVolunteering_null.network.id" optionKey="id" optionValue="name" noSelection="[null:'']" />
                                                </label>
                                                <span class="ui-icon ui-icon-circle-minus"></span>
                                            </li>
                                        </ul>
                                    </div>
                                </fieldset>
                            </g:if>
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
                    <input type="hidden" name="max-papers" value="${Setting.getSetting(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION).value}" />
                    <input type="hidden" name="to-be-deleted" class="to-be-deleted" />

                    <g:each in="${user.getPapersSorted().findAll { !it.isDeleted() && (it.date.id == curDate.id) }}" var="paper" status="i">
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
                                <div>
                                    <label class="property-label">
                                        <g:message code="session.label" />
                                    </label>
                                    <span class="property-value">
                                        <span>
                                            <g:if test="${paper.session && !paper.session.deleted}">
                                                <eca:link controller="session" action="show" id="${paper.session.id}">
                                                    ${fieldValue(bean: paper, field: 'session')}
                                                </eca:link>
                                            </g:if>
                                            <g:else>-</g:else>
                                        </span>
                                    </span>
                                </div>
                                <div>
                                    <label class="property-label">
                                        <g:message code="session.state.label" />
                                    </label>
                                    <span class="property-value">
                                        <span>
                                            <g:if test="${paper.session}">
                                                ${fieldValue(bean: paper.session, field: 'state')}
                                            </g:if>
                                            <g:else>-</g:else>
                                        </span>
                                    </span>
                                </div>
                                <div>
                                    <label class="property-label">
                                        <g:message code="participantType.function.label" />
                                    </label>
                                    <span class="property-value">
                                        <span>
                                            <g:if test="${paper.session}">
                                                <g:set var="functionInSession" value="${SessionParticipant.findAllByUserAndSession(user, paper.session)}" />
                                                <g:if test="${functionInSession.size() > 0}">
                                                    ${functionInSession*.type.join(', ')}
                                                </g:if>
                                            </g:if>
                                            <g:else>-</g:else>
                                        </span>
                                    </span>
                                </div>
                            </fieldset>

                            <fieldset class="form">
                                <legend><g:message code="equipment.label" /></legend>
                                <g:each in="${equipmentList}" var="equip">
                                    <div class="${hasErrors(bean: paper, field: 'equipment', 'error')}">
                                        <label class="property-label">
                                            ${equip.equipment}
                                        </label>
                                        <span class="property-value">
                                            <g:checkBox name="Paper_${i}.equipment" value="${equip.id}" checked="${paper.hasEquipmentWithId(equip.id)}" />
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
                                    <g:select from="${networks}" name="Paper_null.networkProposal.id" optionKey="id" optionValue="name" noSelection="${['': ' ']}" />
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
                                        <input type="checkbox" name="Paper_null.equipment" value="${equipment.id}" />
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
                </div>

                <div id="sessions-tab" class="columns">
                    <g:if test="${participant}">
                        <div id="selected-days">
                            <span class="header">
                                <g:message code="participantDay.is.present.label" />
                            </span>

                            <g:if test="${daysPresent.size() == 0}">
                                <span class="not-found">
                                    <g:message code="participantDay.not.found.label" />
                                </span>
                            </g:if>

                            <ol class="list-days-present">
                                <g:each in="${daysPresent}" var="day">
                                    <li><eca:formatText text="${day.toString()}" /></li>
                                </g:each>
                            </ol>

                            <span class="link change-present-days">
                                <a><g:message code="default.change.label" args="${[g.message(code: 'participantDay.multiple.label')]}" /></a>
                            </span>
                        </div>
                    </g:if>

                    <g:each in="${sessions}" var="participantSessionInfo">
                        <div class="participant-session column">
                            <div>
                                <ol class="property-list">
                                    <li>
                                      <span id="session-id-label" class="property-label">#</span>
                                      <span class="property-value" arial-labelledby="session-id-label">${participantSessionInfo.session.id}</span>
                                    </li>
                                    <li>
                                      <span id="code-label" class="property-label">
                                        <g:message code="session.code.label" />
                                      </span>
                                      <span class="property-value" arial-labelledby="code-label">
                                        <eca:formatText text="${participantSessionInfo.session.code}" />
                                      </span>
                                    </li>
                                    <li>
                                      <span id="name-label" class="property-label">
                                        <g:message code="session.name.label" />
                                      </span>
                                      <span class="property-value" arial-labelledby="name-label">
                                        <eca:link controller="session" action="show" id="${participantSessionInfo.session.id}">
                                          <eca:formatText text="${participantSessionInfo.session.name}" />
                                        </eca:link>
                                      </span>
                                    </li>
                                    <li>
                                      <span id="state-label" class="property-label">
                                        <g:message code="session.state.label" />
                                      </span>
                                      <span class="property-value" arial-labelledby="state-label">
                                        <eca:formatText text="${participantSessionInfo.session.state}" />
                                      </span>
                                    </li>
                                    <li>
                                      <span id="type-label" class="property-label">
                                        <g:message code="participantType.function.label" />
                                      </span>
                                      <span class="property-value" arial-labelledby="type-label">
                                        <ol>
                                          <g:each in="${participantSessionInfo.types}" var="type">
                                            <li>${type.toString()}</li>
                                          </g:each>
                                        </ol>
                                      </span>
                                    </li>
                                    <li>
                                        <span id="planned-date-label" class="property-label">
                                            <g:message code="session.planned.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="planned-date-label">
                                            <g:set var="sessionDateTime" value="${participantSessionInfo.session.getPlannedDateTime()}" />
                                            <g:if test="${sessionDateTime}">
                                                <eca:formatText text="${sessionDateTime.day.toString()}" />
                                                (<eca:formatText text="${sessionDateTime.period.toString()}" />)
                                            </g:if>
                                            <g:else>
                                                <g:message code="session.not.planned.label" />
                                            </g:else>
                                        </span>
                                    </li>
                                </ol>
                            </div>
                        </div>
                    </g:each>
                </div>

                <div id="payments-tab" class="columns">
                    <g:set var="amounts" value="${[]}" />
                    <g:set var="hidePayedButton" value="${false}" />

                    <g:each in="${orders}" var="order">
                        <div class="participant-order column">
                            <div>
                                <ol class="property-list">
                                    <li>
                                        <span id="order-id-label" class="property-label">#</span>
                                        <span class="property-value" arial-labelledby="order-id-label">${order.id}</span>
                                    </li>
                                    <li>
                                        <span id="order-code-label" class="property-label">
                                            <g:message code="order.code.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="order-code-label">
                                            <eca:formatText text="${order.orderCode}" />
                                        </span>
                                    </li>
                                    <li>
                                        <span id="amount-label" class="property-label">
                                            <g:message code="order.amount.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="amount-label">
                                            <eca:formatText text="${order.getAmountAsBigDecimal()}" />
                                        </span>
                                    </li>

                                    <g:if test="${order.payed == Order.ORDER_REFUND_OGONE || order.payed == Order.ORDER_REFUND_BANK}">
                                        <li>
                                            <span id="refunded=amount-label" class="property-label">
                                                <g:message code="order.refunded.amount.label" />
                                            </span>
                                            <span class="property-value" arial-labelledby="amount-label">
                                                <eca:formatText text="${order.getRefundedAmountAsBigDecimal()}" />
                                            </span>
                                        </li>

                                        <li>
                                            <span id="refunded-label" class="property-label">
                                                <g:message code="order.refunded.at.label" />
                                            </span>
                                            <span class="property-value" arial-labelledby="refunded-label">
                                                <g:formatDate date="${order.refundedAt}" formatName="default.date.time.format" />
                                            </span>
                                        </li>
                                    </g:if>

                                    <li>
                                        <span id="status-label" class="property-label">
                                            <g:message code="order.status.label" />
                                        </span>

                                        <g:set var="classStatus" value="red" />
                                        <g:if test="${order.payed}">
                                            <g:set var="classStatus" value="green" />
                                        </g:if>
                                        <g:elseif test="${order.paymentMethod != Order.ORDER_OGONE_PAYMENT}">
                                            <g:set var="classStatus" value="orange" />
                                        </g:elseif>

                                        <span class="property-value bold ${classStatus}" arial-labelledby="status-label">
                                            ${order.getStatusText()}

                                            <g:if test="${  (order.paymentMethod != Order.ORDER_OGONE_PAYMENT) &&
                                                            (order.payed == Order.ORDER_NOT_PAYED) &&
                                                            !hidePayedButton && !amounts.contains(order.amount)}">
                                                <span class="inline-button order-set-payed">
                                                    <g:message code="order.set.payed.label" />
                                                </span>
                                                <g:set var="amounts" value="${amounts + [order.amount]}" />
                                            </g:if>

                                            <g:if test="${order.payed == Order.ORDER_PAYED}">
                                                <g:set var="hidePayedButton" value="${true}" />

                                                <g:if test="${order.amount > 0}">
                                                    <sec:ifAnyGranted roles="admin,superAdmin">
                                                        <g:set var="refundBtnClass" value="inline-button order-refund-payment"/>
                                                        <g:if test="${Setting.getSetting(Setting.SHOW_REFUND_DIALOG).booleanValue}">
                                                            <g:set var="refundBtnClass" value="${refundBtnClass} refund-dialog"/>
                                                        </g:if>
                                                        <span class="${refundBtnClass}" data-order-id="${order.id}"
                                                              data-amount="${order.standardAmountToBeRefunded}">
                                                            <g:message code="order.refund.payment.label" />
                                                        </span>
                                                    </sec:ifAnyGranted>
                                                </g:if>
                                            </g:if>
                                        </span>
                                    </li>
                                    <li>
                                        <span id="method-label" class="property-label">
                                            <g:message code="order.method.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="method-label">
                                            ${order.getPaymentMethodText()}
                                        </span>
                                    </li>
                                    <li>
                                        <span id="created-label" class="property-label">
                                            <g:message code="order.created.at.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="created-label">
                                            <g:formatDate date="${order.createdAt}" formatName="default.date.time.format" />
                                        </span>
                                    </li>
                                    <li>
                                        <span id="updated-label" class="property-label">
                                            <g:message code="order.updated.at.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="updated-label">
                                            <g:formatDate date="${order.updatedAtPayWay}" formatName="default.date.time.format" />
                                        </span>
                                    </li>
                                    <li>
                                        <span id="description-label" class="property-label">
                                            <g:message code="order.description.label" />
                                        </span>
                                        <span class="property-value" arial-labelledby="description-label">
                                            <eca:formatText text="${order.description}" />
                                        </span>
                                    </li>
                                </ol>
                            </div>
                        </div>
                    </g:each>
                </div>

                <div id="emails-tab">
                    <h3><g:message code="email.not.yet.sent.label" /></h3>

                    <div id="emails-not-sent">
                        <g:if test="${emailsNotSent.size() == 0}">
                            <span><g:message code="default.search.empty.message" /></span>
                        </g:if>
                        <g:each in="${emailsNotSent}" var="email" >
                            <span class="emailHeader"><a href="#">
                                ${email.subject} (<g:message code="email.date.created.label" />: <g:formatDate date="${email.dateTimeCreated}" formatName="default.date.time.format" />)
                            </a></span>
                            <ol class="property-list email-content">
                                <input type="hidden" name="email-id" value="${email.id}" />
                                <li>
                                    <span id="original-sent-label" class="property-label">
                                        <g:message code="email.original.sent.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="original-sent-label">
                                        <span class="inline-button resend-email">
                                            <g:message code="email.send.now.label" />
                                        </span>
                                    </span>
                                </li>
                                <li>
                                    <span id="copies-sent-label" class="property-label">
                                        <g:message code="email.copies.sent.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="copies-sent-label"></span>
                                </li>
                                <li>
                                    <span id="from-label" class="property-label">
                                        <g:message code="email.from.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="from-label"></span>
                                </li>
                                <li>
                                    <span id="subject-label" class="property-label">
                                        <g:message code="email.subject.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="subject-label"></span>
                                </li>
                                <li>
                                    <span id="body-label" class="property-label">
                                        <g:message code="email.body.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="body-label"></span>
                                </li>
                            </ol>
                        </g:each>
                    </div>

                    <h3><g:message code="email.sent.label" /></h3>

                    <div id="emails-sent">
                        <g:if test="${emailsSent.size() == 0}">
                            <span><g:message code="default.search.empty.message" /></span>
                        </g:if>
                        <g:each in="${emailsSent}" var="email" >
                            <span class="emailHeader"><a href="#">
                                ${email.subject} (<g:message code="email.date.sent.label" />: <g:formatDate date="${email.dateTimeSent}" formatName="default.date.time.format" />)
                            </a></span>
                            <ol class="property-list email-content">
                                <input type="hidden" name="email-id" value="${email.id}" />
                                <li>
                                    <span id="original-sent-label" class="property-label">
                                        <g:message code="email.original.sent.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="original-sent-label">
                                        <span class="inline-button resend-email">
                                            <g:message code="email.resend.label" />
                                        </span>
                                    </span>
                                </li>
                                <li>
                                    <span id="copies-sent-label" class="property-label">
                                        <g:message code="email.copies.sent.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="copies-sent-label"></span>
                                </li>
                                <li>
                                    <span id="from-label" class="property-label">
                                        <g:message code="email.from.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="from-label"></span>
                                </li>
                                <li>
                                    <span id="subject-label" class="property-label">
                                        <g:message code="email.subject.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="subject-label"></span>
                                </li>
                                <li>
                                    <span id="body-label" class="property-label">
                                        <g:message code="email.body.label" />
                                    </span>
                                    <span class="property-value" arial-labelledby="body-label"></span>
                                </li>
                            </ol>
                        </g:each>
                    </div>

                    <input type="hidden" name="user-id" value="${user.id}" />
                    <div id="resend-registration-email-conainer">
                        <span class="inline-button resend-registration-email">
                            <g:message code="email.resend.registration.label" />
                        </span>
                    </div>
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
                <input type="submit" name="btn_save_close" class="btn_save_close" value="${message(code: 'default.button.save.close.label')}" />
                <g:if test="${participant}">
                    <input type="button" name="btn_add" class="btn_add" value="${message(code: 'default.add.label', args: [message(code: 'paper.label').toLowerCase()])}" />
                    <input type="button" name="btn_add_order" class="btn_add_order" value="${message(code: 'default.add.label', args: [message(code: 'order.label').toLowerCase()])}" />
                </g:if>
            </fieldset>
        </form>

        <g:if test="${participant}">
            <div id="edit-days" class="info">
                <input type="hidden" name="user-id" value="${user.id}" />
                <form method="post" action="#">
                    <fieldset class="form">
                        <g:each in="${Day.list()}" var="day">
                            <div>
                                <span class="property-label">
                                    <g:checkBox name="day" value="${day.id}" id="edit-day-${day.id}" checked="${user.daysPresent.find { it.day.id == day.id }}" />
                                </span>
                                <label class="property-value" for="edit-day-${day.id}">
                                    ${day.toString()}
                                </label>
                            </div>
                        </g:each>
                    </fieldset>
                </form>
            </div>

            <div id="refund-payment" class="info">
                <form method="post" action="#">
                    <fieldset class="form">
                        <div>
                            <label class="property-label">
                                <g:message code="order.refunded.amount.label" />
                            </label>
                            <span class="property-value">
                                <g:field type="text" name="amount" required="required" value="" />
                            </span>
                        </div>
                    </fieldset>
                </form>
            </div>

            <div id="new-order" class="info">
            <form method="post" action="../newOrder" id="new-order-form">
                <fieldset class="form">
                    <input type="hidden" name="participantId" value="${participant.id}" />
                    <div>
                        <label class="property-label">
                            <g:message code="order.amount.label" />
                            <span class="required-indicator">*</span>
                        </label>
                        <span class="property-value">
                            <g:field type="text" name="amount" required="required" value="" />
                        </span>
                    </div>
                    <div>
                        <span class="property-label"></span>
                        <span class="property-value">
                            <g:radio id="bank-method" name="method" value="${Order.ORDER_BANK_PAYMENT}" checked="${true}" />
                            <label class="property-value" for="bank-method">
                                <g:message code="order.method.bank.label" />
                            </label>
                        </span>
                    </div>
                    <div>
                        <span class="property-label"></span>
                        <span class="property-value">
                            <g:radio id="cash-method" name="method" value="${Order.ORDER_CASH_PAYMENT}" />
                            <label class="property-value" for="cash-method">
                                <g:message code="order.method.cash.label" />
                            </label>
                        </span>
                    </div>
                    <div>
                        <label class="property-label">
                            <g:message code="order.description.label" />
                            <span class="required-indicator">*</span>
                        </label>
                        <span class="property-value">
                            <input type="text" name="description" value="${curDate.getShortNameAndYear() + ' payment'}" maxlength="100" />
                        </span>
                    </div>
                    <div>
                        <span class="property-label"></span>
                        <span class="property-value">
                            <g:radio id="status-not-payed" name="status" value="${Order.ORDER_NOT_PAYED}" checked="${true}" />
                            <label class="property-value" for="status-not-payed">
                                <g:message code="order.status.not.payed.label" />
                            </label>
                        </span>
                    </div>
                    <div>
                        <span class="property-label"></span>
                        <span class="property-value">
                            <g:radio id="status-payed" name="status" value="${Order.ORDER_PAYED}" />
                            <label class="property-value" for="status-payed">
                                <g:message code="order.status.payed.label" />
                            </label>
                        </span>
                    </div>
                </fieldset>
            </form>
        </div>
        </g:if>
    </body>
</html>
