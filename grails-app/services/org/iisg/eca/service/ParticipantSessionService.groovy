package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.ParticipantTypeRule

import org.iisg.eca.utils.ParticipantSessionInfo

import org.springframework.context.i18n.LocaleContextHolder

/**
 * Service responsible for requesting participant data in order to allow them in a session
 */
class ParticipantSessionService {
    /**
     * Service with information about the current page, such as the current event date
     */
    def pageInformation

    /**
     * Service to access i18n messages
     */
    def messageSource

    /**
     * Returns all participants of the current event date and session with the given type
     * @param participantType The participant type
     * @param session The session to look for these participants
     * @return A list of users
     */
    List<User> getParticipantsOfType(ParticipantType type, Session session) {
        User.withCriteria {
            participantDates {
                // Make sure the data is filtered here
                eq('date.id', pageInformation.date.id)
                eq('deleted', false)
            }

            sessionParticipants {
                eq('session.id', session.id)
                eq('type.id', type.id)

                // Currently hard deleted, but just in case
                eq('deleted', false)
            }
        }
    }

    /**
     * Returns all participants with papers of the current event date
     * that cannot be added to a session anymore
     * @return A list of users
     */
    List<User> getParticipantsWithoutOpenPapers() {
        User.executeQuery('''
            SELECT u
            FROM User AS u
            LEFT JOIN u.participantDates AS pd
            WHERE pd.date.id = :dateId
            AND pd.deleted = false
            AND u NOT IN (
                SELECT p.user
                FROM Paper AS p
                WHERE p.session.id IS NULL
                AND p.date.id = :dateId
                AND p.deleted = false
            )
        ''', [dateId: pageInformation.date.id])
    }

    /**
     * Returns a list with information about every participant added to the given session
     * @param session The session in question
     * @return A list of <code>ParticipantSessionInfo</code> objects
     */
    List<ParticipantSessionInfo> getParticipantsForSession(Session session) {
        // Query the database for the information,
        // then transform this into a list of <code>ParticipantSessionInfo</code> objects
        getParticipantSessionInfoList(
            SessionParticipant.executeQuery('''
                SELECT sp.session, u, t
                FROM SessionParticipant AS sp
                INNER JOIN sp.type AS t
                INNER JOIN sp.user AS u
                WHERE sp.session.id = :sessionId
                ORDER BY t.importance DESC, u.lastName, u.firstName
            ''', [sessionId: session.id])
        )
    }

    /**
     * Returns a list with information about every session the given participant is added to
     * @param participant The participant in question
     * @return A list of <code>ParticipantSessionInfo</code> objects
     */
    List<ParticipantSessionInfo> getSessionsForParticipant(ParticipantDate participant) {
        // Query the database for the information,
        // then transform this into a list of <code>ParticipantSessionInfo</code> objects
        getParticipantSessionInfoList(
            SessionParticipant.executeQuery('''
                SELECT s, sp.user, t
                FROM SessionParticipant AS sp
                INNER JOIN sp.type AS t
                INNER JOIN sp.session AS s
                WHERE sp.user.id = :userId
                ORDER BY s.code, s.name, t.importance DESC
            ''', [userId: participant?.user?.id])
        )
    }

    List<ParticipantSessionInfo> getParticipantsForSessionMismatches(Session session) {
        // Query the database for the information,
        // then transform this into a list of <code>ParticipantSessionInfo</code> objects
        getParticipantSessionInfoList(
            SessionParticipant.executeQuery('''
                SELECT s, u, t
                FROM SessionParticipant AS sp
                INNER JOIN sp.session AS s
                INNER JOIN sp.type AS t
                INNER JOIN sp.user AS u
                INNER JOIN u.papers AS p
                INNER JOIN s.state AS st
                WHERE s.id = :sessionId
                AND t.withPaper = true
                AND p.state <> st.correspondingPaperState
                AND p.deleted = false
                ORDER BY u.lastName, u.firstName
            ''', [sessionId: session.id])
        )
    }

    /**
     * Transforms a list with information about every session into a list of ParticipantSessionInfo objects
     * @param sessionParticipant An collection with the Session object, the User object and the ParticipantType object
     * @return A list of <code>ParticipantSessionInfo</code> objects
     */
    private List<ParticipantSessionInfo> getParticipantSessionInfoList(Collection<Object[]> sessionParticipants) {
        List<ParticipantSessionInfo> sessionInformation = []

        sessionParticipants.each { sessionParticipant ->
            Session session = sessionParticipant[0]
            User user = sessionParticipant[1]
            ParticipantType type = sessionParticipant[2]

            // See if this user is already in the list somewhere, if so update that one with new information
            ParticipantSessionInfo sessionInfo = sessionInformation.find {
                (it.participant?.user?.id == user?.id) && (it.session?.id == session?.id) }

            // This user is not in the list already, so create a new <code>ParticipantSessionInfo</code> object for this user
            if (!sessionInfo) {
                ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                if (participant) {
                    sessionInfo = new ParticipantSessionInfo(session, participant)
                    sessionInformation.add(sessionInfo)
                }
            }

            // Update the sessionInfo object with the new participant type and paper information
            sessionInfo?.addType(type)
            sessionInfo?.paper = Paper.findAllBySession(session).find { it.user?.id == user.id }
        }

        sessionInformation
    }

    /**
     * Returns a map with information about every participant added to the network
     * The participant information is accessible by the sessions belonging to the network
     * @param network The network in question
     * @return A map containing lists with <code>ParticipantSessionInfo</code> objects
     * for every session added to the network
     */
    Map<Session, List<ParticipantSessionInfo>> getParticipantsForNetwork(Network network) {
        Map sessions = [:]

        // Get all sessions for this network from the database and add this session to the map,
        // including the participant information for that session
       Session.executeQuery('''
            SELECT s
            FROM Session AS s
            INNER JOIN s.networks AS n
            WHERE n.id = :networkId
            ORDER BY s.code, s.name
        ''', [networkId: network.id]).each { session ->
            sessions.put(session, getParticipantsForSession(session))
        }

        sessions
    }

    Map<Session, List<ParticipantSessionInfo>> getParticipantsForSessionMismatches(Network network) {
        Map sessions = [:]

        // Get all sessions for this network from the database and add this session to the map,
        // including the participant information for that session
        Session.executeQuery('''
            SELECT s
            FROM Session AS s
            INNER JOIN s.networks AS n
            WHERE n.id = :networkId
            ORDER BY s.code, s.name
        ''', [networkId: network.id]).each { session ->
            sessions.put(session, getParticipantsForSessionMismatches(session))
        }

        sessions
    }

    /**
     * Returns a map object for a list of <code>ParticipantSessionInfo</code> objects
     * @param info A list with participant session information
     * @return A map object for a list of <code>ParticipantSessionInfo</code> objects
     */
    List<Map<String, Object>> getParticipantSessionInfoMap(List<ParticipantSessionInfo> info) {
        info.collect {
            [   id:             it.participant.user.id,
                participant:    it.participant.user.toString(),
                state:          it.participant.state.toString(),
                types:          it.types.collect { pType ->
                                    [id: pType.id, type:  pType.toString()]
                                },
                paper:          (it.paper) ? "${messageSource.getMessage('paper.label', null, LocaleContextHolder.locale)}: ${it.paper?.toString()} (${it.paper.state.toString()})" : "",
                coauthors:      (it.paper?.coAuthors) ? "${messageSource.getMessage('paper.coAuthors.label', null, LocaleContextHolder.locale)}: ${it.paper.coAuthors}" : "",
                paperId:        (it.paper) ? it.paper.id : "",
                paperStateId:   (it.paper) ? it.paper.state.id : ""
            ]
        }
    }

    /**
     * Returns a map of all participants of which their papers were proposed for the given network,
     * but are not scheduled in any of the network's sessions
     * @param network The network in question
     * @return A map of participants and their papers that were not scheduled in any of the sessions yet
     */
    Map<ParticipantDate, List<Paper>> getParticipantsNotInNetwork(Network network) {
        Map<ParticipantDate, List<Paper>> papersNotScheduled = [:]

        // Query the database for papers not scheduled yet in this network, though proposed
        ParticipantDate.executeQuery('''
            SELECT pd, p
            FROM ParticipantDate AS pd
            INNER JOIN pd.user AS u
            INNER JOIN u.papers AS p
            WHERE p.networkProposal.id = :networkId
            AND p.session IS NULL
            AND pd.state.id IN (1,2)
            AND p.deleted = false
            AND p.date.id = pd.date.id
            ORDER BY u.lastName, u.firstName
        ''', [networkId: network.id]).each { result ->

            // See if the participant is already in the map, if so, get it, otherwise create a new one
            List<Paper> papers = papersNotScheduled.get(result[0], new ArrayList<Paper>())

            // Add the paper to the papers of the participant
            papers.add(result[1])
        }

        papersNotScheduled
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
