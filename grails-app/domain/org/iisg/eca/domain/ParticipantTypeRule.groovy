package org.iisg.eca.domain

/**
 * Domain class of table holding all rules for participants allowed in one session
 * If there is already a participant of one of the two types, then the other type is not allowed
 */
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

    /**
     * Returns a list of all the rules, specifically for the gives participant type
     * @param participantType The type to get all the rules for in return
     * @return A list of rules
     */
    static List<ParticipantTypeRule> getRulesForParticipantType(ParticipantType participantType) {
        findAllByFirstTypeOrSecondType(participantType, participantType)
    }

    @Override
    String toString() {
        "${firstType} : ${secondType}"
    }
}
