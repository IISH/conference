package org.iisg.eca.domain

import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl

/**
 * Domain class of table holding all settings
 */
class Setting extends EventDomain {
    def roleHierarchy

	// Default settings
    static final String LAST_UPDATED = 'last_updated'
    static final String MAX_PAPERS_PER_PERSON_PER_SESSION = 'max_papers_per_person_per_session'
    static final String SALT = 'salt'
    static final String ROLE_HIERARCHY = 'role_hierarchy'
    static final String EMAIL_MAX_NUM_TRIES = 'email_max_num_tries'
    static final String EMAIL_MIN_MINUTES_BETWEEN_SENDING = 'email_min_minutes_between_sending'
    static final String EMAIL_MAX_NUM_EMAILS_PER_SESSION = 'email_max_num_emails_per_session'
    static final String EMAIL_WAITING_TIME = 'email_waiting_time'
    static final String DISABLE_EMAIL_SESSIONS = 'disable_email_sessions'
    static final String DEFAULT_ORGANISATION_EMAIL = 'default_organisation_email'
    static final String EMAIL_ADDRESS_INFO_ERRORS = 'email_address_info_errors'
    static final String MAIL_INVITATION_LETTERS_TO = 'mail_invitation_letters_to'
    static final String WEB_ADDRESS = 'web_address'
    static final String CHANGE_USER = 'change_user'
    static final String DONT_SEND_EMAILS_TO = 'dont_send_emails_to'
    static final String APPLICATION_TITLE = 'application_title'
    static final String IP_AUTHENTICATION = 'ip_authentication'
    static final String CHECK_ACCEPTED_IP = 'check_accepted_ip'
	static final String REFUND_ADMINISTRATION_COSTS = 'refund_administration_costs'
	static final String DEFAULT_FROM_EMAIL = 'default_from_email'

	// Show/hide settings
	static final String SHOW_PROGRAMME_ONLINE = 'show_programme_online'
	static final String SHOW_AWARD = 'show_award'
	static final String SHOW_STUDENT = 'show_student'
	static final String SHOW_CV	= 'show_cv'
	static final String SHOW_ACCOMPANYING_PERSONS = 'show_accompanying_persons'
	static final String SHOW_INVITATION_LETTER = 'show_invitation_letter'
	static final String SHOW_LOWER_FEE = 'show_lower_fee'
	static final String SHOW_CHAIR_DISCUSSANT_POOL = 'show_chair_discussant_pool'
	static final String SHOW_LANGUAGE_COACH_PUPIL = 'show_language_coach_pupil'

	// Layout settings
    static final String BANNER_IMG = 'banner_img'
    static final String BANNER_BG_IMG = 'banner_bg_img'
    static final String LABEL_COLOR = 'label_color'
    static final String MAIN_COLOR_LIGHT = 'main_color_light'
    static final String MAIN_COLOR_DARK = 'main_color_dark'
    static final String MAIN_COLOR_BG = 'main_color_bg'

	// PayWay settings
	static final String PAYWAY_ADDRESS = 'payway_address'
	static final String PAYWAY_PROJECT_ID = 'payway_project_id'
	static final String PAYWAY_PROJECT = 'payway_project'
	static final String PAYWAY_PASSPHRASE_IN = 'payway_passphrase_in'
	static final String PAYWAY_PASSPHRASE_OUT = 'payway_passphrase_out'

    String property
    String value
    boolean showInBackend = false

    static mapping = {
        table 'settings'
        version false

        id              column: 'setting_id'
        property        column: 'property'
        value           column: 'value',            type: 'text'
        showInBackend   column: 'show_in_backend'
    }

    static constraints = {
        property        blank: false,   maxSize: 50
        value           blank: false
    }

    def beforeUpdate() {
        switch (property) {
            case MAX_PAPERS_PER_PERSON_PER_SESSION:
            case DEFAULT_ORGANISATION_EMAIL:
            case EMAIL_ADDRESS_INFO_ERRORS:
            case WEB_ADDRESS:
            case CHANGE_USER:
            case DONT_SEND_EMAILS_TO:
            case APPLICATION_TITLE:
            case REFUND_ADMINISTRATION_COSTS:
            case SHOW_PROGRAMME_ONLINE:
            case SHOW_AWARD:
            case SHOW_STUDENT:
            case SHOW_CV:
            case SHOW_ACCOMPANYING_PERSONS:
            case SHOW_INVITATION_LETTER:
            case SHOW_LOWER_FEE:
            case SHOW_CHAIR_DISCUSSANT_POOL:
            case SHOW_LANGUAGE_COACH_PUPIL:
            case BANNER_IMG:
            case BANNER_BG_IMG:
            case LABEL_COLOR:
            case MAIN_COLOR_LIGHT:
            case MAIN_COLOR_DARK:
            case MAIN_COLOR_BG:
            case PAYWAY_ADDRESS:
            case PAYWAY_PROJECT_ID:
            case PAYWAY_PROJECT:
            case PAYWAY_PASSPHRASE_IN:
            case PAYWAY_PASSPHRASE_OUT:
                super.beforeUpdate()
                break
            default:
                return true
        }
    }

    def afterInsert() {
        setSetting()
    }

    def afterUpdate() {
        setSetting()
    }

    static Setting getSetting(String property, Event event = null) {
        List<Setting> settings = Setting.findAllByProperty(property, [cache: true])

        if (event) {
            (Setting) getByEvent(settings, event)
        }
        else {
            (Setting) getByEvent(settings)
        }
    }

    private void setSetting() {
        switch (property) {
            case ROLE_HIERARCHY:
                setHierarchy()
                break
        }
    }

    private void setHierarchy() {
        if (property == ROLE_HIERARCHY) {
            RoleHierarchyImpl roleHierarchy = (RoleHierarchyImpl) roleHierarchy
            roleHierarchy.setHierarchy(value)
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
