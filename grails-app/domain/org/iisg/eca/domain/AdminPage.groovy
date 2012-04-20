package org.iisg.eca.domain

/**
 * Domain class of table holding all admin pages per event
 */
class AdminPage {
  //  def pageInformation

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

    static hibernateFilters = {
        eventFilter(condition: "event_id = :eventId or event_id = null", types: 'long')
    }

   /* def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventFilter').setParameter('eventId', pageInformation.date.event.id)
        }
    }

    def beforeInsert() {
        if (pageInformation.date) {
            event = pageInformation.date.event
        }
    }

    def beforeUpdate() {
        if (pageInformation.date) {
            event = pageInformation.date.event
        }
    }     */
}
