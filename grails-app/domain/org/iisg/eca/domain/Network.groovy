package org.iisg.eca.domain

/**
 * Domain class of table holding all existing networks
 */
class Network extends EventDateDomain {
    String name
    String comment
    String url
    boolean showOnline = true
    boolean showInternal = true
    
    static hasMany = [chairs: NetworkChair, participantVolunteering: ParticipantVolunteering]

    static constraints = {
        date    nullable: true
        name    blank: false,   maxSize: 30
        comment nullable: true
        url     blank: false,   maxSize: 255,   url: true
    }

    static mapping = {
        table 'networks'
        version false

        id      column: 'network_id'
        date    column: 'date_id'
        name    column: 'name'
        comment column: 'comment',      type: 'text'
        url     column: 'url'
    }
    
    @Override
    String toString() {
        name
    }
}
