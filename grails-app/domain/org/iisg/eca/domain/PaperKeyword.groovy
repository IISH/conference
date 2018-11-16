package org.iisg.eca.domain

/**
 * Domain class of table holding all keywords of papers
 */
class PaperKeyword extends EventDateDomain implements Comparable<PaperKeyword> {
    Paper paper
	String groupName
	String keyword

    static belongsTo = [Paper]

    static mapping = {
        table 'paper_keywords'
        version false

        id      	column: 'paper_keywords_id'
        paper   	column: 'paper_id',         fetch: 'join'
		groupName	column: 'group_name'
		keyword 	column: 'keyword'
	}

	static constraints = {
		groupName 	blank: false,   maxSize: 255
		keyword		blank: false,   maxSize: 255
	}

    static apiActions = ['GET', 'POST', 'PUT', 'DELETE']

    static apiAllowed = [
            'id',
            'paper.id',
			'paper',
			'groupName',
			'keyword',
	]

	static apiPostPut = [
			'paper.id',
			'groupName',
			'keyword'
	]

	void updateForApi(String property, String value) {
		switch (property) {
			case 'paper.id':
				Paper paper = Paper.findById(value.toLong())
				if (paper) {
					this.paper = paper
				}
				break
		}
	}

	@Override
	int compareTo(PaperKeyword o) {
		return this.keyword <=> o.keyword
	}

    @Override
    String toString() {
        "${paperId}: ${groupName}: ${keyword}"
    }
}
