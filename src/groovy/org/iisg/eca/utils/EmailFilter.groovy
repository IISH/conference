package org.iisg.eca.utils

import java.util.regex.Pattern

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.Setting

/**
 * Simple utility class to check whether users or just simply email addresses can be sent an email
 */
class EmailFilter {
	private Set<Pattern> emailPatterns

	/**
	 * Creates a new email filter utility class for the given event
	 * @param event The event
	 */
	public EmailFilter(Event event) {
		this.emailPatterns = new HashSet<Pattern>()

		// Cache the current regular expression for use with multiple email addresses
		Setting regexSettingsForEvent = Setting.getSetting(Setting.DONT_SEND_EMAILS_TO, event)
		regexSettingsForEvent.value?.split()?.each { String regex ->
			this.emailPatterns.add(Pattern.compile(regex))
		}
	}

	/**
	 * Checks the user in question: checks the email address according to the regular expressions,
	 * but in addition also checks whether the email was discontinued
	 * @param user The user in question
	 * @return Whether the user is allowed
	 */
	public boolean isUserAllowed(User user) {
		if (!user.emailDiscontinued) {
			return isEmailAddressAllowed(user.email)
		}

		return false
	}

	/**
	 * Checks the email address with the cached regular expressions
	 * @param emailAddress The email address
	 * @return Whether the email address is allowed
	 */
	public boolean isEmailAddressAllowed(String emailAddress) {
		for (Pattern emailPattern : this.emailPatterns) {
			if (emailPattern.matcher(emailAddress).matches()) {
				return false
			}
		}

		return true
	}
}
