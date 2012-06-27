package org.iisg.eca.domain

abstract class EventDateDomain extends DefaultDomain {
    static def pageInformation
    
    EventDate date
    
    static belongsTo = EventDate
    
    static constraints = {
        date   nullable: true
    }
    
    static mapping = {
        date    column: 'date_id'
    }
    
    static hibernateFilters = {
        hideDeleted(condition: 'deleted = 0', default: true)
        hideDisabled(condition: 'enabled = 1')
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

        if (thizz.date != pageInformation.date) {
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

    /**
     * Searches the list and will try to return the one specifically specified for the current event date
     * @return The element from the list for the current event date if available
     */
    static def getByDate(List list) {
        def element = null

        if (list.size() > 0) {
            element = list.find { it.date?.id == pageInformation.date?.id }
            if (!element) {
                element = list.get(0)
            }
        }

        element
    }
}
