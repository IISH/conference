package org.iisg.eca.domain

import groovy.sql.Sql

class EmailCode extends EventDomain {
    def dataSource

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

    String runScript(Map params) {
        GroovyShell shell = new GroovyShell(new Binding([sql: new Sql(dataSource), params: params]))
        shell.evaluate(this.groovyScript).toString()
    }

    @Override
    String toString() {
        "${code} : ${description}"
    }
}
