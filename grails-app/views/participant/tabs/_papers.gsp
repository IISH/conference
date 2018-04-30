<%@ page import="org.iisg.eca.domain.AgeRange; org.iisg.eca.domain.PaperState; org.iisg.eca.domain.Equipment; org.iisg.eca.domain.Network; org.iisg.eca.domain.Volunteering; org.iisg.eca.domain.Day; org.iisg.eca.domain.SessionDateTime; org.iisg.eca.domain.Setting; org.iisg.eca.domain.Title; org.iisg.eca.domain.FeeState; org.iisg.eca.domain.ParticipantState; org.iisg.eca.domain.Extra; org.iisg.eca.domain.Country; org.iisg.eca.domain.SessionParticipant; org.iisg.eca.domain.Order; org.iisg.eca.domain.PaperReview; org.iisg.eca.domain.ReviewCriteria" %>
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
                <g:if test="${paper.id}">
                    <div>
                        <label class="property-label">#</label>
                        <span class="property-value">${paper.id}</span>
                    </div>
                </g:if>
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
                <g:if test="${Setting.getSetting(Setting.SHOW_PAPER_TYPES, curDate?.event).value == '1'}">
                    <div class="${hasErrors(bean: paper, field: 'type', 'error')}">
                        <label class="property-label">
                            <g:message code="paper.type.label" />
                        </label>
                        <span class="property-value">
                            <g:select from="${paperTypes}" name="Paper_${i}.type.id" optionKey="id" optionValue="type" value="${paper.type?.id}" noSelection="${['': ' ']}" />
                            <g:if test="${Setting.getSetting(Setting.SHOW_OPTIONAL_PAPER_TYPE, curDate?.event).value == '1'}">
                                <input type="text" name="Paper_${i}.differentType" value="${fieldValue(bean: paper, field: 'differentType')}" />
                            </g:if>
                        </span>
                    </div>
                </g:if>
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
                <div class="${hasErrors(bean: paper, field: 'sortOrder', 'error')} ">
                    <label class="property-label">
                        <g:message code="paper.sortOrder.label" />
                    </label>
                    <span class="property-value">
                        <input type="number" name="Paper_${i}.sortOrder" value="${paper?.sortOrder}" />
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

            <g:if test="${Setting.getSetting(Setting.ENABLE_PAPER_REVIEWS, curDate?.event).value == '1'}">
                <fieldset class="form">
                    <legend><g:message code="paper.reviews.label" /></legend>
                    <div class="${hasErrors(bean: paper, field: 'reviewComment', 'error')}">
                        <label class="property-label">
                            <g:message code="paper.reviewComment.label" />
                        </label>
                        <span class="property-value">
                            <textarea name="Paper_${i}.reviewComment" cols="40" rows="5">${fieldValue(bean: paper, field: 'reviewComment')}</textarea>
                        </span>
                    </div>
                    <div>
                        <label class="property-label">
                            <g:message code="paper.reviewers.label" />
                        </label>
                        <ul class="property-value">
                            <g:set var="paperReviews" value="${PaperReview.findAllByPaper(paper)}" />
                            <g:each in="${paperReviews}" var="instance" status="ipr">
                                <li>
                                    <input type="hidden" name="PaperReview_${i}_${ipr}.id" value="${instance.id}" />
                                    <span>
                                        <a class="change-paper-review" href="#">${instance.reviewer}</a>
                                        <g:if test="${instance.avgScore}">
                                            <span class="avgReviewScore">
                                                (<g:message code="paper.avgReviewScore.label" />: <span class="avg-score">${instance.avgScore}</span>)
                                            </span>
                                        </g:if>
                                    </span>
                                    <span class="ui-icon ui-icon-circle-minus"></span>

                                    <input type="hidden" name="PaperReview_${i}_${ipr}.review" value="${instance.review}" />
                                    <input type="hidden" name="PaperReview_${i}_${ipr}.reviewComments" value="${instance.comments}" />
                                    <input type="hidden" name="PaperReview_${i}_${ipr}.reviewAward" value="${instance.award}" />
                                    <g:each in="${instance.scores}" var="score">
                                        <input type="hidden" name="PaperReview_${i}_${ipr}.criteria_${score.criteria.id}" value="${score.score}" />
                                    </g:each>
                                </li>
                            </g:each>
                            <li class="add">
                                <span class="ui-icon ui-icon-circle-plus"></span>
                                <g:message code="default.add.label" args="[g.message(code: 'paper.reviewer.label').toLowerCase()]" />
                            </li>
                            <li class="hidden">
                                <input type="hidden" name="PaperReview_${i}_null.id" />
                                <eca:usersAutoComplete name="PaperReview_${i}_null.reviewer.id" labelValue="" idValue="" queryName="allReviewersConfirmed" required="required" />
                                <span class="ui-icon ui-icon-circle-minus"></span>
                            </li>
                        </ul>
                    </div>
                </fieldset>
            </g:if>

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
                <g:if test="${Setting.getSetting(Setting.SHOW_SESSION_PROPOSAL, curDate?.event).value == '1'}">
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
                </g:if>
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
            <g:if test="${Setting.getSetting(Setting.SHOW_PAPER_TYPES, curDate?.event).value == '1'}">
                <div class="${hasErrors(bean: paper, field: 'type', 'error')}">
                    <label class="property-label">
                        <g:message code="paper.type.label" />
                    </label>
                    <span class="property-value">
                        <g:select from="${paperTypes}" name="Paper_null.type.id" optionKey="id" optionValue="type" noSelection="${['': ' ']}" />
                        <g:if test="${Setting.getSetting(Setting.SHOW_OPTIONAL_PAPER_TYPE, curDate?.event).value == '1'}">
                            <input type="text" name="Paper_null.differentType" value="${fieldValue(bean: paper, field: 'differentType')}" />
                        </g:if>
                    </span>
                </div>
            </g:if>
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