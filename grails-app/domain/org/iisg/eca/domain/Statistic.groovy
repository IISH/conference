package org.iisg.eca.domain

class Statistic extends EventDateDomain {
    String property
    String value
    String lastUpdated

    static constraints = {
        property    blank: false,   maxSize: 50
        value       blank: false,   maxSize: 255
        lastUpdated blank: false,   maxSize: 20
    }

    static mapping = {
        table 'statistics'
        version false

        id          column: 'statistic_id'
        property    column: 'property'
        value       column: 'value'
        lastUpdated column: 'last_updated'
    }
}
