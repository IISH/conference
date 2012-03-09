package org.iisg.eca

import grails.test.mixin.TestMixin

import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

import grails.plugins.springsecurity.SpringSecurityService

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.GrantedAuthorityImpl

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@TestMixin([DomainClassUnitTestMixin, ControllerUnitTestMixin])
class UserControllerTests {
    def setUp() {
        defineBeans {
            springSecurityService(SpringSecurityService)
        }
        mockDomain(User)
        def controller = testFor(UserController)

        Collection<GrantedAuthority> auths = [new GrantedAuthorityImpl('user')] as Collection

        def secondParams = params.clone()
        populateValidParams(secondParams)
        def user = new User(secondParams)

        def userDetails = new MyUserDetails(user.email, user.encryptedPassword, user.enabled, !user.deleted, auths,
                1, user.fullName, user.language, user.salt)
        def authToken = new UsernamePasswordAuthenticationToken(userDetails, "", auths)
        SecurityContextHolder.context.authentication = authToken

        request.method = 'POST'
        response.reset()
        controller
    }

    def populateValidParams(params) {
        params['email'] = 'email@email.com'
        params['fullName'] = 'Firstname Lastname'
        params['institute'] = 'International Institute for Social History'
        params['country'] = 'nl'
        params['language'] = 'nl'
        params['encryptedPassword'] = 'password'
        params['encryptedPasswordAgain'] = 'password'
        params['salt'] = 'abcd1234'
    }

    void testIndex() {
        def controller = setUp()
        controller.index()
        assert "/user/show" == response.redirectedUrl
    }

    void testShow() {
        def controller = setUp()

        populateValidParams(params)
        def user = new User(params)

        assert user.save() != null

        params.id = user.id

        def model = controller.show()

        assert model.userInstance == user
    }

    void testEdit() {
        def controller = setUp()

        populateValidParams(params)
        def user = new User(params)

        assert user.save() != null

        params.id = user.id

        def model = controller.edit()

        assert model.userInstance == user
    }

    void testUpdate() {
        def controller = setUp()

        populateValidParams(params)
        User user = new User(params)

        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/user/show'

        response.reset()

        assert user.save() != null

        // test invalid parameters in update
        params.id = user.id
        params.email = 'not-an-email-address'

        controller.update()

        assert view == "/user/edit"
        assert model.userInstance != null
    }
}
