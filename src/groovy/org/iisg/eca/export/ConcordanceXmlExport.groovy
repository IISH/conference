package org.iisg.eca.export

import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.SessionState
import org.iisg.eca.domain.User
import org.iisg.eca.utils.EmailFilter

class ConcordanceXmlExport extends XmlExport {
    private EmailFilter emailFilter

    ConcordanceXmlExport(String description, EmailFilter emailFilter) {
        super(description)
        this.emailFilter = emailFilter
        getConcordanceXml()
    }

    private void getConcordanceXml() {
        User lastUser = null
        List<String> sessionsOfUser = []

        getXml { builder ->
            builder.names {
                User.executeQuery('''
                    SELECT DISTINCT u, r, sdt
                    FROM ParticipantDate AS pd
                    INNER JOIN pd.user AS u
                    INNER JOIN u.sessionParticipants AS sp
                    INNER JOIN sp.session AS s
                    INNER JOIN s.sessionRoomDateTime AS srdt
                    INNER JOIN srdt.sessionDateTime AS sdt
                    INNER JOIN srdt.room AS r
                    WHERE pd.state.id IN ( :stateParticipantDataChecked, :stateParticipant )
                    AND s.state.id = :sessionStateId
                    AND u.deleted = false AND s.deleted = false AND sdt.deleted = false AND r.deleted = false
                    ORDER BY u.lastName, u.firstName, r.roomNumber, sdt.indexNumber
                ''', [
                        stateParticipantDataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                        stateParticipant           : ParticipantState.PARTICIPANT,
                        sessionStateId             : SessionState.SESSION_ACCEPTED
                ]).each { concordance ->
                    User user = (User) concordance[0]
                    Room room = (Room) concordance[1]
                    SessionDateTime sessionDateTime = (SessionDateTime) concordance[2]

                    lastUser = (lastUser) ?: user
                    if (user.id != lastUser.id) {
                        builder.name {
                            builder.lastname(lastUser.lastName)
                            builder.firstname(lastUser.firstName)
                            builder.email((emailFilter.isUserAllowed(lastUser)) ? lastUser.email : null)
                            builder.sessions(sessionsOfUser.join(', '))
                        }

                        sessionsOfUser = []
                        lastUser = user
                    }

                    sessionsOfUser.add("${room.roomNumber}-${sessionDateTime.indexNumber}")
                }
            }
        }
    }
}
