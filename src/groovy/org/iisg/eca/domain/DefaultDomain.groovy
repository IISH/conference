package org.iisg.eca.domain

/**
 * A default domain class with requirements used by a lot of domain classes
 */
abstract class DefaultDomain {
    boolean enabled = true
    boolean deleted = false

    static mapping = {
        enabled     column: 'enabled'
        deleted     column: 'deleted'
    }

    static hibernateFilters = {
        hideDeleted(condition: 'deleted = 0')
        hideDisabled(condition: 'enabled = 1')
    }  

    void enable(boolean enabled) {
        this.enabled = enabled
    }

    @Override
    void delete() {
        deleted = true
    }

    @Override
    void delete(Map props) {
        deleted = true
    }
}
