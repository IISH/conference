package org.iisg.eca.domain

class StatisticsTemplate extends EventDomain {
    String template

    static constraints = {
        template    nullable: true
    }

    static mapping = {
        table 'statistics_templates'
        version false

        id          column: 'statistics_template_id'
        template    column: 'template', type: 'text'
    }
}
