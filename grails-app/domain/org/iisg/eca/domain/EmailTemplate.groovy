package org.iisg.eca.domain

class EmailTemplate extends EventDomain {
    String description
    String subject
    String body
    String sender
    String action
    String queryType
    int sortOrder = 0
    boolean usedInternal = false
    String comment

    String testEmail
    boolean testAfterSave = false

    static transients = ['testEmail', 'testAfterSave']

    static constraints = {
        description blank: false,   maxSize: 255
        subject     blank: false,   maxSize: 78
        body        blank: false
        sender      blank: false,   maxSize: 30
        action      blank: false,   maxSize: 30
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
        usedInternal    column: 'used_internal'
        comment         column: 'comment',  type: 'text'
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
        description
    }
}
