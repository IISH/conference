package org.iisg.eca

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
        hideDeleted(condition: 'deleted=0')
        hideDisabled(condition: 'enabled=1')
    }

    def enable(boolean enabled) {
        this.enabled = enabled
    }

    @Override
    def void delete() {
        deleted = true
    }

    @Override
    def void delete(Map props) {
        deleted = true
    }
}
