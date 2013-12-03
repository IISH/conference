package org.iisg.eca.domain

class MaintenanceQuery extends DefaultDomain {
    String query
    String description
    int order

    static constraints = {
        query       blank: false,   maxSize: 255
        description blank: false,   maxSize: 255
    }

    static mapping = {
        table 'maintenance_queries'
        version false

        id          column: 'maintenance_query_id'
        query       column: 'query'
        description column: 'description'
        order       column: 'sort_order'

        sort        'order'
    }
}
