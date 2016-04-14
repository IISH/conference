package org.iisg.eca.domain

/**
 * Domain class of table holding all possible participant types
 */
class ParticipantType extends EventDomain {
    static final long CHAIR = 6L
    static final long ORGANIZER = 7L
    static final long AUTHOR = 8L
    static final long CO_AUTHOR = 9L
    static final long DISCUSSANT = 10L

    String type
    boolean withPaper = false
    int importance = 0

    static hasMany = [  sessionParticipants:    SessionParticipant,
                        rulesFirst:             ParticipantTypeRule,
                        rulesSecond:            ParticipantTypeRule]
    static mappedBy = [ rulesFirst:             'firstType',
                        rulesSecond:            'secondType']

    static mapping = {
        table 'participant_types'
	    cache true
        version false

        id          column: 'participant_type_id'
        type        column: 'type'
        withPaper   column: 'with_paper'
        importance  column: 'importance',           sort: 'desc'
        
        sessionParticipants cascade: 'all-delete-orphan'
        rulesFirst          cascade: 'all-delete-orphan'
        rulesSecond         cascade: 'all-delete-orphan'
    }

    static constraints = {
        type        blank: false,   maxSize: 30
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'type',
            'withPaper',
		    'notInCombinationWith.id'
    ]

	Set<ParticipantType> getNotInCombinationWith() {
		Set<ParticipantType> types = new HashSet<ParticipantType>()
		Set<ParticipantTypeRule> firstRules = ParticipantTypeRule.findAllByFirstType(this)
		Set<ParticipantTypeRule> secondRules = ParticipantTypeRule.findAllBySecondType(this)

		if (firstRules) {
			types.addAll(firstRules*.secondType)
		}
		if (secondRules) {
			types.addAll(secondRules*.firstType)
		}

		return types
	}

    @Override
    String toString() {
        type
    }
}
