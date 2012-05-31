package org.iisg.eca.controller

import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Session
import grails.converters.JSON

class NetworkController {
    def participantSessionService

    def index() {
        redirect(action: 'list', params: params)
    }

    def show() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Network network = Network.findById(params.id)
        if (!network) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'network.label'), network.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render(view: "show", model: [network: network, sessions: participantSessionService.getParticipantsForNetwork(network)])
        }
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def edit() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Network network = Network.findById(params.id)
        if (!network) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'network.label'), network.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render(view: "edit", model: [network: network, sessions: participantSessionService.getParticipantsForNetwork(network)])
        }
    }

    /**
     * Returns all unscheduled participants for the current network
     * (AJAX call)
     */
    def participantsNotScheduled() {
        if (request.xhr && params.network_id) {
            Network network = Network.findById(params.long('network_id'))
            Map returnMap = [success: true, participants: participantSessionService.getParticipantsNotInNetwork(network).collect { [
                url:            eca.createLink(controller: 'participant', action: 'show', id: it.key.id),
                participant:    it.key.toString(),
                papers:         it.value.collect() { paper -> [
                                    name:   paper.toString(),
                                    state:  paper.state.toString()
                                ] }
            ] }]
            render returnMap as JSON
        }
    }
}
