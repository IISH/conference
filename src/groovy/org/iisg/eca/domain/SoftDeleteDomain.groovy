package org.iisg.eca.domain

/**
 * A default domain class with soft delete functionality
 */
abstract class SoftDeleteDomain {
    boolean deleted = false

    static mapping = {
        deleted     column: 'deleted'
    }

    static hibernateFilters = {
        hideDeleted(condition: 'deleted = 0', default: true)
    }

    void softDelete() {
        deleted = true
    }
}
