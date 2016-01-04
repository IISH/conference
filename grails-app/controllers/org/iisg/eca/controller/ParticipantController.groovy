package org.iisg.eca.controller

import org.iisg.eca.domain.*
import org.iisg.eca.utils.PaymentQueries
import org.iisg.eca.utils.PaymentStatistic

import groovy.sql.Sql
import grails.converters.JSON

/**
 * Controller responsible for handling requests on participants
 */
class ParticipantController {
	def dataSource
	def emailService
	def exportService
	def pageInformation
	def participantService
	def emailCreationService
	def participantUpdateService
	def participantSessionService

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
	 * Shows all participants that showed interest for one of the extras (final-registration)
	 */
	def extras() {
		forward(controller: 'dynamicPage', action: 'dynamic', params: params)
	}

	/**
	 * Shows all participants that showed interest for one of the extras (pre-registration)
	 */
	def extraspre() {
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
		render(view: "list", model: [participants: participants,
		                             alphabet    : participants.keySet(),
		                             states      : participantService.getParticipantCounts()])
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

			String email = params.email.toString().trim()
			User user = User.findByEmail(email)
			ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

			if (participant) {
				flash.message = g.message(code: 'default.exists.message',
						args: [user.toString(), g.message(code: 'participantDate.label')])
				redirect(uri: eca.createLink(action: 'show', id: user.id, noBase: true))
				return
			}

			// Participant is not found, so create one
			participant = participantService.createNewParticipant(email)
			user = participant.user

			if (user.save() && participant.save()) {
				flash.message = g.message(code: 'default.created.message',
						args: [g.message(code: 'participantDate.label'), participant.toString()])
				redirect(uri: eca.createLink(action: 'show', id: participant.user.id, noBase: true))
				return
			}

			return [participant: participant]
		}
	}

	/**
	 * Shows all participant information and allows for editing
	 */
	def show(User user) {
		// We need an id, check for the id
		if (!params.id) {
			flash.error = true
			flash.message = g.message(code: 'default.no.id.message')
			redirect(uri: eca.createLink(previous: true, noBase: true))
			return
		}

		// We also need a user to be able to show something
		if (!user) {
			flash.error = true
			flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
			redirect(uri: eca.createLink(previous: true, noBase: true))
			return
		}

		ParticipantDate participant = user.getParticipantForDate(pageInformation.date)
		List participantIds = participantService.getParticipantsWithFilters(params).collect { it[0] }
		List sessions = (participant) ? participantSessionService.getSessionsForParticipant(participant) : []
		List daysPresent = ParticipantDay.findAllDaysOfUser(user)
		List orders = (participant) ? Order.findAllByParticipantDate(participant) : []

		if (request.post) {
			if (participantUpdateService.update(user, participant, params)) {
				flash.message = g.message(code: 'default.updated.message',
						args: [g.message(code: 'participantDate.label'), user.toString()])

				if (params['btn_save_close']) {
					redirect(uri: eca.createLink(action: 'list', noBase: true))
					return
				}

				// If a participant was added, load it to the model, so the view correctly shows the new information
				if (!participant && params['add-to-date']?.equals('add')) {
					participant = user.getParticipantForDate(pageInformation.date)
				}
			}
		}

		List participantVolunteering = (participant) ? participant.getParticipantVolunteeringSorted() : []
		render(view: "form", model: [user                   : user,
		                             participant            : participant,
		                             participantVolunteering: participantVolunteering,
		                             participantIds         : participantIds,
		                             sessions               : sessions,
		                             daysPresent            : daysPresent,
		                             orders                 : orders,
		                             emailsSent             : SentEmail.emailsSent(user).list(),
		                             emailsNotSent          : SentEmail.emailsNotSent(user).list()
		])
	}

	/**
	 * Shows the payment status for all participants
	 */
	def payments() {
		Sql sql = new Sql(dataSource)
		Long dateId = pageInformation.date.id

		render(view: "payments", model: [
				paymentsList                 :
						sql.rows(PaymentQueries.PAYMENT_LIST, [dateId: dateId]),
				paymentMethod                :
						PaymentStatistic.createMap(
							sql.rows(PaymentQueries.PAYMENT_METHOD_UNCONFIRMED, [dateId: dateId]),
							sql.rows(PaymentQueries.PAYMENT_METHOD_CONFIRMED, [dateId: dateId])
						),
				paymentAmount                :
						PaymentStatistic.createMap(
							sql.rows(PaymentQueries.PAYMENT_AMOUNT_UNCONFIRMED, [dateId: dateId]),
							sql.rows(PaymentQueries.PAYMENT_AMOUNT_CONFIRMED, [dateId: dateId])
						),
				participantState             :
						PaymentStatistic.createMap(
							sql.rows(PaymentQueries.PARTICIPANT_STATE_UNCONFIRMED, [dateId: dateId]),
							sql.rows(PaymentQueries.PARTICIPANT_STATE_CONFIRMED, [dateId: dateId])
						),
				paymentAmountsList           :
						sql.rows(PaymentQueries.PAYMENT_AMOUNT_LIST, [dateId: dateId]),
				participantsTotalPayed       :
						sql.rows(PaymentQueries.PARTICIPANTS_TOTAL_PAYED, [dateId: dateId]).first(),
				participantsTotalNotCompleted:
						sql.rows(PaymentQueries.PARTICIPANTS_TOTAL_PAYMENT_NOT_COMPLETE, [dateId: dateId]).first(),
				participantsTotalNoAttempt   :
						sql.rows(PaymentQueries.PARTICIPANTS_TOTAL_NO_ATTEMPT, [dateId: dateId]).first(),
				participantsTotal            :
						sql.rows(PaymentQueries.PARTICIPANTS_TOTAL, [dateId: dateId]).first()
		])
	}

	/**
	 * Shows which participants are present on what days and what extras
	 */
	def presence() {
		render(view: 'presence', model: [
				days       : Day.list(),
				extras     : Extra.list(),
				overview   : participantService.getParticipantPresentOverview(),
				daysCount  : participantService.getDaysCount(),
				extrasCount: participantService.getExtrasCount()
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
				flash.message = g.message(code: 'default.deleted.message',
						args: [g.message(code: 'participantDate.label'), participant.toString()])
			}
			else {
				flash.error = true
				flash.message = g.message(code: 'default.not.deleted.message',
						args: [g.message(code: 'participantDate.label'), participant.toString()])
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
	 * Creates a new order and registers it also in PayWay
	 */
	def newOrder() {
		String cleanAmount = params.amount.toString().replace(',', '.').replaceAll('[^\\d.]', '');

		Order order = new Order()
		order.amount = new BigDecimal(cleanAmount).movePointRight(2)
		order.participantDate = ParticipantDate.findByIdAndDate(params.long('participantId'), pageInformation.date)
		order.paymentMethod = params.int('method')
		order.description = params.description.toString()

		if (order.registerInPayWay()) {
			if ((params.int('status') == Order.ORDER_NOT_PAYED) || order.setPayedAndActive(order.participantDate)) {
				flash.message = message(code: 'default.successful.message', args: [message(code: 'order.label')])
				redirect(uri: eca.createLink(action: 'show', id: order.participantDate.user.id, noBase: true))
				return
			}
		}

		flash.error = true
		flash.message = message(code: 'default.not.successful.message', args: [message(code: 'order.label')])
		redirect(uri: eca.createLink(action: 'show', id: order.participantDate.user.id, noBase: true))
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
				responseMap = [success: false, message: g.message(code: 'default.not.found.message',
						args: ["${g.message(code: 'paper.label')}, ${g.message(code: 'paper.state.label')}"])]
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
							responseMap = [success: true, state: order.getStatusText()]
						}
						else {
							responseMap =
									[success: false, message: order.errors.allErrors.collect { g.message(error: it) }]
						}
					}
					else {
						responseMap = [success: false, message: g.message(code: 'default.not.allowed.message')]
					}
				}
			}

			// If there is no responseMap defined yet, it can only mean the paper or state could not be found
			if (!responseMap) {
				responseMap = [success: false, message:
						g.message(code: 'default.not.found.message', args: ["${g.message(code: 'order.label')}"])]
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
					responseMap = [success: true, state: order.getStatusText()]
				}
				else {
					responseMap = [success: false, message: g.message(code: 'default.not.allowed.message')]
				}
			}

			// If there is no responseMap defined yet, it can only mean the paper or state could not be found
			if (!responseMap) {
				responseMap = [success: false, message:
						g.message(code: 'default.not.found.message', args: ["${g.message(code: 'order.label')}"])]
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
				responseMap = [success: false, message: g.
						message(code: 'default.not.found.message', args: ["${g.message(code: 'user.label')}"])]
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

				Long emailId = Setting.getSetting(Setting.INVITATION_LETTER_EMAIL_TEMPLATE_ID).value.toLong()
				EmailTemplate template = EmailTemplate.findById(emailId)
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
				responseMap = [success: false, message: g.
						message(code: 'default.not.found.message', args: ["${g.message(code: 'user.label')}"])]
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
							success    : true,
							orginalSent: g.formatDate(date: email.dateTimeSent, formatName: 'default.date.time.format'),
							copiesSent : email.allDateTimeCopies.
									collect { g.formatDate(date: it, formatName: 'default.date.time.format') }.
									join('<br />'),
							from       : "${email.fromName} ( ${email.fromEmail} )",
							subject    : email.subject,
							body       : eca.formatText(text: email.body)
					]
				}
			}

			// If there is no responseMap defined yet, it can only mean the email could not be found
			if (!responseMap) {
				responseMap = [success: false, message: g.
						message(code: 'default.not.found.message', args: ["${g.message(code: 'email.label')}"])]
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
					Map response = ['success': true, message: 'Email has been successfully resent!']
					render response as JSON
				}
			}

			// If there is no responseMap defined yet, it can only mean the email could not be found
			if (!responseMap) {
				responseMap = [success: false, message: g.
						message(code: 'default.not.found.message', args: ["${g.message(code: 'email.label')}"])]
			}

			render responseMap as JSON
		}
	}

	/**
	 * Resend the registration completed email
	 * (AJAX call)
	 */
	def resendRegistrationEmail() {
		// If this is an AJAX call, continue
		if (request.xhr) {
			Map responseMap = null

			if (params['user-id']?.isLong()) {
				User user = User.findById(params.long('user-id'))
				if (user) {
					emailCreationService.createPreRegistrationEmail(user).each { SentEmail email ->
						emailService.sendEmail(email, true, true)
					}

					Map response = ['success': true, message: 'Registration email has been successfully sent!']
					render response as JSON
				}
			}

			// If there is no responseMap defined yet, it can only mean the email could not be found
			if (!responseMap) {
				responseMap = [success: false, message: g.message(code: 'default.not.found.message',
						args: ["${g.message(code: 'user.label')}"])]
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
						daysPresent = (daysPresent.size() > 0) ? daysPresent :
								g.message(code: 'participantDay.not.found.label')

						responseMap = ['success': true, daysPresent: daysPresent]
					}
				}
			}

			// If there is no responseMap defined yet, it can only mean the email could not be found
			if (!responseMap) {
				responseMap = [success: false, message: g.
						message(code: 'default.not.found.message', args: ["${g.message(code: 'day.label')}"])]
			}

			render responseMap as JSON
		}
	}

    /**
     * Triggers an order refresh from PayWay
     */
    def refreshOrder() {
        if (params.id) {
            Order order = Order.get(params.id)
            boolean insert = false
            if (!order) {
                order = new Order()
                order.setId(params.id)
                insert = true
            }

            if (order.refreshOrder(insert)) {
                render "Order successfully refreshed."
            }
            else {
                render "Failure to refresh order."
            }
        }
        render "No order id given."
    }
}
