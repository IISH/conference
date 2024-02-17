package org.iisg.eca.utils

import groovy.transform.CompileStatic
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantType

/**
 * Information about all the types a participant has in a session
 */
@CompileStatic
class ParticipantSessionInfo {
    Session session
    ParticipantDate participant
    Set<ParticipantType> types
    Paper paper
    Paper paperCoAuthoring

    /**
     * Create information object on a participant in a session
     * @param session The session in question
     * @param participant The participant in question
     */
    ParticipantSessionInfo(Session session, ParticipantDate participant) {
        this.session = session
        this.participant = participant
        this.paper = paper
        this.types = new TreeSet<ParticipantType>()
    }

    /**
     * Create information object on a participant in a session
     * @param session The session in question
     * @param participant The participant in question
     * @param types The participant types given to the participant in this session
     * @param paper If the participant also presents a paper in the session, the paper in question
     * @param paperCoAuthoring If the participant also co-authored a paper in the session, the paper in question
     */
    ParticipantSessionInfo(Session session, ParticipantDate participant, Set<ParticipantType> types, Paper paper,
                           Paper paperCoAuthoring) {
        this.session = session
        this.participant = participant
        this.types = types
        this.paper = paper
        this.paperCoAuthoring = paperCoAuthoring
    }

    /**
     * Returns the session
     * @return The session
     */
    Session getSession() {
        session
    }

    /**
     * Returns the participant
     * @return The participant
     */
    ParticipantDate getParticipant() {
        participant
    }

    /**
     * Returns all the participant types of the participant in this session
     * @return A list of participant types
     */
    Set<ParticipantType> getTypes() {
        types
    }

    /**
     * Returns the paper of the participant in this session
     * Returns null if there is no paper
     * @return The paper
     */
    Paper getPaper() {
        paper
    }

    /**
     * Returns the co-authoring paper in this session
     * Returns null if there is no paper
     * @return The paper
     */
    Paper getPaperCoAuthoring() {
        return paperCoAuthoring
    }

    /**
     * Adds a participant type to the participant in this session
     * @param type The type to give
     */
    void addType(ParticipantType type) {
        types.add(type)
    }

    /**
     * Set the paper presented by the participant during this session
     * @param paper The paper to present
     */
    void setPaper(Paper paper) {
        this.paper = paper
    }

    /**
     * Set the paper co-authored by the participant during this session
     * @param paper The paper
     */
    void setPaperCoAuthoring(Paper paperCoAuthoring) {
        this.paperCoAuthoring = paperCoAuthoring
    }
}
