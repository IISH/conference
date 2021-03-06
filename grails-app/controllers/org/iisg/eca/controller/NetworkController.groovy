package org.iisg.eca.controller

import grails.converters.JSON

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.Session
import org.iisg.eca.domain.SessionState
import org.iisg.eca.domain.NetworkChair

import org.iisg.eca.utils.ParticipantSessionInfo

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
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows all data on a particular network
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Network network = Network.findById(params.id)

        // We also need a network to be able to show something
        if (!network) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'network.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Show all network related information
        render(view: "show", model: [   network:        network, 
                                        sessionStates:  SessionState.list(),
                                        sessions:       participantSessionService.getParticipantsForNetwork(network)])
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
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Network network = Network.findById(params.id)

        // We also need a network to be able to show something
        if (!network) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'network.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        Map<Session, List<ParticipantSessionInfo>> sessions = participantSessionService.getParticipantsForNetwork(network)
        
        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all network related data
            bindData(network, params, [include: ["name", "comment", "longDescription", "url", "email", "showOnline"]], "Network")

            // Remove all chairs from the network and save all new information
            int i = 0
            network.chairs.clear()
            network.save(flush: true)
            while (params["NetworkChair_${i}"]) {
                NetworkChair chair = new NetworkChair()
                bindData(chair, params, [include: ['chair', 'isMainChair']], "NetworkChair_${i}")

                // A chair should only be added to a network once
                if (!network.chairs?.find { it.chair?.id == chair.chair?.id }) {
                    network.addToChairs(chair)
                }
                i++
            }

            // Save the network and redirect to the previous page if everything is ok
            if (network.save()) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'network.label'), network.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }
        }
        
        // All sessions, but ordered by code and by name
        List<Session> allSessions = Session.findAll("FROM Session AS s ORDER BY code, name")

        // Show all network related information
        render(view: "edit", model: [   network:        network, 
                                        sessions:       sessions,
                                        allSessions:    allSessions])
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
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'network.label'), network.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'network.label'), network.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
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
                                        name:           "${g.message(code: 'paper.label')}: ${paper.toString()}",
                                        state:          paper.state.toString(),
                                        paperId:        paper.id,
                                        paperStateId:   paper.state.id
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
        Map responseMap = [success: false]

        // If this is an AJAX call and includes a network id and a session id to add or new session info, continue
        if (request.xhr && params.network_id?.isLong() && (params.session_id?.isLong() || params.session_name)) {
            Session session = null

            // If there is a session id, find that session, otherwise create a new session
            if (params.session_id?.isLong()) {
                session = Session.findById(params.long('session_id'))
            }
            else {
                session = new Session(code: params.session_code, name: params.session_name, state: SessionState.get(SessionState.NEW_SESSION))
                session.save()
            }

            Network network = Network.findById(params.long('network_id'))

            // If the network and session exists, add the session to the network
            if (network && session) {
                network.addToSessions(session)

                // Save the network
                if (network.save(flush: true)) {
                    // Everything is fine, return all updated data to the client
                    def participants = participantSessionService.getParticipantsForNetwork(network)
                    responseMap = [success: true, sessions: participants.collect { curSession -> 
                            [   id:             curSession.key.id,
                                name:           curSession.key.toString(),
                                participants:   participantSessionService.getParticipantSessionInfoMap(curSession.value)
                            ] }
                    ]
                }
                else {
                    responseMap = [success: false, message: network.errors.allErrors.collect { g.message(error: it) }]
                }
            }
            else {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'session.label')}, ${g.message(code: 'network.label')}"])]
            }


        }
        else if (params.session_code?.isEmpty() || params.session_name?.isEmpty()) {
            responseMap = [success: false, message: g.message(code: 'default.blank.message', args: [g.message(code: 'session.name.label')])]
        }

        render responseMap as JSON
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
                    responseMap = [success: true, sessions: participants.collect { curSession -> 
                            [   id:             curSession.key.id,
                                name:           curSession.key.toString(),
                                participants:   participantSessionService.getParticipantSessionInfoMap(curSession.value)
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
