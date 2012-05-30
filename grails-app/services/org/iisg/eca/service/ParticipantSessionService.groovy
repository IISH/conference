package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantTypeRule
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.utils.ParticipantSessionInfo
import org.iisg.eca.domain.ParticipantDate

/**
 * Service responsible for requesting participant data in order to allow them in a session
 */
class ParticipantSessionService {
    /**
     * Service with information about the current page, such as the current event date
     */
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
    List<User> getParticipantsOfType(ParticipantType type, Session session) {
        User.withCriteria {
            participantDates {
                eq('date.id', pageInformation.date.id)
                eq('deleted', false)
            }

            sessionParticipants {
                eq('session.id', session.id)
                eq('type.id', type.id)
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
     * Returns a set with information about every participant added to the given session
     * @param session The session in question
     * @return A set of <code>ParticipantSessionInfo</code> objects
     */
    List<ParticipantSessionInfo> getParticipantsForSession(Session session) {
        List<ParticipantSessionInfo> sessionInformation = []

        SessionParticipant.executeQuery('''
            SELECT sp.user, sp.type
            FROM SessionParticipant AS sp
            INNER JOIN sp.type AS t
            WHERE sp.session.id = :sessionId
            ORDER BY t.importance DESC
        ''', [sessionId: session.id]).each { sessionParticipant ->
            ParticipantSessionInfo sessionInfo = sessionInformation.find { it.participant.user.id == sessionParticipant[0].id }

            if (!sessionInfo) {
                sessionInfo = new ParticipantSessionInfo(session, ParticipantDate.findByUserAndDate(sessionParticipant[0], pageInformation.date))
                sessionInformation.add(sessionInfo)
            }

            sessionInfo.addType(sessionParticipant[1])
            sessionInfo.paper = session.papers.find { it.user.id == sessionParticipant[0].id }
        }

        sessionInformation
    }

    /**
     * Returns a list of arrays with the equipment needed for the given session
     * and the number of participants that need that type of equipment
     * @param session The session to look for this equipment
     * @return A list of arrays with the equipment and number of participant requesting it
     */
    List<Object[]> getEquipmentForSession(Session session) {
        (List<Object[]>) Paper.executeQuery('''
            SELECT e.equipment, count(*)
            FROM Paper AS p
            INNER JOIN p.equipment AS e
            WHERE p.session.id = :sessionId
            GROUP BY e
        ''', [sessionId: session.id])
    }

    /**
     * Based on the rules as set for the current event, create a blacklist of users
     * for the specified type in the specified session
     * @param session The session to create a blacklist for
     * @param type The participant type to create a blacklist for
     * @return A list of all participants that cannot be added to the specified session with the specified type
     */
    List<User> getBlacklistForTypeInSession(Session session, ParticipantType type) {
        List<User> participants = new ArrayList<User>()

        // Check for every individual rule, which participants with what type should be added to the blacklist
        ParticipantTypeRule.getRulesForParticipantType(type).each { rule ->
            if (rule.firstType.id == type.id) {
                participants.addAll(getParticipantsOfType(rule.secondType, session))
            }
            else if (rule.secondType.id == type.id) {
                participants.addAll(getParticipantsOfType(rule.firstType, session))
            }
        }
        // And of course, participants who are already in the specified session
        // with the specified type cannot be added twice
        participants.addAll(getParticipantsOfType(type, session))

        // In case the participant should be added with an paper, blacklist those without (open) papers
        if (type.withPaper) {
            participants.addAll(getParticipantsWithoutOpenPapers())
        }

        participants.unique()
    }
}
