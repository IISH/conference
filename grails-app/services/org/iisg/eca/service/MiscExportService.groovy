package org.iisg.eca.service

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.PaperType
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.SessionParticipant
import org.iisg.eca.domain.User

import org.iisg.eca.export.Export
import org.iisg.eca.export.XlsMapExport

import groovy.sql.Sql
import java.text.SimpleDateFormat
import org.springframework.context.i18n.LocaleContextHolder
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * Service that takes care of creating the export for various cases
 */
class MiscExportService {
	public static final int BADGES_PAYED = 0
	public static final int BADGES_NOT_PAYED = 1
	public static final int BADGES_UNCONFIRMED = 2

	def dataSource
	def messageSource
	def pageInformation

	/**
	 * Create a specific export for the creation of badges
	 * @return An export which can be used to create the XLS file
	 */
	Export getParticipantsExport(int status) {
		// Preparation
		SimpleDateFormat formatter = new SimpleDateFormat('EEEEE d MMMMM', LocaleContextHolder.locale)
		Map<Long, String> days = Day.list().collectEntries { day ->
			[(day.id): formatter.format(day.day)]
		}
		Map<Long, String> extras = Extra.list().collectEntries { extra ->
			[(extra.id): extra.title]
		}

		String trueText = messageSource.getMessage('default.boolean.true', null, LocaleContextHolder.locale)
		String falseText = messageSource.getMessage('default.boolean.false', null, LocaleContextHolder.locale)

		List<String> columns = ['user_id', 'title', 'lastname', 'firstname', 'email', 'organisation', 'department',
		                        'name_english', 'amount', 'name', 'day', 'network']
		columns += extras.values()

		List<String> columnNames = [
				'ID',
				messageSource.getMessage('title.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('user.department.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('user.country.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('order.amount.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('feeState.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('day.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('network.label', null, LocaleContextHolder.locale)
		]
		columnNames += extras.values()

		// Obtain results and transform them
		Sql sql = new Sql(dataSource)
		String sqlQuery = PARTICIPANTS_SQL

		// Make sure we place the right criteria for each export type
		String title = messageSource.getMessage('participantDate.badges.label', null, LocaleContextHolder.locale)
		switch (status) {
			case BADGES_PAYED:
				sqlQuery = sqlQuery.replace('extraCriteria', 'AND o.payed = 1')
				title = messageSource.getMessage('participantDate.badges.payed.label', null, LocaleContextHolder.locale)
				break
			case BADGES_NOT_PAYED:
				sqlQuery = sqlQuery.replace('extraCriteria',
						'AND ((o.payed <> 1 AND o.payment_method <> 1) OR pd.payment_id IS NULL) ' +
						'AND pd.participant_state_id IN (1,2)')
				title = messageSource.getMessage('participantDate.badges.not.payed.label', null, LocaleContextHolder.locale)
				break
			case BADGES_UNCONFIRMED:
				sqlQuery = sqlQuery.replace('extraCriteria',
						'AND o.payed <> 1 ' +
						'AND (o.payment_method = 1 OR o.payment_method = 2) ' +
						'AND pd.participant_state_id IN (1,2)')
				title = messageSource.getMessage('participantDate.badges.unconfirmed.label', null, LocaleContextHolder.locale)
				break
			default:
				sqlQuery = sqlQuery.replace('extraCriteria', '')
		}

		// Query the database and create the export
		List<Map> results = sql.rows(sqlQuery, [dateId: pageInformation.date.id])
		results.each { Map row ->
			List<Long> dayIds = row.days ? row.days.split(',')*.toLong() : []
			row.remove('days')
			if (dayIds.size() == 1) {
				row.put('day', days.get(dayIds.first()))
			}
			else {
				row.put('day', '')
			}

			List<Long> extraIds = row.extras ? row.extras.split(',')*.toLong() : []
			row.remove('extras')
			extras.each { Long id, String name ->
				if (extraIds.contains(id)) {
					row.put(name, trueText)
				}
				else {
					row.put(name, falseText)
				}
			}

			String network = row.network_name
			row.remove('network_name')
			if (network) {
				row.put('network', "Chair $network")
			}
			else {
				row.put('network', null)
			}

			row.put('amount', (row.get('amount') > 0) ? row.get('amount') / 100 : row.get('amount'))
		}

		// Create XLS export
		return new XlsMapExport(columns, results, title, columnNames)
	}

	/**
	 * Create a specific export for the programme at a glance
	 * @return An export which can be used to create the XLS file
	 */
	Export getProgramAtAGlanceExport() {
		List<String> columns = ['day', 'index_number', 'period', 'room_name', 'room_number', 'session_name']
		List<String> columnNames = [
				messageSource.getMessage('day.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('sessionDateTime.indexNumber.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('sessionDateTime.period.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('room.roomName.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('room.roomNumber.label', null, LocaleContextHolder.locale),
				messageSource.getMessage('session.label', null, LocaleContextHolder.locale)
		]

		Sql sql = new Sql(dataSource)
		List<Map> results = sql.rows(PROGRAM_SQL, [dateId: pageInformation.date.id])

		// Create XLS export
		String title = messageSource.getMessage('default.program.label', null, LocaleContextHolder.locale)
		return new XlsMapExport(columns, results, title, columnNames)
	}

	/**
	 * Creates an export of (accepted) participants with filters set by the user
	 * @param params The parameters, filters set by the user
	 * @return An Excel export of all matching participants
	 */
	Export getParticipantsWithFilterExport(GrailsParameterMap params) {
		List results = User.allParticipants(pageInformation.date) {
			participantDates {
				if (params.feeStateId?.isLong()) {
					eq('feeState.id', params.long('feeStateId'))
				}
			}
		}

		return new XlsMapExport(
				['id', 'lastName', 'firstName', 'email', 'organisation', 'department', 'country'],
				results,
				messageSource.getMessage('participantDate.multiple.label', null, LocaleContextHolder.locale),
				[   '#',
					 messageSource.getMessage('user.lastName.label', null, LocaleContextHolder.locale),
					 messageSource.getMessage('user.firstName.label', null, LocaleContextHolder.locale),
					 messageSource.getMessage('user.email.label', null, LocaleContextHolder.locale),
					 messageSource.getMessage('user.organisation.label', null, LocaleContextHolder.locale),
					 messageSource.getMessage('user.department.label', null, LocaleContextHolder.locale),
					 messageSource.getMessage('user.country.label', null, LocaleContextHolder.locale)
				]
		)
	}

	/**
	 * Creates an export of (accepted) participants in a network set by the user
	 * @param network The network
	 * @param title The title
	 * @param columnNames The column names
	 * @return An Excel export of all matching participants
	 */
	XlsMapExport getParticipantsInNetworkExport(Network network, String title, List<String> columnNames) {
		return new XlsMapExport(
				['network', 'lastname', 'firstname', 'email'],
				network.allUsersInNetwork.collect {
					['network'  : network.name,
					 'lastname' : it.lastName,
					 'firstname': it.firstName,
					 'email'    : it.emailDiscontinued ? null : it.email]
				},
				title,
				columnNames
		)
	}

	/**
	 * Creates an export of accepted users, sessions and papers in a network set by the user
	 * @param network The network
	 * @param title The title
	 * @param columnNames The column names
	 * @return An Excel export of all matching participants
	 */
	XlsMapExport getSessionPapersInNetworkExport(Network network, String title, List<String> columnNames) {
		List usersSessionsPapers = ParticipantDate.executeQuery('''
				SELECT DISTINCT u, s, p
                FROM ParticipantDate AS pd
                INNER JOIN pd.user AS u
                INNER JOIN u.sessionParticipants AS sp
                INNER JOIN sp.session AS s
                INNER JOIN s.networks AS n
                LEFT JOIN sp.sessionParticipantPapers AS spp
                LEFT JOIN spp.paper AS p
                WHERE u.deleted = false
                AND s.deleted = false
                AND s.date.id = :dateId
                AND n.id = :networkId
                AND pd.state.id IN (:newParticipant, :dataChecked, :participant, :notFinished)
                ORDER BY s.name ASC, u.lastName ASC, u.firstName ASC
			''', ['dateId'         : pageInformation.date.id,
				  'networkId'      : network.id,
				  'newParticipant' : ParticipantState.NEW_PARTICIPANT,
				  'dataChecked'    : ParticipantState.PARTICIPANT_DATA_CHECKED,
				  'participant'    : ParticipantState.PARTICIPANT,
				  'notFinished'    : ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

		return new XlsMapExport(
				['network', 'lastname', 'firstname', 'email', 'session', 'sessionstate', 'roles',
				 'papertitle', 'paperstate', 'paperabstract'],
				usersSessionsPapers.collect { userSessionPaper ->
					User user = userSessionPaper[0] as User
					Session session = userSessionPaper[1] as Session
					Paper paper = userSessionPaper[2] as Paper

					return ['network'      : network.name,
							'lastname'     : user.lastName,
							'firstname'    : user.firstName,
							'email'        : user.emailDiscontinued ? null : user.email,
							'session'      : session.name,
							'sessionstate' : session.state.description,
							'roles'        : SessionParticipant.findAllByUserAndSession(user, session)*.type.join(', '),
							'papertitle'   : paper?.title,
							'paperabstract': paper?.abstr,
							'paperstate'   : paper?.state?.description]
				},
				title,
				columnNames
		)
	}

	/**
	 * Creates an export of accepted users, sessions and papers in a network set by the user
	 * @param network The network
	 * @param title The title
	 * @param columnNames The column names
	 * @return An Excel export of all matching participants
	 */
	XlsMapExport getSessionPapersInNetworkAcceptedExport(Network network, String title, List<String> columnNames) {
		List usersSessionsPapers = ParticipantDate.executeQuery('''
				SELECT DISTINCT u, s, p
                FROM ParticipantDate AS pd
                INNER JOIN pd.user AS u
                INNER JOIN u.sessionParticipants AS sp
                INNER JOIN sp.session AS s
                INNER JOIN s.networks AS n
			 	LEFT JOIN sp.sessionParticipantPapers AS spp
                LEFT JOIN spp.paper AS p
                WHERE u.deleted = false
                AND s.deleted = false
                AND s.date.id = :dateId
                AND n.id = :networkId
                AND pd.state.id IN (:dataChecked, :participant)
                ORDER BY s.name ASC, u.lastName ASC, u.firstName ASC
			''', ['dateId'         : pageInformation.date.id,
				  'networkId'      : network.id,
				  'dataChecked'    : ParticipantState.PARTICIPANT_DATA_CHECKED,
				  'participant'    : ParticipantState.PARTICIPANT])

		return new XlsMapExport(
				['network', 'lastname', 'firstname', 'email', 'session', 'sessionstate', 'roles',
				 'papertitle', 'paperstate', 'paperabstract'],
				usersSessionsPapers.collect { userSessionPaper ->
					User user = userSessionPaper[0] as User
					Session session = userSessionPaper[1] as Session
					Paper paper = userSessionPaper[2] as Paper

					return ['network'     : network.name,
							'lastname'    : user.lastName,
							'firstname'   : user.firstName,
							'email'        : user.emailDiscontinued ? null : user.email,
							'session'     : session.name,
							'sessionstate': session.state.description,
							'roles'       : SessionParticipant.findAllByUserAndSession(user, session)*.type.join(', '),
							'papertitle'  : paper?.title, 'paperabstract': paper?.abstr,
							'paperstate'  : paper?.state?.description]
				},
				title,
				columnNames
		)
	}

	/**
	 * Creates an export of papers without a session in a network set by the user
	 * @param network The network
	 * @param title The title
	 * @param columnNames The column names
	 * @return An Excel export of all matching participants
	 */
	XlsMapExport getIndividualPapersInNetworkExport(Network network, String title, List<String> columnNames) {
		List usersPapers = ParticipantDate.executeQuery('''
				SELECT DISTINCT u, p, t
                FROM ParticipantDate AS pd
				INNER JOIN pd.user AS u
				INNER JOIN u.papers AS p
				INNER JOIN p.type AS t
                WHERE u.deleted = false
				AND p.networkProposal.id = :networkId
				AND pd.state.id IN (:newParticipant, :dataChecked, :participant, :notFinished)
				AND p.session.id IS NULL
				AND p.deleted = false
				AND p.date.id = :dateId
				AND u.deleted = false
				AND pd.deleted = false
                ORDER BY u.lastName ASC, u.firstName ASC
			''', ['dateId'         : pageInformation.date.id,
				  'networkId'      : network.id,
				  'newParticipant' : ParticipantState.NEW_PARTICIPANT,
				  'dataChecked'    : ParticipantState.PARTICIPANT_DATA_CHECKED,
				  'participant'    : ParticipantState.PARTICIPANT,
				  'notFinished'    : ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])

		return new XlsMapExport(
				['network', 'lastname', 'firstname', 'email', 'paperid', 'papertitle', 'coauthors', 'papertype', 'paperstate', 'paperabstract'],
				usersPapers.collect { userAndPaper ->
					User user = userAndPaper[0] as User
					Paper paper = userAndPaper[1] as Paper
					PaperType type = userAndPaper[2] as PaperType

					return ['network'      : network.name,
							'lastname'     : user.lastName,
							'firstname'    : user.firstName,
							'email'        : user.emailDiscontinued ? null : user.email,
							'paperid'	   : paper.id,
							'papertitle'   : paper.title,
							'coauthors'    : paper.coAuthors,
							'papertype'	   : type.type,
							'paperstate'   : paper.state.description,
							'paperabstract': paper.abstr]
				},
				title,
				columnNames
		)
	}

	private static final String PARTICIPANTS_SQL = '''
			SELECT u.user_id, u.title, u.lastname, u.firstname, u.email, u.organisation,
			u.department, c.name_english, o.amount, fs.name, n.name AS network_name,
            CAST(GROUP_CONCAT(DISTINCT d.day_id ORDER BY d.day_number) AS CHAR) AS days,
            CAST(GROUP_CONCAT(DISTINCT e.extra_id ORDER BY e.extra) AS CHAR) AS extras
            FROM users AS u
            INNER JOIN participant_date AS pd
            ON u.user_id = pd.user_id
            LEFT JOIN countries AS c
            ON u.country_id = c.country_id
            LEFT JOIN orders AS o
            ON pd.payment_id = o.order_id
            INNER JOIN fee_states AS fs
            ON pd.fee_state_id = fs.fee_state_id
            LEFT JOIN participant_day AS pday
            ON u.user_id = pday.user_id 
            AND (pday.date_id = :dateId OR pday.date_id IS NULL)
            LEFT JOIN days AS d
            ON pday.day_id = d.day_id 
            AND (d.date_id = :dateId OR d.date_id IS NULL) 
            AND (d.deleted = 0 OR d.deleted IS NULL)
            LEFT JOIN participant_date_extra AS pde
            ON pd.participant_date_id = pde.participant_date_id
            LEFT JOIN extras AS e
            ON pde.extra_id = e.extra_id
            AND (e.date_id = :dateId OR e.date_id IS NULL)
            AND (e.deleted = 0 OR e.deleted IS NULL)
            LEFT JOIN networks_chairs AS nc
            ON u.user_id = nc.user_id
            LEFT JOIN networks AS n
            ON nc.network_id = n.network_id
            WHERE u.deleted = 0
            AND pd.date_id = :dateId
            AND pd.deleted = 0
            AND (   pd.participant_state_id IN (1,2)
                    OR (pd.payment_id IS NOT NULL AND pd.payment_id > 0))
            extraCriteria
            GROUP BY u.user_id
            ORDER BY u.lastname ASC, u.firstname ASC
	'''

	private static final String PROGRAM_SQL = '''
			SELECT DATE_FORMAT(d.day, '%W %e %M') AS day, sdt.index_number, sdt.period, r.room_name, r.room_number,
			s.session_name
			FROM session_datetime AS sdt
			INNER JOIN days AS d
			ON sdt.day_id = d.day_id
			LEFT JOIN rooms AS r
			ON 1=1
			LEFT JOIN session_room_datetime AS srdt
			ON sdt.session_datetime_id = srdt.session_datetime_id
			AND r.room_id = srdt.room_id
			LEFT JOIN sessions AS s
			ON srdt.session_id = s.session_id
			WHERE sdt.date_id = :dateId
			AND sdt.deleted = 0
			AND d.date_id = :dateId
			AND d.deleted = 0
			AND r.date_id = :dateId
			AND r.deleted = 0
			AND (srdt.date_id = :dateId OR srdt.date_id IS NULL)
			AND (s.date_id = :dateId OR s.date_id IS NULL)
			AND (s.deleted = 0 OR s.deleted IS NULL)
			ORDER BY sdt.index_number, r.room_number
	'''
}
