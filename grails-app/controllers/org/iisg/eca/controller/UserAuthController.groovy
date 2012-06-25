package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Role
import org.iisg.eca.domain.Page

import org.iisg.eca.domain.UserRole
import org.iisg.eca.domain.UserPage

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class UserAuthController {
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    def show() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)
        if (!user) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        [user: user, roles: user.roles, groups: user.groups, pages: user.userPages]
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def edit() {
        if (!params.id) {
            flash.message = message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)
        if (!user) {
            flash.message =  message(code: 'default.not.found.message', args: [message(code: 'user.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        List<Role> allRoles = []
        Role.list().each { (!SpringSecurityUtils.ifAnyGranted(it.role)) ?: allRoles.add(it) }

        if (request.post) {
            bindData(user, params, [include: ["firstName", "lastName", "email", "enabeled", "groups"]], "User")

            allRoles.each { role ->
                if (user.roles.contains(role) && !params["User.roles"].contains(role.id.toString())) {
                    UserRole.remove(user, role, true)
                }
                if (!user.roles.contains(role) && params["User.roles"].contains(role.id.toString())) {
                    UserRole userRole = new UserRole(role: role)
                    user.addToUserRoles(userRole)
                }
            }

            int i = 0
            user.userPages.clear()
            user.save(flush: true)
            while (params["Page_${i}"]) {
                UserPage userPage = new UserPage()
                bindData(userPage, params, [include: ['page', 'denied']], "Page_${i}")
                if (!user.userPages.find { it.page.id == userPage.page.id }) {
                   user.addToUserPages(userPage)
                }
                i++
            }

            if (user.save(flush: true)) {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }
        }

        [user: user, roles: user.roles, groups: user.groups, pages: user.userPages, allRoles: allRoles, allPages: Page.list()]
    }
}
