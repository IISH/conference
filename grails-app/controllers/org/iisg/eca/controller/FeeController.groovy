package org.iisg.eca.controller

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.FeeState
import org.iisg.eca.domain.FeeAmount

/**
 * Controller responsible for handling requests on fees
 */
class FeeController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows a list of all fees for the current event date
     */
    def list() {
        List<FeeState> feeStates = FeeState.list()
        Map<FeeState, List> feeAmounts = [:]

        // Look up all fee amounts per fee state and sort them
        feeStates.each { feeState ->
            List<FeeAmount> amounts = FeeAmount.withCriteria {
                eq('feeState.id', feeState.id)
                order('endDate',        'asc')
                order('numDaysStart',   'asc')
                order('numDaysEnd',     'asc')
            }
            feeAmounts.put(feeState, amounts)
        }

        // Show the fee states and fee amounts
        render(view: "list", model: [feeStates: feeStates, feeAmounts: feeAmounts])
    }

    /**
     * Allows the user to create a new fee for the current event date
     */
    def create() {
        // Find out the number of days possible, for creating fee amounts
        int nrOfDays = Day.countByDayNumberGreaterThan(0)
        FeeState feeState = new FeeState()

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all fee state related data
            bindData(feeState, params, [include: ['name', 'isDefaultFee', 'enabled']], 'feeState')

            // Save all possible fee amounts
            int i = 0
            while (params."feeAmount_${i}") {
                FeeAmount feeAmount = new FeeAmount()
                bindData(feeAmount, params, [include: ['endDate', 'numDaysStart', 'numDaysEnd', 'feeAmount', 'enabled']], "feeAmount_${i}")
                feeState.addToFeeAmounts(feeAmount)
                i++
            }

            // Save the fee state and redirect to the previous page if everything is ok
            if (feeState.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'feeState.label')])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }
        }

        // Show all fee state related information
        render(view: "form", model: [feeState: feeState, days: nrOfDays])
    }

    def edit() {
         // We need an id, check for the id
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Find out the number of days possible, for creating fee amounts
        int nrOfDays = Day.countByDayNumberGreaterThan(0)
        FeeState feeState = FeeState.findById(params.id)

        // We also need a fee state to be able to show something
        if (!feeState) {
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'feeState.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all fee state related data
            bindData(feeState, params, [include: ['name', 'isDefaultFee', 'enabled']], 'FeeState')

            // Get a list of all fee amounts for this fee state
            // If they do not come up, they have to be deleted
            Set<FeeAmount> toBeDeleted = new HashSet<FeeAmount>(feeState.feeAmounts)

            // Save all possible fee amounts
            int i = 0
            while (params."feeAmount_${i}") {
                FeeAmount feeAmount = null

                // Try to find the fee amount in the database
                Long id = params.long("feeAmount_${i}.id")
                if (id) {
                    feeAmount = FeeAmount.findById(id)
                }

                // Otherwise create a new one
                if (!feeAmount) {
                    feeAmount = new FeeAmount()
                }

                // Save the fee amount, add it to the fee state and remove it from the deletion list
                bindData(feeAmount, params, [include: ['endDate', 'numDaysStart', 'numDaysEnd', 'feeAmount', 'enabled']], "feeAmount_${i}")
                feeState.addToFeeAmounts(feeAmount)
                toBeDeleted.remove(feeAmount)
                i++
            }

            // Everything left in the deletion list must be deleted
            toBeDeleted.each { feeAmount ->
                feeAmount.softDelete()
                feeAmount.save()
            }

            // Save the fee state and redirect to the previous page if everything is ok
            if (feeState.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'feeState.label')])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }
        }

        // Show all fee state related information
        render(view: "form", model: [feeState: feeState, days: nrOfDays])
    }

    /**
     * Removes the fee state from the current event date
     */
    def delete() {
        // Of course we need an id of the fee state
        if (params.id) {
            FeeState feeState = FeeState.findById(params.id)
            feeState?.softDelete()

            // Try to remove the fee state, send back a success or failure message
            if (feeState?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'feeState.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'feeState.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
