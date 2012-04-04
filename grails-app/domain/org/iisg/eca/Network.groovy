package org.iisg.eca

class Network extends DefaultDomain {
    EventDate date
    String name
    String connectText
    boolean showOnline = true
    boolean showIntern = true
    String expoit

    static belongsTo = EventDate

    static constraints = {
        date        nullable: true
        name        blank: false,   maxSize: 30
        connectText blank: false,   maxSize: 30
        expoit      nullable: true, maxSize: 30
    }

    static mapping = {
        table 'networks'
		version false

        id          column: 'network_id'
        date        column: 'date_id'
        name        column: 'name'
        connectText column: 'connect_text'
        showOnline  column: 'show_online'
        showIntern  column: 'show_intern'
        expoit      column: 'expoit'

        sort 'name'
	}
}
