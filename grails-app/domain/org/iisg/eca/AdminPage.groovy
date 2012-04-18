package org.iisg.eca

/**
 * Domain class of table holding all admin pages per event
 */
class AdminPage extends EventDomain {
    Page page
    Event event
    boolean showInMenu = true

	static belongsTo = [Event, Page]

    static constraints = {
        event   nullable: true
    }

	static mapping = {
        table 'admin_pages'
		version false

        id          column: 'admin_page_id'
        page        column: 'page_id'
        event       column: 'event_id'
        showInMenu  column: 'show_in_menu'
	}
}
