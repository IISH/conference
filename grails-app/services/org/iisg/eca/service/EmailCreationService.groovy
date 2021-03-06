package org.iisg.eca.service

import java.text.SimpleDateFormat

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Order
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.FeeAmount
import org.iisg.eca.domain.PaperReview
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDay
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.CombinedSessionParticipant

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Service that is responsible for the creation of specific emails
 */
class EmailCreationService {
	def emailService
	def pageInformation

	/**
	 * Creates a specific email or emails
	 * @param settingPropertyName The name of the property that holds the necessary email template to user
	 * @param user The user to whom the email is addressed
	 * @param props Extra properties that will have to be included in the email
	 * @return Emails ready to be sent
	 */
	Set<SentEmail> createEmail(String settingPropertyName, User user, GrailsParameterMap props = (GrailsParameterMap) [:] as Map) {
		switch (settingPropertyName) {
			case Setting.BANK_TRANSFER_EMAIL_TEMPLATE_ID:
				return [createBankTransferEmail(user, Order.get(props.long('orderId')))] as Set<SentEmail>
				break
			case Setting.PAYMENT_ACCEPTED_EMAIL_TEMPLATE_ID:
				return [createPaymentAcceptedEmail(user, Order.get(props.long('orderId')))] as Set<SentEmail>
            case Setting.PAYMENT_ON_SITE_EMAIL_TEMPLATE_ID:
                return [createPaymentOnSiteEmail(user, Order.get(props.long('orderId')))] as Set<SentEmail>
			case Setting.PRE_REGISTRATION_EMAIL_TEMPLATE_ID:
				return createPreRegistrationEmail(user)
			default:
				return null
		}
	}

	/**
	 * Creates an email that tells the user how to make a bank transfer
	 * @param user The user to whom the email is addressed
	 * @param order The order that has been accepted
	 * @return An email ready to be sent
	 */
	SentEmail createBankTransferEmail(User user, Order order) {
		SentEmail email = findAndCreateEmail(user, Setting.BANK_TRANSFER_EMAIL_TEMPLATE_ID)

		// Make sure the given order belongs to the given user
		if (order.participantDate.user.id == user.id) {
			final SimpleDateFormat dateFormat = new SimpleDateFormat('EEEE dd MMMM yyyy', Locale.US)

			Setting bankTransferText = Setting.getSetting(Setting.BANK_TRANSFER_INFO)
			Setting bankTransferClosesOn = Setting.getSetting(Setting.BANK_TRANSFER_LASTDATE)

			email.addAdditionalValue('BankTransferInfo', bankTransferText.value)
			email.addAdditionalValue('PaymentNumber', order.id.toString())
			email.addAdditionalValue('PaymentAmount', FeeAmount.getReadableFeeAmount(order.amountAsBigDecimal))
			email.addAdditionalValue('PaymentDescription', order.description)
			email.addAdditionalValue('PaymentFinalDate', dateFormat.format(bankTransferClosesOn.dateValue))

			// Is actually not an additional value, but as BankTransferInfo contains this code, we have to insert it
			email.addAdditionalValue('NameParticipant', user.toString())

			return email
		}

		return null
	}

	/**
	 * Creates an email that informs the user his payment has been accepted
	 * @param user The user to whom the email is addressed
	 * @param order The order that has been accepted
	 * @return An email ready to be sent
	 */
	SentEmail createPaymentAcceptedEmail(User user, Order order) {
		SentEmail email = findAndCreateEmail(user, Setting.PAYMENT_ACCEPTED_EMAIL_TEMPLATE_ID)

		// Make sure the given order belongs to the given user
		if (order.participantDate.user.id == user.id) {
			List<Day> daysPresent = ParticipantDay.findAllDaysOfUser(user)

			email.addAdditionalValue('PaymentNumber', order.id.toString())
			email.addAdditionalValue('PaymentAmount', FeeAmount.getReadableFeeAmount(order.amountAsBigDecimal))
			email.addAdditionalValue('PaymentDescription', order.description)
			email.addAdditionalValue('OrderDescription', order.getExtendedOrderDescription())
			email.addAdditionalValue('DaysPresent', daysPresent.join('\n'))

			return email
		}

		return null
	}

    /**
     * Creates an email that informs the user his payment on site has been registered
     * @param user The user to whom the email is addressed
     * @param order The order
     * @return An email ready to be sent
     */
    SentEmail createPaymentOnSiteEmail(User user, Order order) {
        SentEmail email = findAndCreateEmail(user, Setting.PAYMENT_ON_SITE_EMAIL_TEMPLATE_ID)

        // Make sure the given order belongs to the given user
        if (order.participantDate.user.id == user.id) {
            List<Day> daysPresent = ParticipantDay.findAllDaysOfUser(user)

            email.addAdditionalValue('PaymentNumber', order.id.toString())
            email.addAdditionalValue('PaymentAmount', FeeAmount.getReadableFeeAmount(order.amountAsBigDecimal))
            email.addAdditionalValue('PaymentDescription', order.description)
            email.addAdditionalValue('OrderDescription', order.getExtendedOrderDescription())
            email.addAdditionalValue('DaysPresent', daysPresent.join('\n'))

            return email
        }

        return null
    }

	/**
	 * Creates emails that informs the user about his pre-registration
	 * and the participants that were added to a session by this participant
	 * @param user The user to whom the email is addressed, the one who did the pre-registration
	 * @return All emails ready to be sent
	 */
	Set<SentEmail> createPreRegistrationEmail(User user) {
		Set<SentEmail> emails = new HashSet<SentEmail>()

		// Make sure the participant state is updated as well
		ParticipantDate participant = user.getParticipantForDate(pageInformation.date)
		if (participant?.stateId == ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION) {
			participant.state = ParticipantState.get(ParticipantState.NEW_PARTICIPANT)
			participant.save()
		}

		// First create the mail for the participant who just registered
		SentEmail email = findAndCreateEmail(user, Setting.PRE_REGISTRATION_EMAIL_TEMPLATE_ID,
				[addedByUserId: user.id])
		emails.add(email)

		// Now for each created session also mail the session participants
		CombinedSessionParticipant.findAllByAddedBy(user)*.session.unique().each { session ->
			CombinedSessionParticipant.findAllBySessionAndAddedBy(session, user)*.user.unique().each { sessionParticipant ->
				// Make sure the participant state is updated as well
				ParticipantDate pd = sessionParticipant.getParticipantForDate(pageInformation.date)
				if (pd?.stateId == ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION) {
					pd.state = ParticipantState.get(ParticipantState.NEW_PARTICIPANT)
					pd.save()
				}

				// Then create the email
				SentEmail sentEmail = findAndCreateEmail(
						sessionParticipant,
						Setting.SESSION_REGISTRATION_EMAIL_TEMPLATE_ID,
						[sessionId: session.id, addedByUserId: user.id]
				)
				sentEmail.addAdditionalValue('OrganizerName', user.toString())

				emails.add(sentEmail)
			}
		}

		return emails
	}

	/**
	 * Creates an email that informs the user that he has been assigned to review a paper
	 * @param paperReview The paper review
	 * @return An email ready to be sent
	 */
	SentEmail createPaperReviewerEmail(PaperReview paperReview) {
		SentEmail email = findAndCreateEmail(paperReview.reviewer, Setting.PAPER_REVIEWER_EMAIL_TEMPLATE_ID)
		email.addAdditionalValue('PaperTitle', paperReview.paper.title)
		email.addAdditionalValue('PaperId', paperReview.paper.id.toString())
		return email
	}

	/**
	 * Finds an email template based on the given property name and fills in already known code for the given user
	 * @param user The user to whom the email is addressed
	 * @param settingPropertyName The name of the property that holds the necessary email template id
	 * @param identifiers Additional identifiers for translating email codes
	 * @return An email ready to be sent
	 */
	private SentEmail findAndCreateEmail(User user, String settingPropertyName, Map<String, Long> identifiers = [:]) {
		Long emailId = Setting.getSetting(settingPropertyName).value.toLong()
		EmailTemplate template = EmailTemplate.findById(emailId)
		return emailService.createEmail(user, template, false, identifiers)
	}
}
