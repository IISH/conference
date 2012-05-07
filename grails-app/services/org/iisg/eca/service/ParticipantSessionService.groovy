package org.iisg.eca.service

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.SessionParticipant

class ParticipantSessionService {
    def pageInformation

    List<User> getAllParticipants() {
        User.withCriteria {
            participantDates {
                eq('date.id', pageInformation.date.id)
            }
        }
    }

    List<User> getParticipantsOfType(ParticipantType participantType, Session, session) {
        getParticipantsOfType(participantType.id, session.id)
    }

    List<User> getParticipantsOfType(long participantTypeId, long sessionId) {
        User.withCriteria {
            participantDates {
                eq('date.id', pageInformation.date.id)
            }

            sessionParticipants {
                eq('session.id', sessionId)
                eq('type.id', participantTypeId)
            }
        }
    }

    List<Object[]> getEquipmentForSession(Session session) {
        getEquipmentForSession(session.id)
    }

    List<Object[]> getEquipmentForSession(long sessionId) {
        SessionParticipant.executeQuery('''
            SELECT e.equipment, count(*)
            FROM SessionParticipant AS sp
            INNER JOIN sp.user AS u
            INNER JOIN u.papers AS p
            INNER JOIN p.equipment AS e
            WHERE sp.session.id = :sessionId
            GROUP BY e
        ''', [sessionId: sessionId])
    }
}
