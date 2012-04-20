import org.iisg.eca.domain.Page
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

class EcaFilters {
    def pageInformation

    def filters = {
        /**
         *  Every page (except login/logout) should be in the database, so lookup the page information from the database
         *  If it is there, allow access and cache the page information for this request
         */
        page(controller: '*', action: '*', controllerExclude: 'login|logout') {
            before = {
                Page page = Page.findByControllerAndAction(params.controller, params.action)

                if (page) {
                    pageInformation.page = page
                    return true
                }

                return false
            }
            after = { Map model ->
                pageInformation.removePage()
            }
        }

        /**
         *  Every page (except login/logout/event related actions) should be in the database belongs to a certain event
         *  Lookup the event and event date for this request in the database
         *  If it is there, allow access and cache the event date information for this request
         */
        eventDate(controller: '*', action: '*', controllerExclude: 'login|logout|event') {
            before = {
                // Issue with dynamic pages, which uses the dynamic page controller
                if (params.controller == 'event') {
                    return true
                }

                String eventCode = params.event?.replaceAll('-', '\\s')
                String dateCode = params.date?.replaceAll('-', '\\s')

                Event event = Event.findByCode(eventCode)
                EventDate date = EventDate.findByEventAndYearCode(event, dateCode)

                if (date) {
                    pageInformation.date = date
                    return true
                }

                return false
            }
            after = { Map model ->
                pageInformation.removeDate()
            }
        }
    }
}
