package org.iisg.eca.controller

import org.iisg.eca.domain.Room
import org.iisg.eca.domain.RoomSessionDateTimeEquipment
import org.iisg.eca.domain.SessionDateTime
import org.iisg.eca.domain.Equipment

class RoomController {
    def index() {
        redirect(action: 'list', params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        Room room = new Room()
        if (request.get) {
            render(view: "form", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
        }
        else if (request.post) {
            bindData(room, params, [include: ['roomName', 'roomNumber', 'noOfSeats', 'comment']])

            Equipment.list().each { equip ->
                SessionDateTime.list().each { timeSlot ->
                    if (params.timeslot.contains("${equip.id}_${timeSlot.id}")) {
                        room.addToRoomSessionDateTimeEquipment(new RoomSessionDateTimeEquipment(sessionDateTime: timeSlot, equipment: equip))
                    }
                }
            }

            if (!room.save(flush: true)) {
                render(view: "form", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
                return
            }

            flash.message = message(code: 'default.created.message', args: [message(code: 'room.label'), room.id])
            redirect(uri: eca.createLink(action: "show", id: room.id, noBase: true))
        }
    }

    def edit() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        Room room = Room.findById(params.id)

        if (!room) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }
        
        if (request.get) {
            render(view: "form", model: [room: Room.get(params.id), equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
        }
        else if (request.post) {
            bindData(room, params, [include: ['roomName', 'roomNumber', 'noOfSeats', 'comment']])

            room.roomSessionDateTimeEquipment.clear()
            Equipment.list().each { equip ->
                SessionDateTime.list().each { timeSlot ->
                    if (params.timeslot.contains("${equip.id}_${timeSlot.id}")) {
                        room.addToRoomSessionDateTimeEquipment(new RoomSessionDateTimeEquipment(room: room, sessionDateTime: timeSlot, equipment: equip))
                    }
                }
            }

            if (!room.save(flush: true)) {
                render(view: "form", model: [room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
                return
            }

            flash.message = message(code: 'default.updated.message', args: [message(code: 'room.label'), room.id])
            redirect(uri: eca.createLink(action: "show", id: room.id, noBase: true))
        }
    }
}
