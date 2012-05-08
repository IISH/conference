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
        if (pageInformation.date) {
            event = pageInformation.date.event
        }
        else {
            event = null 
        }
    }
}
