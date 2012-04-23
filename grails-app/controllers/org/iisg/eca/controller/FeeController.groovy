package org.iisg.eca.controller

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.FeeState

class FeeController {
    def pageInformation

    def index() {
        redirect(action: 'create', params: params)
    }

    def show() {

    }

    def list() {

    }

    def create() {
        int nrOfDays = Day.countByDayNumberGreaterThan(0)
        render(view: "form", model: [page: pageInformation.page, feeState: new FeeState(), days: nrOfDays])
    }

    def edit() {
        FeeState feeState
        if (params.id) {
            feeState = FeeState.findById(params.id)
        }
        else {
            feeState = new FeeState()
        }

        int nrOfDays = Day.countByDayNumberGreaterThan(0)

        render(view: "form", model: [page: pageInformation.page, feeState: feeState, days: nrOfDays])
    }
}
