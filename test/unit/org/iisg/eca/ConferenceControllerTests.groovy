package org.iisg.eca

import grails.plugins.springsecurity.SpringSecurityService

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin

@TestMixin([DomainClassUnitTestMixin, ControllerUnitTestMixin])
class ConferenceControllerTests {
    def setUp() {
        defineBeans {
            springSecurityService(SpringSecurityService)
        }
        mockDomain(Conference)
        def controller = testFor(ConferenceController)

        // TODO: Continue with test
    }

    def populateValidParams(params) {
        assert params != null

        params['shortName'] = 'ESSHC'
        params['longName'] = 'European Social Science History Conference'
        params['type'] = 'type'
    }

    void testIndex() {
        def controller = setUp()
        controller.index()

        assert "/conference/list" == response.redirectedUrl
    }

    void testList() {
        def controller = setUp()
        def model = controller.list()

        assert model.conferenceInstanceList.size() == 0
        assert model.dateInstanceList.size() == 0
    }
}
