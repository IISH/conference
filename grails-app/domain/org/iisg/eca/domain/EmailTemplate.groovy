package org.iisg.eca.domain

class EmailTemplate extends EventDomain {
    String usedBy
    String subject
    String body
    String sender
    String comment

    String testEmail
    boolean testAfterSave = false

    static transients = ['testEmail', 'testAfterSave']

    static constraints = {
        usedBy  blank: false, maxSize: 255
        subject blank: false, maxSize: 78
        body    blank: false
        sender  blank: false, maxSize: 30
        comment nullable: true

        testEmail   email: true,    maxSize: 255
    }

    static mapping = {
        table 'email_templates'
        version false

        id      column: 'email_template_id'
        usedBy  column: 'used_by'
        subject column: 'subject'
        body    column: 'body',     type: 'text'
        sender  column: 'sender'
        comment column: 'comment',  type: 'text'
    }

    def afterInsert() {
        if (testAfterSave) {
            // TODO: sent test email
        }
    }

    def afterUpdate() {
        if (testAfterSave) {
            // TODO: sent test email
        }
    }

    @Override
    String toString() {
        usedBy
    }
}
