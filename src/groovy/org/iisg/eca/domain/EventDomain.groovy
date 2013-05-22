package org.iisg.eca.domain

/**
 * The abstract domain class for all domain classes
 * which contain data to be filtered per event tenant
 */
abstract class EventDomain extends DefaultDomain {
    /**
     * Contains the tenant to filter on
     */
    static def pageInformation

    /**
     * The event tenant
     */
    Event event
    
    boolean internalUpdate = false
    
    static belongsTo = Event
    
    static transients = ['internalUpdate']
    
    static constraints = {
        event   nullable: true
    }
    
    static mapping = {
        event   column: 'event_id'
    }
    
    static hibernateFilters = {
        hideDeleted(condition: 'deleted = 0', default: true)
        hideDisabled(condition: 'enabled = 1')
        eventFilter(condition: '(event_id = :eventId OR event_id IS NULL)', types: 'long')
    }

    /**
     * Make sure that when a new record is inserted in the database,
     * the current event tenant is also set
     */
    def beforeInsert() {
        if (!event && pageInformation.date) {
            event = pageInformation.date.event
        }
    }
    
    /**
     * Make sure that when a record is updated from a different event tenant (general setting),
     * a new record is inserted with the changes for the current event tenant only
     */
    def beforeUpdate() {
        // Make sure we can also reach 'this' from within a closure
        def thizz = this
        
        if (internalUpdate) {
            return true
        }
        else if (thizz.event != pageInformation.date?.event) {
            // Delete the old tenant record if it exists: we'll make a new one
            def tenantRecord = thizz.getTenantRecord(pageInformation.date?.event)
            tenantRecord?.delete()
            
            // In a new Hibernate session, change the update to an insert for this event
            thizz.withNewSession {
                def instance = thizz.class.newInstance()
                instance.properties = thizz.properties
                instance.event = pageInformation.date?.event
                instance.save()
            }
            
            // And cancel this update
            return false
        }
    }
    
    static def getByEvent(List list) {
        getByEvent(list, pageInformation.date?.event)
    }

    /**
     * Searches the list and will try to return the one specifically specified for the current event
     * @return The element from the list for the current event if available
     */
    static def getByEvent(List list, Event event) {
        def element = null

        if (list.size() > 0) {
            element = list.find { it.event?.id == event?.id }
            if (!element) {
                element = list.get(0)
            }
        }
        
        element
    }
    
    /**
     * When a record is changed from a global update to a tenant update, 
     * make sure that the tenant record exists.
     * @param event The event to check against
     * @return The tenant record of this object, if it exists
     */
    protected EventDomain getTenantRecord(Event event) {
        return null
    }
}
