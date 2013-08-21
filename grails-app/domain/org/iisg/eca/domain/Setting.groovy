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
