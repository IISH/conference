package org.iisg.eca.controller

import grails.converters.JSON
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.PaperReview
import org.iisg.eca.domain.PaperReviewScore
import org.iisg.eca.domain.PaperState
import org.iisg.eca.domain.ReviewCriteria

import java.math.RoundingMode

/**
 * Controller responsible for handling requests on papers
 */
class PaperController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows a list of all settings for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all data on a particular paper
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Paper paper = Paper.findById(params.id)

        // We also need a paper to be able to show something
        if (!paper) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'paper.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Refer to the participant page, paper tab
        redirect(uri: eca.createLink(controller: 'participant', action: 'show', id: paper.user.id, fragment: 'papers-tab', noBase: true))
    }

    /**
     * Update the reviews for a paper
     * (AJAX call)
     */
    def updateReview() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a paper review id, try to find the records for these ids
            if (params.id?.isLong() && params.id?.isLong()) {
                PaperReview paperReview = PaperReview.findById(params.id)

                // Update the paper review if it exists
                if (paperReview) {
                    BigDecimal total = BigDecimal.ZERO
                    int count = 0

                    Set<PaperReviewScore> toBeDeleted = []
                    toBeDeleted += paperReview.scores

                    ReviewCriteria.list().each { criteria ->
                        if (params["criteria_${criteria.id}"]) {
                            BigDecimal score = new BigDecimal(params["criteria_${criteria.id}"].toString())
                            total = total.add(score)
                            count++

                            PaperReviewScore paperReviewScore = paperReview.scores.find { it.criteria.id == criteria.id }
                            if (paperReviewScore) {
                                paperReviewScore.score = score
                                toBeDeleted.remove(paperReviewScore)
                            }
                            else {
                                paperReview.addToScores(new PaperReviewScore(criteria: criteria, score: score))
                            }
                        }
                    }

                    // Whatever is left should be deleted
                    toBeDeleted.each { paperReview.removeFromScores(it) }

                    paperReview.review = !params.review.isEmpty() ? params.review.trim() : null
                    paperReview.comments = !params.reviewComments.isEmpty() ? params.reviewComments.trim() : null
                    paperReview.award = params.reviewAward == 'true'
                    paperReview.avgScore = (count > 0) ? total.divide(count, 1, RoundingMode.HALF_UP) : null

                    // Save the paper review
                    if (paperReview.save(flush: true)) {
                        responseMap = [success: true, avgScore: paperReview.avgScore]
                    }
                    else {
                        responseMap = [success: false, message: paperReview.errors.allErrors.collect {
                            g.message(error: it)
                        }]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the paper review could not be found
            if (!responseMap) {
                responseMap = [success: false, message:
                        g.message(code: 'default.not.found.message', args: [g.message(code: 'paper.review.label')])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Change the paper state of the given paper
     * (AJAX call)
     */
    def changeState() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a paper id and state id, try to find the records for these ids
            if (params.paper_id?.isLong() && params.state_id?.isLong()) {
                Paper paper = Paper.findById(params.paper_id)
                PaperState state = PaperState.findById(params.state_id)

                // Change the paper state if they both exist
                if (paper && state) {
                    paper.state = state

                    // Save the paper
                    if (paper.save(flush: true)) {
                        // Everything is fine
                        responseMap = [success: true, paper: "${g.message(code: 'paper.label')}: ${paper.toString()} (${state.toString()})"]
                    }
                    else {
                        responseMap = [success: false, message: paper.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the paper or state could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message',
                        args: ["${g.message(code: 'paper.label')}, ${g.message(code: 'paper.state.label')}"])]
            }

            render responseMap as JSON
        }
    }
}