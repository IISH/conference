package org.iisg.eca.controller

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.FeeState
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.PaperState
import org.iisg.eca.domain.Volunteering
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDay
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.ParticipantVolunteering

import org.iisg.eca.domain.payway.Order

import org.iisg.eca.utils.PaymentQueries
import org.iisg.eca.utils.PaymentStatistic

import grails.converters.JSON
import grails.validation.ValidationException

import groovy.sql.Sql
import org.hibernate.impl.SessionImpl
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * Controller responsible for handling requests on participants
 */
class ParticipantController {
    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Service taking care of participant information
     */
    def participantService

    /**
     * Service taking care of participant in session related information
     */
    def participantSessionService

    /**
     * Service taking care of exporting the participants paper
     */
    def exportService

    /**
     * Service that takes care of sending emails
     */
    def emailService

    /**
     * Data source of the PayWay and conference databases
     */
    def dataSource_payWay

    def sessionFactory

    def sessionFactory_payWay

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all invitations requests made by participants
     */
    def invitations() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all lower fee requests made by participants
     */
    def lowerFee() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all participants that showed interest for one of the extras
     */
    def extras() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all participants with no discontinued email
     */
    def listAll() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all participants who signed up for the current event date
     * and offers users to filter the results
     */
    def list() {
        // When the page is first requested, show no results yet: start with an empty map
        Map participants = [:]

        // If the user clicked on search, let the participant service get all participants from the database
        if (params['filter-type']) {
            participants = participantService.getParticipants(params)
        }

        // Return a map with participants, the characters (alphabet) and
        // participant states including the number of participants for each state
        render(view: "list", model: [   participants:   participants,
                                        alphabet:       participants.keySet(),
                                        states:         participantService.getParticipantCounts()])
    }

    /**
     * Add a new participant to the current event date
     */
    def add() {
        if (request.post) {
            // We need an email address, check for the email
            if (!params.email) {
                flash.error = true
                flash.message = g.message(code: 'default.no.email.message')
                return
            }

            ParticipantDate participant = null
            User user = User.findByEmail(params.email)

            if (!user) {
                user = new User(lastName: "n/a", firstName: "n/a", email: params.email)
            }
            else {
                // Does the participant exist in the database already
                participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

                if (participant) {
                    flash.message = g.message(code: 'default.exists.message', args: [user.toString(), g.message(code: 'participantDate.label')])
                    redirect(uri: eca.createLink(action: 'show', id: user.id, noBase: true))
                    return
                }

                // Look for the participant, maybe he is deleted
                ParticipantDate.withoutHibernateFilters {
                    participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                }

                // If we found the filtered participant, undo the deletion
                if (participant) {
                    participant.deleted = false
                }
            }

            // This user is not a participant yet, but the user indicated he/she wants to make him/her one
            if (!participant) {
                participant = new ParticipantDate(user: user, state: ParticipantState.get(ParticipantState.NEW_PARTICIPANT), feeState: FeeState.get(FeeState.NO_FEE_SELECTED))
            }

            if (user.save(flush: true) && participant.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'participantDate.label'), participant.toString()])
                redirect(uri: eca.createLink(action: 'show', id: user.id, noBase: true))
                return
            }
            else {
                return [participant: participant]
            }
        }
    }

    /**
     * Shows all participant information and allows for editing
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Try to look up this user as a participant for the current event date
        ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

        // Already collect participant ids in the case of an error
        List participantIds = participantService.getParticipantsWithFilters(params).collect { it[0] }
        List sessions = participantSessionService.getSessionsForParticipant(participant)
        List daysPresent = ParticipantDay.findAllDaysOfUser(user)
        List orders = Order.findAllOrdersOfUserLastYear(user)

        // Obtain the emails
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -18)

        List emailsNotSent = SentEmail.withCriteria {
            eq('user.id', user.id)
            isNull('dateTimeSent')
            order('dateTimeCreated', 'desc')
        }
        List emailsSent = SentEmail.withCriteria {
            eq('user.id', user.id)
            ge('dateTimeSent', cal.getTime())
            order('dateTimeSent', 'desc')
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            try {
                // Save all user information
                bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                        'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'cv',
                        'extraInfo', 'emailDiscontinued']], "User")
                user.save(failOnError: true)

                if (!participant && params['add-to-date']?.equals('add')) {
                    // Try to find out if this participant has been deleted before or is filtered for some other reason
                    ParticipantDate.withoutHibernateFilters {
                        participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                    }

                    // If we found the filtered participant, undo the deletion
                    if (participant) {
                        participant.deleted = false
                    }
                    else {
                        // This user is not a participant yet, but the user indicated he/she wants to make him/her one
                        participant = new ParticipantDate(user: user, state: ParticipantState.get(0), feeState: FeeState.get(0))
                    }

                    participant.save(failOnError: true)
                }
                else if (participant) {
                    // He/she is a participant, save all of that information as well
                    bindData(participant, params, [include: ['invitationLetter', 'invitationLetterSent', 'lowerFeeRequested',
                            'lowerFeeAnswered', 'lowerFeeText', 'student', 'studentConfirmed', 'award',
                            'state', 'feeState', 'extraInfo']], "ParticipantDate")
                    participant.save(failOnError: true)

                    // Remove all extras the participant is interested in and save all new information
                    participant.extras.clear()
                    params."ParticipantDate.extras".each { extraId ->
                        participant.addToExtras(Extra.get(extraId))
                    }
                    participant.save(failOnError: true)

                    // Remove all date/times the user is not present and save all new information
                    // However, we are only interested in the dates/times the participant is NOT present
                    user.dateTimesNotPresent.clear()
                    user.save(failOnError: true)
                    List<SessionDateTime> sessionDateTimes = SessionDateTime.list()
                    params.present.each { dateTimeId ->
                        sessionDateTimes.remove(sessionDateTimes.find { dateTimeId.isLong() && (it.id == dateTimeId.toLong()) })
                    }
                    user.dateTimesNotPresent.addAll(sessionDateTimes)
                    user.save(failOnError: true)

	                // Remove all accompanying persons from the participant and save all new information
	                int i = 0
	                participant.accompanyingPersons.clear()
	                participant.save(failOnError: true, flush: true)
	                while (params["AccompanyingPerson_${i}"]) {
		                participant.accompanyingPersons << params["AccompanyingPerson_${i}"]
		                i++
	                }
	                participant.save(failOnError: true)

                    // Remove all volunteering offers from the participant and save all new information
                    i = 0
                    participant.participantVolunteering.clear()
                    participant.save(failOnError: true, flush: true)
                    while (params["ParticipantVolunteering_${i}"]) {
                        ParticipantVolunteering pv = new ParticipantVolunteering()
                        bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}")
                        i++

                        if (!participant.participantVolunteering.find { it.equalsWithoutParticipant(pv) }) {
                            participant.addToParticipantVolunteering(pv)
                        }
                    }
                    participant.save(failOnError: true)

                    // Check which papers have to be deleted, try to soft delete them
                    String[] ids = params["to-be-deleted"].split(';')
                    ids.each { idToDelete ->
                        if (idToDelete.isLong()) {
                            Paper paperToDelete = Paper.findById(idToDelete.toLong())
                            paperToDelete?.softDelete()
                            paperToDelete?.save(failOnError: true)
                        }
                    }

                    // Now try to save the paper information, one paper at the time
                    i = 0
                    while (params["Paper_${i}"]) {
                        Paper paper = null

                        // Try to look up the paper in the database
                        if (params["Paper_${i}.id"]?.isLong()) {
                            paper = Paper.findByUserAndId(user, params.long("Paper_${i}.id"))
                        }

                        // Not found, then create a new paper
                        if (!paper) {
                            paper = new Paper(state: PaperState.get(0))
                            user.addToPapers(paper)
                            paper.setDate(pageInformation.date)
                        }

                        // Make sure that the paper can be saved first
                        bindData(paper, params, [include: ['title', 'abstr']], "Paper_${i}")
                        paper.save(failOnError: true, flush: true)

                        // Also save all other paper information
                        bindData(paper, params, [include: ['coAuthors', 'state', 'comment',
                                'sessionProposal', 'proposalDescription', 'networkProposal',
                                'equipmentComment']], "Paper_${i}")
                        paper.save(failOnError: true)

                        // If a paper file is uploaded, save all file information
                        CommonsMultipartFile file = (CommonsMultipartFile) params["Paper_${i}.file"]
                        if (file?.size > 0) {
                            paper.fileSize = file.size
                            paper.contentType = file.contentType
                            paper.fileName = file.originalFilename
                            paper.file = file.bytes
                        }
                        paper.save(failOnError: true)

                        // Remove all equipment and save all new equipment information for this paper
                        paper.equipment?.clear()
                        params["Paper_${i}.equipment"].each { equipmentId ->
                            if (equipmentId.isLong()) {
                                paper.addToEquipment(Equipment.get(equipmentId.toLong()))
                            }
                        }

                        // Save the paper
                        paper.save(failOnError: true)
                        i++
                    }

                    // Save the user and the participant
                    user.save(failOnError: true)
                    participant.save(failOnError: true)
                }

                // We arrived here, so everything should be fine
                // Go back to the previous back with an update message
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'participantDate.label'), user.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(action: 'list', noBase: true))
                    return
                }
            }
            catch (ValidationException ve) {
                // Validation failed, just send back the page and show all errors
                render(view: "form", model: [   user: user,
                                                participant: participant,
                                                papers: Paper.findAllByUser(user),
                                                volunteering: Volunteering.list(),
                                                networks: Network.list(),
                                                paperStates: PaperState.list(),
                                                equipmentList: Equipment.list(),
                                                participantIds: participantIds,
                                                sessions: sessions,
                                                daysPresent: daysPresent,
                                                orders: orders,
                                                emailsSent: emailsSent,
                                                emailsNotSent: emailsNotSent
                ])
            }
        }

        // Show the participant data
        render(view: "form", model: [   user: user,
                                        participant: participant,
                                        papers: Paper.findAllByUser(user),
                                        volunteering: Volunteering.list(),
                                        networks: Network.list(),
                                        paperStates: PaperState.list(),
                                        equipmentList: Equipment.list(),
                                        participantIds: participantIds,
                                        sessions: sessions,
                                        daysPresent: daysPresent,
                                        orders: orders,
                                        emailsSent: emailsSent,
                                        emailsNotSent: emailsNotSent
        ])
    }

    /**
     * Shows the payment status for all participants
     */
    def payments() {
        Sql sql = new Sql(dataSource_payWay)

        String dbName = ((SessionImpl) sessionFactory.currentSession).connection().catalog
        String dbNamePayWay = ((SessionImpl) sessionFactory_payWay.currentSession).connection().catalog

        render(view: "payments", model: [
                paymentsList:                   sql.rows(PaymentQueries.PAYMENT_LIST
                                                        .replace('db-name-payway', dbNamePayWay)
                                                        .replace('db-name', dbName),
                                                        [dateId: pageInformation.date.id]),

                paymentMethod:                  PaymentStatistic.createMap(
                                                    sql.rows(PaymentQueries.PAYMENT_METHOD_UNCONFIRMED
                                                            .replace('db-name-payway', dbNamePayWay)
                                                            .replace('db-name', dbName),
                                                            [dateId: pageInformation.date.id]),
                                                    sql.rows(PaymentQueries.PAYMENT_METHOD_CONFIRMED
                                                            .replace('db-name-payway', dbNamePayWay)
                                                            .replace('db-name', dbName),
                                                            [dateId: pageInformation.date.id])
                                                ),
                paymentAmount:                  PaymentStatistic.createMap(
                                                    sql.rows(PaymentQueries.PAYMENT_AMOUNT_UNCONFIRMED
                                                            .replace('db-name-payway', dbNamePayWay)
                                                            .replace('db-name', dbName),
                                                            [dateId: pageInformation.date.id]),
                                                    sql.rows(PaymentQueries.PAYMENT_AMOUNT_CONFIRMED
                                                            .replace('db-name-payway', dbNamePayWay)
                                                            .replace('db-name', dbName),
                                                            [dateId: pageInformation.date.id])
                                                ),
                participantState:               PaymentStatistic.createMap(
                                                    sql.rows(PaymentQueries.PARTICIPANT_STATE_UNCONFIRMED
                                                            .replace('db-name-payway', dbNamePayWay)
                                                            .replace('db-name', dbName),
                                                            [dateId: pageInformation.date.id]),
                                                    sql.rows(PaymentQueries.PARTICIPANT_STATE_CONFIRMED
                                                            .replace('db-name-payway', dbNamePayWay)
                                                            .replace('db-name', dbName),
                                                            [dateId: pageInformation.date.id])
                                                ),

                paymentAmountsList:             sql.rows(PaymentQueries.PAYMENT_AMOUNT_LIST
                                                        .replace('db-name-payway', dbNamePayWay)
                                                        .replace('db-name', dbName),
                                                        [dateId: pageInformation.date.id]),

                participantsTotalPayed:         sql.rows(PaymentQueries.PARTICIPANTS_TOTAL_PAYED
                                                        .replace('db-name-payway', dbNamePayWay)
                                                        .replace('db-name', dbName),
                                                        [dateId: pageInformation.date.id])
                                                        .first(),
                participantsTotalNotCompleted:  sql.rows(PaymentQueries.PARTICIPANTS_TOTAL_PAYMENT_NOT_COMPLETE
                                                        .replace('db-name-payway', dbNamePayWay)
                                                        .replace('db-name', dbName),
                                                        [dateId: pageInformation.date.id])
                                                        .first(),
                participantsTotalNoAttempt:     sql.rows(PaymentQueries.PARTICIPANTS_TOTAL_NO_ATTEMPT
                                                        .replace('db-name-payway', dbNamePayWay)
                                                        .replace('db-name', dbName),
                                                        [dateId: pageInformation.date.id])
                                                        .first(),
                participantsTotal:              sql.rows(PaymentQueries.PARTICIPANTS_TOTAL
                                                        .replace('db-name-payway', dbNamePayWay)
                                                        .replace('db-name', dbName),
                                                        [dateId: pageInformation.date.id])
                                                        .first()
        ])
    }

    /**
     * Shows which participants are present on what days and what extras
     */
    def presence() {
        render(view: 'presence', model: [
                days:           Day.list(),
                extras:         Extra.list(),
                overview:       participantService.getParticipantPresentOverview(),
                daysCount:      participantService.getDaysCount(),
                extrasCount:    participantService.getExtrasCount()
        ])
    }

    /**
     * Shows all participants and allows to change their paper state
     */
    def paperstate() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all participants with unknown gender, allowing the user to change the gender of these participants
     */
    def gender() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Removes the user as a participant from the current event date
     */
    def delete() {
        // Of course we need an id of the user
        if (params.id) {
            User user = User.get(params.id)
            ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
            participant?.softDelete()

            // Try to remove the user as a participant, send back a success or failure message
            if (participant?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'participantDate.label'), participant.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'participantDate.label'), participant.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        // The previous page probably does not exist anymore, so send back to the participant listing page
        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }

    /**
     * Tries to download the uploaded paper
     */
    def downloadPaper() {
        Paper paper = Paper.get(params.id)

        // Let the export service deal with returning the uploaded paper
        if (paper) {
            exportService.getPaper(paper, response)
        }
    }

    /**
     * Tries to remove the uploaded paper
     * (AJAX call)
     */
    def removePaper() {
        if (request.xhr) {
            Paper paper = Paper.get(params.long('paper-id'))

            // We found the paper, now just set everything to null
            if (paper) {
                paper.file = null
                paper.fileName = null
                paper.fileSize = null
                paper.contentType = null
                paper.save(flush: true)
            }

            // That's it
            Map returnMap = [success: true]
            render returnMap as JSON
        }
    }

    /**
     * Change the paper state of the given paper
     * (AJAX call)
     */
    def changePaperState() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a paper id and state id, try to find the records for these ids
            if (params.paper_id?.isLong() && params.state_id?.isLong()) {
                Paper paper = Paper.findById(params.paper_id)
                PaperState state = PaperState.findById(params.state_id)

                // Change the paper state if they both exist
                if (paper && state) {
                    paper.state = state

                    // Save the paper
                    if (paper.save(flush: true)) {
                        // Everything is fine
                        responseMap = [success: true, paper: "${g.message(code: 'paper.label')}: ${paper.toString()} (${state.toString()})"]
                    }
                    else {
                        responseMap = [success: false, message: paper.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the paper or state could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'paper.label')}, ${g.message(code: 'paper.state.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Change the status of a bank transfer to 'payed'
     * (AJAX call)
     */
    def setPayed() {
	    // If this is an AJAX call, continue
	    if (request.xhr) {
		    Map responseMap = null

		    // If we have a user id and order id, try to find the record for the order id
		    if (params.user_id?.isLong() && params.order_id?.isLong()) {
			    User user = User.get(params.user_id.toLong())
			    Order order = Order.findById(params.order_id.toLong())
			    ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

			    // Change the payment status if it exists
			    if (user && participant) {
				    if (order.setPayedAndActive(participant)) {
					    // Save the participant
					    if (participant.save(flush: true)) {
						    // Everything is fine
						    order = order.refresh()
						    responseMap = [success: true, state: order.getStatusText()]
					    }
					    else {
						    responseMap = [success: false, message: order.errors.allErrors.collect { g.message(error: it) }]
					    }
				    }
				    else {
					    responseMap = [success: false, message: g.message(code: 'default.not.allowed.message')]
				    }
			    }
		    }

		    // If there is no responseMap defined yet, it can only mean the paper or state could not be found
		    if (!responseMap) {
			    responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'order.label')}"])]
		    }

		    render responseMap as JSON
	    }
    }

	/**
	 * Refund the payment of the given order
	 * (AJAX call)
	 */
	def refundPayment() {
		// If this is an AJAX call, continue
		if (request.xhr) {
			Map responseMap = null

			// If we have a user id and order id, try to find the record for the order id
			if (params.order_id?.isLong()) {
				Order order = Order.findById(params.order_id.toLong())
				if (order && order.fullRefund()) {
					// Everything is fine
					order = order.refresh()
					responseMap = [success: true, state: order.getStatusText()]
				}
				else {
					responseMap = [success: false, message: g.message(code: 'default.not.allowed.message')]
				}
			}

			// If there is no responseMap defined yet, it can only mean the paper or state could not be found
			if (!responseMap) {
				responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'order.label')}"])]
			}

			render responseMap as JSON
		}
	}

    /**
     * Change the gender of the given user
     * (AJAX call)
     */
    def changeGender() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a user, find the user
            if (params.user_id?.isLong()) {
                User user = User.findById(params.user_id)

                // Change the gender of the user
                if (user) {
                    if (!params.gender || "null".equals(params.gender)) {
                        user.gender = null
                    }
                    else {
                        user.gender = params.gender
                    }

                    // Save the paper
                    if (user.save(flush: true)) {
                        // Everything is fine
                        responseMap = [success: true]
                    }
                    else {
                        responseMap = [success: false, message: user.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the user could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'user.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Sends an invitation letter concerning the given user
     * (AJAX call)
     */
    def sendInvitationLetter() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a user, find the user
            if (params.user_id?.isLong()) {
                User user = User.findById(params.user_id)
                ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

                EmailTemplate template = EmailTemplate.findByDescriptionAndEvent('Invitation letter', pageInformation.date.event)
                User recipient = User.get(Setting.getSetting(Setting.MAIL_INVITATION_LETTERS_TO).value)

                // Send invitation letter
                if (user && participant) {
                    SentEmail email = emailService.createEmail(user, template)
                    email.user = recipient
                    participant.invitationLetterSent = true

                    // Save the participant
                    if (email.save(flush: true) && participant.save(flush: true)) {
                        // Everything is fine
                        responseMap = [success: true, sent: g.message(code: 'default.boolean.true')]
                    }
                    else {
                        responseMap = [success: false, message: user.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the user could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'user.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Returns the email details
     * (AJAX call)
     */
    def emailDetails() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            if (params['email-id']?.isLong()) {
                SentEmail email = SentEmail.findById(params.long('email-id'))
                if (email) {
                    responseMap = [
                            success: true,
                            orginalSent: g.formatDate(date: email.dateTimeSent, formatName: 'default.date.time.format'),
                            copiesSent: email.allDateTimeCopies.collect { g.formatDate(date: it, formatName: 'default.date.time.format') }.join('<br />'),
                            from: "${email.fromName} ( ${email.fromEmail} )",
                            subject: email.subject,
                            body: eca.formatText(text: email.body)
                    ]
                }
            }

            // If there is no responseMap defined yet, it can only mean the email could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'email.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Resend the given email
     * (AJAX call)
     */
    def resendEmail() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            if (params['email-id']?.isLong()) {
                SentEmail email = SentEmail.findById(params.long('email-id'))
                if (email) {
                    emailService.sendEmail(email, true, true)
                    Map response = ['success': true, message: 'Email has been succesfully resend!']
                    render response as JSON
                }
            }

            // If there is no responseMap defined yet, it can only mean the email could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'email.label')}"])]
            }

            render responseMap as JSON
        }
    }

	/**
	 * Changes the present days of a participant
	 * (AJAX call)
	 */
	def changeDays() {
		// If this is an AJAX call, continue
		if (request.xhr) {
			Map responseMap = null

			if (params['user-id']?.toString()?.isLong()) {
				User user = User.get(params.long('user-id'))
				if (user) {
					user.daysPresent.clear()
					user.save(flush: true)

					params.days.split(';').each { dayId ->
						if (dayId.toString().isLong()) {
							Day day = Day.findById(dayId.toString().toLong())
							if (day) {
								user.addToDaysPresent(new ParticipantDay(day: day))
							}
						}
					}

					if (user.save(flush: true)) {
						def daysPresent = ParticipantDay.findAllDaysOfUser(user).collect { it.toString() }
						daysPresent = (daysPresent.size() > 0) ? daysPresent : g.message(code: 'participantDay.not.found.label')

						responseMap = ['success': true, daysPresent: daysPresent]
					}
				}
			}

			// If there is no responseMap defined yet, it can only mean the email could not be found
			if (!responseMap) {
				responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'day.label')}"])]
			}

			render responseMap as JSON
		}
	}
}
