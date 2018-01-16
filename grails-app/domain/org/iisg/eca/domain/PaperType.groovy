package org.iisg.eca.domain

/**
 * Domain class of table holding all possible paper types
 */
class PaperType extends EventDomain {
    String type

    static hasMany = [papers: Paper]

    static mapping = {
        table 'paper_types'
        cache true
        version false

        id      column: 'paper_type_id'
        type    column: 'type'
    }
    
    static constraints = {
        type    blank: false,   maxSize: 30
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'type'
    ]

    @Override
    String toString() {
        type
    }
}
