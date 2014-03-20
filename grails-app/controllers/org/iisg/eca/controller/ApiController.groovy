package org.iisg.eca.controller

import grails.converters.JSON
import grails.orm.PagedResultList

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.SentEmail
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantType
import org.iisg.eca.domain.ParticipantState

import org.iisg.eca.export.XlsMapExport
import org.iisg.eca.utils.PlannedSession

import org.springframework.security.oauth2.common.OAuth2AccessToken

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
		Long id = (params.userId?.toString()?.isLong()) ? params.userId.toString().toLong() : null
		String newPwd = params.newPassword?.toString()
		String newPwdRepeat = params.newPasswordRepeat?.toString()
		Map response = ['success': false]

		if (id && newPwd && newPwdRepeat) {
			User user = User.findById(id)
			if (user) {
				boolean result = passwordService.changePassword(user, newPwd, newPwdRepeat)
				response.put('success', result)
			}
		}

		render response as JSON
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
		Long id = (params.userId?.toString()?.isLong()) ? params.userId.toString().toLong() : null
		Map response = ['success': false] as Map<String, Object>

		if (id) {
			User user = User.findById(id)

			if (user) {
				OAuth2AccessToken token = user.getAccessToken()
				response.put('access_token', token.getValue())
				response.put('success', true)
			}
		}

		render response as JSON
	}

	def program() {
		Long dayId = (params.dayId?.toString()?.isLong()) ? params.dayId?.toString()?.toLong() : null
		Long timeId = (params.timeId?.toString()?.isLong()) ? params.timeId?.toString()?.toLong() : null
		Long networkId = (params.networkId?.toString()?.isLong()) ? params.networkId?.toString()?.toLong() : null
		Long roomId = (params.roomId?.toString()?.isLong()) ? params.roomId?.toString()?.toLong() : null
		String terms = params.terms?.toString()

		List<PlannedSession> program = sessionPlannerService.getProgram(dayId, timeId, networkId, roomId, terms)

		render program as JSON
	}

	def sendEmail() {
		Long id = (params.userId?.toString()?.isLong()) ? params.userId.toString().toLong() : null
		String settingPropertyName = params.settingPropertyName?.toString()
		Map response = ['success': false]

		if (id && settingPropertyName) {
			User user = User.findById(id)

			if (user) {
				Set<SentEmail> emails = emailCreationService.createEmail(settingPropertyName, user, params)
				emails.each { email ->
					emailService.sendEmail(email)
				}
				response.put('success', true)
			}
		}

		render response as JSON
	}

	def resendEmail() {
		Long id = (params.emailId?.toString()?.isLong()) ? params.emailId.toString().toLong() : null
		Map response = ['success': false]

		if (id) {
			SentEmail email = SentEmail.findById(id)

			if (email) {
				emailService.sendEmail(email, true, true)
				response.put('success', true)
			}
		}

		render response as JSON
	}

	def removePaper() {
		Long id = (params.paperId?.toString()?.isLong()) ? params.paperId.toString().toLong() : null
		Map response = [success: false]

		if (id) {
			Paper paper = Paper.findById(id)

			if (paper) {
				paper.file = null
				paper.fileName = null
				paper.fileSize = null
				paper.contentType = null

				if (paper.save()) {
					response.put('success', true)
				}
			}
		}

		render response as JSON
	}

	def settings() {
		Map<String, String> settings = Setting.getSettingsMapForApi()
		render settings as JSON
	}

	def participantsInNetwork() {
		Long id = (params.networkId?.toString()?.isLong()) ? params.networkId.toString().toLong() : null
		Map results = [success: false] as Map<String, Object>

		if (id) {
			Network network = Network.findById(id)
			List<Map> users = network?.allUsersInNetwork?.collect {
				['lastname': it.lastName, 'firstname': it.firstName, 'email': it.email]
			}

			results.put('success', true)
			if (params.excel?.equalsIgnoreCase('true') || params.excel?.equalsIgnoreCase('1')) {
				XlsMapExport xls = new XlsMapExport(['lastname', 'firstname', 'email'], users,
						'Sheet 1', [params.lastName, params.firstName, params.email])
				results.put('xls', xls.parse().encodeBase64().toString())
			}
			else {
				results.put('users', users)
			}
		}

		render results as JSON
	}

	def participantsInSession() {
		Long sessionId = (params.sessionId?.toString()?.isLong()) ? params.sessionId.toString().toLong() : null
		Long networkId = (params.networkId?.toString()?.isLong()) ? params.networkId.toString().toLong() : null
		List results = []

		if (sessionId) {
			results = ParticipantDate.executeQuery('''
				SELECT u, p, t
				FROM ParticipantDate AS pd
				INNER JOIN pd.user AS u
				INNER JOIN u.sessionParticipants AS sp
				INNER JOIN sp.type AS t
				LEFT JOIN u.papers AS p
				WHERE u.deleted = false
				AND (p.deleted = false OR p IS NULL)
				AND (p.date.id = :dateId OR p IS NULL)
				AND sp.session.id = :sessionId
				AND pd.state.id IN (:dataChecked, :participant)
				ORDER BY t.importance DESC, u.lastName ASC, u.firstName ASC
			''',
					['dateId': pageInformation.date.id, 'sessionId': sessionId, 'dataChecked': ParticipantState
							.PARTICIPANT_DATA_CHECKED, 'participant': ParticipantState.PARTICIPANT])
		}
		else if (networkId) {
			results = ParticipantDate.executeQuery('''
				SELECT u, p
				FROM ParticipantDate AS pd
				INNER JOIN pd.user AS u
				INNER JOIN u.papers AS p
				WHERE u.deleted = false
				AND p.deleted = false
				AND p.date.id = :dateId
				AND p.session.id IS NULL
				AND p.networkProposal.id = :networkId
				AND pd.state.id IN (:newParticipant, :dataChecked, :participant)
				ORDER BY u.lastName ASC, u.firstName ASC
			''',
					['dateId': pageInformation.date.id, 'networkId': networkId, 'newParticipant': ParticipantState
							.NEW_PARTICIPANT, 'dataChecked': ParticipantState.PARTICIPANT_DATA_CHECKED,
							'participant': ParticipantState.PARTICIPANT])
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
				AND pd.state.id IN (:dataChecked, :participant)
				AND p.networkProposal.id = :networkId
				ORDER BY u.lastName ASC, u.firstName ASC
			''',
					['dateId': pageInformation.date.id, 'networkId': networkId, 'dataChecked': ParticipantState
							.PARTICIPANT_DATA_CHECKED, 'participant': ParticipantState.PARTICIPANT])
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

	def mailNewPassword() {
		Long id = (params.userId?.toString()?.isLong()) ? params.userId.toString().toLong() : null
		Map response = ['success': false] as Map<String, Object>

		if (id) {
			User user = User.findById(id)

			if (user) {
				user.password = User.createPassword()
				passwordService.sendPassword(user, user.password)
				if (user.save()) {
					response.put('success', true)
				}
			}
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
}
