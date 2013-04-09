package org.iisg.eca.domain

class SentEmail extends EventDateDomain {
    User user
    String fromName
    String fromEmail
    String subject
    String body
    //String queryType
    Date dateTimeSent
    int numTries = 0

    static belongsTo = User

    static constraints = {
        fromName        blank: false
        fromEmail       blank: false
        subject         blank: false
        body            blank: false
        //queryType       nullable: true
        dateTimeSent    nullable: true
        numTries        min: 0
    }

    static mapping = {
        table 'sent_emails'
        version false

        id              column: 'sent_email_id'
        user            column: 'user_id'
        fromName        column: 'from_name'
        fromEmail       column: 'from_email'
        subject         column: 'subject'
        body            column: 'body',             type: 'text'
        //queryType       column: 'query_type'
        dateTimeSent    column: 'date_time_sent'
        numTries        column: 'num_tries'
    }

    @Override
    String toString() {
        "${subject} : ${user}"
    }
}
