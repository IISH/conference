package org.iisg.eca.domain

class SentEmail extends EventDateDomain {
    User user
    String from
    String fromEmail
    String subject
    String body
    Date dateTimeSent
    int numTries = 0

    static constraints = {
        from            blank: false
        fromEmail       blank: false
        subject         blank: false
        body            blank: false
        dateTimeSent    nullable: true
        numTries        min: 0
    }

    static mapping = {
        table 'sent_emails'
        version false

        id              column: 'sent_email_id'
        user            column: 'user_id'
        from            column: 'from'
        fromEmail       column: 'from_email'
        subject         column: 'subject'
        body            column: 'body',             type: 'text'
        dateTimeSent    column: 'date_time_sent'
        numTries        column: 'num_tries'
    }

    @Override
    String toString() {
        "${subject} : ${user}"
    }
}
