package org.iisg.eca.domain

/**
 * The abstract domain class for all domain classes
 * which contain data to be filtered per event date tenant
 */
abstract class EventDateDomain extends SoftDeleteDomain {
    /**
     * Contains the tenant to filter on
     */
    static def pageInformation

    /**
     * The event date tenant
     */
    EventDate date
    
    boolean internalUpdate = false
    
    static belongsTo = EventDate
    
    static transients = ['internalUpdate']
    
    static constraints = {
        date   nullable: true
    }
    
    static mapping = {
        date    column: 'date_id'
    }
    
    static hibernateFilters = {
        hideDeleted(condition: 'deleted = 0', default: true)
        dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
    }

    /**
     * Make sure that when a new record is inserted in the database,
     * the current event date tenant is also set
     */
    def beforeInsert() {
        if (!date && pageInformation.date) {
            date = pageInformation.date
        }
    }

    /**
     * Make sure that when a record is updated from a different event date tenant (general setting),
     * a new record is inserted with the changes for the current event date tenant only
     */
    def beforeUpdate() {
        // Make sure we can also reach 'this' from within a closure
        def thizz = this

        if (internalUpdate) {
            return true
        }
        else if (thizz.date != pageInformation.date) {
            // Delete the old tenant record if it exists: we'll make a new one
            def tenantRecord = thizz.getTenantRecord(pageInformation.date)
            tenantRecord?.delete()
            
            // In a new Hibernate session, change the update to an insert for this event date
            thizz.withNewSession {   
                def newInstance = thizz.class.newInstance()
                newInstance.properties = thizz.properties
                newInstance.date = pageInformation.date
                newInstance.save()
            }
            // And cancel this update
            return false
        }
    }
    
    static def getByDate(List list) {
        getByDate(list, pageInformation.date)
    }
    
    /**
     * Searches the list and will try to return the one specifically specified for the current event date
     * @return The element from the list for the current event date if available
     */
    static def getByDate(List list, EventDate date) {
        def element = null

        if (list.size() > 0) {
            element = list.find { it.date?.id == date?.id }
            if (!element) {
                element = list.get(0)
            }
        }

        element
    }
    
    /**
     * When a record is changed from a global update to a tenant update, 
     * make sure that the tenant record exists.
     * @param date The event date to check against
     * @return The tenant record of this object, if it exists
     */
    protected EventDomain getTenantRecord(EventDate date) {
        return null
    }
}
