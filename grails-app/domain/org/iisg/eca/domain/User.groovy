package org.iisg.eca.domain

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request

import java.util.regex.Pattern
import java.security.SecureRandom
import org.apache.commons.lang.RandomStringUtils
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all registered users
 */
class User {
	static final int USER_STATUS_NOT_FOUND = 0
	static final int USER_STATUS_FOUND = 1
	static final int USER_STATUS_DISABLED = 2
	static final int USER_STATUS_DELETED = 3
	static final int USER_STATUS_EMAIL_DISCONTINUED = 4
	static final Pattern PASSWORD_PATTERN = Pattern.compile('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$')

	/**
	 * Information about the current page
	 */
	def static pageInformation
	def gormClientDetailsService
	def gormTokenStoreService
	def tokenServices

	/**
	 * The saltSource is responsible for the creation of salts
	 */
	def saltSource

	/**
	 * Information about the currently logged in user
	 */
	transient springSecurityService

	String email
	String lastName
	String firstName
	String gender
	String title
	String address
	String city
	Country country
	String language = 'en'
	String password
	String salt
	String requestCode
	Date requestCodeValidUntil
	Date newPasswordEmailed
	boolean sendNewPassword = false
	String phone
	String fax
	String mobile
	String organisation
	String department
	String cv
	String extraInfo
	Date dateAdded = new Date()
	boolean emailDiscontinued = false
	boolean enabled = true
	boolean deleted = false
	User addedBy

	static belongsTo = [Country, Group]
	static hasMany = [groups             : Group,
					  networks           : NetworkChair,
					  participantDates   : ParticipantDate,
					  userRoles          : UserRole,
					  papers             : Paper,
					  sessionParticipants: SessionParticipant,
					  sentEmails         : SentEmail,
					  dateTimesNotPresent: SessionDateTime,
					  userPages          : UserPage,
					  daysPresent        : ParticipantDay,
					  sessionsAdded      : Session]

	static mapping = {
		table 'users'
		version false
		sort "lastName"

        id                      column: 'user_id'
        email                   column: 'email'
        lastName                column: 'lastname'
        firstName               column: 'firstname'
        gender                  column: 'gender'
        title                   column: 'title'
        address                 column: 'address',      type: 'text'
        city                    column: 'city'
        country                 column: 'country_id',   fetch: 'join'
        language                column: 'language'
        password                column: 'password'
        salt                    column: 'salt'
        requestCode             column: 'request_code'
        requestCodeValidUntil   column: 'request_code_valid_until'
        newPasswordEmailed      column: 'new_password_emailed'
	    sendNewPassword         column: 'send_new_password'
        phone                   column: 'phone'
        fax                     column: 'fax'
        mobile                  column: 'mobile'
        organisation            column: 'organisation'
        department              column: 'department'
        cv                      column: 'cv',           type: 'text'
        extraInfo               column: 'extra_info',   type: 'text'
        dateAdded               column: 'date_added'
        emailDiscontinued       column: 'email_discontinued'
	    enabled                 column: 'enabled'
	    deleted                 column: 'deleted'
		addedBy                 column: 'added_by',     fetch: 'join'

        groups                  joinTable: 'users_groups'
        dateTimesNotPresent     joinTable: 'participant_not_present'
        userPages               cascade: 'all-delete-orphan'
        papers                  cascade: 'all-delete-orphan'
        sessionParticipants     cascade: 'all-delete-orphan'
        daysPresent             cascade: 'all-delete-orphan'
    }

    static constraints = {
        email                   maxSize: 100,   blank: false,   unique: true,   email: true
        lastName                maxSize: 100,   blank: false
        firstName               maxSize: 100,   blank: false
        gender                                  nullable: true, inList: ['M', 'F']
        title                   maxSize: 20,    nullable: true
        address                                 nullable: true
        city                    maxSize: 100,   nullable: true
        country                                 nullable: true
        language                maxSize: 10,    blank: false
        password                maxSize: 128,   nullable: true, display: false, password: true
        salt                    maxSize: 26,    nullable: true, display: false
        requestCode             maxSize: 26,    nullable: true, display: false
        requestCodeValidUntil                   nullable: true, display: false
        newPasswordEmailed                      nullable: true, display: false
        phone                   maxSize: 50,    nullable: true
        fax                     maxSize: 50,    nullable: true
        mobile                  maxSize: 50,    nullable: true
        organisation            maxSize: 255,   nullable: true
        department              maxSize: 255,   nullable: true
        cv                                      nullable: true
        extraInfo                               nullable: true
	    addedBy                                 nullable: true
    }

	static hibernateFilters = {
		hideDeleted(condition: 'deleted = 0', default: true)
	}

    static apiActions = ['GET', 'POST', 'PUT']

	static apiAllowed = [
			'id',
			'email',
			'lastName',
			'firstName',
			'gender',
			'title',
			'address',
			'city',
			'country.id',
			'phone',
			'fax',
			'mobile',
			'organisation',
			'department',
			'cv',
			'extraInfo',
			'papers.id',
			'daysPresent.day.id',
			'addedBy.id'
	]

	static apiPostPut = [
			'email',
			'lastName',
			'firstName',
			'gender',
			'city',
			'address',
			'phone',
			'fax',
			'mobile',
			'organisation',
			'department',
			'cv',
			'country.id',
			'daysPresent.day.id',
			'addedBy.id'
	]

	static namedQueries = {
		allUsers {
			order('lastName', "asc")
			order('firstName', "asc")
		}

		networkChairs { date ->
			allUsers()

			networks {
				network {
					eq('showOnline', true)
					eq('date.id', date.id)
					eq('deleted', false)
				}
			}
		}

		networkChairsInfo { date ->
			networkChairs(date)
		}

		allParticipantUsers {
			allUsers()

			participantDates {
				eq('deleted', false)
			}
		}

		allParticipants { date ->
			allParticipantUsers()

			participantDates {
				'in'('state.id', [ParticipantState.PARTICIPANT_DATA_CHECKED, ParticipantState.PARTICIPANT])
				eq('date.id', date.id)
			}
		}

		allParticipantsSoftState { date ->
			allParticipantUsers()

			participantDates {
				'in'('state.id', [ParticipantState.NEW_PARTICIPANT, ParticipantState.PARTICIPANT_DATA_CHECKED,
								  ParticipantState.PARTICIPANT, ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])
				eq('date.id', date.id)
			}
		}

		allParticipantsNotDeleted { date ->
			allParticipantUsers()

			participantDates {
				not {
					'in'('state.id', [ParticipantState.REMOVED_CANCELLED, ParticipantState.REMOVED_DOUBLE_ENTRY,
									  ParticipantState.WILL_BE_REMOVED])
				}
				eq('date.id', date.id)
			}
		}

		allParticipantsToBeRemoved { date ->
			allParticipantUsers()

			participantDates {
				eq('state.id', ParticipantState.WILL_BE_REMOVED)
				eq('date.id', date.id)
			}
		}

		allParticipantPapers { date ->
			allParticipants(date)

			createAlias('papers', 'papers')
			eq('papers.date.id', date.id)
			eq('papers.deleted', false)
		}

		paperAccepted { date ->
			allParticipantPapers(date)
			eq('papers.state.id', PaperState.PAPER_ACCEPTED)
		}

		paperAcceptedNotAnswered { date ->
			paperAccepted(date)
			eq('papers.mailPaperState', true)
		}

		paperInConsideration { date ->
			allParticipantPapers(date)
			eq('papers.state.id', PaperState.PAPER_IN_CONSIDERATION)
		}

		paperInConsiderationNotAnswered { date ->
			paperInConsideration(date)
			eq('papers.mailPaperState', true)
		}

		paperNotAccepted { date ->
			allParticipantPapers(date)
			eq('papers.state.id', PaperState.PAPER_NOT_ACCEPTED)
		}

		paperNotAcceptedNotAnswered { date ->
			paperNotAccepted(date)
			eq('papers.mailPaperState', true)
		}

		studentLowerFee { date ->
			allParticipantsNotDeleted(date)

			participantDates {
				eq('student', true)
				eq('lowerFeeRequested', true)
			}
		}

		studentLowerFeeNotAnswered { date ->
			studentLowerFee(date)

			participantDates {
				eq('lowerFeeAnswered', false)
			}
		}

		noStudentLowerFee { date ->
			allParticipantsNotDeleted(date)

			participantDates {
				eq('student', false)
				eq('lowerFeeRequested', true)
			}
		}

		noStudentLowerFeeNotAnswered { date ->
			noStudentLowerFee(date)

			participantDates {
				eq('lowerFeeAnswered', false)
			}
		}

		allSessionParticipants { date ->
			allParticipants(date)

			createAlias('sessionParticipants', 'sp')
			createAlias('sp.session', 'sessions')

			eq('sessions.date.id', date.id)
			eq('sessions.deleted', false)
		}

		allAcceptedSessionParticipants { date ->
			allSessionParticipants(date)
			eq('sessions.state.id', SessionState.SESSION_ACCEPTED)
		}

		allAcceptedSessionParticipantsNotAnswered { date ->
			allAcceptedSessionParticipants(date)

			participantDates {
				eq('emailSessionInfo', false)
			}
		}

		allAcceptedSessionChairs { date ->
			allAcceptedSessionParticipants(date)
			eq('sp.type.id', ParticipantType.CHAIR)
		}

		allAcceptedSessionChairsNotAnswered { date ->
			allAcceptedSessionChairs(date)

			participantDates {
				eq('emailSessionChairInfo', false)
			}
		}

		allSessionOrganizers { date ->
			allSessionParticipants(date)
			eq('sp.type.id', ParticipantType.ORGANIZER)
		}

		sessionAcceptedOrganizers { date ->
			allSessionOrganizers(date)
			eq('sessions.state.id', SessionState.SESSION_ACCEPTED)
		}

		sessionAcceptedOrganizersNotAnswered { date ->
			sessionAcceptedOrganizers(date)
			eq('sessions.mailSessionState', true)
		}

		sessionInConsiderationOrganizers { date ->
			allSessionOrganizers(date)
			eq('sessions.state.id', SessionState.SESSION_IN_CONSIDERATION)
		}

		sessionInConsiderationOrganizersNotAnswered { date ->
			sessionInConsiderationOrganizers(date)
			eq('sessions.mailSessionState', true)
		}

		sessionNotAcceptedOrganizers { date ->
			allSessionOrganizers(date)
			eq('sessions.state.id', SessionState.SESSION_NOT_ACCEPTED)
		}

		sessionNotAcceptedOrganizersNotAnswered { date ->
			sessionNotAcceptedOrganizers(date)
			eq('sessions.mailSessionState', true)
		}

		allSessionCreators { date ->
			allParticipants(date)

			createAlias('sessionsAdded', 'sessions')
			eq('sessions.date.id', date.id)
			eq('sessions.deleted', false)
		}

		sessionAcceptedCreators { date ->
			allSessionCreators(date)
			eq('sessions.state.id', SessionState.SESSION_ACCEPTED)
		}

		sessionAcceptedCreatorsNotAnswered { date ->
			sessionAcceptedCreators(date)
			eq('sessions.mailSessionState', true)
		}

		sessionInConsiderationCreators { date ->
			allSessionCreators(date)
			eq('sessions.state.id', SessionState.SESSION_IN_CONSIDERATION)
		}

		sessionInConsiderationCreatorsNotAnswered { date ->
			sessionInConsiderationCreators(date)
			eq('sessions.mailSessionState', true)
		}

		sessionNotAcceptedCreators { date ->
			allSessionCreators(date)
			eq('sessions.state.id', SessionState.SESSION_NOT_ACCEPTED)
		}

		sessionNotAcceptedCreatorsNotAnswered { date ->
			sessionNotAcceptedCreators(date)
			eq('sessions.mailSessionState', true)
		}

		noPaymentInfo { date ->
			allParticipants(date)

			participantDates {
				eq('emailPaymentInfo', false)
			}
		}

		confirmedPayments { date ->
			allParticipantsSoftState(date)

			participantDates {
				orders {
					eq('payed', Order.ORDER_PAYED)
				}
			}
		}

		allUnconfirmedBankPayments { date ->
			allParticipantsSoftState(date)

			participantDates {
				orders {
					eq('paymentMethod', Order.ORDER_BANK_PAYMENT)
					eq('payed', Order.ORDER_NOT_PAYED)
				}
			}
		}

		allUnconfirmedOnlinePayments { date ->
			allParticipantsSoftState(date)

			participantDates {
				orders {
					eq('paymentMethod', Order.ORDER_OGONE_PAYMENT)
					eq('payed', Order.ORDER_NOT_PAYED)
				}
			}
		}
	}

	void softDelete() {
		deleted = true
	}

	/**
	 * Returns all roles assigned to this user
	 * @return A set of roles assigned to this user
	 */
	Set<Role> getRoles() {
		try {
			return UserRole.findAllByUser(this, [cache: true]).collect { it.role } as Set
		}
		// Just return an empty set in case of an exception,
		// this only happens during other fatal exceptions which are far more important
		catch (Exception e) {
			return [] as Set
		}
	}

	/**
	 * Returns all events this user can access
	 * @return A set of events this user can access
	 */
	List<Event> getEvents() {
		List<Event> events
		List<Role> roles = Role.findAllByFullRights(true, [cache: true])

		// If the user is granted access to all events, just return a list of all events
		// Otherwise, only return the events he/she is specifically given access to
		if (SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
			events = Event.listOrderByShortName([cache: true])
		}
		else {
			events = Event.executeQuery('''
                SELECT e
                FROM UserRole AS ur
                INNER JOIN ur.event AS e
                WHERE ur.user.id = :userId
                ORDER BY e.shortName
            ''', [userId: this.id], [cache: true])
		}

		events
	}

	/**
	 * Find out if user is granted full rights
	 * @return Whether this user has full rights
	 */
	boolean hasFullRights() {
		Role fullRights = getRoles().find { it.fullRights }
		return (fullRights != null)
	}

	/**
	 * Find out if user is part of the crew and can access the CMS
	 * @return Whether this user is part of the crew
	 */
	boolean isCrew() {
		return (getRoles().size() > 0)
	}

	/**
	 * Find out if user is a network chair
	 * @return Whether this user is a network chair
	 */
	boolean isNetworkChair() {
		NetworkChair chair = NetworkChair.findByChair(this)
		return (chair != null)
	}

	/**
	 * Find out if user has this role in one or more sessions
	 * @param type The participant type in question
	 * @return Whether this user has this role in one or more sessions
	 */
	boolean hasRoleInASession(ParticipantType type) {
		SessionParticipant sessionParticipant = SessionParticipant.findByUserAndType(this, type)
		return (sessionParticipant != null)
	}

	/**
	 * Returns an access token for the user or will create a new one if the access token is not found or expired
	 * @return An OAuth2 access token
	 */
	OAuth2AccessToken getAccessToken() {
		Collection<OAuth2AccessToken> tokens = gormTokenStoreService.findTokensByClientIdAndUserName('userClient', this.email)
		OAuth2AccessToken token = (tokens?.size() > 0) ? tokens.first() : null

		// If the token expires within an hour, then request a new one already
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.HOUR, 1)

		if (!token || token.expired || calendar.time.after(token.expiration)) {
			ClientDetails client = gormClientDetailsService.loadClientByClientId('userClient')

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					this.email, this.password, client.authorities)
			OAuth2Request oauthRequest = new OAuth2Request([:], client.clientId, client.authorities,
					true, client.scope, client.resourceIds, '', [] as Set, [:])

			OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oauthRequest, authToken)
			token = tokenServices.createAccessToken(oAuth2Authentication)
		}

		return token
	}

	void updateForApi(String property, String value) {
		switch (property) {
			case 'daysPresent.day.id':
				this.daysPresent?.clear()
				this.save(flush: true)
				value.split(';').each { dayId ->
					if (dayId.toString().isLong()) {
						Day day = Day.findById(dayId.toString().toLong())
						if (day) {
							this.addToDaysPresent(new ParticipantDay(day: day))
						}
					}
				}
				break
			case 'country.id':
				Country country = (value.isLong()) ? Country.get(value.toLong()) : null
				if (country) {
					this.country = country
				}
				break
			case 'addedBy.id':
				User addedBy = (value.isLong()) ? User.findById(value.toLong()) : null
				if (addedBy) {
					this.addedBy = addedBy
				}
				break
		}
	}

	def beforeInsert() {
		// Make sure the email address is in lowercase
		emailToLowercase()

		// Before insertion of a user, hash a new password
		if (!password) {
			password = createPassword()
		}
		encodePassword()
	}

	def beforeUpdate() {
		// Make sure the email address is in lowercase
		emailToLowercase()

		// Make sure to hash the password if changed
		if (isDirty('password')) {
			encodePassword()
		}
	}

	/**
	 * Returns the participant info of this user for the given event date
	 * @param date The event date to obtain participant data of
	 * @return The participant date, if found
	 */
	ParticipantDate getParticipantForDate(EventDate date) {
		ParticipantDate.findByUserAndDate(this, date)
	}

	/**
	 * Creates a participant for this user for the given event date
	 * @param date The event date of which the user participates
	 * @return A new participant date instance (should still be saved)
	 */
	ParticipantDate createParticipantForDate(EventDate date) {
		ParticipantDate participant = null
		ParticipantDate.withoutHibernateFilters {
			participant = ParticipantDate.findByUserAndDate(this, date)
		}

		// If we found the filtered participant, undo the deletion
		if (participant) {
			participant.deleted = false
		}
		else {
			participant = new ParticipantDate(
					user: this,
					state: ParticipantState.get(ParticipantState.NEW_PARTICIPANT),
					feeState: FeeState.get(FeeState.NO_FEE_SELECTED)
			)
		}

		return participant
	}

	/**
	 * Sorting here instead of using the database, to overcome issues
	 * @return The papers of this user sorted
	 */
	List<Paper> getPapersSorted() {
		if (papers) {
			papers.sort { Paper paper1, Paper paper2 ->
				paper1.title <=> paper2.title
			}
		}
		else {
			[]
		}
	}

	/**
	 * Check to see if the given password equals the hashed password
	 * @param plainPassword The password to hash and compare
	 * @return Is a correct password or not
	 */
	boolean isPasswordCorrect(String plainPassword) {
		plainPassword = plainPassword?.trim()
		String hashedPassword = springSecurityService.encodePassword(plainPassword, saltSource.getSalt(this))
		hashedPassword.equals(password)
	}

	/**
	 * Returns the status of the user
	 * @return The status
	 */
	int getStatus() {
		// The user is at least found
		int status = USER_STATUS_FOUND

		if (this.deleted) {
			status = USER_STATUS_DELETED
		}
		else if (!this.enabled) {
			status = USER_STATUS_DISABLED
		}
		else if (this.emailDiscontinued) {
			status = USER_STATUS_EMAIL_DISCONTINUED
		}

		return status
	}

	/**
	 * Creates a 26 characters long salt using a secure random generator
	 * @return A new secure random salt
	 */
	static String createSalt() {
		createPassword(26)
	}

	/**
	 * Creates a new password of the given length
	 * @return A new password
	 */
	static String createPassword(int length = 8) {
		Set<Character> charSet = ('0'..'9') + ('a'..'z') + ('A'..'Z')
		charSet.removeAll('0', 'O', '1', 'l', 'I')
		RandomStringUtils.random(length, 0, charSet.size(), true, true, charSet as char[], new SecureRandom())
	}

	/**
	 * Every time a new password is saved (and has to be hashed), also create a new user salt
	 */
	protected void encodePassword() {
		salt = createSalt()
		password = springSecurityService.encodePassword(password, saltSource.getSalt(this))
	}

	/**
	 * Makes sure the email address is always saved in lowercase characters
	 */
	private void emailToLowercase() {
		this.email = this.email.trim().toLowerCase()
	}

	@Override
	String toString() {
		return "${firstName.trim()} ${lastName.trim()}"
	}

	/**
	 * Return firstname and lastname
	 */
	String getFullName() {
		return "${firstName} ${lastName}".trim()
	}
}
