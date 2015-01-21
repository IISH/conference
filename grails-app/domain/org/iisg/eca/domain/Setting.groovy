package org.iisg.eca.domain

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Domain class of table holding all settings
 */
class Setting extends EventDomain {
	// Default settings
	static final String ALLOWED_PAPER_EXTENSIONS = 'allowed_paper_extensions'
	static final String APPLICATION_TITLE = 'application_title'
	static final String AUTHOR_REGISTRATION_LASTDATE = 'author_registration_lastdate'
	static final String AWARD_NAME = 'award_name'
	static final String BANK_TRANSFER_ALLOWED = 'bank_transfer_allowed'
	static final String BANK_TRANSFER_INFO = 'bank_transfer_info'
	static final String BANK_TRANSFER_LASTDATE = 'bank_transfer_lastdate'
	static final String CHANGE_USER = 'change_user'
	static final String CHECK_ACCEPTED_IP = 'check_accepted_ip'
	static final String COUNTRY_ID = 'country_id'
	static final String DEFAULT_FROM_EMAIL = 'default_from_email'
	static final String DEFAULT_NETWORK_ID = 'default_network_id'
	static final String DEFAULT_ORGANISATION_EMAIL = 'default_organisation_email'
	static final String DISABLE_EMAIL_SESSIONS = 'disable_email_sessions'
	static final String DONT_SEND_EMAILS_TO = 'dont_send_emails_to'
	static final String DOWNLOAD_PAPER_LASTDATE = 'download_paper_lastdate'
	static final String EMAIL_ADDRESS_INFO_ERRORS = 'email_address_info_errors'
	static final String EMAIL_BCC = 'email_bcc'
	static final String EMAIL_MAX_NUM_EMAILS_PER_SESSION = 'email_max_num_emails_per_session'
	static final String EMAIL_MAX_NUM_TRIES = 'email_max_num_tries'
	static final String EMAIL_MIN_MINUTES_BETWEEN_SENDING = 'email_min_minutes_between_sending'
	static final String EMAIL_WAITING_TIME = 'email_waiting_time'
	static final String FINAL_REGISTRATION_LASTDATE = 'final_registration_lastdate'
	static final String GENERAL_TERMS_CONDITIONS_LINK = 'general_terms_conditions_link'
	static final String IP_AUTHENTICATION = 'ip_authentication'
	static final String LAST_UPDATED = 'last_updated'
	static final String MAIL_INVITATION_LETTERS_TO = 'mail_invitation_letters_to'
	static final String MAX_PAPERS_PER_PERSON_PER_SESSION = 'max_papers_per_person_per_session'
	static final String MAX_UPLOAD_SIZE_PAPER = 'max_upload_size_paper'
	static final String NETWORK_NAME_PLURAL = 'network_name_plural'
	static final String NETWORK_NAME_SINGULAR = 'network_name_singular'
	static final String NUM_CANDIDATE_VOTES_ADVISORY_BOARD = 'num_candidate_votes_advisory_board'
	static final String ORGANIZER_REGISTRATION_LASTDATE = 'organizer_registration_lastdate'
	static final String PATH_FOR_ADMIN_MENU = 'path_for_admin_menu'
	static final String PATH_FOR_MENU = 'path_for_menu'
	static final String PREREGISTRATION_LASTDATE = 'preregistration_lastdate'
	static final String PREREGISTRATION_STARTDATE = 'preregistration_startdate'
	static final String PREREGISTRATION_SESSIONS = 'preregistration_sessions'
	static final String REFUND_ADMINISTRATION_COSTS = 'refund_administration_costs'
	static final String SALT = 'salt'
	static final String WEB_ADDRESS = 'web_address'

	// Show/hide settings
	static final String HIDE_ALWAYS_IN_ONLINE_PROGRAMME = 'hide_always_in_online_programme'
	static final String HIDE_IF_EMPTY_IN_ONLINE_PROGRAMME = 'hide_if_empty_in_online_programme'
	static final String SHOW_ACCOMPANYING_PERSONS = 'show_accompanying_persons'
	static final String SHOW_AUTHOR_REGISTRATION = 'show_author_registration'
	static final String SHOW_AWARD = 'show_award'
	static final String SHOW_CHAIR_DISCUSSANT_POOL = 'show_chair_discussant_pool'
	static final String SHOW_CV = 'show_cv'
	static final String SHOW_DAYS = 'show_days'
	static final String SHOW_DAYS_SESSION_PLANNED = 'show_days_session_planned'
	static final String SHOW_EQUIPMENT = 'show_equipment'
	static final String SHOW_GENERAL_COMMENTS = 'show_general_comments'
	static final String SHOW_INVITATION_LETTER = 'show_invitation_letter'
	static final String SHOW_LANGUAGE_COACH_PUPIL = 'show_language_coach_pupil'
	static final String SHOW_LOWER_FEE = 'show_lower_fee'
	static final String SHOW_NETWORK = 'show_network'
	static final String SHOW_ORGANIZER_REGISTRATION = 'show_organizer_registration'
	static final String SHOW_PROGRAMME_ONLINE = 'show_programme_online'
	static final String SHOW_SESSION_CODES = 'show_session_codes'
	static final String SHOW_SESSION_PARTICIPANT_TYPES_REGISTRATION = 'show_session_participant_types_registration'
	static final String SHOW_STUDENT = 'show_student'

	// Required fields settings
	static final String REQUIRED_CV = 'required_cv'

	// Layout settings
	static final String BANNER_BG_IMG = 'banner_bg_img'
	static final String BANNER_IMG = 'banner_img'
	static final String LABEL_COLOR = 'label_color'
	static final String MAIN_COLOR_BG = 'main_color_bg'
	static final String MAIN_COLOR_DARK = 'main_color_dark'
	static final String MAIN_COLOR_LIGHT = 'main_color_light'

	// Email templates
	static final String BANK_TRANSFER_EMAIL_TEMPLATE_ID = 'bank_transfer_email_template_id'
	static final String INVITATION_LETTER_EMAIL_TEMPLATE_ID = 'invitation_letter_email_template_id'
	static final String LOST_PASSWORD_EMAIL_TEMPLATE_ID = 'lost_password_email_template_id'
	static final String NEW_PASSWORD_EMAIL_TEMPLATE_ID = 'new_password_email_template_id'
	static final String PAYMENT_ACCEPTED_EMAIL_TEMPLATE_ID = 'payment_accepted_email_template_id'
	static final String PRE_REGISTRATION_EMAIL_TEMPLATE_ID = 'pre_registration_email_template_id'
	static final String SESSION_REGISTRATION_EMAIL_TEMPLATE_ID = 'session_registration_email_template_id'
	static final String UPDATED_PASSWORD_EMAIL_TEMPLATE_ID = 'updated_password_email_template_id'

	// PayWay settings
	static final String PAYWAY_ADDRESS = 'payway_address'
	static final String PAYWAY_PASSPHRASE_IN = 'payway_passphrase_in'
	static final String PAYWAY_PASSPHRASE_OUT = 'payway_passphrase_out'
	static final String PAYWAY_PROJECT = 'payway_project'

	private static final SimpleDateFormat SETTINGS_DATE_FORMAT = new SimpleDateFormat('yyyy-MM-dd')

	String property
	String value
	boolean showInBackend = false
	boolean apiAllowedSetting = false
	String comment

	static mapping = {
		table 'settings'
		cache true
		version false

		id column: 'setting_id'
		property column: 'property'
		value column: 'value', type: 'text'
		showInBackend column: 'show_in_backend'
		apiAllowedSetting column: 'api_allowed'
		comment column: 'comment', type: 'text'
	}

	static constraints = {
		property blank: false, maxSize: 50
		value nullable: true
		comment nullable: true
	}

	def beforeUpdate() {
		switch (property) {
			case DISABLE_EMAIL_SESSIONS:
			case DONT_SEND_EMAILS_TO:
			case EMAIL_ADDRESS_INFO_ERRORS:
			case EMAIL_MAX_NUM_EMAILS_PER_SESSION:
			case EMAIL_MAX_NUM_TRIES:
			case EMAIL_MIN_MINUTES_BETWEEN_SENDING:
			case EMAIL_WAITING_TIME:
			case IP_AUTHENTICATION:
			case LAST_UPDATED:
			case SALT:
			case PAYWAY_ADDRESS:
				return true
				break
			default:
				super.beforeUpdate()
		}
	}

	/**
	 * Return the setting for a given property
	 * @param property The property in question
	 * @param event Return the setting for this specific event, if not given, the current event is used instead
	 * @return The setting for the given property for the given event
	 */
	static Setting getSetting(String property, Event event = null) {
		List<Setting> settings = findAllByProperty(property, [cache: true])

		if (event) {
			(Setting) getByEvent(settings, event)
		}
		else {
			(Setting) getByEvent(settings)
		}
	}

	/**
	 * Returns a map with all settings (properties and their values) for the current event,
	 * but only those accessible by the API
	 * @return A map with all settings (properties and their values)
	 */
	static Map<String, String> getSettingsMapForApi() {
		Map<String, String> settings = new TreeMap<String, String>()

		withCriteria {
			eq('apiAllowedSetting', true)
			order('property', 'asc')
			order('event', 'asc')
		}.each {
			settings.put(it.property, it.value)
		}

		return settings
	}

	/**
	 * If the setting has multiple values (split by ';'), return an array with all values
	 * @return An array with all values
	 */
	String[] getMultipleValues() {
		return (value != null) ? value.split(';') as String[] : [] as String[]
	}

	/**
	 * If the setting holds a boolean value, return the value as a boolean
	 * @return The boolean value
	 */
	boolean getBooleanValue() {
		return value?.equals('1')
	}

	/**
	 * If the setting holds a date value, return the value as a date
	 * It won't throw an exception, but returns null instead, to prevent getProperties() to fail
	 * @return The date value
	 */
	Date getDateValue() {
		try {
			if (value) {
				return SETTINGS_DATE_FORMAT.parse(value)
			}

			return null
		}
		catch (ParseException pe) {
			return null
		}
	}

	@Override
	protected EventDomain getTenantRecord(Event event) {
		EventDomain[] domains = executeQuery(
				""" FROM Setting
                    WHERE property = :property
                    AND event.id = :eventID""",
				[property: property, eventID: event.id, max: 1])

		if (domains.length == 0) {
			return null
		}
		else {
			return domains[0]
		}
	}

	@Override
	String toString() {
		"${property}: ${value}"
	}
}
