package org.iisg.eca.domain

import java.security.SecureRandom
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Domain class of table holding all registered users
 */
class User extends DefaultDomain {
    def static pageInformation

    /**
     * The saltSource is responsible for the creation of salts
     */
    def saltSource

    /**
     * Information about the currently logged in user
     */
    transient springSecurityService

    enum Gender {
        M("M"), F("F")

        final String value

        Gender(value) {
            this.value = value
        }

        @Override
        String toString() {
            value
        }
    }

    String email
    String lastName
    String firstName
    Gender gender
    String title
    String address
    String city
    Country country
    String language
    String password
    String salt
    String phone
    String fax
    String mobile
    String organisation
    String department
    String extraInfo
    Date dateAdded = new Date()

    static belongsTo = [Country, Group]
    static hasMany = [  groups:                 Group,
                        networks:               NetworkChair,
                        participantDates:       ParticipantDate,
                        userRoles:              UserRole,
                        papers:                 Paper,
                        sessionParticipants:    SessionParticipant,
                        sentEmails:             SentEmail,
                        dateTimesNotPresent:    SessionDateTime,
                        userPages:              UserPage]

    static mapping = {
        table 'users'
        version false

        id              column: 'user_id'
        email           column: 'email'
        lastName        column: 'lastname'
        firstName       column: 'firstname'
        gender          column: 'gender',       sqlType: 'enum'
        title           column: 'title'
        address         column: 'address',      type: 'text'
        city            column: 'city'
        country         column: 'country_id'
        language        column: 'language'
        password        column: 'password'
        salt            column: 'salt'
        phone           column: 'phone'
        fax             column: 'fax'
        mobile          column: 'mobile'
        organisation    column: 'organisation'
        department      column: 'department'
        extraInfo       column: 'extra_info',   type: 'text'
        dateAdded       column: 'date_added'

        groups              joinTable: 'users_groups'
        dateTimesNotPresent joinTable: 'participant_not_present', cascade: 'all-delete-orphan'
        userRoles           cascade: 'all-delete-orphan'
        papers              cascade: 'all-delete-orphan'
        sessionParticipants cascade: 'all-delete-orphan'
    }

    static constraints = {
        email           maxSize: 30,    blank: false,   unique: true,   email: true
        lastName        maxSize: 100,   blank: false
        firstName       maxSize: 100,   blank: false
        gender                          nullable: true
        title           maxSize: 30,    nullable: true
        address                         nullable: true
        city            maxSize: 100,   blank: false
        language        maxSize: 10,    blank: false
        password        maxSize: 128,   blank: false,   display: false, password: true
        salt            maxSize: 26,    nullable: true, display: false
        phone           maxSize: 50,    nullable: true
        fax             maxSize: 50,    nullable: true
        mobile          maxSize: 50,    nullable: true
        organisation    maxSize: 255,   nullable: true
        department      maxSize: 255,   nullable: true
        extraInfo                       nullable: true
        papers          validator: { val, obj ->
                            Integer maxPapers = Setting.getByEvent(Setting.findAllByProperty(Setting.MAX_PAPERS_PER_PERSON_PER_SESSION))?.value?.toInteger()
                            if (maxPapers && (obj?.papers?.findAll { !it.deleted && (it.date.id == pageInformation.date.id) }?.size() > maxPapers)) {
                                "paper.validation.max.message"
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
     * Returns all event dates this user can access
     * @return A set of event dates this user can access
     */
    Set<EventDate> getDates() {
        def roles = Role.findAllByFullRights(true)

        // If the user is granted access to all events, just return a list of all event dates
        // Otherwise, only return the event dates he/she is specifically given access to
        if (SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
            EventDate.list()
        }
        else {
            UserRole.findAllByUser(this).collect { it.date } 
        }
    }

    def beforeInsert() {
        // Before insertion of a user, hash the password
        encodePassword()
    }

    def beforeUpdate() {
        // Make sure to hash the password if changed
        if (isDirty('password')) {
            encodePassword()
        }
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

    @Override
    String toString() {
        "${lastName}, ${firstName}"
    }
}
