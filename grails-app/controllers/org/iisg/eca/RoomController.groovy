package org.iisg.eca

class RoomController {
    def pageInformation

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
        if (request.get) {
            render(view: "form", model: [page: pageInformation.page, room: new Room(), equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
        }
        else if (request.post) {
            Room room = new Room()
            bindData(room, params, [include: ['roomName', 'roomNumber', 'noOfSeats', 'comment']])

            Equipment.list().each { equip ->
                SessionDateTime.list().each { timeSlot ->
                    if (params.timeslot.contains("${equip.id}_${timeSlot.id}")) {
                        room.addToRoomSessionDateTimeEquipment(new RoomSessionDateTimeEquipment(sessionDateTime: timeSlot, equipment: equip))
                    }
                }
            }

            if (!room.save(flush: true)) {
                render(view: "form", model: [page: pageInformation.page, room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
                return
            }

            flash.message = message(code: 'default.created.message', args: [message(code: 'room.label'), room.id])
            redirect(action: "show", id: room.id)
        }
    }

    def edit() {
        if (request.get) {
            render(view: "form", model: [page: pageInformation.page, room: Room.get(params.id), equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
        }
        else if (request.post) {
            Room room = Room.get(params.id)
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
                render(view: "form", model: [page: pageInformation.page, room: room, equipment: Equipment.list(), timeSlots: SessionDateTime.list()])
                return
            }

            flash.message = message(code: 'default.updated.message', args: [message(code: 'room.label'), room.id])
            redirect(action: "show", id: room.id)
        }
    }
}
