package org.iisg.eca.domain

class EmailTemplate extends EventDomain {
    def emailService

    String description
    String subject
    String body
    String sender
    String action
    String queryType
    int sortOrder = 0
    boolean showInBackend = true
    String comment

    String testEmail
    boolean testAfterSave = false

    static transients = ['testEmail', 'testAfterSave']

    static constraints = {
        description blank: false,   maxSize: 255
        subject     blank: false,   maxSize: 78
        body        blank: false
        sender      blank: false,   maxSize: 30
        action      nullable: true, maxSize: 30
        queryType   nullable: true, maxSize: 30
        comment     nullable: true

        testEmail   email: true,    maxSize: 255
    }

    static mapping = {
        table 'email_templates'
        version false
        sort sortOrder: 'asc'

        id              column: 'email_template_id'
        description     column: 'description'
        subject         column: 'subject'
        body            column: 'body',     type: 'text'
        sender          column: 'sender'
        action          column: 'action'
        queryType       column: 'query_type'
        sortOrder       column: 'sort_order'
        showInBackend   column: 'show_in_backend'
        comment         column: 'comment',  type: 'text'
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
     * Tests the template with the testEmail specified
     */
    private void testTemplate() {
        User user = User.findByEmail(testEmail)

        if (user) {
            SentEmail email = emailService.createEmail(user, this, pageInformation.date)
            emailService.sendEmail(email, false)
        }
        else {
            String subject = "Could not find the user for the given email address"
            String message = "Could not find the user for the given email address. \r\nTesting the email template failed!"

            emailService.sendInfoMail(subject, message, testEmail)
        }
    }

    @Override
    String toString() {
        description
    }
}
