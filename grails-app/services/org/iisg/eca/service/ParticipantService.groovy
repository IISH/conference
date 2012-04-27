package org.iisg.eca.service

import org.iisg.eca.domain.Role
import org.iisg.eca.domain.User
import org.iisg.eca.domain.ParticipantDate

import grails.orm.HibernateCriteriaBuilder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib

class ParticipantService {
     /**
     * Required for looking up the translated column names
     */
    protected static final ValidationTagLib MESSAGES = new ValidationTagLib()

    def pageInformation

    Map<String, Object[]> getParticipants(GrailsParameterMap params) {
        HibernateCriteriaBuilder criteria = User.createCriteria()
        Map<String, Object[]> participants = [:]

        Object[] prevParticipant = null
        List<String> states = new ArrayList<String>()

        Long dateId = params.long('filter-date')
        Long stateId = params.long('filter-state')
        String type = params['filter-type']
        String filter = params['filter-text']

        criteria.list {
            userRoles {
                eq('role.id', Role.findWhere(role: 'participant').id)
            }

            if (type && filter && !filter.isEmpty()) {
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

            projections {
                property('id')
                property('lastName')
                property('firstName')

                participantDates {
                    date {
                        if (dateId != null && (dateId >= 0)) {
                            eq('id', dateId)
                        }
                        property('yearCode')
                    }

                    state {
                        if (stateId != null && (stateId >= 0)) {
                            eq('id', stateId)
                        }
                        property('state')
                    }
                }
            }

            order('lastName', 'asc')
            order('firstName', 'asc')
        }.each { participant ->
            if (prevParticipant && (prevParticipant[0] != participant[0])) {
                List list = participants.get(prevParticipant[1].toUpperCase()[0], new ArrayList())
                list.add([prevParticipant[0], "${prevParticipant[2]} ${prevParticipant[1]} (${states.join(', ')})"])
                states.clear()
            }

            states.add("${participant[3]}: ${participant[4]}")
            prevParticipant = participant
        }

        if (prevParticipant) {
            List list = participants.get(prevParticipant[1].toUpperCase()[0], new ArrayList())
            list.add([prevParticipant[0], "${prevParticipant[2]} ${prevParticipant[1]} (${states.join(', ')})"])
        }

        participants
    }

    List<Object[]> getParticipantCounts(GrailsParameterMap params) {
        List<Object[]> states = null
        Integer count = 0

        ParticipantDate.withoutHibernateFilter('dateFilter') {
            states = ParticipantDate.executeQuery('''
                SELECT s.id, s.state, count(pd)
                FROM ParticipantDate pd
                RIGHT JOIN pd.state AS s
                GROUP BY s.state
                ORDER BY s.id''')
        }
        ParticipantDate.enableHibernateFilter('dateFilter').setParameter('dateId', pageInformation.date.id)

        // WHERE pd.date.id IN (:dates)
        // , [dates: pageInformation.date.event.dates.collect { it.id }]

        states.each { count = count + it[2] }
        states.add(0, [new Integer(-1), MESSAGES.message(code: 'default.all.label'), count])
        states
    }

    List<Object[]> getDatesList() {
        List<Object[]> dates = pageInformation.date.event.dates.collect { [it.id, it.yearCode] }
        dates.add(0, [new Integer(-1), MESSAGES.message(code: 'default.all.label')])
        dates
    }
}

