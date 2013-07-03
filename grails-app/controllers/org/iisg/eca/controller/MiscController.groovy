package org.iisg.eca.controller

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

/*
 *
 */
class MiscController {
    def dataSource

    def lastNameUpperCase() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id=1
           AND
           (
               lastname <> ""
               AND lastname <> "n/a"
               AND ( binary lastname = binary upper( lastname ) OR binary lastname = binary lower( lastname ) )
           )
           ORDER BY lastname
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last Name", "First Name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants with all lowercase or uppercase lastname."
        ])
    }

    def firstNameUpperCase() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id=1
           AND
           (
           firstname <> ""
           AND firstname <> "n/a"
           AND ( binary firstname = binary upper( firstname ) OR binary firstname = binary lower( firstname ) )
           )
           ORDER BY firstname
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last Name", "First Name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants with all lowercase or uppercase firstname."
        ])
    }

    def cityUpperCase() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname, city
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id=1
           AND
           (
           city <> ""
           AND city <> "n/a"
           AND ( binary city = binary upper( city ) OR binary city = binary lower( city ) )
           )
           ORDER BY city
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last Name", "First Name", "City"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants with all lowercase or uppercase city."
        ])
    }

    def orgUpperCase() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname, city, organisation
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id=1
           AND
           (
           organisation <> ""
           AND organisation <> "n/a"
           AND organisation like '% %'
           AND ( binary organisation = binary upper( organisation ) OR binary organisation = binary lower( organisation ) )
           )
           ORDER BY organisation
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last Name", "First Name", "City", "Organisation"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants with all lowercase or uppercase organisation (containing a space)."
        ])
    }

    def sessionSameName() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT A.session_id, A.session_name
           FROM sessions A, sessions B
           WHERE A.date_id=1 AND B.date_id =1
           AND A.session_name = B.session_name
           AND A.enabled=1 AND A.deleted=0
           AND B.enabled=1 AND B.deleted=0
           AND A.session_id <> B.session_id
           ORDER BY A.session_name, A.session_id
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Session name"],
                controller: "session",
                action:     "show",
                info:       "Overview of sessions with exact the same names."
        ])
    }

    def sessionNoNetwork() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT session_id, session_name
           FROM `sessions`
           WHERE date_id=1
           AND deleted= 0
           AND session_id NOT IN (SELECT session_id FROM session_in_network)
           ORDER BY session_name
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Session name"],
                controller: "session",
                action:     "show",
                info:       "Overview of sessions with no network."
        ])
    }

    def sessionCoAuthor() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT session_id, session_name
           FROM sessions
           WHERE enabled=1 AND deleted=0
           AND date_id=1
           AND session_id IN (
               SELECT session_id
               FROM session_participant
               WHERE participant_type_id = 9
               GROUP BY session_id
           )
           ORDER BY session_name, session_id
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Session name"],
                controller: "session",
                action:     "show",
                info:       "Overview of sessions with a co-author."
        ])
    }

    def paperSameTitle() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id=1
           AND users.user_id IN (
               SELECT A.user_id FROM papers A, papers B WHERE A.user_id = B.user_id and A.paper_id <> B.paper_id and A.title = B.title and A.paper_id > B.paper_id
               AND A.enabled=1 AND A.deleted=0
               AND B.enabled=1 AND B.deleted=0
           )
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants who have multiple papers with exact the same title."
        ])
    }

    def multiplePapers() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id=1
           AND users.user_id IN (
               SELECT user_id
               FROM `papers`
               WHERE enabled=1 AND deleted=0 AND date_id=1
               GROUP BY user_id
               HAVING count(*) > 1
           ) ORDER BY lastname, firstname
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants with multiple papers."
        ])
    }

    def authorDelSession() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
          SELECT users.user_id, lastname, firstname
          FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
          WHERE users.enabled=1 AND users.deleted=0
          AND participant_date.enabled=1 and participant_date.deleted=0
          AND participant_date.participant_state_id IN (0,1,2,999)
          AND participant_date.date_id=1
          AND users.user_id IN (
            SELECT user_id FROM `papers` WHERE session_id IN (SELECT session_id FROM sessions WHERE deleted=1 ) OR session_id NOT IN (SELECT session_id FROM sessions) ) ;
        """)

        render(view: "list", model: [
                data:       result,
                headers:    ["#", "Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of (co)authors in deleted sessions."
        ])
    }
}

