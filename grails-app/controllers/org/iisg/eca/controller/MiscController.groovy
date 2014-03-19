package org.iisg.eca.controller

import groovy.sql.Sql
import groovy.sql.GroovyRowResult

import org.iisg.eca.domain.SessionState
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantState

/*
 * Misc queries
 * First returned column is assumed to be the id
 */
class MiscController {
    def dataSource
	def exportService
    def pageInformation

    def lastNameUpperCase() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
           SELECT users.user_id, lastname, firstname
           FROM users INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.enabled=1 AND users.deleted=0
           AND participant_date.deleted=0
           AND participant_date.participant_state_id IN (:new, :dataChecked, :participant, :notFinished)
           AND participant_date.date_id = :date_id
           AND
           (
               lastname <> ""
               AND lastname <> "n/a"
               AND ( binary lastname = binary upper( lastname ) OR binary lastname = binary lower( lastname ) )
           )
           ORDER BY lastname
        """, [date_id: pageInformation.date.id, new: ParticipantState.NEW_PARTICIPANT, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant: ParticipantState.PARTICIPANT, notFinished: ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

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
           FROM users
           INNER JOIN participant_date ON users.user_id=participant_date.user_id
           WHERE users.deleted=0
           AND participant_date.deleted=0
           AND participant_date.participant_state_id IN (:new, :dataChecked, :participant, :notFinished)
           AND participant_date.date_id = :date_id
           AND (
	           firstname <> ""
	           AND firstname <> "n/a"
	           AND ( binary firstname = binary upper( firstname ) OR binary firstname = binary lower( firstname ) )
           )
           ORDER BY firstname
        """, [date_id: pageInformation.date.id, new: ParticipantState.NEW_PARTICIPANT, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant: ParticipantState.PARTICIPANT, notFinished: ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

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
           AND participant_date.deleted=0
           AND participant_date.participant_state_id IN (:new, :dataChecked, :participant, :notFinished)
           AND participant_date.date_id = :date_id
           AND
           (
           city <> ""
           AND city <> "n/a"
           AND ( binary city = binary upper( city ) OR binary city = binary lower( city ) )
           )
           ORDER BY city
        """, [date_id: pageInformation.date.id, new: ParticipantState.NEW_PARTICIPANT, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant: ParticipantState.PARTICIPANT, notFinished: ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

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
           AND participant_date.deleted=0
           AND participant_date.participant_state_id IN (:new, :dataChecked, :participant, :notFinished)
           AND participant_date.date_id = :date_id
           AND
           (
           organisation <> ""
           AND organisation <> "n/a"
           AND organisation like '% %'
           AND ( binary organisation = binary upper( organisation ) OR binary organisation = binary lower( organisation ) )
           )
           ORDER BY organisation
        """, [date_id: pageInformation.date.id, new: ParticipantState.NEW_PARTICIPANT, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant: ParticipantState.PARTICIPANT, notFinished: ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

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
           AND A.deleted=0
           AND B.deleted=0
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
           WHERE deleted=0
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
           AND participant_date.deleted=0
           AND participant_date.participant_state_id IN (:new, :dataChecked, :participant, :notFinished)
           AND participant_date.date_id = :date_id
           AND users.user_id IN (
               SELECT A.user_id FROM papers A, papers B WHERE A.user_id = B.user_id and A.paper_id <> B.paper_id and A.title = B.title and A.paper_id > B.paper_id
               AND A.deleted=0
               AND B.deleted=0
           )
        """, [date_id: pageInformation.date.id, new: ParticipantState.NEW_PARTICIPANT, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant: ParticipantState.PARTICIPANT, notFinished: ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

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
           SELECT u.user_id, u.lastname, u.firstname
           FROM users AS u
           INNER JOIN participant_date pd
           ON u.user_id = pd.user_id
           LEFT JOIN papers p
           ON u.user_id = p.user_id
           WHERE u.enabled=1 AND u.deleted=0
           AND pd.deleted=0
           AND pd.participant_state_id IN (:dataChecked, :participant)
           AND pd.date_id = :date_id
           AND p.deleted=0 AND p.date_id = :date_id
           GROUP BY p.user_id
           HAVING count(*) > 1

           ORDER BY u.lastname, u.firstname
        """, [date_id: pageInformation.date.id, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED, participant: ParticipantState.PARTICIPANT])

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
          AND participant_date.deleted=0
          AND participant_date.participant_state_id IN (:new, :dataChecked, :participant, :notFinished)
          AND participant_date.date_id = :date_id
          AND users.user_id IN (
            SELECT user_id FROM `papers` WHERE session_id IN (SELECT session_id FROM sessions WHERE deleted=1 ) OR session_id NOT IN (SELECT session_id FROM sessions) ) ;
        """, [date_id: pageInformation.date.id, new: ParticipantState.NEW_PARTICIPANT, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED,
                participant: ParticipantState.PARTICIPANT, notFinished: ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Overview of (co)authors in deleted sessions."
        ])
    }

    def award() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
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
            SELECT u.user_id, u.lastname, u.firstname
            FROM users AS u
            INNER JOIN participant_date pd
            ON u.user_id = pd.user_id
            LEFT JOIN papers p
            ON u.user_id = p.user_id
            INNER JOIN session_participant sp
            ON p.user_id = sp.user_id
            AND p.session_id = sp.session_id
            WHERE u.enabled=1 AND u.deleted=0
            AND pd.deleted=0
            AND pd.participant_state_id IN (1,2)
            AND pd.date_id = :dateId
            AND p.deleted=0
            AND p.date_id = :dateId
            AND p.paper_state_id = 2
            AND participant_type_id = 8
            GROUP BY u.user_id, u.lastname, u.firstname
            HAVING count(*) > 1
            ORDER BY u.lastname, u.firstname
        """, [dateId: pageInformation.date.id, dataChecked: ParticipantState.PARTICIPANT_DATA_CHECKED, participant: ParticipantState.PARTICIPANT])

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
            SELECT users.user_id, networks.name as network_name, users.lastname, users.firstname, users.email, fee_states.name as fee_state_name
            FROM networks
            INNER JOIN networks_chairs on networks.network_id=networks_chairs.network_id
            INNER JOIN users ON networks_chairs.user_id=users.user_id
            LEFT JOIN participant_date ON participant_date.user_id=users.user_id
            LEFT JOIN fee_states ON fee_states.fee_state_id=participant_date.fee_state_id
            WHERE users.deleted = 0
            AND participant_date.deleted = 0
            AND networks.date_id = :dateId
            AND participant_date.date_id = :dateId
            ORDER BY networks.name, users.lastname, users.firstname
        """, [dateId: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Network", "Last name", "First name", "Email", "Fee state"],
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

    def sessionsNoOrganizer() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
            SELECT session_id, session_code, session_name
            FROM `sessions`
            WHERE deleted=0
            AND date_id=:dateId
            AND session_state_id = :accepted
            AND session_id NOT IN (
                SELECT session_participant.session_id
                FROM session_participant
                INNER JOIN vw_accepted_participants ON session_participant.user_id=vw_accepted_participants.user_id
                WHERE AND session_participant.deleted=0
                AND session_participant.participant_type_id = :organizer
                GROUP BY session_participant.session_id
            )
            ORDER BY session_code ASC, session_name ASC
        """, [dateId: pageInformation.date.id, accepted: SessionState.SESSION_ACCEPTED, organizer: ParticipantType.ORGANIZER])

        render(view: "list", model: [
                data:       result,
                headers:    ["Code", "Name"],
                controller: "session",
                action:     "show",
                info:       "Sessions without an organizer."
        ])
    }

    def sessionsNoChair() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
            SELECT session_id, session_code, session_name, description
            FROM `sessions` s
            INNER JOIN session_states ss ON s.session_state_id = ss.session_state_id
            WHERE s.deleted=0
            AND date_id=:dateId
            AND s.session_state_id IN (:accepted, :consideration)
            AND session_id NOT IN (
                SELECT session_participant.session_id
                FROM session_participant
                INNER JOIN vw_accepted_participants ON session_participant.user_id=vw_accepted_participants.user_id
                WHERE session_participant.participant_type_id = :chair
                GROUP BY session_participant.session_id
            )
            ORDER BY s.session_state_id ASC, session_code ASC, session_name ASC
        """, [  dateId: pageInformation.date.id,
                accepted: SessionState.SESSION_ACCEPTED,
                consideration: SessionState.SESSION_IN_CONSIDERATION,
                chair: ParticipantType.CHAIR])

	    List<String> columns = ['session_code', 'session_name', 'description'] as List<String>
	    List<String> headers = ["Code", "Name", "Session state"] as List<String>
	    String info = "Sessions without a chair"

	    // If an export of the results is requested, try to delegate the request to the export service
	    if (params.format) {
		    exportService.getSQLExport(params.format, response, columns, result, info, headers, params.sep)
		    return
	    }

        render(view: "list", model: [
                data:       result,
                headers:    headers,
                controller: "session",
                action:     "show",
                info:       info,
		        export:     true
        ])
    }

    def participantNoCountry() {
        Sql sql = new Sql(dataSource)
        List<GroovyRowResult> result = sql.rows("""
            SELECT u.user_id, u.lastname, u.firstname
            FROM users AS u
            INNER JOIN participant_date AS pd
            ON u.user_id = pd.user_id
            WHERE pd.date_id = :dateId
            AND pd.deleted = 0
            AND u.deleted = 0
            AND (u.country_id IS NULL OR u.country_id = 0)
            ORDER BY u.lastname, u.firstname
        """, [dateId: pageInformation.date.id])

        render(view: "list", model: [
                data:       result,
                headers:    ["Last name", "First name"],
                controller: "participant",
                action:     "show",
                info:       "Participants without a country."
        ])
    }

	def noPaymentAttempt() {
		Sql sql = new Sql(dataSource)
		List<GroovyRowResult> result = sql.rows("""
            SELECT users.user_id, users.lastname, users.firstname, users.email
			FROM users
			INNER JOIN participant_date
			ON users.user_id = participant_date.user_id
			WHERE users.enabled=1 AND users.deleted=0
			AND participant_date.deleted=0
			AND participant_date.participant_state_id IN (1,2)
			AND participant_date.date_id = :dateId
			AND ( participant_date.payment_id IS NULL OR participant_date.payment_id = 0 )
			ORDER BY lastname, firstname, email
        """, [dateId: pageInformation.date.id])

		List<String> columns = ['lastname', 'firstname', 'email'] as List<String>
		List<String> headers = ["Last name", "First name", "E-mail"] as List<String>
		String info = "Participants without any made payment attempt"

		// If an export of the results is requested, try to delegate the request to the export service
		if (params.format) {
			exportService.getSQLExport(params.format, response, columns, result, info, headers, params.sep)
			return
		}

		render(view: "list", model: [
				data:       result,
				headers:    headers,
				controller: "participant",
				action:     "show",
				info:       info,
				export:     true
		])
	}
}

