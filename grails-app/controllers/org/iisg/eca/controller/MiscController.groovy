package org.iisg.eca.controller

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

/*
 * Misc queries
 * First returned column is assumed to be the id
 */
class MiscController {
    def dataSource
    def pageInformation

    def lastNameUpperCase() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.enabled=1 and participant_date.deleted=0
           AND participant_date.participant_state_id IN (0,1,2,999)
           AND participant_date.date_id = :date_id
           AND
           (
               lastname <> ""
               AND lastname <> "n/a"
               AND ( binary lastname = binary upper( lastname ) OR binary lastname = binary lower( lastname ) )
           )
           ORDER BY lastname
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last Name", "First Name"],
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
           AND participant_date.date_id = :date_id
           AND
           (
           firstname <> ""
           AND firstname <> "n/a"
           AND ( binary firstname = binary upper( firstname ) OR binary firstname = binary lower( firstname ) )
           )
           ORDER BY firstname
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last Name", "First Name"],
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
           AND participant_date.date_id = :date_id
           AND
           (
           city <> ""
           AND city <> "n/a"
           AND ( binary city = binary upper( city ) OR binary city = binary lower( city ) )
           )
           ORDER BY city
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last Name", "First Name", "City"],
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
           AND participant_date.date_id = :date_id
           AND
           (
           organisation <> ""
           AND organisation <> "n/a"
           AND organisation like '% %'
           AND ( binary organisation = binary upper( organisation ) OR binary organisation = binary lower( organisation ) )
           )
           ORDER BY organisation
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last Name", "First Name", "City", "Organisation"],
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
           WHERE A.date_id=1 AND B.date_id = :date_id
           AND A.session_name = B.session_name
           AND A.enabled=1 AND A.deleted=0
           AND B.enabled=1 AND B.deleted=0
           AND A.session_id <> B.session_id
           ORDER BY A.session_name, A.session_id
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Session name"],
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
           WHERE date_id = :date_id
           AND deleted= 0
           AND session_id NOT IN (SELECT session_id FROM session_in_network)
           ORDER BY session_name
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Session name"],
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
           AND date_id = :date_id
           AND session_id IN (
               SELECT session_id
               FROM session_participant
               WHERE participant_type_id = 9
               GROUP BY session_id
           )
           ORDER BY session_name, session_id
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Session name"],
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
           AND participant_date.date_id = :date_id
           AND users.user_id IN (
               SELECT A.user_id FROM papers A, papers B WHERE A.user_id = B.user_id and A.paper_id <> B.paper_id and A.title = B.title and A.paper_id > B.paper_id
               AND A.enabled=1 AND A.deleted=0
               AND B.enabled=1 AND B.deleted=0
           )
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name"],
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
           AND participant_date.date_id = :date_id
           AND users.user_id IN (
               SELECT user_id
               FROM `papers`
               WHERE enabled=1 AND deleted=0 AND date_id = :date_id
               GROUP BY user_id
               HAVING count(*) > 1
           ) ORDER BY lastname, firstname
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name"],
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
          AND participant_date.date_id = :date_id
          AND users.user_id IN (
            SELECT user_id FROM `papers` WHERE session_id IN (SELECT session_id FROM sessions WHERE deleted=1 ) OR session_id NOT IN (SELECT session_id FROM sessions) ) ;
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of (co)authors in deleted sessions."
        ])
    }

    def award() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
          SELECT users.user_id, lastname, firstname, email
          FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
          WHERE users.enabled=1 AND users.deleted=0
          AND participant_date.enabled=1 and participant_date.deleted=0
          AND participant_date.participant_state_id IN (0,1,2,999)
          AND participant_date.date_id = :date_id
          AND participant_date.award=1
          ORDER BY lastname, firstname
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name", "E-mail"],
                controller: "participant",
                action:     "show",
                info:       "Overview of students participating in the 'Award'."
        ])
    }

    def notRegisteredChairs() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
          SELECT users.user_id, lastname, firstname, email
          FROM networks_chairs
          INNER JOIN users ON networks_chairs.user_id = users.user_id
          WHERE users.user_id NOT IN (
              SELECT user_id
              FROM participant_date WHERE date_id = :date_id
              AND enabled=1 AND deleted=0
          )
          ORDER BY lastname, firstname
        """, [date_id: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name", "E-mail"],
                controller: null,
                action:     null,
                info:       "Not Registered Chairs."
        ])
    }

    def multipleAccPapers() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
            SELECT users.user_id, lastname, firstname
            FROM users 
            INNER JOIN participant_date ON users.user_id=participant_date.user_id
            WHERE users.enabled=1 AND users.deleted=0
            AND participant_date.enabled=1 and participant_date.deleted=0
            AND participant_date.participant_state_id IN (0,1,2,999)
            AND participant_date.date_id = :dateId
            AND users.user_id IN (
                SELECT user_id
                FROM vw_dbl_acc_papers 
                WHERE date_id = :dateId
            ) 
            ORDER BY lastname, firstname
        """, [dateId: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of participants with multiple accepted papers."
        ])
    }
    
    def chairs() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
            SELECT users.user_id, networks.name, users.lastname, users.firstname, users.email
            FROM networks
            INNER JOIN networks_chairs on networks.network_id=networks_chairs.network_id
            INNER JOIN users ON networks_chairs.user_id=users.user_id
            WHERE networks_chairs.enabled=1 
            AND networks_chairs.deleted=0 
            AND users.enabled=1 
            AND users.deleted=0
            AND networks.date_id = :dateId
            ORDER BY networks.name, users.lastname, users.firstname
        """, [dateId: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Network", "Last name", "First name", "Email"],
                controller: "participant",
                action:     "show",
                info:       "Overview of all chairs."
        ])
    }

    def participantsSameName() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
            SELECT A.user_id AS A_id, A.lastname AS A_lastname, A.firstname AS A_firstname, B.user_id AS B_id, B.lastname AS B_lastname, B.firstname AS B_firstname
            FROM vw_accepted_participants A
            INNER JOIN vw_accepted_participants B ON A.lastname = B.lastname
            AND A.firstname = B.firstname
            AND A.user_id < B.user_id
            WHERE A.date_id = :dateId
            AND B.date_id = :dateId
            ORDER BY A.lastname, A.firstname
        """, [dateId: pageInformation.date.id])

        render(view: "participantsSameName", model: [
                data:       result,
                controller: "participant",
                action:     "show",
                info:       "Participants with same name."
        ])
    }
}

