<%@ page import="org.iisg.eca.domain.AgeRange; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order; org.iisg.eca.domain.PaperReview; org.iisg.eca.domain.ReviewCriteria; org.iisg.eca.domain.User" %>
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
                                <a href="${link.replace('[EMAIL]', user.id)}" target="_blank">
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
            <g:if test="${Setting.getSetting(Setting.SHOW_DEPARTMENT).booleanValue}">
                <div class="${hasErrors(bean: user, field: 'department', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.department.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="user.department" maxlength="50" value="${fieldValue(bean: user, field: 'department')}" />
                    </span>
                </div>
            </g:if>
            <g:if test="${Setting.getSetting(Setting.SHOW_EDUCATION).booleanValue}">
                <div class="${hasErrors(bean: user, field: 'education', 'error')} required">
                    <label class="property-label">
                        <g:message code="user.education.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="user.education" maxlength="50" value="${fieldValue(bean: user, field: 'education')}" />
                    </span>
                </div>
            </g:if>
            <div class="${hasErrors(bean: user, field: 'email', 'error')} required">
                <label class="property-label">
                    <g:message code="user.email.label" />
                    <span class="required-indicator">*</span>
                </label>
                <span class="property-value">
                    <input type="email" name="user.email" required="required" maxlength="100" value="${fieldValue(bean: user, field: 'email')}"
                           data-user="${user.id}" data-back="${curPageId}" />
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
            <g:if test="${Setting.getSetting(Setting.SHOW_OPT_IN).booleanValue}">
                <div class="${hasErrors(bean: user, field: 'optIn', 'error')}">
                    <label class="property-label">
                        <g:message code="user.optIn.label" />
                    </label>
                    <span class="property-value">
                        <g:checkBox name="user.optIn" checked="${user.optIn}" />
                    </span>
                </div>
            </g:if>
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
            <g:if test="${Setting.getSetting(Setting.SHOW_DIETARY_WISHES, curDate?.event).value == '1'}">
                <div class="${hasErrors(bean: user, field: 'dietaryWishes', 'error')}">
                    <label class="property-label">
                        <g:message code="user.dietaryWishes.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="user.dietaryWishes" from="${User.DIETARY_WISHES_IDS}"
                                  valueMessagePrefix="user.dietaryWishes" value="${user.dietaryWishes}"
                                  noSelection="${['': ' ']}" />
                    </span>
                </div>
                <div class="${hasErrors(bean: user, field: 'otherDietaryWishes', 'error')}">
                    <label class="property-label">
                        <g:message code="user.otherDietaryWishes.label" />
                    </label>
                    <span class="property-value">
                        <input type="text" name="user.otherDietaryWishes" maxlength="255"
                               value="${fieldValue(bean: user, field: 'otherDietaryWishes')}" />
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

        <g:if test="${participant && Setting.getSetting(Setting.SHOW_AGE_RANGE).booleanValue}">
            <fieldset class="form">
                <legend><g:message code="participantDate.personal.registration.label" /></legend>
                <div class="${hasErrors(bean: user, field: 'country', 'error')} required">
                    <label class="property-label">
                        <g:message code="participantDate.ageRange.label" />
                    </label>
                    <span class="property-value">
                        <g:select name="participantDate.ageRange.id" from="${AgeRange.list()}" optionKey="id"
                                  value="${participant.ageRange?.id}" noSelection="${[null:' ']}" />
                    </span>
                </div>
            </fieldset>
        </g:if>
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

				<g:if test="${Setting.getSetting(Setting.SHOW_ALLOW_LATE_PAYMENT, curDate?.event).value == '1'}">
					<div class="${hasErrors(bean: participant, field: 'allowLatePayment', 'error')}">
						<label class="property-label">
							<g:message code="participantDate.allowLatePayment.label" />
						</label>
						<span class="property-value">
							<g:checkBox name="participantDate.allowLatePayment" checked="${participant.allowLatePayment}" />
						</span>
					</div>
				</g:if>

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

                <legend><g:message code="participantDate.presence.label" /></legend>
                <br />
                <label>
                    <g:message code="participantDate.presence.description.label" />
                </label>
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
