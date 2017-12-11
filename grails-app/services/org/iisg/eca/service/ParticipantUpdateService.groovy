package org.iisg.eca.service

import org.iisg.eca.domain.*

import org.codehaus.groovy.grails.web.metaclass.BindDynamicMethod
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.commons.metaclass.GroovyDynamicMethodsInterceptor

import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * Service specifically aimed at updating a participant
 */
class ParticipantUpdateService {
	def pageInformation
	def passwordService
	def emailCreationService
	def emailService

	/**
	 * Make sure we can use the bind method within all methods of this service
	 */
	ParticipantUpdateService() {
		GroovyDynamicMethodsInterceptor i = new GroovyDynamicMethodsInterceptor(this)
		i.addDynamicMethodInvocation(new BindDynamicMethod())
	}

	/**
	 * The update method to call with the user and participant to be updated
	 * and the params containing the to be updated data
	 * @param user The user in question
	 * @param participant The participant in question
	 * @param params The params in question
	 * @return Whether the update was successful
	 */
	boolean update(User user, ParticipantDate participant, GrailsParameterMap params) {
		try {
			// Make sure the title is not deleted
			Title.addTitleIfNotExists(user.title)
			updateUser(user, params)

			// Should we update the participant data as well, or add a participant to the current event date?
			if (participant || params['add-to-date']?.equals('add')) {
				if (participant) {
					updateParticipantForEventDate(user, participant, params)
				}
				else {
					participant = user.createParticipantForDate(pageInformation.date)
					participant.save(flush: true, failOnError: true)
				}

				user.save(failOnError: true)
				participant.save(flush: true, failOnError: true)
			}
			else {
				user.save(flush: true, failOnError: true)
			}

			// Now that the session is flushed, check whether we also have to send a new password to this user
			if (user.sendNewPassword) {
				passwordService.sendPassword(user)
				user.save(flush: true, failOnError: true)
			}

			return true
		}
		catch (Exception e) {
			return false
		}
	}

	/**
	 * Updates the user data
	 * @param user The user in question
	 * @param params The data to update the user with
	 */
	private void updateUser(User user, GrailsParameterMap params) {
		bindData(user, params, [
				include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
				          'department', 'education', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'cv',
				          'extraInfo', 'emailDiscontinued']
		], "user")
	}

	/**
	 * Updates participant data for the current event date
	 * @param user The user in question
	 * @param participant The participant in question
	 * @param params The data to update the user and the participant with
	 */
	private void updateParticipantForEventDate(User user, ParticipantDate participant, GrailsParameterMap params) {
		updateParticipantDate(participant, params)
		updateUserNotPresent(user, params)

		// Update papers
		int i = 0
		while (params["Paper_${i}"]) {
			Paper paper = user.papers.find { it.id == params.long("Paper_${i}.id") }
			updatePaper(paper, user, params, i)
			i++
		}

		// Soft delete papers in the 'to-be-deleted' list
		String[] ids = params["to-be-deleted"].toString().split(';')
		ids.each { String idToDelete ->
			if (idToDelete.isLong()) {
				Long paperId = idToDelete.toLong()
				Paper paperToDelete = user.papers.find { it.id == paperId }
				paperToDelete?.softDelete()
				paperToDelete?.save(flush: true)
			}
		}
	}

	/**
	 * Specifically update the participant
	 * @param participant The participant in question
	 * @param params The data to update the participant with
	 */
	private void updateParticipantDate(ParticipantDate participant, GrailsParameterMap params) {
		// If all accompanying persons were deleted by the user, then actually delete them instead of doing nothing
		if (!params['participantDate.accompanyingPersons']) {
			params['participantDate.accompanyingPersons'] = []
		}

		bindData(participant, params, [
				include: ['ageRange', 'invitationLetter', 'invitationLetterSent', 'lowerFeeRequested', 'lowerFeeAnswered',
				          'lowerFeeText', 'student', 'studentConfirmed', 'award', 'state', 'feeState', 'extraInfo',
				          'accompanyingPersons']
		], "participantDate")

		participant.extras?.clear()
		params.list("participantDate.extras").each { String extraId ->
			participant.addToExtras(Extra.get(extraId))
		}

		updateParticipantVolunteering(participant, params)
	}

	/**
	 * Updates the volunteering options chosen by this participant
	 * @param participant The participant in question
	 * @param params The volunteering data to update the participant with
	 */
	private void updateParticipantVolunteering(ParticipantDate participant, GrailsParameterMap params) {
		Set<ParticipantVolunteering> toBeDeleted = []
		toBeDeleted += participant.participantVolunteering

		int i = 0
		while (params["ParticipantVolunteering_${i}"]) {
			if (params["ParticipantVolunteering_${i}.id"].toString().isLong()) {
				Long id = params.long("ParticipantVolunteering_${i}.id")
				toBeDeleted.removeAll { it.id == id }
				ParticipantVolunteering pv =  participant.participantVolunteering.find { it.id == id }
				bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}".toString())
			}
			else {
				ParticipantVolunteering pv = new ParticipantVolunteering()
				bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}".toString())
				participant.addToParticipantVolunteering(pv)
			}

			i++
		}

		// Whatever is left should be deleted from the participant
		toBeDeleted.each { participant.removeFromParticipantVolunteering(it) }
	}

	/**
	 * Update the session date/times the user is not present
	 * @param user The user in question
	 * @param params The session date/times data to update the user with
	 */
	private void updateUserNotPresent(User user, GrailsParameterMap params) {
		// We are only interested in the dates/times the participant is NOT present
		List<SessionDateTime> sessionDateTimes = SessionDateTime.list()
		params.list('present').each { String dateTimeId ->
			sessionDateTimes.removeAll { dateTimeId.isLong() && (it.id == dateTimeId.toLong()) }
		}

		// First remove all old date/times he/she is present now
		List<SessionDateTime> dateTimesNotPresent = []
		dateTimesNotPresent += user.dateTimesNotPresent
		dateTimesNotPresent.each { SessionDateTime sessionDateTime ->
			if (!sessionDateTimes*.id.contains(sessionDateTime.id)) {
				user.removeFromDateTimesNotPresent(sessionDateTime)
			}
		}

		user.dateTimesNotPresent.addAll(sessionDateTimes)
	}

	/**
	 * Update a specific paper of the user for the current event date
	 * @param paper The paper to be updated. Is <code>null</code> if a new paper is to be created
	 * @param user The user of the paper to be updated
	 * @param params The paper data to update the user with
	 * @param i The counter, to identify the paper data from <code>params</code> to update the paper with
	 */
	private void updatePaper(Paper paper, User user, GrailsParameterMap params, int i) {
		paper = (paper) ?: new Paper(date: pageInformation.date)
		user.addToPapers(paper)

		bindData(paper, params, [
				include: ['title', 'abstr', 'typeOfContribution', 'coAuthors', 'state', 'reviewComment', 'comment',
						  'sessionProposal', 'proposalDescription', 'networkProposal', 'equipmentComment']
		], "Paper_$i".toString())

		CommonsMultipartFile file = (CommonsMultipartFile) params["Paper_${i}.file"]
		if (file?.size > 0) {
			paper.fileSize = file.size
			paper.contentType = file.contentType
			paper.fileName = file.originalFilename
			paper.file = file.bytes
		}

		paper.equipment?.clear()
		params.list("Paper_${i}.equipment").each { String equipmentId ->
			paper.addToEquipment(Equipment.get(equipmentId))
		}

		updatePaperReviews(paper, params, i)

		paper.save(flush: true, failOnError: true)
	}

	/**
	 * Update the paper reviews of a specific paper of the user for the current event date
	 * @param paper The paper of which the reviews should be updated
	 * @param params The paper reviews data to update
	 * @param i The counter, to identify the paper data from <code>params</code> to update the paper reviews with
	 */
	private void updatePaperReviews(Paper paper, GrailsParameterMap params, int i) {
		Set<PaperReview> toBeDeleted = []
		if ((paper.reviews != null) && (paper.reviews.size() > 0)) {
			toBeDeleted += paper.reviews
		}

		int j = 0
		while (params["PaperReview_${i}_${j}"]) {
			if (params["PaperReview_${i}_${j}.id"].toString().isLong()) {
				Long id = params.long("PaperReview_${i}_${j}.id")
				toBeDeleted.removeAll { it.id == id }
			}
			else {
				PaperReview paperReview = new PaperReview()
				bindData(paperReview, params, [include: ['reviewer']], "PaperReview_${i}_${j}".toString())

				// Make sure the reviewer is not already added before and is not the paper author
				PaperReview doublePaperReview = paper.reviews.find { it.reviewer.id == paperReview.reviewer.id }
				if (doublePaperReview) {
					toBeDeleted.remove(doublePaperReview)
				}
				else if (paperReview.reviewer.id != paper.user.id) {
					paperReview.paper = paper
					paper.addToReviews(paperReview)

					SentEmail email = emailCreationService.createPaperReviewerEmail(paperReview)
					emailService.sendEmail(email, true, true)
				}
			}

			j++
		}

		// Whatever is left should be deleted
		toBeDeleted.each { paper.removeFromReviews(it) }
	}
}
