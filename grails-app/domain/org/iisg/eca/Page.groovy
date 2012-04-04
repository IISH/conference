package org.iisg.eca

/**
 * Domain class of table holding all pages of this application
 */
class Page extends DefaultDomain {
	String name
	String url
	String description
    Page parent

    static hasMany = [  adminPages:     AdminPage,
                        dynamicPages:   DynamicPage,
                        subPages:       Page]
    static belongsTo = Page

	static mapping = {
		table 'pages'
		version false

        id          column: 'page_id'
        name        column: 'name'
        url         column: 'url'
        description column: 'description',      sqlType: 'tinytext'
        parent      column: 'parent_page_id'
	}

	static constraints = {
		name        blank: false,   maxSize: 30
		url         blank: false,   maxSize: 30
        description blank: false,   maxSize: 1000
        parent      nullable: true
        subPages    nullable: true
	}

    Set<User> getUsers() {
        UserPage.findAllByPage(this).collect { it.user } as Set
    }

    Set<Group> getGroups() {
        GroupPage.findAllByPage(this).collect { it.group } as Set
    }

    @Override
    def String toString() {
        name
    }
}
