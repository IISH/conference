package org.iisg.eca.export

import groovy.xml.MarkupBuilder
import org.iisg.eca.domain.*

import java.text.DateFormat
import java.text.SimpleDateFormat

class SessionsOrderedByCodeXmlExport extends XmlExport {
    private static final DateFormat WEEKDAY_FORMATTER = new SimpleDateFormat('EEEEE', Locale.US)
    private static final DateFormat DAY_FORMATTER = new SimpleDateFormat('dd', Locale.US)
    private static final DateFormat MONTH_FORMATTER = new SimpleDateFormat('MMMMM', Locale.US)
    private static final DateFormat YEAR_FORMATTER = new SimpleDateFormat('yyyy', Locale.US)

    private boolean printCoAuthors
    private Map<SessionDateTime, List> sessionsByDateTime
    private Map<Session, List<Network>> networksBySession
    private Map<Session, Map<ParticipantType, List<User>>> sessionParticipantsBySession
    private Map<Session, List> papersBySession

    SessionsOrderedByCodeXmlExport(String description, boolean printCoAuthors) {
        super(description)
        this.printCoAuthors = printCoAuthors
        setNetworks()
        setSessionPapers()
//        setSessionsByDateTime()
        setSessionParticipants()
        getSessionsXml()
    }

    private void setNetworks() {
        networksBySession = new HashMap<>()

        Network.executeQuery('''
            SELECT n, s
            FROM Network AS n
            INNER JOIN n.sessions AS s
            ORDER BY n.name
        ''').each { networkResults ->
            Network network = (Network) networkResults[0]
            Session session = (Session) networkResults[1]

            List<Network> networks = networksBySession.get(session, new ArrayList<>())
            networks.add(network)
        }
    }

    private void setSessionPapers() {
        papersBySession = new HashMap<>()

        Paper.executeQuery('''
            SELECT p, u, s
            FROM Paper AS p
            INNER JOIN p.user AS u
            INNER JOIN p.session AS s
            WHERE u.deleted = false AND s.deleted = false
            ORDER BY u.lastName, u.firstName
        ''').each { paperInfo ->
            Paper paper = (Paper) paperInfo[0]
            User user = (User) paperInfo[1]
            Session session = (Session) paperInfo[2]

            List papers = papersBySession.get(session, new ArrayList())
            papers.add([paper, user])
        }
    }

//    private void setSessionsByDateTime() {
//        sessionsByDateTime = new HashMap<>()
//
//        Session.executeQuery('''
//            SELECT s, r, t, sdt
//            FROM Session AS s
//            LEFT JOIN s.sessionRoomDateTime AS srdt
//            LEFT JOIN srdt.room AS r
//            LEFT JOIN srdt.sessionDateTime AS sdt
//            LEFT JOIN s.type AS t
//            WHERE r.deleted = false
//            ORDER BY s.code
//        ''').each { sessionResults ->
//            Session session = (Session) sessionResults[0]
//            Room room = (Room) sessionResults[1]
//            SessionType sessionType = (SessionType) sessionResults[2]
//            SessionDateTime sessionDateTime = (SessionDateTime) sessionResults[3]
//
//            List sessions = sessionsByDateTime.get(sessionDateTime, new ArrayList())
//            sessions.add([session, room, sessionType])
//        }
//    }

    private void setSessionParticipants() {
        sessionParticipantsBySession = new HashMap<>()

        SessionParticipant.executeQuery('''
            SELECT u, s, t
            FROM SessionParticipant AS sp
            INNER JOIN sp.user AS u
            INNER JOIN sp.session AS s
            INNER JOIN sp.type AS t            
            WHERE u.deleted = false AND s.deleted = false AND t.withPaper = false
            ORDER BY t.importance desc, u.lastName, u.firstName
        ''').each { sessionParticipantsResults ->
            User user = (User) sessionParticipantsResults[0]
            Session session = (Session) sessionParticipantsResults[1]
            ParticipantType participantType = (ParticipantType) sessionParticipantsResults[2]

            Map<ParticipantType, List<User>> sessionParticipants =
                    sessionParticipantsBySession.get(session, new LinkedHashMap<>())
            List<User> users = sessionParticipants.get(participantType, new ArrayList<>())
            users.add(user)
        }
    }

    private void getSessionsXml() {
        getXml { builder ->

            Session.executeQuery('''
            SELECT s, r, t, sdt
            FROM Session AS s
            LEFT JOIN s.sessionRoomDateTime AS srdt
            LEFT JOIN srdt.room AS r
            LEFT JOIN srdt.sessionDateTime AS sdt
            LEFT JOIN s.type AS t
            WHERE r.deleted = false
            ORDER BY s.code
        ''').each { sessionResults ->
                //            Session session = (Session) sessionResults[0]
                //          Room room = (Room) sessionResults[1]
                //        SessionType sessionType = (SessionType) sessionResults[2]
                //      SessionDateTime sessionDateTime = (SessionDateTime) sessionResults[3]

//                List sessions = sessionsByDateTime.get(sessionDateTime, new ArrayList())
                //              sessions.add([session, room, sessionType])
                Session session = (Session) sessionResults[0]
                Room room = (Room) sessionResults[1]
                SessionType sessionType = (SessionType) sessionResults[2]
                SessionDateTime sessionDateTime = (SessionDateTime) sessionResults[3]

                builder.sessions {
                    forEachSession(session, room, sessionType, sessionDateTime, builder)
                }
            }


//            Day.executeQuery('''
//				SELECT d, sdt
//				FROM Day AS d
//				INNER JOIN d.sessionDateTimes AS sdt
//				WHERE sdt.deleted = false
//				ORDER BY d.dayNumber, sdt.indexNumber
//            ''').each { results ->
//                Day d = results[0]
//                SessionDateTime sdt = results[1]
//
//                builder.sessions {
//                    builder.time {
//                        builder.weekday(WEEKDAY_FORMATTER.format(d.day))
//                        builder.year(YEAR_FORMATTER.format(d.day))
//                        builder.month(MONTH_FORMATTER.format(d.day))
//                        builder.day(DAY_FORMATTER.format(d.day))
//
//                        String[] times = sdt.period.split('-')
//                        builder.starttime(times[0].trim())
//                        builder.endtime((times.length >= 2) ? times[1].trim() : null)
//                    }
//
//                    sessionsByDateTime.get(sdt)?.each { sessionResults ->
//                        Session session = (Session) sessionResults[0]
//                        Room room = (Room) sessionResults[1]
//                        SessionType sessionType = (SessionType) sessionResults[2]
//
//                        forEachSession(session, room, sessionType, sdt, builder)
//                    }
//                }
//            }
        }
    }

    private void forEachSession(Session session, Room room, SessionType sessionType, SessionDateTime sdt,
                                MarkupBuilder builder) {
        builder.session {
            builder.sessioncode(session.code)
            builder.sessionname(session.name)
            builder.sessionabstract(session.abstr)

            if (sessionType) {
                builder.sessiontype(sessionType.type)
            }

//            builder.location {
//                builder.code("${room.roomNumber}-${sdt.indexNumber}")
//                builder.locationname(room.roomName)
//            }

//            builder.networks {
//                networksBySession.get(session)?.each { network ->
//                    builder.networkname(network.name)
//                }
//            }

            sessionParticipantsBySession.get(session)?.each { type, users ->
                // Co-authors are for internal use only
                if ((type.id != ParticipantType.CO_AUTHOR) || this.printCoAuthors) {
                    builder."${type.type.toLowerCase()}s" {
                        users.each { user ->
                            builder."${type.type.toLowerCase()}" {
                                builder.'person-id'(user.id)
                                builder.name("$user.firstName $user.lastName")
                                builder.firstname(user.firstName)
                                builder.lastname(user.lastName)
                                builder.organisation(user.organisation)
                            }
                        }
                    }
                }
            }

            builder.papers {
                papersBySession.get(session)?.each { paperInfo ->
                    Paper paper = (Paper) paperInfo[0]
                    User user = (User) paperInfo[1]

                    builder.paper {
                        builder.'person-id'(user.id)
                        builder.presenter("$user.firstName $user.lastName")
                        builder.firstname(user.firstName)
                        builder.lastname(user.lastName)
                        builder.organisation(user.organisation)
                        builder.copresenters(paper.coAuthors)
                        builder.subject(paper.title)
                        builder.abstract(paper.abstr)
                    }
                }
            }
        }
    }
}
