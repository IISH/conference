package org.iisg.eca.controller

import org.iisg.eca.domain.ParticipantDate

class ParticipantController {
    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Service taking care of participant information
     */
    def participantService

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        def participants = participantService.getParticipants(params)
        render(view: "list", model: [   page:           pageInformation.page,
                                        alfabet:        participants.keySet(),
                                        participants:   participants,
                                        states:         participantService.getParticipantCounts(params),
                                        dates:          participantService.datesList])
    }

    def show() {
        render(view: "form", model: [page: pageInformation.page, participant: ParticipantDate.get(params.id)])
    }
}
