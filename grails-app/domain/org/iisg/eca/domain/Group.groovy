package org.iisg.eca.domain

/**
 * Domain class of table holding all existing permission groups
 */
class Group extends DefaultDomain {
    def pageInformation

	String name
    EventDate date

	static hasMany = [users: User]
    static belongsTo = [EventDate, User]

	static mapping = {
        table 'groups'
		version false

        id      column: 'group_id'
        name    column: 'name'
        date    column: 'date_id'

        users   joinTable: 'users_groups'
	}

	static constraints = {
		name    blank: false,   maxSize: 30
        date    nullable: true
	}

    static hibernateFilters = {
        eventDateFilter(condition: "date_id = (:dateId) or date_id = null", types: 'long')
    }

   /* def beforeLoad() {
        if (pageInformation.date) {
            enableHibernateFilter('eventDateFilter').setParameter('dateId', pageInformation.date.id)
        }
    }   */

  /*  def beforeInsert() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }

    def beforeUpdate() {
        if (pageInformation.date) {
            date = pageInformation.date
        }
    }     */

    Set<Page> getPages() {
        GroupPage.findAllByGroup(this).collect { it.page } as Set
    }

    @Override
    String toString() {
        name
    }
}
