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
        sort "lastName"

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
        requestCode     column: 'request_code'
        phone           column: 'phone'
        fax             column: 'fax'
        mobile          column: 'mobile'
        organisation    column: 'organisation'
        department      column: 'department'
        extraInfo       column: 'extra_info',   type: 'text'
        dateAdded       column: 'date_added'

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
        List<EventDate> dates
        List<Role> roles = Role.findAllByFullRights(true)

        // If the user is granted access to all events, just return a list of all event dates
        // Otherwise, only return the event dates he/she is specifically given access to
        if (SpringSecurityUtils.ifAnyGranted(roles*.role.join(','))) {
            dates = EventDate.list()
        }
        else {
            dates = UserRole.findAllByUser(this).collect { it.date }
        }

        dates
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
        this.email = this.email.toLowerCase()
    }

    @Override
    String toString() {
        "${lastName}, ${firstName}"
    }
}
