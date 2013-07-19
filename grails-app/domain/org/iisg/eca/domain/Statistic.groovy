package org.iisg.eca.domain

class Statistic extends EventDateDomain {
    String property
    String value

    static constraints = {
        property    blank: false,   maxSize: 50
        value       nullable: true
    }

    static mapping = {
        table 'statistics'
        version false

        id          column: 'statistic_id'
        property    column: 'property'
        value       column: 'value',        type: 'text'
    }
}
