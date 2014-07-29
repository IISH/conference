package org.iisg.eca.service

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantState

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

import org.springframework.context.i18n.LocaleContextHolder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import java.text.Normalizer

/**
 * Service responsible for requesting participant data
 */
class ParticipantService {
    def dataSource
    def messageSource
    def pageInformation

    /**
     * Returns all participants of the current event date with filters set by the user
     * @param params The parameters of the current request containing the filters set by the user
     * @return A map with characters A-Z as key, which lists all participants which last names start with the keys character
     */
    Map<String, Object[]> getParticipants(GrailsParameterMap params) {
        Map<String, Object[]> participants = [:]

        // Now loop over all participants returned by the database and create a map out if it
        getParticipantsWithFilters(params).eachWithIndex { participant, i ->
	        String character = Normalizer
			        .normalize(participant[2].toUpperCase()[0], Normalizer.Form.NFKD)
			        .replaceAll('\\p{InCombiningDiacriticalMarks}+', '')
	        List list = participants.get(character, new ArrayList())
            list.add([participant[0], "${participant[1]} ${participant[2]}", "(#${participant[0]}, ${participant[3]})", i])
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
            GROUP BY participant_states.participant_state
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
}

