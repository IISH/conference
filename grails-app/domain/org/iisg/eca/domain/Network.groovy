package org.iisg.eca.domain

/**
 * Domain class of table holding all existing networks
 */
class Network extends EventDateDomain {
    String name
    String comment
    String url
    String email
    boolean showOnline = true
    boolean showInternal = true
    
    static hasMany = [  chairs: NetworkChair,
                        participantVolunteering: ParticipantVolunteering,
                        paperProposals: Paper,
                        sessions: Session]

    static constraints = {
        date    nullable: true
        name    blank: false,   maxSize: 100
        comment nullable: true
        url     blank: false,   maxSize: 255
        email   nullable: true, maxSize: 100,   email: true
    }

    static mapping = {
        table 'networks'
        version false
        sort name: 'asc'

        id              column: 'network_id'
        date            column: 'date_id'
        name            column: 'name'
        comment         column: 'comment',      type: 'text'
        url             column: 'url'
        email           column: 'email'
        showOnline      column: 'show_online'
        showInternal    column: 'show_internal'

        chairs                      sort: 'isMainChair', order: 'desc', cascade: 'all-delete-orphan'
        sessions                    joinTable: 'session_in_network'
        participantVolunteering     cascade: 'all-delete-orphan'
    }
    
    @Override
    String toString() {
        name
    }
}
