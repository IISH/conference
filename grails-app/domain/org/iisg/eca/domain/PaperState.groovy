package org.iisg.eca.domain

/**
 * Domain class of table holding all possible paper states
 */
class PaperState extends EventDateDomain {
    String description
    String shortDescription
    boolean sessionStateTrigger = false

    static hasMany = [papers: Paper]

    static mapping = {
        table 'paper_states'
        version false

        id                  column: 'paper_state_id'
        description         column: 'description'
        shortDescription    column: 'short_description'
        sessionStateTrigger column: 'session_state_trigger'
    }

    static constraints = {
        description         blank: false,   maxSize: 50
        shortDescription    blank: false,   maxSize: 10
    }

    @Override
    String toString() {
        description
    }
}
