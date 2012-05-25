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

    String property
    String value

    static mapping = {
        table 'settings'
        cache true
        version false

        id          column: 'setting_id'
        property    column: 'property'
        value       column: 'value'
    }

    static constraints = {
        property    blank: false,   maxSize: 50
        value       blank: false,   maxSize: 255
    }

    /**
     * Searches the settings table for the specified property
     * If there are multiple results, it will try to return the one specifically specified for the current event
     * @return The settings of the specified property
     */
    static Setting getByProperty(String property) {
        List<Setting> settings = Setting.findAllByProperty(property)
        Setting setting = null

        if (settings.size() > 0) {
            setting = settings.find { it.event?.id == pageInformation.date?.event?.id }
            if (!setting) {
                setting = settings.get(0)
            }
        }

        setting
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
    String toString() {
        "${property}: ${value}"
    }
}
