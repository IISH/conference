package org.iisg.eca.domain

abstract class EventDomain extends DefaultDomain {
    static def pageInformation
    
    Event event
    
    static belongsTo = Event
    
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

        if (thizz.event != pageInformation.date?.event) {
            // In a new Hibernate session, change the update to an insert for this event
            thizz.withNewSession {
                def newInstance = thizz.class.newInstance()
                newInstance.properties = thizz.properties
                newInstance.event = pageInformation.date?.event
                newInstance.save()
            }
            // And cancel this update
            return false
        }
    }

    /**
     * Searches the list and will try to return the one specifically specified for the current event
     * @return The element from the list for the current event if available
     */
    static def getByEvent(List list) {
        def element = null

        if (list.size() > 0) {
            element = list.find { it.event?.id == pageInformation.date?.event?.id }
            if (!element) {
                element = list.get(0)
            }
        }

        element
    }
}
