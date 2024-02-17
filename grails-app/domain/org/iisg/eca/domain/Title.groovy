package org.iisg.eca.domain

/**
 * Domain class of table holding all titles
 */
class Title extends EventDomain {
    String title

    static constraints = {
        event   nullable: true
        title   blank: false,   maxSize: 20
    }

    static mapping = {
        table 'titles'
        version false

        id      column: 'title_id'
        event   column: 'event_id'
        title   column: 'title'
    }

	/**
	 * Adds the title to the list of titles if it does not contain the title yet
	 * @param title The title to check for and add if it is not in the list
	 */
	static void addTitleIfNotExists(String title) {
		List<String> titles = list()*.title
		if (!titles.contains(title)) {
			Title missingTitle = new Title(title: title)
			missingTitle.save(flush: true)
		}
	}
    
    @Override
    String toString() {
        title
    }
}
