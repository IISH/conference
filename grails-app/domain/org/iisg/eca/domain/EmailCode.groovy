package org.iisg.eca.domain

import groovy.sql.Sql

class EmailCode extends EventDomain {
    def pageInformation
    def dataSource_readOnly

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

    /**
     * Translates this code with data from the database using the specified identifiers
     * @param identifiers A map which contains all ids to filter data upon
     * @return The translated text to be placed in the email
     */
    String translate(Map<String, Long> identifiers) {
        Binding binding = new Binding([sql: new Sql(dataSource_readOnly), params: identifiers])
        GroovyShell shell = new GroovyShell(binding)
        Object ret = shell.evaluate(groovyScript)
        ret.toString()
    }

    @Override
    String toString() {
        "[${code}] : ${description}"
    }
}
