package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Role
import org.iisg.eca.domain.Page

import org.iisg.eca.domain.UserRole
import org.iisg.eca.domain.UserPage

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Controller responsible for handling requests on user authentication
 */
class UserAuthController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    /**
     * Shows all authentication data of a particular user
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'user.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Show all user authentication related information
        [user: user, roles: user.roles, groups: user.groups, pages: user.userPages]
    }

    /**
     * Shows a list of all the users
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new user
     */
    def create() {
        User user = new User(password: User.createSecureRandomString())

        // A user can only be assigned a role which is equal or lower in the hierarchy
        List<Role> allRoles = []
        Role.list().each { (!SpringSecurityUtils.ifAnyGranted(it.role)) ?: allRoles.add(it) }

        // Assigned roles, transient problem with user.roles
        List<Role> assignedRoles = []

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all user related data
            bindData(user, params, [include: ["firstName", "lastName", "email", "city", "country", "enabeled", "groups"]], "User")
            user.save()

            // Check for all the possible roles, whether they have to be removed or added to the user
            allRoles.each { role ->
                if (params["User.roles"].contains(role.id.toString())) {
                    UserRole userRole = new UserRole(role: role)
                    user.addToUserRoles(userRole)
                }
            }

            // Create the users individual rules list
            int i = 0
            while (params["Page_${i}"]) {
                UserPage userPage = new UserPage()
                bindData(userPage, params, [include: ['page', 'denied']], "Page_${i}")
                if (!user.userPages.find { it.page.id == userPage.page.id }) {
                   user.addToUserPages(userPage)
                }
                i++
            }

            // Save the user and redirect to the previous page if everything is ok
            if (user.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'user.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }

            // Now get the roles, user is saved
            assignedRoles = user.roles
        }

        // Show all user authentication related information
        render(view: "form", model: [user: user, roles: assignedRoles, groups: user.groups, pages: user.userPages, allRoles: allRoles, allPages: Page.list()])
    }

    /**
     *  Allows the user to make changes to a users authentication rights
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'user.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // A user can only be assigned a role which is equal or lower in the hierarchy
        List<Role> allRoles = []
        Role.list().each { (!SpringSecurityUtils.ifAnyGranted(it.role)) ?: allRoles.add(it) }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all user related data
            bindData(user, params, [include: ["firstName", "lastName", "email", "city", "country", "enabeled", "groups"]], "User")

            // Check for all the possible roles, whether they have to be removed or added to the user
            allRoles.each { role ->
                if (user.roles.contains(role) && !params["User.roles"].contains(role.id.toString())) {
                    UserRole.remove(user, role)
                }
                if (!user.roles.contains(role) && params["User.roles"].contains(role.id.toString())) {
                    UserRole userRole = new UserRole(role: role)
                    user.addToUserRoles(userRole)
                }
            }

            // Remove all pages from the users individual rules list and save all new information
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

            // Save the user and redirect to the previous page if everything is ok
            if (user.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'user.label')])
                redirect(uri: eca.createLink(action: 'list', noBase: true))
                return
            }
        }

        // Show all user authentication related information
        render(view: "form", model: [user: user, roles: user.roles, groups: user.groups, pages: user.userPages, allRoles: allRoles, allPages: Page.list()])
    }
}
