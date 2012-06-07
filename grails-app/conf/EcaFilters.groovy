import org.iisg.eca.domain.Page
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

import org.iisg.eca.domain.EventDomain
import org.iisg.eca.domain.EventDateDomain
import org.iisg.eca.domain.User

class EcaFilters {
    def pageInformation
    def grailsApplication
    def springSecurityService
    
    def filters = {
        /**
         *  Every page (except login/logout/xhr) should be in the database, so lookup the page information from the database
         *  If it is there, cache the page information for this request
         */
        page(controller: '*', action: '*') {
            before = {
                Page page = Page.findByControllerAndAction(params.controller, params.action)

                if (page) {
                    pageInformation.page = page
                }

                return true
            }
            afterView = { Exception e ->
                pageInformation.removePage()
            }
        }

        /**
         *  Every page (except login/logout/event related actions) should be in the database belongs to a certain event
         *  Lookup the event and event date for this request in the database
         *  If it is there, allow access and cache the event date information for this request
         */
        eventDate(controller: '*', action: '*', controllerExclude: 'login|logout|user|event') {
            before = {
                // Issue with dynamic pages, which uses the dynamic page controller
                if (params.controller == 'event') {
                    return true
                }

                String eventCode = params.event?.replaceAll('-', ' ')
                String dateCode = params.date?.replaceAll('-', ' ')

                Event event = Event.findByCode(eventCode)
                EventDate date = EventDate.findByEventAndYearCode(event, dateCode)

                if (date) {
                    pageInformation.date = date

                    grailsApplication.domainClasses.each { domainClass -> 
                        Class domain = domainClass.clazz
                        if (EventDateDomain.class.isAssignableFrom(domain)) {
                            domain.enableHibernateFilter('dateFilter').setParameter('dateId', date.id)
                        }
                        else if (EventDomain.class.isAssignableFrom(domain)) {
                            domain.enableHibernateFilter('eventFilter').setParameter('eventId', date.event.id)  
                        }
                    }
                    
                    return true
                }

                return false
            }
            afterView = { Exception e ->
                pageInformation.removeDate()
            }
        }
        
         /**
         * Default filter for all requests
         */
        defaultFilter(controller: '*', action: '*') {
            before = {
                // Update locale of currently logged in user if it has changed
                if (params.lang) {
                    User user = User.get(springSecurityService.principal.id)
                    if (user) {
                        user.language = params.lang
                        user.save()
                    }
                }

                return true
            }
            after = { Map model ->
                if (!model) {
                    model = [:]
                }
                
                // Add the pageInformation bean information to all models by default
                model.put('curPage', pageInformation.page)
                model.put('curDate', pageInformation.date)
            }
        }
    }
}
