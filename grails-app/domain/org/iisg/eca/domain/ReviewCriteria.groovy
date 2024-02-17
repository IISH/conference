package org.iisg.eca.domain

/**
 * Domain class of table holding all possible review criteria
 */
class ReviewCriteria extends EventDateDomain {
    String name
    int sortOrder = 0
    boolean deleted

    static hasMany = [scores: PaperReviewScore]

    static mapping = {
        table 'review_criteria'
        version false
        sort 'sortOrder'

        id        column: 'review_criteria_id'
        name      column: 'name'
        sortOrder column: 'sort_order'
        deleted   column: 'deleted'
    }

    static constraints = {
        name blank: false, maxSize: 30
    }

    static hibernateFilters = {
        dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
        hideDeleted(condition: 'deleted = 0', default: true)
    }

    static apiActions = ['GET']

    static apiAllowed = [
            'id',
            'name',
            'sortOrder'
    ]

    void softDelete() {
        deleted = true
    }

    @Override
    String toString() {
        name
    }
}
