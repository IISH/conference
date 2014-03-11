package org.iisg.eca.domain

/**
 * Domain class of table holding all (to be) sent emails
 */
class SentEmail extends EventDateDomain {
    User user
    String fromName
    String fromEmail
    String subject
    String body
    Date dateTimeCreated = new Date()
    Date dateTimeSent
    String dateTimesSentCopy
    boolean sendAsap = false
    int numTries = 0

    static belongsTo = User

    static constraints = {
        fromName            blank: false
        fromEmail           blank: false
        subject             blank: false
        body                blank: false
        dateTimeCreated     nullable: true
        dateTimeSent        nullable: true
        dateTimesSentCopy   nullable: true
        numTries            min: 0
    }

    static mapping = {
        table 'sent_emails'
        version false

        id                  column: 'sent_email_id'
        user                column: 'user_id'
        fromName            column: 'from_name'
        fromEmail           column: 'from_email'
        subject             column: 'subject'
        body                column: 'body',                     type: 'text'
        dateTimeCreated     column: 'date_time_created'
        dateTimeSent        column: 'date_time_sent'
        dateTimesSentCopy   column: 'dates_times_sent_copy',    type: 'text'
        sendAsap            column: 'send_asap'
        numTries            column: 'num_tries'
    }

    /**
     * Returns all date time copies as a list with Date objects
     * @return A list with all the date/times a copy of this email was sent
     */
    List<Date> getAllDateTimeCopies() {
        List<String> copies = (dateTimesSentCopy) ? dateTimesSentCopy.split(';') : [];
        return copies.collect { date ->
            new Date(date.toLong() * 1000L)
        }
    }

    /**
     * Update the date/time this email was sent with the current date/time.
     * If the email has already been sent before, the copies field is updated instead
     */
    void updateDateTimeSent() {
        if (!dateTimeSent) {
            dateTimeSent = new Date()
        }
        else {
            List<String> copies = (dateTimesSentCopy) ? dateTimesSentCopy.split(';') : []
            copies.add(((long) (System.currentTimeMillis() / 1000L)).toString())
            dateTimesSentCopy = copies.join(';')
        }
    }

    /**
     * Replaces any additional properties in the email with the provided value
     * @param property The property to replace
     * @param value The value which should take the properties place
     */
    void addAdditionalValue(String property, String value) {
        if (body.contains("[${property}]")) {
            body = body.replace("[$property]", value)
        }

	    if (subject.contains("[${property}]")) {
		    subject = subject.replace("[$property]", value)
	    }
    }

    @Override
    String toString() {
        "${subject} : ${user}"
    }
}
