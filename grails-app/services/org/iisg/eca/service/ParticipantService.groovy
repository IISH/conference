package org.iisg.eca.service

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

import java.text.Normalizer
import org.springframework.context.i18n.LocaleContextHolder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import org.iisg.eca.domain.*

/**
 * Service responsible for requesting participant data
 */
class ParticipantService {
    def dataSource
    def messageSource
    def pageInformation
	def hibernateFilterHelper

    /**
     * Returns all participants of the current event date with filters set by the user
     * @param params The parameters of the current request containing the filters set by the user
     * @return A map with characters A-Z as key, which lists all participants which last names start with the keys character
     */
    Map<String, List> getParticipants(GrailsParameterMap params) {
	    Map<String, List> participants = new LinkedHashMap<String, List>()

        // Now loop over all participants returned by the database and create a map out if it
        getParticipantsWithFilters(params).eachWithIndex { participant, i ->
	        Long id = (Long) participant[0]
	        String firstName = participant[1]
	        String lastName = participant[2]
	        String participantState = participant[3]

	        String character = lastName.toUpperCase().charAt(0).toString()
	        character = Normalizer
			        .normalize(character, Normalizer.Form.NFKD)
			        .replaceAll('\\p{InCombiningDiacriticalMarks}+', '')

	        List list = participants.get(character, new ArrayList())
            list.add([id, "$firstName $lastName", "(#$id, $participantState)", i])
        }

        participants
    }

    /**
     * Returns all participants of the current event date with filters set by the user
     * @param params The parameters of the current request containing the filters set by the user
     * @return A list of all filtered participants
     */
    List<Object[]> getParticipantsWithFilters(GrailsParameterMap params) {
        Long stateId = params.long('filter-state')
        String type = params['filter-type']
        String filter = params['filter-text']

        (List<Object[]>) ParticipantDate.withCriteria {
            // Filter the results based on a category with keywords
            if (type && filter && !filter.isEmpty()) {
                user {
                    String[] words = filter.split()
                    and {
                        switch (type) {
                            case "id":
                                for (String w : words) {
                                    if (!w.isEmpty() && w.isLong()) {
                                        or {
                                            eq('id', w.toLong())
                                        }
                                    }
                                }
                                break
                            case "organisation":
                                for (String w : words) {
                                    if (!w.isEmpty()) {
                                        or {
                                            like('organisation', "%${w}%")
                                            like('department', "%${w}%")
                                        }
                                    }
                                }
                                break
                            case "name":
                                for (String w : words) {
                                    if (!w.trim().isEmpty()) {
                                        or {
                                            like('lastName', "%${w}%")
                                            like('firstName', "%${w}%")
                                        }
                                    }
                                }
                                break
                            case "address":
                                for (String w : words) {
                                    if (!w.trim().isEmpty()) {
                                        or {
                                            like('address', "%${w}%")
                                            like('city', "%${w}%")
                                        }
                                    }
                                }
                                break
                        }
                    }
                }
            }

            // State which properties to return
            projections {
                user {
                    property('id')
                    property('firstName')
                    property('lastName')
                }

                // Filter the results based on the state selected by the user
                state {
                    if (stateId != null && (stateId >= 0)) {
                        eq('id', stateId)
                    }
                    property('state')
                }
            }

            user {
                order('lastName', 'asc')
                order('firstName', 'asc')
            }
        }
    }

    /**
     * Returns all possible participant states and the number of hits for every single state for the current event date
     * @return A list of arrays with the value, label and the number of hits
     */
    List<Object[]> getParticipantCounts() {
        Integer count = 0
        Sql sql = new Sql(dataSource)

        // Due to unusual join, not possible with HQL
        List<Object[]> states = sql.rows('''
            SELECT participant_states.participant_state_id, participant_states.participant_state, count( participant_date.participant_date_id )
            FROM participant_states
            LEFT JOIN participant_date
            ON participant_date.participant_state_id = participant_states.participant_state_id
            AND participant_date.date_id = :dateId
            AND participant_date.deleted = 0
            GROUP BY participant_states.participant_state_id, participant_states.participant_state
            ORDER BY participant_states.participant_state_id
        ''', [dateId: pageInformation.date.id]).collect { it.values() as Object[] }

        states.each { count = count + it[2] }
        states.add(0, [new Integer(-1), messageSource.getMessage('default.all.label', null, LocaleContextHolder.locale), count])
        states
    }

    /**
     * Returns an overview of all days and extras the accepted participants signed up for
     * @return An overview for all accepted participants
     */
    List<GroovyRowResult> getParticipantPresentOverview() {
        Sql sql = new Sql(dataSource)

        // Due to group_concat function, not possible with HQL
        sql.rows('''
            SELECT u.user_id, u.lastname, u.firstname,
                CAST(GROUP_CONCAT(DISTINCT d.day_id ORDER BY d.day_number) AS CHAR) AS days,
                CAST(GROUP_CONCAT(DISTINCT e.extra_id ORDER BY e.extra) AS CHAR) AS extras
            FROM users AS u
            INNER JOIN participant_date AS pd
            ON u.user_id = pd.user_id
            LEFT JOIN participant_day AS pday
            ON u.user_id = pday.user_id
            LEFT JOIN days AS d
            ON pday.day_id = d.day_id
            LEFT JOIN participant_date_extra AS pde
            ON pd.participant_date_id = pde.participant_date_id
            LEFT JOIN extras AS e
            ON pde.extra_id = e.extra_id
            WHERE u.deleted = 0
            AND pd.date_id = :dateId
            AND pd.deleted = 0
            AND pd.participant_state_id IN (:participantDataChecked, :participant)
            GROUP BY u.user_id
            ORDER BY u.lastname, u.firstname
        ''', [dateId: pageInformation.date.id, participantDataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED, participant: ParticipantState.PARTICIPANT])
    }

    /**
     * Returns the number of participants that signed up for each conference day
     * @return A map with the day id as the key and the number of participants for that day
     */
    Map<Long, Long> getDaysCount() {
        Day.executeQuery('''
            SELECT d.id, COUNT(d.id) AS total
            FROM Day AS d
            INNER JOIN d.participantPresent AS pday
            INNER JOIN pday.user AS u
            INNER JOIN u.participantDates AS pd
            WHERE u.deleted = false
            AND pd.date.id = :dateId
            AND pd.deleted = false
            GROUP BY d.id
            ORDER BY d.dayNumber
        ''', [dateId: pageInformation.date.id]).collectEntries {
            [(it[0]) : (it[1])]
        }
    }

    /**
     * Returns the number of participants that signed up for each extra
     * @return A map with the extra id as the key and the number of participants for that extra
     */
    Map<Long, Long> getExtrasCount() {
        Extra.executeQuery('''
            SELECT e.id, COUNT(e.id) AS total
            FROM Extra AS e
            INNER JOIN e.participantDates AS pd
            WHERE pd.date.id = :dateId
            AND pd.deleted = false
            GROUP BY e.id
            ORDER BY e.extra
        ''', [dateId: pageInformation.date.id]).collectEntries {
            [(it[0]) : (it[1])]
        }
    }

	/**
	 * Creates a new user and/or participant for the current event date
	 * @param emailAddress The email address of the user in question
	 * @return The new user
	 */
	User createNewUser(String emailAddress, boolean createParticipant) {
		ParticipantDate participant = null
		User user = User.findByEmail(emailAddress)

		// There is no user with this email address, so create one
		if (!user) {
			user = new User(lastName: 'n/a', firstName: 'n/a', email: emailAddress, sendNewPassword: true)
		}
		else if (createParticipant) {
			// Does the participant exist in the database already, maybe deleted?
			hibernateFilterHelper.disableSoftDeleteFilter()
			participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
			hibernateFilterHelper.enableSoftDeleteFilter()

			// Make sure that we undo the deletion if found
			if (participant) {
				participant.deleted = false
				return user
			}
		}

		// This user is not a participant yet, but the user indicated he/she wants to make him/her one
		if (createParticipant && !participant) {
            participant = new ParticipantDate(
                    state: ParticipantState.get(ParticipantState.NEW_PARTICIPANT),
					feeState: FeeState.get(FeeState.NO_FEE_SELECTED)
            )
            user.addToParticipantDates(participant)
		}

        // Make sure that we undo the deletion if found deleted
        user.deleted = false

        return user
	}

    /**
     * Merges two users
     * @param userA The (participant) user which should remain
     * @param userB The user which should be merged into user A
     * @return Whether the merge was successful
     */
    boolean mergeParticipantUsers(User userA, User userB) {
        ParticipantDate.disableHibernateFilter('dateFilter')
        ParticipantDate.disableHibernateFilter('hideDeleted')

        // Collect all participant dates for the users and compare them
        Set<ParticipantDate> participantDatesA = userA.participantDates
        Set<ParticipantDate> participantDatesB = userB.participantDates

        Set<Long> datesConflict = participantDatesA
                .collect { it.dateId }
                .intersect(participantDatesB.collect { it.dateId })
        // TODO: For later: datesConflict.remove(pageInformation.date.id)

        // If there are no conflicts, continue with the merge
        if (datesConflict.size() > 0)
            return false

        // TODO: For later: Start by merging all of user Bs participant dates
        /*participantDatesB.each { ParticipantDate participantB ->
            if (!success) return
            ParticipantDate participantA = null

            // When we merge two participants of the same (current) date
            if (participantB.date == pageInformation.date) {
                participantA = userA.getParticipantForDate(pageInformation.date)

                // Move certain properties of participant B to participant A
                ParticipantVolunteering.executeUpdate(
                        'update ParticipantVolunteering set participantDate = ? where participantDate = ?',
                        [participantA, participantB])
                Order.executeUpdate(
                        'update Order set participantDate = ? where participantDate = ?',
                        [participantA, participantB])

                new ArrayList<>(participantB.extras).each { Extra extra ->
                    extra.removeFromParticipantDates(participantB)
                    extra.addToParticipantDates(participantA)
                }

                new ArrayList<>(participantB.favoriteSessions).each { Session session ->
                    session.removeFromParticipantsFavorite(participantB)
                    session.addToParticipantsFavorite(participantA)
                }

                participantA.accompanyingPersons = participantB.accompanyingPersons
                participantB.accompanyingPersons = null

                // Check which creation date is older
                if (participantB.dateAdded.before(participantA.dateAdded)) {
                    participantA.dateAdded = participantB.dateAdded
                }

                // Move payment participant B to participant A, if newer
                if (participantB.paymentId) {
                    if (!participantA.paymentId) {
                        participantA.paymentId = participantB.paymentId
                        participantB.paymentId = null
                    }
                    else {
                        Order orderA = participantA.findOrder()
                        Order orderB = participantB.findOrder()

                        if (orderB.createdAt.before(orderA.createdAt)) {
                            participantA.paymentId = participantB.paymentId
                            participantB.paymentId = null
                        }
                    }
                }

                // Mark participant B a duplicate registration
                participantB.state = ParticipantState.get(ParticipantState.REMOVED_DOUBLE_ENTRY)
            }

            // Change the user
            participantB.user = userA

            success = (!participantA || participantB.save(flush: true)) && participantA.save(flush: true)
        }

        // If we successfully merged the participants, continue with the users
        if (!success)
            return false;*/

        // Go over all foreign keys and update them
        ParticipantDate.executeUpdate('update ParticipantDate set user = ? where user = ?', [userA, userB])
        NetworkChair.executeUpdate('update NetworkChair set chair = ? where chair = ?', [userA, userB])
        UserRole.executeUpdate('update UserRole set user = ? where user = ?', [userA, userB])
        Paper.executeUpdate('update Paper set user = ? where user = ?', [userA, userB])
        SessionParticipant.executeUpdate('update SessionParticipant set user = ? where user = ?', [userA, userB])
        SentEmail.executeUpdate('update SentEmail set user = ? where user = ?', [userA, userB])
        UserPage.executeUpdate('update UserPage set user = ? where user = ?', [userA, userB])
        ParticipantDay.executeUpdate('update ParticipantDay set user = ? where user = ?', [userA, userB])

        Session.executeUpdate('update Session set addedBy = ? where addedBy = ?', [userA, userB])
        Paper.executeUpdate('update Paper set addedBy = ? where addedBy = ?', [userA, userB])
        SessionParticipant.executeUpdate('update SessionParticipant set addedBy = ? where addedBy = ?', [userA, userB])

        new ArrayList<>(userB.groups).each { Group group ->
            group.removeFromUsers(userB)
            group.addToUsers(userA)
        }

        new ArrayList<>(userB.dateTimesNotPresent).each { SessionDateTime sessionDateTime ->
            sessionDateTime.removeFromUsersNotPresent(userB)
            sessionDateTime.addToUsersNotPresent(userA)
        }

        // Switch email addresses by using temporary values
        String emailA = userA.email
        String emailB = userB.email
        userA.email = emailA + ".conference-shs.org"
        userB.email = emailB + ".conference-shs.org"

        // Check which creation date is older
        if (userB.dateAdded.before(userA.dateAdded)) {
            userA.dateAdded = userB.dateAdded
        }

        // Mark userB merged and deleted
        userB.softDelete()
        userB.mergedWith = userA

        boolean success = userA.save(flush: true) && userB.save(flush: true)

        // Now actually swap the email addresses and update the addedBy
        if (success) {
            userA.email = emailB
            userB.email = emailA

            User.executeUpdate('update User set addedBy = ? where addedBy = ?', [userA, userB])
            ParticipantDate.executeUpdate('update ParticipantDate set addedBy = ? where addedBy = ?', [userA, userB])

            success = userA.save() && userB.save()
        }

        return success
    }
}
