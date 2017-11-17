package org.iisg.eca.domain

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Domain class of table holding all chairs of a network
 */
class PaperReviewScore implements Serializable {
    PaperReview paperReview
    ReviewCriteria criteria
    BigDecimal score

    static belongsTo = [PaperReview, ReviewCriteria]

    static mapping = {
        table 'paper_review_scores'
        version false

        id           column: 'paper_review_score_id'
        paperReview  column: 'paper_review_id',       fetch: 'join'
        criteria     column: 'review_criteria_id',    fetch: 'join'
        score        column: 'score'
    }

    static constraints = {
        paperReview unique: 'criteria'
    }

    static apiActions = ['GET', 'PUT']

    static apiAllowed = [
            'id',
		    'paperReview.id',
            'criteria.id',
            'score'
    ]

	static apiPostPut = [
            'paperReview.id',
            'criteria.id',
            'score'
	]

    void updateForApi(String property, String value) {
        switch (property) {
            case 'paperReview.id':
                PaperReview paperReview = (value.isLong()) ? PaperReview.get(value.toLong()) : null
                if (paperReview) {
                    this.paperReview = paperReview
                }
                break
            case 'criteria.id':
                ReviewCriteria reviewCriteria = (value.isLong()) ? ReviewCriteria.get(value.toLong()) : null
                if (reviewCriteria) {
                    this.criteria = reviewCriteria
                }
                break
        }
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append paperReview
        builder.append criteria
        builder.toHashCode()
    }

    @Override
    boolean equals(other) {
        if (other == null || !(other instanceof PaperReviewScore)) {
            return false
        }

        def builder = new EqualsBuilder()
        builder.append paperReview, other.paperReview
        builder.append criteria, other.criteria
        builder.isEquals()
    }

    @Override
    String toString() {
        "$criteria: $score"
    }
}
