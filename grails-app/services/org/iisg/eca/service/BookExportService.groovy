package org.iisg.eca.service

import java.text.DateFormat
import java.text.SimpleDateFormat

import groovy.xml.MarkupBuilder

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.SessionState
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.ParticipantState

/**
 * Service creating the XML for the program book
 */
class BookExportService {
    def pageInformation

    /**
     * Creates an XML with all the conference days
     * @return A string representing the XML
     */
    String getDaysXml() {
        DateFormat weekDayFormatter = new SimpleDateFormat('EEEEE', Locale.US)
        DateFormat dayFormatter = new SimpleDateFormat('dd', Locale.US)
        DateFormat monthFormatter = new SimpleDateFormat('MMMMM', Locale.US)

        getXml("Export of Days for ${getEventCode()} Programbook (English)") { builder ->
            builder.days {
                Day.executeQuery('''
                    SELECT d, sdt
                    FROM Day AS d
                    INNER JOIN d.sessionDateTimes AS sdt
                    ORDER BY d.id, sdt.indexNumber
                ''').each { results ->
                    builder.day {
                        Day d = results[0]
                        SessionDateTime sdt = results[1]

                        builder.weekday(weekDayFormatter.format(d.day))
                        builder.day(dayFormatter.format(d.day))
                        builder.month(monthFormatter.format(d.day))

                        String[] times = sdt.period.split('-')
                        builder.starttime(times[0].trim())
                        builder.endtime(times[1].trim())
                    }
                }
            }
        }
    }

    /**
     * Creates an XML with all the participants
     * @return A string representing the XML
     */
    String getConcordanceXml() {
        getXml("Export of Concordance for ${getEventCode()} Programbook") { builder ->
            builder.names {
                User.executeQuery('''
                    SELECT DISTINCT u
                    FROM ParticipantDate AS pd
                    INNER JOIN pd.user AS u
                    INNER JOIN u.sessionParticipants AS sp
                    INNER JOIN sp.session AS s
                    WHERE pd.state.id = :stateId
                    AND s.state.id = :sessionStateId
                    AND u.enabled = true
                    AND u.deleted = false
                    AND s.deleted = false
                    ORDER BY u.lastName, u.firstName
                ''', [stateId: ParticipantState.PARTICIPANT, sessionStateId: SessionState.SESSION_ACCEPTED]).each { user ->
                    builder.name {
                        builder.lastname(user.lastName)
                        builder.firstname(user.firstName)
                        builder.email(user.email)
                    }
                }
            }
        }
    }

    /**
     * Creates an XML with all the sessions
     * @return A string representing the XML
     */
    String getSessionsXml() {
        DateFormat weekDayFormatter = new SimpleDateFormat('EEEEE', Locale.US)
        DateFormat dayFormatter = new SimpleDateFormat('dd', Locale.US)
        DateFormat monthFormatter = new SimpleDateFormat('MMMMM', Locale.US)
        DateFormat yearFormatter = new SimpleDateFormat('yyyy', Locale.US)

        getXml("Export ${getEventCode()} Programbook (Sessions) (English)") { builder ->
            Day.executeQuery('''
                SELECT d, sdt
                FROM Day AS d
                INNER JOIN d.sessionDateTimes AS sdt
                ORDER BY d.id, sdt.indexNumber
            ''').each { results ->
                Day d = results[0]
                SessionDateTime sdt = results[1]

                builder.sessions {
                    builder.time {
                        builder.weekday(weekDayFormatter.format(d.day))
                        builder.year(yearFormatter.format(d.day))
                        builder.month(monthFormatter.format(d.day))
                        builder.day(dayFormatter.format(d.day))

                        String[] times = sdt.period.split('-')
                        builder.starttime(times[0].trim())
                        builder.endtime(times[1].trim())
                    }

                    Session.executeQuery('''
                        SELECT s, r
                        FROM Session AS s
                        INNER JOIN s.sessionRoomDateTime AS srdt
                        INNER JOIN srdt.room AS r
                        WHERE srdt.sessionDateTime.id = :sessionDateTimeId
                        AND r.deleted = false
                        ORDER BY r.roomNumber
                    ''', [sessionDateTimeId: sdt.id]).each { sessionResults ->
                        Session session = (Session) sessionResults[0]
                        Room room = (Room) sessionResults[1]

                        forEachSession(session, room, sdt, builder)
                    }
                }
            }
        }
    }

    /**
     * The event code to use in the header of the XML
     * @return A string representing the event in question
     */
    private String getEventCode() {
        "${pageInformation.date.event.shortName}${pageInformation.date.yearCode}"
    }

    /**
     * Sets up the ground work for creating XML exports
     * @param description The description to print in the XML
     * @param body The body contents of the XML
     * @return A string with the complete XML string
     */
    private String getXml(String description, Closure body) {
        DateFormat formatter = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss', Locale.US)
        StringWriter writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.doubleQuotes = true

        writer.write('<?xml version="1.0" encoding="utf-8" ?>\n')

        xml.data {
            xml.description(description)
            xml.exportDate(formatter.format(new Date()))

            body(xml)
        }

        writer.toString()
    }

    /**
     * Creates the XML content for each session
     * @param session The session to create the xml for
     * @param room The room of the planned session
     * @param sdt The date/time of the planned session
     * @param builder The builder for creating the XML
     */
    private void forEachSession(Session session, Room room, SessionDateTime sdt, MarkupBuilder builder) {
        builder.session {
            builder.sessionname(session.name)

            builder.location {
                builder.code("${room.roomNumber}-${sdt.indexNumber}")
                builder.locationname(room.roomName)
            }

            builder.networks {
                Network.executeQuery('''
                    SELECT n
                    FROM Network AS n
                    INNER JOIN n.sessions AS s
                    WHERE s.id = :sessionId
                    ORDER BY n.name
                ''', [sessionId: session.id]).each { network ->
                    builder.networkname(network.name)
                }
            }

            Map<ParticipantType, List<User>> sessionParticipants = new HashMap<ParticipantType, List<User>>()

            User.executeQuery('''
                SELECT u, t
                FROM User AS u
                INNER JOIN u.sessionParticipants AS sp
                INNER JOIN sp.type AS t
                WHERE sp.session.id = :sessionId
                AND t.withPaper = false
                ORDER BY t.importance desc, u.lastName, u.firstName
            ''', [sessionId: session.id]).each { participant ->
                User user = (User) participant[0]
                ParticipantType type = (ParticipantType) participant[1]

                List<User> users = sessionParticipants.get(type, new ArrayList<User>())
                users.add(user)
            }

            sessionParticipants.each { type, users ->
                builder."${type.type.toLowerCase()}s" {
                    users.each { user ->
                        builder."${type.type.toLowerCase()}name"("$user.firstName $user.lastName")
                    }
                }
            }

            builder.papers {
                Paper.executeQuery('''
                    SELECT p, u
                    FROM Paper AS p
                    INNER JOIN p.user AS u
                    INNER JOIN u.sessionParticipants AS sp
	                INNER JOIN sp.type AS t
                    WHERE p.session.id = :sessionId
                    AND u.deleted = false
                    AND sp.session.id = :sessionId
					AND t.withPaper = true
                    ORDER BY u.lastName, u.firstName
                ''', [sessionId: session.id]).each { paperInfo ->
                    Paper paper = (Paper) paperInfo[0]
                    User user = (User) paperInfo[1]

                    builder.paper {
                        builder.presenter("$user.firstName $user.lastName")
                        builder.copresenters(paper.coAuthors)
                        builder.subject(paper.title)
                    }
                }
            }
        }
    }
}
