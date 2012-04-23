package org.iisg.eca.domain

abstract class EventDateDomain extends DefaultDomain {
    def pageInformation
    
    EventDate date
    
    static belongsTo = EventDate
    
    static constraints = {
        date   nullable: true
    }
    
    static mapping = {
        date    column: 'date_id'
    }
    
    static hibernateFilters = {
        dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
    }
    
    def beforeInsert() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
        else {
            date = null 
        }
    }
    
    def beforeUpdate() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
        else {
            date = null 
        }
    }
}
