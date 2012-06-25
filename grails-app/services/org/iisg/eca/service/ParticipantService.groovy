package org.iisg.eca.service

import org.iisg.eca.domain.ParticipantDate

import groovy.sql.Sql
import org.springframework.context.i18n.LocaleContextHolder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

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
        getParticipantsWithFilters(params).each { participant ->
            List list = participants.get(participant[2].toUpperCase()[0], new ArrayList())
            list.add([participant[0], "${participant[1]} ${participant[2]}", "(#: ${participant[0]}, ${participant[3]})"])
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
                    filter = filter.split().join('%')
                    or {
                        switch (type) {
                            case "organisation":
                                like('organisation', "%${filter}%")
                                like('department', "%${filter}%")
                                break
                            case "name":
                                like('lastName', "%${filter}%")
                                like('firstName', "%${filter}%")
                                break
                            case "address":
                                like('address', "%${filter}%")
                                like('city', "%${filter}%")
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
}

