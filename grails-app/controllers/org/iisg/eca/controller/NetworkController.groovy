package org.iisg.eca.controller

import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Session
import grails.converters.JSON
import org.iisg.eca.domain.NetworkChair

class NetworkController {
    def participantSessionService

    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
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
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'network.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        if (request.get) {
            render(view: "edit", model: [network: network, sessions: participantSessionService.getParticipantsForNetwork(network)])
        }
        else if (request.post) {
            bindData(network, params, [include: ["name", "comment", "url", "showOnline", "showInternal", "enabeled"]], "Network")

            int i = 0
            network.chairs.clear()
            network.save(flush: true)
            while (params["NetworkChair_${i}"]) {
                NetworkChair chair = new NetworkChair()
                bindData(chair, params, [include: ['chair', 'isMainChair']], "NetworkChair_${i}")
                if (!network.chairs.find { it.chair.id == chair.chair.id }) {
                    network.addToChairs(chair)
                }
                i++
            }

            if (network.save()) {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'network.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }

            render(view: "edit", model: [network: network, sessions: participantSessionService.getParticipantsForNetwork(network)])
        }
    }

    def delete() {
        if (params.id) {
            Network network = Network.findById(params.id)
            network?.softDelete()

            if (network?.save(flush: true)) {
                flash.message =  message(code: 'default.deleted.message', args: [message(code: 'network.label')])
            }
            else {
                flash.message =  message(code: 'default.not.deleted.message', args: [message(code: 'network.label')])
            }
        }
        else {
            flash.message =  message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }

    /**
     * Returns all unscheduled participants for the current network
     * (AJAX call)
     */
    def participantsNotScheduled() {
        if (request.xhr && params.network_id) {
            Map responseMap = null
            Network network = Network.findById(params.long('network_id'))

            if (network) {
                responseMap = [success: true, participants: participantSessionService.getParticipantsNotInNetwork(network).collect { [
                    url:            eca.createLink(controller: 'participant', action: 'show', id: it.key.user.id),
                    participant:    it.key.toString(),
                    papers:         it.value.collect() { paper -> [
                                        name:   "${g.message(code: 'paper.label')}: ${paper.toString()}",
                                        state:  paper.state.toString()
                                    ] }
                ] }]
            }
            else {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', attrs: [g.message(code: 'network.label')])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Creates a new session or looks up a session and adds the session to the current network
     * (AJAX call)
     */
    def addSession() {
        if (request.xhr && params.network_id && (params.session_id || (params.session_code && params.session_name))) {
            Map responseMap = null
            Session session = null

            if (params.session_id) {
                session = Session.findById(params.long('session_id'))
            }
            else {
                session = new Session(code: params.session_code, name: params.session_name)
            }

            Network network = Network.findById(params.long('network_id'))

            if (network && session) {
                network.addToSessions(session)

                if (network.save(flush: true)) {
                    // Everything is fine, return all updated data to the client
                    def participants = participantSessionService.getParticipantsForNetwork(network)
                    responseMap = [success: true, sessions: participants.collect { curSession -> [
                        id:             curSession.key.id,
                        name:           curSession.key.toString(),
                        participants:   curSession.value.collect {
                                        [   participant:    it.participant.user.toString(),
                                            state:          it.participant.state.toString(),
                                            types:          it.types.collect { pType ->
                                                                [id: pType.id, type:  pType.toString()]
                                                            },
                                            paper:          (it.paper) ? "${g.message(code: 'paper.label')}: ${it.paper?.toString()}" : ""] }
                        ] }
                    ]
                }
                else {
                    responseMap = [success: false, message: network.errors.allErrors.collect { g.message(error: it) }]
                }
            }
            else {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', attrs: ["${g.message(code: 'session.label')}, ${g.message(code: 'network.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Removes a session from the current network
     * (AJAX call)
     */
    def removeSession() {
        if (request.xhr && params.network_id && params.session_id) {
            Map responseMap = null
            Network network = Network.findById(params.long('network_id'))
            Session session = Session.findById(params.long('session_id'))

            if (network && session) {
                network.removeFromSessions(session)

                if (network.save(flush: true)) {
                    // Everything is fine, return all updated data to the client
                    def participants = participantSessionService.getParticipantsForNetwork(network)
                    responseMap = [success: true, sessions: participants.collect { curSession -> [
                        id:             curSession.key.id,
                        name:           curSession.key.toString(),
                        participants:   curSession.value.collect {
                                        [   participant:    it.participant.user.toString(),
                                            state:          it.participant.state.toString(),
                                            types:          it.types.collect { pType ->
                                                                [id: pType.id, type:  pType.toString()]
                                                            },
                                            paper:          (it.paper) ? "${g.message(code: 'paper.label')}: ${it.paper?.toString()}" : ""] }
                        ] }
                    ]
                }
                else {
                    responseMap = [success: false, message: network.errors.allErrors.collect { g.message(error: it) }]
                }
            }
            else {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', attrs: ["${g.message(code: 'session.label')}, ${g.message(code: 'network.label')}"])]
            }

            render responseMap as JSON
        }
    }
}
