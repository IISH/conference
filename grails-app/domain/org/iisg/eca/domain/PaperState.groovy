package org.iisg.eca.domain

/**
 * Domain class of table holding all possible paper states
 */
class PaperState extends EventDomain {
    static final long NO_PAPER = 0L
    static final long NEW_PAPER = 1L
    static final long PAPER_ACCEPTED = 2L
    static final long PAPER_NOT_ACCEPTED = 3L
    static final long PAPER_IN_CONSIDERATION = 4L

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
