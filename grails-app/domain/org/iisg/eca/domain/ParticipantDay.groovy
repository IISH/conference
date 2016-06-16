package org.iisg.eca.domain

/**
 * Domain class of table holding all days that participants are present
 * according to their final registration information
 */
class ParticipantDay extends EventDateDomain {
    User user
    Day day

    static belongsTo = [User, Day]

    static mapping = {
        table 'participant_day'
        version false

        id      column: 'participant_day_id'
        user    column: 'user_id',  fetch: 'join'
        day     column: 'day_id',   fetch: 'join'
    }

    static List<Day> findAllDaysOfUser(User user) {
        ParticipantDay.withCriteria {
            eq('user.id', user.id)
            day {
                order('dayNumber', 'asc')
            }
        }*.day
    }

    @Override
    String toString() {
        "${user}: ${day}"
    }
}
