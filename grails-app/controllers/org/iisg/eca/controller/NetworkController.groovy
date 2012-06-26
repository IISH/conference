package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.NetworkChair

/**
 * Controller responsible for handling requests on networks
 */
class NetworkController {
    /**
     * Service taking care of participants and how they are added to a session
     */
    def participantSessionService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all data on a particular network
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Network network = Network.findById(params.id)

        // We also need a network to be able to show something
        if (!network) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'network.label'), network.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Show all network related information
        render(view: "show", model: [network: network, sessions: participantSessionService.getParticipantsForNetwork(network)])
    }

    /**
     * Shows a list of all networks for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new network for the current event date
     */
    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the network
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Network network = Network.findById(params.id)

        // We also need a network to be able to show something
        if (!network) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'network.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all network related data
            bindData(network, params, [include: ["name", "comment", "url", "showOnline", "showInternal", "enabeled"]], "Network")

            // Remove all chairs from the network and save all new information
            int i = 0
            network.chairs.clear()
            network.save(flush: true)
            while (params["NetworkChair_${i}"]) {
                NetworkChair chair = new NetworkChair()
                bindData(chair, params, [include: ['chair', 'isMainChair']], "NetworkChair_${i}")

                // A chair should only be added to a network once
                if (!network.chairs.find { it.chair.id == chair.chair.id }) {
                    network.addToChairs(chair)
                }
                i++
            }

            // Save the network and redirect to the previous page if everything is ok
            if (network.save()) {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'network.label')])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }
        }

        // Show all network related information
        render(view: "edit", model: [network: network, sessions: participantSessionService.getParticipantsForNetwork(network)])
    }

    /**
     * Removes the network from the current event date
     */
    def delete() {
        // Of course we need an id of the network
        if (params.id) {
            Network network = Network.findById(params.id)
            network?.softDelete()

            // Try to remove the network, send back a success or failure message
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
        // If this is an AJAX call and includes a network id, continue
        if (request.xhr && params.network_id?.isLong()) {
            Map responseMap = null
            Network network = Network.findById(params.long('network_id'))

            // If the network is found, return a map of all unscheduled participants and their unscheduled papers
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
        // If this is an AJAX call and includes a network id and a session id to add or new session info, continue
        if (request.xhr && params.network_id?.isLong() && (params.session_id?.isLong() || (params.session_code && params.session_name))) {
            Map responseMap = null
            Session session = null

            // If there is a session id, find that session, otherwise create a new session
            if (params.session_id?.isLong()) {
                session = Session.findById(params.long('session_id'))
            }
            else {
                session = new Session(code: params.session_code, name: params.session_name)
            }

            Network network = Network.findById(params.long('network_id'))

            // If the network and session exists, add the session to the network
            if (network && session) {
                network.addToSessions(session)

                // Save the network
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
        // If this is an AJAX call and includes a network id and a session id to remove, continue
        if (request.xhr && params.network_id?.isLong() && params.session_id?.isLong()) {
            Map responseMap = null
            Network network = Network.findById(params.long('network_id'))
            Session session = Session.findById(params.long('session_id'))

            // Found both the network and session, then remove the session from the network
            if (network && session) {
                network.removeFromSessions(session)

                // Save the network
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
