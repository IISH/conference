package org.iisg.eca.domain

/**
 * Domain class of table holding all settings
 */
class Setting extends EventDomain {
    static final String LAST_UPDATED = 'last_updated'
    static final String MAX_PAPERS_PER_PERSON_PER_SESSION = 'max_papers_per_person_per_session'

    String property
    String value

    static mapping = {
        table 'settings'
        version false
        cache true

        id          column: 'setting_id'
        property    column: 'property'
        value       column: 'value'
        event       column: 'event_id'
    }

    static constraints = {
        property    blank: false,   maxSize: 50
        value       blank: false,   maxSize: 255
        event       nullable: true
    }
    
    @Override
    String toString() {
        "${property}: ${value}"
    }
}
