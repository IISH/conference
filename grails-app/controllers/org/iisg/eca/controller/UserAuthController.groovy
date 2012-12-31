package org.iisg.eca.controller

import org.iisg.eca.domain.User
import org.iisg.eca.domain.Role
import org.iisg.eca.domain.Page
import org.iisg.eca.domain.Group

import org.iisg.eca.domain.UserRole
import org.iisg.eca.domain.UserPage

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
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'user.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Show all user authentication related information
        [user: user, roles: user.roles, groups: Group.getAllGroupsOfUser(user), pages: UserPage.findAllByUser(user)]
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
        // And pages which the current user can access already
        Set<Role> roles = Role.getPossibleRoles()
        Set<Page> pages = Page.getAllPagesWithAccess()
        Set<Group> groups = Group.getAllGroupsWithAccess()
        
        // Transient problems with relations
        Set<Role> assignedRoles = []
        Set<Group> assignedGroups = []
        Set<UserPage> assignedPages = []
        
        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all user related data
            bindData(user, params, [include: ["firstName", "lastName", "email", "city", "country", "enabeled"]], "User")
            user.save()
            
            // Check for all the possible roles, whether they have to be added to the user
            roles.each { role ->
                if (params["User.roles"]?.contains(role.id.toString())) {
                    UserRole.create(user, role)
                }
            }
            
            // Check for all the possible groups, whether they have to be added to the user
            groups.each { group -> 
                if (params["User.groups"]?.contains(group.id.toString())) {
                    user.addToGroups(group)
                }
            }
            
            // Create the users individual rules list
            int i = 0
            while (params["Page_${i}"]) {
                UserPage userPage = new UserPage()
                bindData(userPage, params, [include: ['page', 'denied']], "Page_${i}")                
                if (pages.find { it.id == params.long("Page_${i}.page.id") } && 
                    !user.userPages.find { it.page.id == userPage.page.id }) {
                   user.addToUserPages(userPage)
                }
                i++
            }
            
            // Save the user and redirect to the previous page if everything is ok
            if (user.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'user.label'), user.toString()])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }

            // Now get the roles, user is saved
            assignedRoles = user.roles
            assignedGroups = Group.getAllGroupsOfUser(user)
            assignedPages = UserPage.findAllByUser(user)
        }

        // Show all user authentication related information
        render(view: "form", model: [user: user, roles: assignedRoles, groups: assignedGroups, 
                pages: assignedPages, allRoles: roles, allGroups: groups, allPages: pages])
    }
    
    /**
     *  Allows the user to make changes to a users authentication rights
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        User user = User.get(params.id)

        // We also need a user to be able to show something
        if (!user) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'user.label')])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // A user can only be assigned a role which is equal or lower in the hierarchy
        // And pages which the current user can access already
        Set<Role> roles = Role.getPossibleRoles()
        Set<Page> pages = Page.getAllPagesWithAccess()
        Set<Group> groups = Group.getAllGroupsWithAccess()

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all user related data
            bindData(user, params, [include: ["firstName", "lastName", "email", "city", "country", "enabeled"]], "User")
            
            // Check for all the possible roles, whether they have to be removed or added to the user
            UserRole.removeAll(user)
            roles.each { role ->                
                if (params["User.roles"]?.contains(role.id.toString())) {
                    UserRole.create(user, role)
                }
            }
            
            // Check for all the possible groups, whether they have to be removed or added to the user
            groups.each { group -> 
                if (user.groups.contains(group) && !params["User.groups"]?.contains(group.id.toString())) {
                    user.removeFromGroups(group)
                }
                if (!user.groups.contains(group) && params["User.groups"]?.contains(group.id.toString())) {
                    user.addToGroups(group)
                }
            }
            
            // Remove all pages from the users individual rules list and save all new information
            int i = 0            
            user.userPages.clear()
            user.save(flush: true)
            while (params["Page_${i}"]) {
                UserPage userPage = new UserPage()
                bindData(userPage, params, [include: ['page', 'denied']], "Page_${i}")                
                if (pages.find { it.id == params.long("Page_${i}.page.id") } && 
                    !user.userPages.find { it.page.id == userPage.page.id }) {
                   user.addToUserPages(userPage)
                }
                i++
            }

            // Save the user and redirect to the previous page if everything is ok
            if (user.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'user.label'), user.toString()])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }
        }

        // Show all user authentication related information
        render(view: "form", model: [user: user, roles: user.roles, groups: Group.getAllGroupsOfUser(user), 
                pages: UserPage.findAllByUser(user), allRoles: roles, allGroups: groups, allPages: pages])
    }
}
