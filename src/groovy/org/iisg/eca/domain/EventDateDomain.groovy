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
    
    def beforeInsert() {
        if (!date && pageInformation.date) {
            date = pageInformation.date
        }
    }
    
    def beforeUpdate() {
        if (this.date != pageInformation.date) {
           /* this.withNewSession {
                def newInstance = this.newInstance()
                newInstance.params = this.params
                newInstance.date = pageInformation.date
                newInstance.save()
            }
            return false   */
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
