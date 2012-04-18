package org.iisg.eca

abstract class EventDomain {
    def pageInformation

    static hibernateFilters = {
        if (event) {
            eventFilter(condition: "event.id = ${pageInformation.date.event.id}", enabled: true)
        }
        else if (date) {
            eventFilter(condition: "date.id = ${pageInformation.date.id}", enabled: true)
        }
        else if (day) {
            eventFilter(condition: "day.id IN ${pageInformation.date.days*.id}", enabled: true)
        }
    }
}
