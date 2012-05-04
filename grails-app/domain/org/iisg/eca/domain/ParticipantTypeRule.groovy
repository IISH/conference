package org.iisg.eca.domain

class ParticipantTypeRule extends EventDomain {
    ParticipantType firstType
    ParticipantType secondType

    static belongsTo = [firstType: ParticipantType, secondType: ParticipantType]

    static mapping = {
        table 'participant_type_rules'
        version false

        id          column: 'participant_type_rule_id'
        firstType   column: 'participant_type_1_id'
        secondType  column: 'participant_type_2_id'
    }

    static List<ParticipantTypeRule> getRulesForParticipantType(ParticipantType participantType) {
        ParticipantTypeRule.findAllByFirstTypeOrSecondType(participantType, participantType)
    }

    @Override
    String toString() {
        "${firstType} : ${secondType}"
    }
}
