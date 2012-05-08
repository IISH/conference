package org.iisg.eca.domain

/**
 * Domain class of table holding all possible paper states
 */
class PaperState extends EventDateDomain {
    String description

    static hasMany = [papers: Paper]

    static mapping = {
        table 'paper_states'
        version false

        id          column: 'paper_state_id'
        description column: 'description'
    }

    static constraints = {
        description blank: false,   maxSize: 50
    }

    @Override
    String toString() {
        description
    }
}
