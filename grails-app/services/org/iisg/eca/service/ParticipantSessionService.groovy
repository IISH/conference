package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType

/**
 * Service responsible for requesting participant data in order to allow them in a session
 */
class ParticipantSessionService {
    def pageInformation

    /**
     * Returns a list of all participants for the current event date
     * @return A list of participants
     */
    List<User> getAllParticipants() {
        User.withCriteria {
            participantDates {
                eq('date.id', pageInformation.date.id)
                eq('deleted', false)
            }
        }
    }

    /**
     * Returns all participants of the current event date and session with the given type
     * @param participantType The participant type
     * @param session The session to look for these participants
     * @return A list of participants
     */
    List<User> getParticipantsOfType(ParticipantType participantType, Session session) {
        getParticipantsOfType(participantType.id, session.id)
    }

    /**
     * Returns all participants of the current event date and session with the given type
     * @param participantTypeId The id of the participant type
     * @param sessionId The id of the session to look for these participants
     * @return A list of participants
     */
    List<User> getParticipantsOfType(long participantTypeId, long sessionId) {
        User.withCriteria {
            participantDates {
                eq('date.id', pageInformation.date.id)
                eq('deleted', false)
            }

            sessionParticipants {
                eq('session.id', sessionId)
                eq('type.id', participantTypeId)
                eq('deleted', false)
            }
        }
    }

    /**
     * Returns all participants with papers of the current event date which are still not assigned to a session
     * @return A list of participants
     */
    List<User> getParticipantsWithoutOpenPapers() {
        User.executeQuery('''
            SELECT u
            FROM User AS u
            LEFT JOIN u.papers AS p
            WHERE p.id IS NULL
            OR p.session.id IS NOT NULL
            OR p.date.id <> :dateId
        ''', [dateId: pageInformation.date.id])
    }

    /**
     * Returns a list of arrays with the equipment needed for the given session
     * and the number of participants that need that type of equipment
     * @param session The session to look for this equipment
     * @return A list of arrays with the equipment and number of participant requesting it
     */
    List<Object[]> getEquipmentForSession(Session session) {
        getEquipmentForSession(session.id)
    }

    /**
     * Returns a list of arrays with the equipment needed for the given session
     * and the number of participants that need that type of equipment
     * @param sessionId The id of the session to look for this equipment
     * @return A list of arrays with the equipment and number of participant requesting it
     */
    List<Object[]> getEquipmentForSession(long sessionId) {
        (List<Object[]>) Paper.executeQuery('''
            SELECT e.equipment, count(*)
            FROM Paper AS p
            INNER JOIN p.equipment AS e
            WHERE p.session.id = :sessionId
            GROUP BY e
        ''', [sessionId: sessionId])
    }
}
