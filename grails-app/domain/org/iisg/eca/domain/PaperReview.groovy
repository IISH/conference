package org.iisg.eca.domain

/**
 * Domain class of table holding all paper reviews
 */
class PaperReview extends EventDateDomain {
    Paper paper
    User reviewer
    String review
    BigDecimal avgScore

    static belongsTo = [Paper, User]
    static hasMany = [scores: PaperReviewScore]

    static mapping = {
        table 'paper_reviews'
        version false

        id        column: 'paper_review_id'
        paper     column: 'paper_id'
        reviewer  column: 'reviewer_id'
        review    column: 'review',          type: 'text'
        avgScore  column: 'avg_score'

        scores    cascade: 'all-delete-orphan'
    }

    static constraints = {
        review   nullable: true
        avgScore nullable: true
    }

    static hibernateFilters = {
        dateFilter(condition: '(date_id = :dateId OR date_id IS NULL)', types: 'long')
    }

    static apiActions = ['GET', 'POST']

    static apiAllowed = [
            'id',
            'paper.id',
            'reviewer.id',
            'review',
            'avgScore'
    ]

    static apiPostPut = [
            'review',
            'avgScore'
    ]

    /**
     * Updates the average review score of the paper on an update
     */
    def beforeUpdate() {
        this.paper.updateAvgReviewScore()
        this.paper.save()
    }

    @Override
    String toString() {
        "$reviewer"
    }
}
