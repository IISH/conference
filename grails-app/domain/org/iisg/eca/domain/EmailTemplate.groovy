package org.iisg.eca.domain

import groovy.sql.Sql

/**
 * An email template
 */
class EmailTemplate extends EventDomain {
    def dataSource

    /**
     * The possible query types for multiple participants which requires an action after creation
     */
    public static final String QUERY_PAPER_ACCEPTED_NOT_ANSWERED = "paperAcceptedNotAnswered"
    public static final String QUERY_PAPER_IN_CONSIDERATION_NOT_ANSWERED = "paperInConsiderationNotAnswered"
    public static final String QUERY_PAPER_NOT_ACCEPTED_NOT_ANSWERED = "paperNotAcceptedNotAnswered"
    public static final String QUERY_STUDENT_LOWER_FEE_NOT_ANSWERED = "studentLowerFeeNotAnswered"
    public static final String QUERY_NO_STUDENT_LOWER_FEE_NOT_ANSWERED = "noStudentLowerFeeNotAnswered"
    public static final String QUERY_NO_PAYMENT_INFO = "noPaymentInfo"

    /**
     * The identifier names
     */
    public static final String USER_ID = 'userId'
    public static final String DATE_ID = 'dateId'
    public static final String PAPER_ID = 'paperId'

    /**
     * What to filter on for each action when sending mails?
     */
    private static final Map<String, Boolean> ACTION_NULL = [
            participant: false,
            participantState: false,
            paperState: false,
            eventDates: false
    ] as HashMap<String, Boolean>

    private static final Map<String, Boolean> ACTION_PARTICIPANT = [
            participant: true,
            participantState: false,
            paperState: false,
            eventDates: false
    ] as HashMap<String, Boolean>

    private static final Map<String, Boolean> ACTION_PARTICIPANT_PAPERSTATE = [
            participant: true,
            participantState: true,
            paperState: true,
            eventDates: false
    ] as HashMap<String, Boolean>

    private static final Map<String, Boolean> ACTION_PARTICIPANT_STATE = [
            participant: true,
            participantState: true,
            paperState: false,
            eventDates: false
    ] as HashMap<String, Boolean>

    private static final Map<String, Boolean> ACTION_EVENTDATES = [
            participant: false,
            participantState: false,
            paperState: false,
            eventDates: true
    ] as HashMap<String, Boolean>

    /**
     * Email service for sending test emails
     */
    def emailService

    String description
    String subject
    String body
    String sender
    String action = "participant"
    String queryTypeOne
    String queryTypeMultiple
    int sortOrder = 0
    boolean showInBackend = true
    String comment

    String testEmail
    boolean testAfterSave = false

    static transients = EventDomain.transients + ['testEmail', 'testAfterSave']

    static constraints = {
        description         blank: false,   maxSize: 255
        subject             blank: false,   maxSize: 78
        body                blank: false
        sender              blank: false,   maxSize: 100
        action              nullable: true, maxSize: 30
        queryTypeOne        nullable: true, maxSize: 100
        queryTypeMultiple   nullable: true, maxSize: 100
        comment             nullable: true

        testEmail   email: true,    maxSize: 255
    }

    static mapping = {
        table 'email_templates'
        version false
        sort sortOrder: 'asc'

        id                  column: 'email_template_id'
        description         column: 'description'
        subject             column: 'subject'
        body                column: 'body',     type: 'text'
        sender              column: 'sender'
        action              column: 'action'
        queryTypeOne        column: 'query_type_one'
        queryTypeMultiple   column: 'query_type_multiple'
        sortOrder           column: 'sort_order'
        showInBackend       column: 'show_in_backend'
        comment             column: 'comment',  type: 'text'
    }

    def afterInsert() {
        if (testAfterSave) {
            testTemplate()
        }
    }

    def afterUpdate() {
        if (testAfterSave) {
            testTemplate()
        }
    }

    /**
     * Returns a map which indicates what types of filters are allowed
     * for creating emails with this email template
     * @return A map which indicates what is allowed and what is not
     */
    Map<String, Boolean> getFilterMap() {
        switch (action) {
            case 'participant':
                return ACTION_PARTICIPANT
            case 'participantPaperState':
                return ACTION_PARTICIPANT_PAPERSTATE
            case 'participantState':
                return ACTION_PARTICIPANT_STATE
            case 'eventDates':
                return ACTION_EVENTDATES
            default:
               return ACTION_NULL
        }
    }

    /**
     * Tests the template with the testEmail specified
     */
    public void testTemplate() {
        User user = User.findByEmail(testEmail)

        if (user) {
            SentEmail email = emailService.createEmail(user, this, false)
            emailService.sendEmail(email, false)
        }
        else {
            String subject = "Could not find the user for the given email address"
            String message = "Could not find the user for the given email address. \r\nTesting the email template failed!"

            emailService.sendInfoMail(subject, message, pageInformation.date.event, testEmail)
        }

        this.testAfterSave = false
    }

    /**
     * Finds out the names of the associations to call for ids
     * @return An array with the associations if called from a <code>User</code>
     */
    String[] getAssociationsNames() {
        switch (queryTypeMultiple) {
            case QUERY_PAPER_ACCEPTED_NOT_ANSWERED:
            case QUERY_PAPER_IN_CONSIDERATION_NOT_ANSWERED:
            case QUERY_PAPER_NOT_ACCEPTED_NOT_ANSWERED:
                return ['papers']
                break
            default:
                return []
        }
    }

    /**
     * Returns a map that identifies the ids
     * @param identifiers The ids for a single recipient (correct array is expected)
     * @return A map that gives each of the ids a name
     */
    Map<String, Long> getIdentifiersMap(Long[] identifiers) {
        switch (queryTypeMultiple) {
            case QUERY_PAPER_ACCEPTED_NOT_ANSWERED:
            case QUERY_PAPER_IN_CONSIDERATION_NOT_ANSWERED:
            case QUERY_PAPER_NOT_ACCEPTED_NOT_ANSWERED:
                return [(USER_ID) : identifiers[0], (PAPER_ID) : identifiers[1]]
                break
            default:
                return [(USER_ID) : identifiers[0]]
        }
    }

    /**
     * Update the recipients based based on the type of email sent     *
     */
    void updateRecipient(Map<String, Long> identifiers) {
        // Workaround for Hibernate exception "two open sessions" when using Quartz
        Sql sql = new Sql(dataSource)

        switch (queryTypeMultiple) {
            case QUERY_PAPER_ACCEPTED_NOT_ANSWERED:
            case QUERY_PAPER_IN_CONSIDERATION_NOT_ANSWERED:
            case QUERY_PAPER_NOT_ACCEPTED_NOT_ANSWERED:
                sql.executeUpdate('''
                    UPDATE  papers
                    SET     mail_paper_state = 0
                    WHERE   user_id = :userId
                    AND     paper_id = :paperId
                ''', identifiers)
                break
            case QUERY_STUDENT_LOWER_FEE_NOT_ANSWERED:
            case QUERY_NO_STUDENT_LOWER_FEE_NOT_ANSWERED:
                sql.executeUpdate('''
                    UPDATE  participant_date
                    SET     lower_fee_requested = 1,
                            lower_fee_answered = 1
                    WHERE   user_id = :userId
                    AND     date_id = :dateId
                ''', identifiers)
                break
            case QUERY_NO_PAYMENT_INFO:
                sql.executeUpdate('''
                    UPDATE  participant_date
                    SET     email_payment_info = 1
                    WHERE   user_id = :userId
                    AND     date_id = :dateId
                ''', identifiers)
                break
        }
    }

    @Override
    String toString() {
        description
    }
}
