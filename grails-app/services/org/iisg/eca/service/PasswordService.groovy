package org.iisg.eca.service

import java.text.DateFormat
import java.text.SimpleDateFormat

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EmailTemplate

import org.springframework.context.i18n.LocaleContextHolder

/**
 * Service that is responsible for password related actions
 */
class PasswordService {
    static final int CONFIRM_LOST_PASSWORD_NOT_FOUND = 0
    static final int CONFIRM_LOST_PASSWORD_ACCEPT = 1
    static final int CONFIRM_LOST_PASSWORD_PASSWORD_ALREADY_SENT = 2
    static final int CONFIRM_LOST_PASSWORD_CODE_EXPIRED = 3

    def emailService
    def messageSource

	/**
	 * Allows for changing the password
	 * @param user The user who's password has to be updated
	 * @param newPassword The new password of the user
	 * @return <code>true</code> in case the change was successful and the email was sent
	 */
	boolean changePassword(User user, String newPassword) {
		return changePassword(user, newPassword, newPassword)
	}

	/**
	 * Allows users to change their password
	 * @param user The user who's password has to be updated
	 * @param newPassword The new password of the user
	 * @param newPasswordRepeat The new password of the user repeated
	 * @param oldPassword The old password, must be correct if null is not given
	 * @return <code>true</code> in case the change was successful and the email was sent
	 */
    boolean changePassword(User user, String newPassword, String newPasswordRepeat, String oldPassword=null) {
        if (    user &&
                (!oldPassword || user.isPasswordCorrect(oldPassword)) &&
                newPassword.equals(newPasswordRepeat) &&
                User.PASSWORD_PATTERN.matcher(newPassword).matches()) {
            user.password = newPassword
            if (user.save()) {
                Long templateId = Setting.getSetting(Setting.CHANGE_PASSWORD_EMAIL_TEMPLATE_ID).value.toLong()
                EmailTemplate template = EmailTemplate.get(templateId)

                SentEmail email = emailService.createEmail(user, template, false)
                email.addAdditionalValue('PasswordParticipant', newPassword.toString())
                emailService.sendEmail(email, false)

                return true
            }
        }

        return false
    }

	/**
	 * Creates a new request code that is mailed to the user in order to confirm
	 * the correct user indicated he/she lost his/her password
	 * @param user The user in question
	 * @return <code>true</code> in case everything was successful and the email was sent
	 */
    boolean lostPassword(User user) {
        if (user) {
            if (!user.requestCodeValidUntil?.before(new Date()) || (user.requestCode?.size() < 10)) {
                Calendar calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, 3)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)

                user.requestCodeValidUntil = calendar.getTime()
                user.requestCode = User.createPassword(12)

                user.save()
            }

            String dateFormat = messageSource.getMessage('default.date.time.format', null, null, LocaleContextHolder.locale)
            DateFormat formatter = new SimpleDateFormat(dateFormat, LocaleContextHolder.locale)
            String validUntil = formatter.format(user.requestCodeValidUntil)

            Long templateId = Setting.getSetting(Setting.LOST_PASSWORD_EMAIL_TEMPLATE_ID).value.toLong()
            EmailTemplate template = EmailTemplate.get(templateId)

            SentEmail email = emailService.createEmail(user, template, false)
            email.addAdditionalValue('ID', user.id.toString())
            email.addAdditionalValue('CODE', user.requestCode)
            email.addAdditionalValue('CodeValidUntil', validUntil)
            emailService.sendEmail(email, false)

            return true
        }

        return false
    }

	/**
	 * Sends a new password to the user when he/she confirms to be the one who requested a new password
	 * @param user The user in question
	 * @param code The code as entered by the user to confirm he/she lost his password
	 * @return The lost password status
	 */
    int confirmLostPassword(User user, String code) {
        if (user && user.requestCode.equals(code)) {
            if (!user.requestCodeValidUntil || user.requestCodeValidUntil.before(new Date())) {
                return CONFIRM_LOST_PASSWORD_CODE_EXPIRED
            }

            Calendar calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR, -1)

            if (user.newPasswordEmailed?.after(calendar.getTime())) {
                return CONFIRM_LOST_PASSWORD_PASSWORD_ALREADY_SENT
            }

            String newPassword = User.createPassword()
            user.password = newPassword
            if (user.save()) {
                Long templateId = Setting.getSetting(Setting.CONFIRM_LOST_PASSWORD_EMAIL_TEMPLATE_ID).value.toLong()
                EmailTemplate template = EmailTemplate.get(templateId)

                SentEmail email = emailService.createEmail(user, template, false)
                email.addAdditionalValue('PasswordParticipant', newPassword)
                emailService.sendEmail(email, false)

                return CONFIRM_LOST_PASSWORD_ACCEPT
            }
        }

        return CONFIRM_LOST_PASSWORD_NOT_FOUND
    }
}
