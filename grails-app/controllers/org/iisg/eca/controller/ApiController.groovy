package org.iisg.eca.controller

import grails.converters.JSON
import grails.orm.PagedResultList
import org.iisg.eca.domain.Order
import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.EventDate
import org.iisg.eca.domain.Translate
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.SessionParticipant

import org.iisg.eca.export.XlsMapExport
import org.iisg.eca.utils.PlannedSession

import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 * The controller for all API related actions
 */
class ApiController {
	def grailsApplication
	def passwordService
	def emailService
	def emailCreationService
	def pageInformation
	def sessionPlannerService
	def apiService

	/*
	 * GENERAL CRUD API CALLS
	 */

	def get() {
		try {
			PagedResultList results = apiService.getWithCriteria(params.domain.toString(), params)
			Map allResults = [totalSize: results.totalCount, results: results]
			render allResults as JSON
		}
		catch (Exception e) {
			response.sendError(500)
		}
	}

	def postPut() {
		Map results = [success: false] as Map<String, Object>

		try {
			Long id = (params.id?.toString()?.isLong()) ? params.id?.toString()?.toLong() : null
			Object instance = apiService.saveToDomain(params.domain.toString(), id, params)
			results.put('success', true)
			results.put('id', instance.id)
		}
		catch (Exception e) {
			results.put('success', false)
		}
		finally {
			render results as JSON
		}
	}

	def delete() {
		Long id = (params.id?.toString()?.isLong()) ? params.id?.toString()?.toLong() : null
		Map results = [success: apiService.deleteRecord(params.domain.toString(), id)]
		render results as JSON
	}

	/*
	 * SPECIFIC CASES API CALLS
	 */

	def login() {
		String email = params.email?.toString()
		String password = params.password?.toString()
		Map response = ['status': User.USER_STATUS_NOT_FOUND] as Map<String, Object>

		if (email && password) {
			User.disableHibernateFilter('hideDeleted')
			User user = User.findByEmail(email)
			User.enableHibernateFilter('hideDeleted')

			if (user && user.isPasswordCorrect(password)) {
				returnUserInfo(response, user)
			}
		}

		render response as JSON
	}

	def userInfo() {
		Map response = ['status': User.USER_STATUS_NOT_FOUND] as Map<String, Object>
		Long userId = (params.userId?.toString()?.isLong()) ? params.userId.toString().toLong() : null
		String email = params.userId?.toString()

		if (userId || email) {
			User.disableHibernateFilter('hideDeleted')
			User user = (userId) ? User.findById(userId) : User.findByEmail(email)
			User.enableHibernateFilter('hideDeleted')

			if (user) {
				returnUserInfo(response, user)
			}
		}

		render response as JSON
	}

	def changePassword() {
		actionById(User, 'userId', params, ['success': false]) { User user, Map response ->
			String newPwd = params.containsKey('newPassword') ? params.newPassword.toString() : null
			String newPwdRepeat = params.containsKey('newPasswordRepeat') ? params.newPasswordRepeat.toString() : null

			if (newPwd && newPwdRepeat) {
				boolean result = passwordService.changePassword(user, newPwd, newPwdRepeat)
				response.put('success', result)
			}
		}
	}

	def lostPassword() {
		String email = params.email?.toString()
		Map response = ['status': User.USER_STATUS_NOT_FOUND] as Map<String, Object>

		if (email) {
			User.disableHibernateFilter('hideDeleted')
			User user = User.findByEmail(email)
			User.enableHibernateFilter('hideDeleted')

			if (user) {
				response.put('status', user.getStatus())

				if ((user.getStatus() == User.USER_STATUS_FOUND) && !passwordService.lostPassword(user)) {
					response.put('status', User.USER_STATUS_NOT_FOUND)
				}
			}
		}

		render response as JSON
	}

	def confirmLostPassword() {
		Long id = (params.userId?.toString()?.isLong()) ? params.userId.toString().toLong() : null
		String code = params.code?.toString()
		Map response = ['status': User.USER_STATUS_NOT_FOUND] as Map<String, Object>

		if (id && code) {
			User.disableHibernateFilter('hideDeleted')
			User user = User.findById(id)
			User.enableHibernateFilter('hideDeleted')

			if (user) {
				int status = passwordService.confirmLostPassword(user, code)
				response.put('status', status)
			}
		}

		render response as JSON
	}

	def accessToken() {
		actionById(User, 'userId', params, ['success': false]) { User user, Map response ->
			OAuth2AccessToken token = user.getAccessToken()
			response.put('access_token', token.getValue())
			response.put('success', true)
		}
	}

	def programme() {
		Long dayId = (params.dayId?.toString()?.isLong()) ? params.dayId?.toString()?.toLong() : null
		Long timeId = (params.timeId?.toString()?.isLong()) ? params.timeId?.toString()?.toLong() : null
		Long networkId = (params.networkId?.toString()?.isLong()) ? params.networkId?.toString()?.toLong() : null
		Long roomId = (params.roomId?.toString()?.isLong()) ? params.roomId?.toString()?.toLong() : null
		String terms = params.terms?.toString()

		List<PlannedSession> programme = sessionPlannerService.
				getProgramme(pageInformation.date.id, dayId, timeId, networkId, roomId, terms)

		render programme as JSON
	}

	def sendEmail() {
		actionById(User, 'userId', params, ['success': false]) { User user, Map response ->
			String settingPropertyName = params.settingPropertyName?.toString()
			if (settingPropertyName) {
				Set<SentEmail> emails = emailCreationService.createEmail(settingPropertyName, user, params)
				emails.each { email ->
					emailService.sendEmail(email)
				}
				response.put('success', true)
			}
		}
	}

	def resendEmail() {
		actionById(SentEmail, 'emailId', params, ['success': false]) { SentEmail email, Map response ->
			emailService.sendEmail(email, true, true)
			response.put('success', true)
		}
	}

	def removePaper() {
		actionById(Paper, 'paperId', params, ['success': false]) { Paper paper, Map response ->
			response.put('success', paper.removePaperFile())
		}
	}

	def settings() {
		Map<String, String> settings = Setting.getSettingsMapForApi()
		render settings as JSON
	}

	def translations() {
		Map<String, String> translations = Translate.getTranslationsMap()
		render translations as JSON
	}

	def participantsInNetwork() {
		actionById(Network, 'networkId', params, ['success': false]) { Network network, Map response ->
			List<Map> users = network.allUsersInNetwork.collect {
				['network': network.name, 'lastname': it.lastName, 'firstname': it.firstName, 'email': it.email]
			}

			response.put('success', true)

			if (params.excel?.equalsIgnoreCase('true') || params.excel?.equalsIgnoreCase('1')) {
				XlsMapExport xls = new XlsMapExport(
						['network', 'lastname', 'firstname', 'email'], users,
						'Sheet 1', [params.networkName, params.lastName, params.firstName, params.email] as List<String>)
				response.put('xls', xls.parse().encodeBase64().toString())
			}
			else {
				response.put('users', users)
			}
		}
	}

	def sessionPapersInNetworkXls() {
		actionById(Network, 'networkId', params, ['success': false]) { Network network, Map response ->
			List usersSessions = ParticipantDate.executeQuery('''
				SELECT DISTINCT u, s
                FROM ParticipantDate AS pd
                INNER JOIN pd.user AS u
                INNER JOIN u.sessionParticipants AS sp
                INNER JOIN sp.session AS s
                INNER JOIN s.networks AS n
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

			List<Map> users = []
			Map<Long, Set<Paper>> papersPersSession = new HashMap<>()
			usersSessions.each { userAndSession ->
				User user = userAndSession[0]
				Session session = userAndSession[1]

				if (!papersPersSession.containsKey(session.id)) {
					papersPersSession.put(session.id, session.papers ?: new HashSet<>())
				}

				boolean paperFound = false
				papersPersSession.get(session.id).each { Paper paper ->
					if (!paper.deleted && (paper.user.id == user.id)) {
						paperFound = true
						users << ['network'     : network.name, 'lastname': user.lastName, 'firstname': user.firstName,
						          'email'       : user.email, 'session': session.name,
						          'sessionstate': session.state.description,
						          'roles'       : SessionParticipant.findAllByUserAndSession(user, session)*.type.join(', '),
						          'papertitle'  : paper.title, 'paperabstract': paper.abstr,
						          'paperstate'  : paper.state.description]
					}
				}

				if (!paperFound) {
					users << ['network'     : network.name, 'lastname': user.lastName, 'firstname': user.firstName,
					          'email'       : user.email, 'session': session.name,
					          'sessionstate': session.state.description,
					          'roles'       : SessionParticipant.findAllByUserAndSession(user, session)*.type.join(', '),
					          'papertitle'  : null, 'paperabstract': null, 'paperstate': null]
				}
			}

			response.put('success', true)

			if (params.excel?.equalsIgnoreCase('true') || params.excel?.equalsIgnoreCase('1')) {
				XlsMapExport xls = new XlsMapExport(
						['network', 'lastname', 'firstname', 'email', 'session', 'sessionstate', 'roles',
						 'papertitle', 'paperstate', 'paperabstract'], users, 'Sheet 1',
						[params.networkName, params.lastName, params.firstName,
						 params.email, params.session, params.sessionState, params.roles,
						 params.paperTitle, params.paperST, params.paperAbstract] as List<String>)
				response.put('xls', xls.parse().encodeBase64().toString())
			} else {
				response.put('users', users)
			}
		}
	}

	def sessionPapersInNetworkAcceptedXls() {
		actionById(Network, 'networkId', params, ['success': false]) { Network network, Map response ->
			List usersSessions = ParticipantDate.executeQuery('''
				SELECT DISTINCT u, s
                FROM ParticipantDate AS pd
                INNER JOIN pd.user AS u
                INNER JOIN u.sessionParticipants AS sp
                INNER JOIN sp.session AS s
                INNER JOIN s.networks AS n
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

			List<Map> users = []
			Map<Long, Set<Paper>> papersPersSession = new HashMap<>()
			usersSessions.each { userAndSession ->
				User user = userAndSession[0]
				Session session = userAndSession[1]

				if (!papersPersSession.containsKey(session.id)) {
					papersPersSession.put(session.id, session.papers ?: new HashSet<>())
				}

				boolean paperFound = false
				papersPersSession.get(session.id).each { Paper paper ->
					if (!paper.deleted && (paper.user.id == user.id)) {
						paperFound = true
						users << ['network'     : network.name, 'lastname': user.lastName, 'firstname': user.firstName,
						          'email'       : user.email, 'session': session.name,
						          'sessionstate': session.state.description,
						          'roles'       : SessionParticipant.findAllByUserAndSession(user, session)*.type.join(', '),
						          'papertitle'  : paper.title, 'paperabstract': paper.abstr,
						          'paperstate'  : paper.state.description]
					}
				}

				if (!paperFound) {
					users << ['network'     : network.name, 'lastname': user.lastName, 'firstname': user.firstName,
					          'email'       : user.email, 'session': session.name,
					          'sessionstate': session.state.description,
					          'roles'       : SessionParticipant.findAllByUserAndSession(user, session)*.type.join(', '),
					          'papertitle'  : null, 'paperabstract': null, 'paperstate': null]
				}
			}

			response.put('success', true)

			if (params.excel?.equalsIgnoreCase('true') || params.excel?.equalsIgnoreCase('1')) {
				XlsMapExport xls = new XlsMapExport(
						['network', 'lastname', 'firstname', 'email', 'session', 'sessionstate', 'roles',
						 'papertitle', 'paperstate', 'paperabstract'], users, 'Sheet 1',
						[params.networkName, params.lastName, params.firstName,
						 params.email, params.session, params.sessionState, params.roles,
						 params.paperTitle, params.paperST, params.paperAbstract] as List<String>)
				response.put('xls', xls.parse().encodeBase64().toString())
			} else {
				response.put('users', users)
			}
		}
	}

	// individual papers in network (xls)
	def individualPapersInNetworkXls() {
		actionById(Network, 'networkId', params, ['success': false]) { Network network, Map response ->
			List usersPapers = ParticipantDate.executeQuery('''
				SELECT DISTINCT u, p
                FROM ParticipantDate AS pd
                    INNER JOIN pd.user AS u
                    INNER JOIN u.papers AS p
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

			List<Map> users = []

			usersPapers.each { userAndPaper ->
				User user = userAndPaper[0]
				Paper paper = userAndPaper[1]

				users << ['network'      : network.name,
				          'lastname'     : user.lastName,
				          'firstname'    : user.firstName,
				          'email'        : user.email,
				          'papertitle'   : paper.title,
				          'paperabstract': paper.abstr,
						  'paperstate'   : paper.state.description]
			}

			response.put('success', true)

			if (params.excel?.equalsIgnoreCase('true') || params.excel?.equalsIgnoreCase('1')) {
				XlsMapExport xls = new XlsMapExport(
						['network', 'lastname', 'firstname',
						 'email', 'papertitle', 'paperstate', 'paperabstract'],
						users, 'Sheet 1',
						[params.networkName, params.lastName, params.firstName,
						 params.email, params.paperTitle, params.paperST, params.paperAbstract] as List<String>)
				response.put('xls', xls.parse().encodeBase64().toString())
			} else {
				response.put('users', users)
			}
		}
	}

	def participantsInSession() {
		Long sessionId = (params.sessionId?.toString()?.isLong()) ? params.sessionId.toString().toLong() : null
		Long networkId = (params.networkId?.toString()?.isLong()) ? params.networkId.toString().toLong() : null
		List results = []

		// comment: sessionId -1 is a special code, it means show all paper participants without a session, these
		// participants will be shown in a special session called 'individual paper proposals'...

		if (sessionId && sessionId > 0) {
			results = ParticipantDate.executeQuery('''
				SELECT u, pd, p, t
				FROM ParticipantDate AS pd
					INNER JOIN pd.user AS u
					INNER JOIN u.sessionParticipants AS sp
					INNER JOIN sp.type AS t
					LEFT JOIN u.papers AS p WITH (
						(p.deleted = false OR p IS NULL)
						AND (p.date.id = :dateId OR p IS NULL)
						AND (p.session.id = :sessionId OR p IS NULL)
					)
				WHERE u.deleted = false
				AND sp.session.id = :sessionId
				AND pd.state.id IN (:newParticipant, :dataChecked, :participant, :notFinished)

				ORDER BY t.importance DESC, u.lastName ASC, u.firstName ASC
			''',
					['dateId'         : pageInformation.date.id, 'sessionId' : sessionId,
					 'newParticipant' : ParticipantState.NEW_PARTICIPANT,
					 'dataChecked'    : ParticipantState.PARTICIPANT_DATA_CHECKED,
                     'participant'    : ParticipantState.PARTICIPANT,
                     'notFinished'    : ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])
		}
		else if (networkId) {
			results = ParticipantDate.executeQuery('''
				SELECT u, pd, p
				FROM ParticipantDate AS pd
					INNER JOIN pd.user AS u
					INNER JOIN u.papers AS p
				WHERE u.deleted = false
					AND p.deleted = false
					AND p.date.id = :dateId
					AND p.session.id IS NULL
					AND p.networkProposal.id = :networkId
					AND pd.state.id IN (:newParticipant, :dataChecked, :participant)
				AND p.deleted = false
				AND p.date.id = :dateId
				AND p.session.id IS NULL
				AND p.networkProposal.id = :networkId
				AND pd.state.id IN (:newParticipant, :dataChecked, :participant, :notFinished)
				ORDER BY u.lastName ASC, u.firstName ASC
			''',
					['dateId'         : pageInformation.date.id, 'networkId': networkId,
                     'newParticipant' : ParticipantState.NEW_PARTICIPANT,
                     'dataChecked'    : ParticipantState.PARTICIPANT_DATA_CHECKED,
					 'participant'    : ParticipantState.PARTICIPANT,
					 'notFinished'    : ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])
		}

		render results as JSON
	}

	def participantsInProposedNetwork() {
		Long networkId = (params.networkId?.toString()?.isLong()) ? params.networkId.toString().toLong() : null
		List results = []

		if (networkId) {
			results = Paper.executeQuery('''
				SELECT u, p, s
				FROM Paper AS p
				INNER JOIN p.user AS u
				INNER JOIN u.participantDates AS pd
				LEFT JOIN p.session AS s
				WHERE u.deleted = false
				AND pd.deleted = false
				AND pd.date.id = :dateId
				AND (s.deleted = false OR s IS NULL)
				AND (s.date.id = :dateId OR s IS NULL)
				AND pd.state.id IN (:newParticipant, :dataChecked, :participant, :notFinished)
				AND p.networkProposal.id = :networkId
				ORDER BY u.lastName ASC, u.firstName ASC
			''',
					['dateId'         : pageInformation.date.id,
					 'networkId'      : networkId,
					 'newParticipant' : ParticipantState.NEW_PARTICIPANT,
					 'dataChecked'    : ParticipantState.PARTICIPANT_DATA_CHECKED,
					 'participant'    : ParticipantState.PARTICIPANT,
					 'notFinished'    : ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])
		}

		render results as JSON
	}

	def sessionsSearch() {
		String[] terms = params.search?.trim()?.split()
		List results = Session.withCriteria {
			if (terms) {
				or {
					for (String term : terms) {
						if (!term.trim().isEmpty()) {
							like('name', "%${term.trim()}%")
						}
					}
				}
			}
			order('name', 'asc')
		}

		render results as JSON
	}

	def eventDateInfo() {
		render pageInformation.date as JSON
	}

	def eventDates() {
		render EventDate.getAllForEvent(pageInformation.date.event).list() as JSON
	}

	def mailNewPassword() {
		actionById(User, 'userId', params, ['success': false]) { User user, Map response ->
			passwordService.sendPassword(user)
			if (user.save()) {
				response.put('success', true)
			}
		}
	}

	def refreshOrder() {
		Map response = [success: false]
		Long id = (params.containsKey('orderId') && params.orderId.isLong()) ? params.long('orderId') : null
		if (id) {
			Order order = Order.get(id)
			boolean insert = false
			if (!order) {
				order = new Order()
				order.setId(id)
				insert = true
			}

			response.put('success', order.refreshOrder(insert))
		}

		render response as JSON
	}

	private void returnUserInfo(Map response, User user) {
		response.put('status', user.getStatus())
		response.put('hasFullRights', user.hasFullRights())
		response.put('isNetworkChair', user.isNetworkChair())
		response.put('isChair', user.hasRoleInASession(ParticipantType.get(ParticipantType.CHAIR)))
		response.put('isOrganiser', user.hasRoleInASession(ParticipantType.get(ParticipantType.ORGANIZER)))
		response.put('isCrew', user.isCrew())

		response.put('user', user)
		response.put('participant', ParticipantDate.findByUserAndDate(user, pageInformation.date))
	}

	private void actionById(Class domainClass, String idName, GrailsParameterMap params, Map defaultResponse,
			Closure onInstanceFound) {
		Long id = (params.containsKey(idName) && params[idName].toString().isLong()) ? params.long(idName) : null
		if (id) {
			def instance = domainClass.findById(id)
			if (instance) {
				onInstanceFound(instance, defaultResponse)
			}
		}

		render defaultResponse as JSON
	}
}
