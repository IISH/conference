package org.iisg.eca.controller

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.FeeState
import org.iisg.eca.domain.FeeAmount
import org.iisg.eca.domain.Page

class FeeController {
    def pageInformation

    def index() {
        redirect(action: 'create', params: params)
    }

    def list() {
        List<FeeState> feeStates = FeeState.list(sort: 'isDefaultFee', order: 'desc')
        Map<FeeState, List> feeAmounts = [:]

        feeStates.each { feeState ->
            List<FeeAmount> amounts = FeeAmount.withCriteria {
                eq('feeState.id', feeState.id)
                order('endDate',        'asc')
                order('numDaysStart',   'asc')
                order('numDaysEnd',     'asc')
            }
            feeAmounts.put(feeState, amounts)
        }

        render(view: "list", model: [feeStates: feeStates, feeAmounts: feeAmounts])
    }

    def create() {
        int nrOfDays = Day.countByDayNumberGreaterThan(0)
        FeeState feeState = new FeeState()
        
        if (request.get) {
            render(view: "form", model: [feeState: feeState, days: nrOfDays])
        }
        else if (request.post) {
            bindData(feeState, params, [include: ['name', 'isDefaultFee']], 'FeeState')

            int i = 0
            while (params."FeeAmount_${i}") {
                FeeAmount feeAmount = new FeeAmount()
                bindData(feeAmount, params, [include: ['endDate', 'numDaysStart', 'numDaysEnd', 'feeAmount']], "FeeAmount_${i}")
                feeState.addToFeeAmounts(feeAmount)
                i++
            }

            if (!feeState.save(flush: true)) {
                render(view: "form", model: [feeState: feeState, days: nrOfDays])
                return
            }

            flash.message = message(code: 'default.created.message', args: [message(code: 'feestate.label'), feeState.id])
            redirect(uri: eca.createLink(action: "list", id: template.id, noBase: true))
        }
    }

    def edit() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        int nrOfDays = Day.countByDayNumberGreaterThan(0)
        FeeState feeState = FeeState.findById(params.id)

        if (!feeState) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'feestate.label'), feeState.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render(view: "form", model: [feeState: feeState, days: nrOfDays])
        }
        else if (request.post) {
            Set<FeeAmount> toBeDeleted = new HashSet<FeeAmount>(feeState.feeAmounts)
            bindData(feeState, params, [include: ['name', 'isDefaultFee']], 'FeeState')

            int i = 0
            while (params."FeeAmount_${i}") {
                FeeAmount feeAmount = null
                Long id = params.long("FeeAmount_${i}.id")
                if (id) {
                    feeAmount = FeeAmount.findById(id)
                }

                if (!feeAmount) {
                    feeAmount = new FeeAmount()
                }

                bindData(feeAmount, params, [include: ['endDate', 'numDaysStart', 'numDaysEnd', 'feeAmount']], "FeeAmount_${i}")
                feeState.addToFeeAmounts(feeAmount)
                toBeDeleted.remove(feeAmount)
                i++
            }

            toBeDeleted.each { feeAmount ->
                feeAmount.deleted = true
                feeAmount.save()
            }

            if (!feeState.save(flush: true)) {
                render(view: "form", model: [feeState: feeState, days: nrOfDays])
                return
            }

            flash.message = message(code: 'default.updated.message', args: [message(code: 'feestate.label'), feeState.id])
            redirect(uri: eca.createLink(action: "list", id: template.id, noBase: true))
        }
    }

    def delete() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        FeeState feeState = FeeState.findById(params.id)

        if (feeState) {
            feeState.feeAmounts*.deleted = true
            feeState.deleted = true

            if (!feeState.save(flush: true)) {
                flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'feestate.label'), feeState.id])
                redirect(uri: eca.createLink(previous: true, noBase: true))
            }

            flash.message = message(code: 'default.deleted.message', args: [message(code: 'feestate.label'), feeState.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
        }
        else {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'feestate.label'), feeState.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
        }
    }
}
