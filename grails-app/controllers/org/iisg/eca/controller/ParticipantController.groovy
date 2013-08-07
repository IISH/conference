package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Paper
import org.iisg.eca.domain.Extra
import org.iisg.eca.domain.Network
import org.iisg.eca.domain.FeeState
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.PaperState
import org.iisg.eca.domain.Volunteering
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.ParticipantDate
import org.iisg.eca.domain.ParticipantState
import org.iisg.eca.domain.ParticipantVolunteering

import grails.converters.JSON
import grails.validation.ValidationException

import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * Controller responsible for handling requests on participants
 */
class ParticipantController {
    /**
     * Information about the current page
     */
    def pageInformation

    /**
     * Service taking care of participant information
     */
    def participantService
    
    /**
     * Service taking care of participant in session related information
     */
    def participantSessionService
    
    /**
     * Service taking care of exporting the participants paper
     */
    def exportService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all invitations requests made by participants
     */
    def invitations() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all lower fee requests made by participants
     */
    def lowerFee() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all participants that showed interest for one of the extras
     */
    def extras() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows a list of all participants who signed up for the current event date
     * and offers users to filter the results
     */
    def list() {
        // When the page is first requested, show no results yet: start with an empty map
        Map participants = [:]

        // If the user clicked on search, let the participant service get all participants from the database
        if (params['filter-type']) {
            participants = participantService.getParticipants(params)
        }

        // Return a map with participants, the characters (alphabet) and
        // participant states including the number of participants for each state
        render(view: "list", model: [   participants:   participants,
                                        alphabet:       participants.keySet(),
                                        states:         participantService.getParticipantCounts()])
    }
    
    /**
     * Add a new participant to the current event date
     */
    def add() {
        if (request.post) {
            // We need an email address, check for the email
            if (!params.email) {
                flash.error = true
                flash.message = g.message(code: 'default.no.email.message')                
                return
            }
            
            ParticipantDate participant = null
            User user = User.findByEmail(params.email)
            
            if (!user) {
                user = new User(lastName: "n/a", firstName: "n/a", email: params.email)                
            }
            else {            
                // Does the participant exist in the database already
                participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

                if (participant) {
                    flash.message = g.message(code: 'default.exists.message', args: [user.toString(), g.message(code: 'participantDate.label')]) 
                    redirect(uri: eca.createLink(action: 'show', id: user.id, noBase: true))
                    return
                }

                // Look for the participant, maybe he is deleted
                ParticipantDate.withoutHibernateFilters {
                    participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                }

                // If we found the filtered participant, undo the deletion
                if (participant) {
                    participant.deleted = false
                }
            } 
            
            // This user is not a participant yet, but the user indicated he/she wants to make him/her one
            if (!participant) {                
                participant = new ParticipantDate(user: user, state: ParticipantState.get(0), feeState: FeeState.get(0))
            }
            
            if (user.save(flush: true) && participant.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'participantDate.label'), participant.toString()])
                redirect(uri: eca.createLink(action: 'show', id: user.id, noBase: true))
                return
            }
            else {
                return [participant: participant]
            }
        }    
    }
    
    /**
     * Shows all participant information and allows for editing
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        // Try to look up this user as a participant for the current event date
        ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)

        // Already collect participant ids in the case of an error
        List participantIds = participantService.getParticipantsWithFilters(params).collect { it[0] }
        List sessions = participantSessionService.getSessionsForParticipant(participant)

        // The 'save' button was clicked, save all data
        if (request.post) {
            try {
                // Save all user information
                bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                        'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo']], "User")
                user.save(failOnError: true)
                
                if (!participant && params['add-to-date']?.equals('add')) {
                    // Try to find out if this participant has been deleted before or is filtered for some other reason
                    ParticipantDate.withoutHibernateFilters {
                        participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
                    }

                    // If we found the filtered participant, undo the deletion
                    if (participant) {
                        participant.deleted = false
                    }
                    else {
                        // This user is not a participant yet, but the user indicated he/she wants to make him/her one
                        participant = new ParticipantDate(user: user, state: ParticipantState.get(0), feeState: FeeState.get(0))
                    }

                    participant.save(failOnError: true)
                }
                else if (participant) {
                    // He/she is a participant, save all of that information as well
                    bindData(participant, params, [include: ['invitationLetter', 'invitationLetterSent', 'lowerFeeRequested',
                            'lowerFeeAnswered', 'lowerFeeText', 'student', 'studentConfirmed', 'award',
                            'state', 'feeState']], "ParticipantDate")
                    participant.save(failOnError: true)
                    
                    // Remove all extras the participant is interested in and save all new information
                    participant.extras.clear()
                    params."ParticipantDate.extras".each { extraId ->
                        participant.addToExtras(Extra.get(extraId))
                    }
                    participant.save(failOnError: true)    
                    
                    // Remove all date/times the user is not present and save all new information
                    // However, we are only interested in the dates/times the participant is NOT present
                    user.dateTimesNotPresent.clear()
                    user.save(failOnError: true)
                    List<SessionDateTime> sessionDateTimes = SessionDateTime.list()
                    params.present.each { dateTimeId ->
                        sessionDateTimes.remove(sessionDateTimes.find { dateTimeId.isLong() && (it.id == dateTimeId.toLong()) })
                    }
                    user.dateTimesNotPresent.addAll(sessionDateTimes)
                    user.save(failOnError: true)
                    
                    // Remove all volunteering offers from the participant and save all new information
                    int i = 0
                    participant.participantVolunteering.clear()
                    participant.save(failOnError: true, flush: true)
                    while (params["ParticipantVolunteering_${i}"]) {
                        ParticipantVolunteering pv = new ParticipantVolunteering()
                        bindData(pv, params, [include: ['volunteering', 'network']], "ParticipantVolunteering_${i}")
                        i++
                        
                        if (!participant.participantVolunteering.find { it.equalsWithoutParticipant(pv) }) {
                            participant.addToParticipantVolunteering(pv)
                        }
                    }
                    participant.save(failOnError: true)

                    // Check which papers have to be deleted, try to soft delete them
                    String[] ids = params["to-be-deleted"].split(';')
                    ids.each { idToDelete ->
                        if (idToDelete.isLong()) {
                            Paper paperToDelete = Paper.findById(idToDelete.toLong())
                            paperToDelete?.softDelete()
                            paperToDelete?.save(failOnError: true)
                        }
                    }

                    // Now try to save the paper information, one paper at the time
                    i = 0
                    while (params["Paper_${i}"]) {
                        Paper paper = null

                        // Try to look up the paper in the database
                        if (params["Paper_${i}.id"]?.isLong()) {
                            paper = Paper.findByUserAndId(user, params.long("Paper_${i}.id"))
                        }

                        // Not found, then create a new paper
                        if (!paper) {
                            paper = new Paper(state: PaperState.get(0))
                            user.addToPapers(paper)
                            paper.setDate(pageInformation.date)
                        }
                        
                        // Make sure that the paper can be saved first
                        bindData(paper, params, [include: ['title', 'abstr']], "Paper_${i}")
                        paper.save(failOnError: true, flush: true)
                        
                        // Also save all other paper information
                        bindData(paper, params, [include: ['coAuthors', 'state', 'comment',
                                'sessionProposal', 'proposalDescription', 'networkProposal',
                                'equipmentComment']], "Paper_${i}")
                        paper.save(failOnError: true)
                        
                        // If a paper file is uploaded, save all file information
                        CommonsMultipartFile file = (CommonsMultipartFile) params["Paper_${i}.file"]
                        if (file?.size > 0) {
                            paper.fileSize = file.size
                            paper.contentType = file.contentType
                            paper.fileName = file.originalFilename
                            paper.file = file.bytes
                        }
                        paper.save(failOnError: true)

                        // Remove all equipment and save all new equipment information for this paper
                        paper.equipment?.clear()
                        params["Paper_${i}.equipment"].each { equipmentId ->
                            if (equipmentId.isLong()) {
                                paper.addToEquipment(Equipment.get(equipmentId.toLong()))
                            }
                        }

                        // Save the paper
                        paper.save(failOnError: true)
                        i++
                    }

                    // Save the user and the participant
                    user.save(failOnError: true)
                    participant.save(failOnError: true)
                }

                // We arrived here, so everything should be fine
                // Go back to the previous back with an update message
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'participantDate.label'), user.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(action: 'list', noBase: true))
                    return
                }
            }
            catch (ValidationException ve) {
                // Validation failed, just send back the page and show all errors
                render(view: "form", model: [   user: user,
                                                participant: participant,
                                                papers: Paper.findAllByUser(user),
                                                volunteering: Volunteering.list(),
                                                networks: Network.list(),
                                                paperStates: PaperState.list(),
                                                equipmentList: Equipment.list(),
                                                participantIds: participantIds,
                                                sessions: sessions
                ])
            }
        }

        // Show the participant data
        render(view: "form", model: [   user: user,
                                        participant: participant,
                                        papers: Paper.findAllByUser(user),
                                        volunteering: Volunteering.list(),
                                        networks: Network.list(),
                                        paperStates: PaperState.list(),
                                        equipmentList: Equipment.list(),
                                        participantIds: participantIds,
                                        sessions: sessions
        ])
    }
    
    /**
     * Shows all participants and allows to change their paper state
     */
    def paperstate() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Shows all participants with unknown gender, allowing the user to change the gender of these participants
     */
    def gender() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
    
    /**
     * Removes the user as a participant from the current event date
     */
    def delete() {
        // Of course we need an id of the user
        if (params.id) {
            User user = User.get(params.id)
            ParticipantDate participant = ParticipantDate.findByUserAndDate(user, pageInformation.date)
            participant?.softDelete()

            // Try to remove the user as a participant, send back a success or failure message
            if (participant?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'participantDate.label'), participant.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'participantDate.label'), participant.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        // The previous page probably does not exist anymore, so send back to the participant listing page
        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
    
    /**
     * Returns a list will all participants that have signed up for the current event date
     * (AJAX call)
     */
    def participants() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            // Let the participantService come up with all the participants for the current event date
            List<User> participants = participantService.allParticipants

            // Return all participants and their paper, which are still not added to a session
            render participants.collect { user ->
                [label: "${user.lastName}, ${user.firstName}", value: user.id]
            } as JSON
        }
    }

    /**
     * Tries to download the uploaded paper
     */
    def downloadPaper() {
        Paper paper = Paper.get(params.id)

        // Let the export service deal with returning the uploaded paper
        if (paper) {
            exportService.getPaper(paper, response)
        }
    }

    /**
     * Tries to remove the uploaded paper
     * (AJAX call)
     */
    def removePaper() {
        if (request.xhr) {
            Paper paper = Paper.get(params.long('paper-id'))

            // We found the paper, now just set everything to null
            if (paper) {
                paper.file = null
                paper.fileName = null
                paper.fileSize = null
                paper.contentType = null
                paper.save(flush: true)
            }

            // That's it
            Map returnMap = [success: true]
            render returnMap as JSON
        }
    }
    
    /**
     * Change the paper state of the given paper
     * (AJAX call)
     */
    def changePaperState() {
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
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'paper.label')}, ${g.message(code: 'paper.state.label')}"])]
            }

            render responseMap as JSON
        }
    }

    /**
     * Change the gender of the given user
     * (AJAX call)
     */
    def changeGender() {
        // If this is an AJAX call, continue
        if (request.xhr) {
            Map responseMap = null

            // If we have a user, find the user
            if (params.user_id?.isLong()) {
                User user = User.findById(params.user_id)

                // Change the gender of the user
                if (user) {
                    if (!params.gender || "null".equals(params.gender)) {
                        user.gender = null
                    }
                    else {
                        user.gender = params.gender
                    }

                    // Save the paper
                    if (user.save(flush: true)) {
                        // Everything is fine
                        responseMap = [success: true]
                    }
                    else {
                        responseMap = [success: false, message: user.errors.allErrors.collect { g.message(error: it) }]
                    }
                }
            }

            // If there is no responseMap defined yet, it can only mean the paper or state could not be found
            if (!responseMap) {
                responseMap = [success: false, message: g.message(code: 'default.not.found.message', args: ["${g.message(code: 'user.label')}"])]
            }

            render responseMap as JSON
        }
    }
}
