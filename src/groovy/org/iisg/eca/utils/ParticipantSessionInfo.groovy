package org.iisg.eca.utils

import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantType

class ParticipantSessionInfo {
    Session session
    ParticipantDate participant
    List<ParticipantType> types
    Paper paper

    ParticipantSessionInfo(Session session, ParticipantDate participant) {
        this.session = session
        this.participant = participant
        this.paper = paper
        this.types = new ArrayList<ParticipantType>()
    }

    ParticipantSessionInfo(Session session, ParticipantDate participant, List<ParticipantType> types, Paper paper) {
        this.session = session
        this.participant = participant
        this.types = types
        this.paper = paper
    }

    Session getSession() {
        session
    }

    ParticipantDate getParticipant() {
        participant
    }

    List<ParticipantType> getTypes() {
        types
    }

    Paper getPaper() {
        paper
    }

    void addType(ParticipantType type) {
        types.add(type)
    }

    void setPaper(Paper paper) {
        this.paper = paper
    }
}
