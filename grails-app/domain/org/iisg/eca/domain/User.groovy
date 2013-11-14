package org.iisg.eca.domain

import java.security.SecureRandom
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all registered users
 */
class User extends DefaultDomain {
    /**
     * Information about the current page
     */
    def static pageInformation

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
    String phone
    String fax
    String mobile
    String organisation
    String department
    String cv
    String extraInfo
    Date dateAdded = new Date()
    boolean emailDiscontinued = false

    static belongsTo = [Country, Group]
    static hasMany = [  groups:                 Group,
                        networks:               NetworkChair,
                        participantDates:       ParticipantDate,
                        userRoles:              UserRole,
                        papers:                 Paper,
                        sessionParticipants:    SessionParticipant,
                        sentEmails:             SentEmail,
                        dateTimesNotPresent:    SessionDateTime,
                        userPages:              UserPage,
                        daysPresent:            ParticipantDay]

    static mapping = {
        table 'users'
        version false
        sort "lastName"

        id                  column: 'user_id'
        email               column: 'email'
        lastName            column: 'lastname'
        firstName           column: 'firstname'
        gender              column: 'gender',       sqlType: 'enum'
        title               column: 'title'
        address             column: 'address',      type: 'text'
        city                column: 'city'
        country             column: 'country_id'
        language            column: 'language'
        password            column: 'password'
        salt                column: 'salt'
        requestCode         column: 'request_code'
        phone               column: 'phone'
        fax                 column: 'fax'
        mobile              column: 'mobile'
        organisation        column: 'organisation'
        department          column: 'department'
        cv                  column: 'cv',           type: 'text'
        extraInfo           column: 'extra_info',   type: 'text'
        dateAdded           column: 'date_added'
        emailDiscontinued   column: 'email_discontinued'

        groups              joinTable: 'users_groups'
        dateTimesNotPresent joinTable: 'participant_not_present'
        userPages           cascade: 'all-delete-orphan'
        papers              cascade: 'all-delete-orphan'
        sessionParticipants cascade: 'all-delete-orphan'
    }

    static constraints = {
        email           maxSize: 100,   blank: false,   unique: true,   email: true
        lastName        maxSize: 100,   blank: false
        firstName       maxSize: 100,   blank: false
        gender                          nullable: true, inList: ['M', 'F']
        title           maxSize: 20,    nullable: true
        address                         nullable: true
        city            maxSize: 100,   nullable: true
        country                         nullable: true
        language        maxSize: 10,    blank: false
        password        maxSize: 128,   nullable: true, display: false, password: true
        salt            maxSize: 26,    nullable: true, display: false
        requestCode     maxSize: 26,    nullable: true, display: false
        phone           maxSize: 50,    nullable: true
        fax             maxSize: 50,    nullable: true
        mobile          maxSize: 50,    nullable: true
        organisation    maxSize: 255,   nullable: true
        department      maxSize: 255,   nullable: true
        cv                              nullable: true
        extraInfo                       nullable: true
    }

    static namedQueries = {
        allUsers {
            order('lastName', "asc")
            order('firstName', "asc")
        }

        networkChairs { date ->
            allUsers()

            networks {
                network {
                    eq('date.id', date.id)
                    eq('deleted', false)
                }
            }
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
                'in'('state.id', [  ParticipantState.NEW_PARTICIPANT, ParticipantState.PARTICIPANT_DATA_CHECKED,
                                    ParticipantState.PARTICIPANT, ParticipantState.PARTICIPANT_DID_NOT_FINISH_REGISTRATION])
                eq('date.id', date.id)
            }
        }

        allParticipantsNotDeleted { date ->
            allParticipantUsers()

            participantDates {
                not {
                    'in'('state.id', [  ParticipantState.REMOVED_CANCELLED, ParticipantState.REMOVED_DOUBLE_ENTRY,
                                        ParticipantState.WILL_BE_REMOVED])
                }
                eq('date.id', date.id)
            }
        }

        paperAccepted { date ->
            allParticipants(date)

            papers {
                eq('state.id', PaperState.PAPER_ACCEPTED)
                eq('date.id', date.id)
            }
        }

        paperInConsideration { date ->
            allParticipants(date)

            papers {
                eq('state.id', PaperState.PAPER_IN_CONSIDERATION)
                eq('date.id', date.id)
            }
        }

        paperNotAccepted { date ->
            allParticipants(date)

            papers {
                eq('state.id', PaperState.PAPER_NOT_ACCEPTED)
                eq('date.id', date.id)
            }
        }

        studentLowerFeeNotAnswered { date ->
            allParticipantsNotDeleted(date)

            participantDates {
                eq('student', true)
                eq('lowerFeeRequested', true)
                eq('lowerFeeAnswered', false)
            }
        }

        noStudentLowerFeeNotAnswered { date ->
            allParticipantsNotDeleted(date)

            participantDates {
                eq('student', false)
                eq('lowerFeeRequested', true)
                eq('lowerFeeAnswered', false)
            }
        }

        noPaymentInfo { date ->
            allParticipants(date)

            participantDates {
                eq('emailPaymentInfo', false)
            }
        }
    }

    /**
     * Returns all roles assigned to this user
     * @return A set of roles assigned to this user
     */
    Set<Role> getRoles() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    /**
     * Returns all events this user can access
     * @return A set of events this user can access
     */
    List<Event> getEvents() {
        List<Event> events
        List<Role> roles = Role.findAllByFullRights(true)

        // If the user is granted access to all events, just return a list of all events
        // Otherwise, only return the events he/she is specifically given access to
        if (SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
            events = Event.listOrderByShortName()
        }
        else {
            events = Event.executeQuery('''
                SELECT e
                FROM UserRole AS ur
                INNER JOIN ur.event AS e
                WHERE ur.user.id = :userId
                ORDER BY e.shortName
            ''', [userId: this.id])
        }

        events
    }

    def beforeInsert() {
        // Before insertion of a user, hash the password
        encodePassword()

        // Make sure the email address is in lowercase
        emailToLowercase()
    }

    def beforeUpdate() {
        // Make sure to hash the password if changed
        if (isDirty('password')) {
            encodePassword()
        }

        // Make sure the email address is in lowercase
        emailToLowercase()
    }

    /**
     * Check to see if the given password equals the hashed password
     * @param plainPassword The password to hash and compare
     * @return Is a correct password or not
     */
    boolean isPasswordCorrect(String plainPassword) {
        String hashedPassword = springSecurityService.encodePassword(plainPassword, saltSource.getSalt(this))
        hashedPassword.equals(password)
    }

    /**
     * Creates a 26 characters long String using a secure random generator
     * @return A new secure random String
     */
    static String createSecureRandomString() {
        Random r = new SecureRandom()
        new BigInteger(130, r).toString(32)
    }

    /**
     * Every time a new password is saved (and has to be hashed), also create a new user salt
     */
    protected void encodePassword() {
        salt = createSecureRandomString()
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
        "${lastName}, ${firstName}"
    }
}
