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
    def sessionFactory
    def pageInformation
    
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
        Map newParams = params + ['sort_0_lastName': 'asc', 'sort_0_firstName': 'asc'];
        forward(controller: 'dynamicPage', action: 'dynamic', params: newParams)
    }

    /**
     * Allows the user to create a new user
     */
    def create() {
        User user = new User(password: User.createSecureRandomString())
        
        // A user can only be assigned a role which is equal or lower in the hierarchy
        // And pages which the current user can access already
        Set<Role> roles = Role.getPossibleRoles()
        List<Page> pages = Page.getAllPagesWithAccess()
        Set<Group> groups = Group.getAllGroupsWithAccess()
        
        // Transient problems with relations
        Set<Role> assignedRoles = []
        Set<Group> assignedGroups = []
        Set<UserPage> assignedPages = []
        
        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all user related data
            bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                        'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo', 'enabled']], "User")
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
                
                // Only add pages that are allowed and not in the list
                boolean pageIsAllowed = pages.find { it.id == userPage.page.id }
                UserPage existingUserPage = user.userPages.find { (it.page.id == userPage.page.id) }
                
                if (pageIsAllowed && !existingUserPage) {
                   user.addToUserPages(userPage)
                }
                else if (existingUserPage) {
                    bindData(existingUserPage, params, [include: ['denied']], "Page_${i}")
                    existingUserPage.save()
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
        List<Page> pages = Page.getAllPagesWithAccess()
        Set<Group> groups = Group.getAllGroupsWithAccess()

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all user related data
            bindData(user, params, [include: ['title', 'firstName', 'lastName', 'gender', 'organisation',
                        'department', 'email', 'address', 'city', 'country', 'phone', 'mobile', 'extraInfo', 'enabled']], "User")
            
            // Check for all the possible roles, whether they have to be removed or added to the user
            UserRole.removeAll(user)
            roles.each { role ->                
                if (params["User.roles"]?.contains(role.id.toString())) {
                    UserRole.create(user, role)
                }
            }
            
            // Check for all the possible groups, whether they have to be removed or added to the user
            Set<Long> groupIds = (params["User.groups"]) ? params["User.groups"]*.toLong() : []            
            groups.each { group -> 
                if (user.groups.contains(group) && !groupIds.contains(group.id)) {
                    user.removeFromGroups(group)                    
                }
                if (!user.groups.contains(group) && groupIds.contains(group.id)) {
                    user.addToGroups(group)
                }
            }
            
            // Remove all user pages the user indicated to be deleted
            Set<Long> toBeRemoved = (params["Page.to-be-deleted"]) ? params["Page.to-be-deleted"].split(';')*.toLong() : []
            UserPage.findAllByUser(user).each { userPage -> 
                if (toBeRemoved.contains(userPage.page.id)) {
                    UserPage.executeUpdate("DELETE FROM UserPage WHERE user=? AND page=? AND date=?", 
                        [user, userPage.page, pageInformation.date])
                }
            }
            
            // Update the users individual rules list
            int i = 0
            while (params["Page_${i}"]) {
                UserPage userPage = new UserPage()
                bindData(userPage, params, [include: ['page', 'denied']], "Page_${i}")
                
                // Only add pages that are allowed and not in the list
                boolean pageIsAllowed = pages.find { it.id == userPage.page.id }
                UserPage existingUserPage = user.userPages.find { (it.page.id == userPage.page.id) && 
                                                                  (it.date == pageInformation.date) }
                
                if (pageIsAllowed && !existingUserPage) {
                   user.addToUserPages(userPage)
                }
                else if (existingUserPage) {
                    bindData(existingUserPage, params, [include: ['denied']], "Page_${i}")
                    existingUserPage.save()
                }
                
                i++
            }

            // Save the user and redirect to the previous page if everything is ok
            if (user.save(flush: true)) {
                // Make sure the cache is cleared
                sessionFactory.queryCache.clear()
                
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'user.label'), user.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }
        }
        
        // Show all user authentication related information
        render(view: "form", model: [user: user, roles: user.roles, groups: Group.getAllGroupsOfUser(user), 
                pages: UserPage.findAllByUser(user), allRoles: roles, allGroups: groups, allPages: pages])
    }
}
