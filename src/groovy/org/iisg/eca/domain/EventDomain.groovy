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
    
    def beforeInsert() {
        if (pageInformation.date) {
            event = pageInformation.date.event
        }
        else {
            event = null 
        }
    }
    
    def beforeUpdate() {
        if (this.date != pageInformation.date) {
            // TODO: Create trigger???
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
