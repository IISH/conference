package org.iisg.eca.domain

import groovy.sql.Sql

class EmailCode extends EventDomain {
    def dataSource
    def pageInformation

    String code
    String description
    String groovyScript

    static constraints = {
        code            blank: false,   maxSize: 30
        description     nullable: true, maxSize: 255
        groovyScript    blank: false
    }

    static mapping = {
        table 'email_codes'
        version false

        id              column: 'email_code_id'
        code            column: 'code'
        description     column: 'description'
        groovyScript    column: 'groovy_script',    type: 'text'
    }

    String translateForUser(User user) {
        Binding binding = new Binding([sql: new Sql(dataSource), params: [userId: user.id, dateId: pageInformation.date.id]])
        GroovyShell shell = new GroovyShell(binding)
        Object ret = shell.evaluate(groovyScript)
        ret.toString()
    }

    @Override
    String toString() {
        "[${code}] : ${description}"
    }
}
