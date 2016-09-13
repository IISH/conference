package org.iisg.eca.domain

/**
 * Domain class of table holding all possible age ranges
 */
class AgeRange extends EventDomain {
    Integer minAge
    Integer maxAge

    static hasMany = [participants: ParticipantDate]

    static mapping = {
        table 'age_ranges'
	    cache true
        version false

        id          column: 'age_range_id'
        minAge      column: 'min_age'
        maxAge      column: 'max_age'
    }

    static constraints = {
        minAge  min: 0, max: 100, nullable: true
        maxAge  min: 0, max: 100, nullable: true
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'minAge',
            'maxAge'
    ]

    @Override
    String toString() {
        if (minAge && maxAge) {
            return "$minAge - $maxAge"
        }

        if (minAge) {
            return "$minAge >"
        }

        if (maxAge) {
            return "< $maxAge"
        }
    }
}
