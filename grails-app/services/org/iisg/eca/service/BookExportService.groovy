package org.iisg.eca.service

import java.text.DateFormat
import java.text.SimpleDateFormat

import groovy.xml.MarkupBuilder

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.ParticipantState

/**
 * Service creating the XML for the program book
 */
class BookExportService {
    def pageInformation

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

                        String times = sdt.period.split('-')
                        builder.starttime(times[0].trim())
                        builder.endtime(times[1].trim())
                    }
                }
            }
        }
    }

    String getConcordanceXml() {
        getXml("Export of Concordance for ${getEventCode()} Programbook") { builder ->
            builder.names {
                User.executeQuery('''
                    SELECT u
                    FROM ParticipantDate AS pd
                    INNER JOIN pd.user AS u
                    WHERE pd.state.id = :stateId
                    AND u.enabled = true
                    AND u.deleted = false
                    AND pd.enabled = true
                    ORDER BY u.lastName, u.firstName
                ''', [stateId: ParticipantState.PARTICIPANT]).each { user ->
                    builder.name {
                        builder.lastname(user.lastName)
                        builder.firstname(user.firstName)
                        builder.email(user.email)
                    }
                }
            }
        }
    }

    String getSessionsXml() {
        DateFormat weekDayFormatter = new SimpleDateFormat('EEEEE', Locale.US)
        DateFormat dayFormatter = new SimpleDateFormat('dd', Locale.US)
        DateFormat monthFormatter = new SimpleDateFormat('MMMMM', Locale.US)
        DateFormat yearFormatter = new SimpleDateFormat('yyyy', Locale.US)

        getXml("Export ${getEventCode()} Programbook (Sessions) (English)") { builder ->
            builder.sessions {
                Day.executeQuery('''
                    SELECT d, sdt
                    FROM Day AS d
                    INNER JOIN d.sessionDateTimes AS sdt
                    ORDER BY d.id, sdt.indexNumber
                ''').each { results ->
                    Day d = results[0]
                    SessionDateTime sdt = results[1]

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
                        INNER JOIN srdt.sessionDateTime AS sdt
                        INNER JOIN srdt.room AS r
                        WHERE sdt.id = :sessionDateTimeId
                        AND s.enabled = true
                        AND s.deleted = false
                        AND srdt.enabled = true
                        AND srdt.deleted = false
                        AND r.enabled = true
                        AND r.deleted = false
                        ORDER BY r.roomNumber
                    ''', [sessionDateTimeId: sdt.id]).each { sessionResults ->
                        Session session = sessionResults[0]
                        Room room = sessionResults[1]

                        builder.session {
                            //builder.code(session.code)
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
                                    AND n.enabled = true
                                    ORDER BY n.name
                                ''', [sessionId: session.id]).each { network ->
                                    builder.networkname(network.name)
                                }
                            }

                            builder.chairs {
                                User.executeQuery('''
                                    SELECT u
                                    FROM User AS u
                                    INNER JOIN u.sessionParticipants AS sp
                                    WHERE sp.session.id = :sessionId
                                    AND sp.type.id = 6
                                    AND u.enabled = true
                                    ORDER BY u.lastName, u.firstName
                                ''', [sessionId: session.id]).each { participant ->
                                    builder.chairname("$participant.firstName $participant.lastName")
                                }
                            }

                            builder.organizers {
                                User.executeQuery('''
                                    SELECT u
                                    FROM User AS u
                                    INNER JOIN u.sessionParticipants AS sp
                                    WHERE sp.session.id = :sessionId
                                    AND sp.type.id = 7
                                    AND u.enabled = true
                                    ORDER BY u.lastName, u.firstName
                                ''', [sessionId: session.id]).each { participant ->
                                    builder.organizername("$participant.firstName $participant.lastName")
                                }
                            }

                            builder.discussants {
                                User.executeQuery('''
                                    SELECT u
                                    FROM User AS u
                                    INNER JOIN u.sessionParticipants AS sp
                                    WHERE sp.session.id = :sessionId
                                    AND sp.type.id = 10
                                    AND u.enabled = true
                                    ORDER BY u.lastName, u.firstName
                                ''', [sessionId: session.id]).each { participant ->
                                    builder.discussantname("$participant.firstName $participant.lastName")
                                }
                            }

                            builder.papers {
                                Paper.executeQuery('''
                                    SELECT p, u
                                    FROM Paper AS p
                                    INNER JOIN p.user AS u
                                    INNER JOIN u.sessionParticipants AS sp
                                    WHERE sp.session.id = :sessionId
                                    AND sp.type.id = 8
                                    AND p.enabled = true
                                    AND u.enabled = true
                                    AND u.deleted = false
                                    ORDER BY u.lastName, u.firstName
                                ''', [sessionId: session.id]).each { paperInfo ->
                                    Paper paper = paperInfo[0]
                                    User user = paperInfo[1]

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
            }
        }
    }

    private String getEventCode() {
        "${pageInformation.date.event.shortName}${pageInformation.date.yearCode}"
    }

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
}
