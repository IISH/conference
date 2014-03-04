package org.iisg.eca.service

import java.text.SimpleDateFormat

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EmailTemplate
import org.iisg.eca.domain.ParticipantDay

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Service that is responsible for the creation of specific emails
 */
class EmailCreationService {
    def emailService

    /**
     * Creates a specific email
     * @param settingPropertyName The name of the property that holds the necessary email template to user
     * @param user The user to whom the email is addressed
     * @param props Extra properties that will have to be included in the email
     * @return An email ready to be sent
     */
    SentEmail createEmail(String settingPropertyName, User user, GrailsParameterMap props = (GrailsParameterMap) [:] as Map) {
        switch (settingPropertyName) {
            case Setting.BANK_TRANSFER_EMAIL_TEMPLATE_ID:
                return createBankTransferEmail(user, props.paymentNumber.toString(), props.paymentAmount.toString(),
                        props.paymentDescription.toString(), props.date('bankTransferFinalDate', 'yyyy/MM/dd'))
                break
            case Setting.PAYMENT_ACCEPTED_EMAIL_TEMPLATE_ID:
                return createPaymentAcceptedEmail(user, props.paymentNumber.toString(), props.paymentAmount.toString(),
                        props.paymentDescription.toString())
            default:
                return null
        }
    }

    /**
     * Creates an email that tells the user how to make a bank transfer
     * @param user The user to whom the email is addressed
     * @param paymentNumber The payment number
     * @param paymentAmount The amount payed
     * @param paymentDescription The payment description
     * @param bankTransferFinalDate The final date a bank transfer should have been made
     * @return An email ready to be sent
     */
    SentEmail createBankTransferEmail(User user, String paymentNumber, String paymentAmount,
                                      String paymentDescription, Date bankTransferFinalDate) {
        SentEmail email = findAndCreateEmail(user, Setting.BANK_TRANSFER_EMAIL_TEMPLATE_ID)

        Setting bankTransferText = Setting.getSetting(Setting.BANK_TRANSFER_INFO)
        SimpleDateFormat dateFormat = new SimpleDateFormat('EEEE dd MMMM yyyy')

        email.addAdditionalValue('BankTransferInfo',    bankTransferText.value)
        email.addAdditionalValue('PaymentNumber',       paymentNumber)
        email.addAdditionalValue('PaymentAmount',       paymentAmount)
        email.addAdditionalValue('PaymentDescription',  paymentDescription)
        email.addAdditionalValue('PaymentFinalDate',    dateFormat.format(bankTransferFinalDate))

        return email
    }

    /**
     * Creates an email that informs the user his payment has been accepted
     * @param user The user to whom the email is addressed
     * @param paymentNumber The payment number
     * @param paymentAmount The amount payed
     * @param paymentDescription The payment description
     * @return An email ready to be sent
     */
    SentEmail createPaymentAcceptedEmail(User user, String paymentNumber, String paymentAmount, String paymentDescription) {
        SentEmail email = findAndCreateEmail(user, Setting.PAYMENT_ACCEPTED_EMAIL_TEMPLATE_ID)

        List<Day> daysPresent = ParticipantDay.findAllDaysOfUser(user)

        email.addAdditionalValue('PaymentNumber',       paymentNumber)
        email.addAdditionalValue('PaymentAmount',       paymentAmount)
        email.addAdditionalValue('PaymentDescription',  paymentDescription)
        email.addAdditionalValue('DaysPresent',         daysPresent.join('\n'))

        return email
    }

    /**
     * Finds an email template based on the given property name and fills in already known code for the given user
     * @param user The user to whom the email is addressed
     * @param settingPropertyName The name of the property that holds the necessary email template id
     * @return An email ready to be sent
     */
    private SentEmail findAndCreateEmail(User user, String settingPropertyName) {
        Long emailId = Setting.getSetting(settingPropertyName).value.toLong()
        EmailTemplate template = EmailTemplate.findById(emailId)
        return emailService.createEmail(user, template, false)
    }
}
