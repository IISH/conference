package org.iisg.eca.domain

import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl

/**
 * Domain class of table holding all settings
 */
class Setting extends EventDomain {
    def roleHierarchy

    static final String LAST_UPDATED = 'last_updated'
    static final String MAX_PAPERS_PER_PERSON_PER_SESSION = 'max_papers_per_person_per_session'
    static final String SALT = 'salt'
    static final String ROLE_HIERARCHY = 'role_hierarchy'
    static final String SHOW_PROGRAMME_ONLINE = 'show_programme_online'
    static final String EMAIL_MAX_NUM_TRIES = 'email_max_num_tries'
    static final String EMAIL_MIN_MINUTES_BETWEEN_SENDING = 'email_min_minutes_between_sending'
    static final String EMAIL_MAX_NUM_EMAILS_PER_SESSION = 'email_max_num_emails_per_session'
    static final String DISABLE_EMAIL_SESSIONS = 'disable_email_sessions'
    static final String DEFAULT_ORGANISATION_EMAIL = 'default_organisation_email'
    static final String EMAIL_ADDRESS_INFO_ERRORS = 'email_address_info_errors'
    static final String WEB_ADDRESS = 'web_address'
    static final String CHANGE_USER = 'change_user'
    static final String DONT_SEND_EMAILS_TO = 'dont_send_emails_to'
    static final String APPLICATION_TITLE = 'application_title'

    static final String BANNER_IMG = 'banner_img'
    static final String BANNER_BG_IMG = 'banner_bg_img'
    static final String LABEL_COLOR = 'label_color'
    static final String MAIN_COLOR_LIGHT = 'main_color_light'
    static final String MAIN_COLOR_DARK = 'main_color_dark'
    static final String MAIN_COLOR_BG = 'main_color_bg'

    String property
    String value
    boolean showInBackend = false

    static mapping = {
        table 'settings'
        cache true
        version false

        id              column: 'setting_id'
        property        column: 'property'
        value           column: 'value'
        showInBackend   column: 'show_in_backend'
    }

    static constraints = {
        property        blank: false,   maxSize: 50
        value           blank: false,   maxSize: 255
    }
    
    def beforeUpdate() {
        switch (property) {
            case MAX_PAPERS_PER_PERSON_PER_SESSION:
            case SHOW_PROGRAMME_ONLINE:
            case DEFAULT_ORGANISATION_EMAIL:
            case EMAIL_ADDRESS_INFO_ERRORS:
            case WEB_ADDRESS:
            case CHANGE_USER:
            case DONT_SEND_EMAILS_TO:
            case APPLICATION_TITLE:
            case BANNER_IMG:
            case BANNER_BG_IMG:
            case LABEL_COLOR:
            case MAIN_COLOR_LIGHT:
            case MAIN_COLOR_DARK:
            case MAIN_COLOR_BG:
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
