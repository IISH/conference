package org.iisg.eca.domain

/**
 * Domain class of table holding all possible participant types
 */
class ParticipantType extends EventDomain {
    String type

    static hasMany = [  sessionParticipants:    SessionParticipant,
                        rulesFirst:             ParticipantTypeRule,
                        rulesSecond:            ParticipantTypeRule]
    static mappedBy = [ rulesFirst:             'firstType',
                        rulesSecond:            'secondType']

    static mapping = {
        table 'participant_types'
        version false

        id      column: 'participant_type_id'
        type    column: 'type'
    }

    static constraints = {
        type    blank: false,   maxSize: 30
    }

    @Override
    String toString() {
        type
    }
}
