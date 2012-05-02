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
        HibernateCriteriaBuilder criteria = ParticipantDate.createCriteria()
        Map<String, Object[]> participants = [:]

        Long stateId = params.long('filter-state')
        String type = params['filter-type']
        String filter = params['filter-text']

        criteria.list {
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

            projections {
                user {
                    property('id')
                    property('firstName')
                    property('lastName')
                }

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
        }.each { participant ->
            List list = participants.get(participant[2].toUpperCase()[0], new ArrayList())
            if (participant[3].isEmpty()) {
                list.add([participant[0], "${participant[1]} ${participant[2]}", "(#: ${participant[0]})"])
            }
            else {
                list.add([participant[0], "${participant[1]} ${participant[2]}", "(#: ${participant[0]}, ${participant[3]})"])
            }
        }

        participants
    }

    List<Object[]> getParticipantCounts(GrailsParameterMap params) {
        Integer count = 0
        List<Object[]> states = ParticipantDate.executeQuery('''
            SELECT s.id, s.state, count(pd)
            FROM ParticipantDate pd
            RIGHT JOIN pd.state AS s
            GROUP BY s.state
            ORDER BY s.id''')
        states.each { count = count + it[2] }
        states.add(0, [new Integer(-1), MESSAGES.message(code: 'default.all.label'), count])
        states
    }
}

