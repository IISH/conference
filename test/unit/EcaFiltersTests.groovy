import org.junit.Before

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin

import org.iisg.eca.domain.Page
import org.iisg.eca.domain.Event
import org.iisg.eca.domain.EventDate

import org.iisg.eca.utils.PageInformation
import org.iisg.eca.controller.EventController
import grails.test.mixin.domain.DomainClassUnitTestMixin

/**
 * Tests the filters used
 * TODO: Work around beans problem during testing
 */
@TestFor(EventController)
@TestMixin(DomainClassUnitTestMixin)
@Mock(EcaFilters)
class EcaFiltersTests {
    def pageInformation

    /**
     * Setup an event and event date for testing purposes
     */
    @Before
    void setUp() {
        defineBeans {
            pageInformation(PageInformation)
        }
        PageInformation pageInfoBean = applicationContext.getBean('pageInformation')
        pageInformation = pageInfoBean

        EventDate date = new EventDate([yearCode: "juli 3000", dateAsText: "3000 date", description: "3000 date"])
        Event event = new Event([code: "test", shortName: "TEST", longName: "Test event"])
        Page page = new Page([titleDefault: "test", controller: "fee", action: "list"])

        mockDomain(EventDate, [date])
        mockDomain(Event, [event])
        mockDomain(Page, [page])

        event.addToDates(date)
        event.save()
    }

    /**
     * Tests the filter named page
     */
    void testPageFilter() {
        params.controller = "fee"
        params.action = "create"
        withFilters(controller: "fee", action: "create") {
            controller.create()
        }
        assert pageInformation.page?.titleDefault == null

        params.controller = "fee"
        params.action = "list"
        withFilters(controller: "fee", action: "list") {
            controller.list()
        }
        assert pageInformation.page?.titleDefault == "test"
    }

    /**
     * Tests the filter named eventDate
     */
    void testEventDateFilter() {
        params.controller = "event"
        params.action = "index"
        withFilters(controller:  "event", action: "index") {
            controller.index()
        }
        assert pageInformation.date?.yearCode == null

        params.controller = "event"
        params.action = "index"
        params.event = "test"
        params.date = "juli-3000"
        withFilters(controller:  "event", action: "index") {
            controller.index()
        }
        assert pageInformation.date?.yearCode == "juli 3000"
    }
}
