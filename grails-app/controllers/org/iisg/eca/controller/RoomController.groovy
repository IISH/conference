package org.iisg.eca.controller

import org.iisg.eca.domain.Room
import org.iisg.eca.domain.Equipment
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.RoomSessionDateTimeEquipment
import org.iisg.eca.domain.DynamicPage
import org.iisg.eca.domain.Page
import org.iisg.eca.dynamic.DynamicPageResults
import org.iisg.eca.dynamic.DataContainer

/**
 * Controller responsible for handling requests on rooms
 */
class RoomController {
    def dynamicPageService

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all data on a particular room
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Room room = Room.findById(params.id)

        // We also need a room to be able to show something
        if (!room) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'room.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // If the user came to this page via the dynamic page listing all rooms, ask for the last results
        // Otherwise just return all listed rooms in its default order
        DynamicPage dynamicPage = dynamicPageService.getDynamicPage(Page.findByControllerAndAction(params.controller, 'list'))
        DynamicPageResults results = new DynamicPageResults((DataContainer) dynamicPage.elements.get(0), params)

        // Now we have the results, but we're only interested in the ids
        // (Actually, just the prev/next ids for the navigation section)
        List<Long> roomIds = results.get()*.id

        // Show all room related information
        render(view: "show", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list(), roomIds: roomIds])
    }

    /**
     * Shows a list of all rooms for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new room for the current event date
     */
    def create() {
        Room room = new Room()

        // If it is a 'get' request, just show an empty form, otherwise the user probably pressed the 'save' button
        if (request.get) {
            render(view: "form", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
        }
        else if (request.post) {
            // Save all room related data
            bindData(room, params, [include: ['roomName', 'roomNumber', 'noOfSeats', 'comment', 'enabled']])

            // Loop over all equipment and time slots to find out if we have
            // to add that type of equipment to this room on the given time slot
            Equipment.list().each { equip ->
                SessionDateTime.list().each { timeSlot ->
                    if (params.timeslot?.contains("${equip.id}_${timeSlot.id}")) {
                        room.addToRoomSessionDateTimeEquipment(new RoomSessionDateTimeEquipment(sessionDateTime: timeSlot, equipment: equip))
                    }
                }
            }

            // Save the room
            if (!room.save(flush: true)) {
                render(view: "form", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
                return
            }

            // Everything ok, so redirect to the previous page
            flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'room.label'), room.toString()])
            redirect(uri: eca.createLink(previous: true, noBase: true))
        }
    }

    /**
     * Allows the user to make changes to the room
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Room room = Room.findById(params.id)

        // We also need a room to be able to show something
        if (!room) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'room.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all room related data
            bindData(room, params, [include: ['roomName', 'roomNumber', 'noOfSeats', 'comment', 'enabled']])

            // Remove all equipment availability from the room and save all new information
            room.roomSessionDateTimeEquipment.clear()
            room.save(flush: true)

            // Loop over all equipment and time slots to find out if we have
            // to add that type of equipment to this room on the given time slot
            Equipment.list().each { equip ->
                SessionDateTime.list().each { timeSlot ->
                    if (params.timeslot?.contains("${equip.id}_${timeSlot.id}")) {
                        room.addToRoomSessionDateTimeEquipment(new RoomSessionDateTimeEquipment(room: room, sessionDateTime: timeSlot, equipment: equip))
                    }
                }
            }

            // Save the room and redirect to the previous page if everything is ok
            if (room.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'room.label'), room.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }
        }

        // Show all room related information
        render(view: "form", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
    }

    /**
     * Removes the room from the current event date
     */
    def delete() {
        // Of course we need an id of the room
        if (params.id) {
            Room room = Room.findById(params.id)
            room?.softDelete()

            // Try to remove the room, send back a success or failure message
            if (room?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'room.label'), room.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'room.label'), room.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
