package org.iisg.eca.controller

import org.iisg.eca.domain.Group
import org.iisg.eca.domain.Page
import org.iisg.eca.domain.User

/**
 * Controller responsible for handling requests on authentication groups
 */
class AuthGroupController {

    /**
     * Index action, redirects to the list action
     */
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true, noPreviousInfo: true, params: params))
    }

    /**
     * Shows a list of all authentication groups for the current event date
     */
    def list() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to create a new authentication group for the current event date
     */
    def create() {
        // Create a new group
        Group group = new Group();
        
        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all group related data
            bindData(group, params, [include: ["name", "pages", "enabeled"]], "Group")
            
            // Add the users to the group
            int i = 0
            while (params["User_${i}.id"]?.isLong()) {
                User user = User.findById(params["User_${i}.id"].toLong())

                // A user should only be added to a group once
                if (!group.users?.find { it.id == user.id }) {
                    group.addToUsers(user)
                }

                i++
            }

            // Save the group and redirect to the previous page if everything is ok
            if (group.save(flush: true)) {
                flash.message = g.message(code: 'default.created.message', args: [g.message(code: 'group.label'), group.toString()])
                redirect(uri: eca.createLink(previous: true, noBase: true))
                return
            }
        }
        
        // Show all group related information
        render(view: "form", model: [   group:              group,
                                        pages:              group.allPagesInGroup,
                                        users:              group.allUsersInGroup,
                                        pagesNotInGroup:    group.allPagesNotInGroup])
    }

    /**
     * Shows all data on a particular authentication group
     */
    def show() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Group group = Group.findById(params.id)

        // We also need a group to be able to show something
        if (!group) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'group.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // Show all group related information
        render(view: "show", model: [   group:  group,
                                        pages:  group.allPagesInGroup,
                                        users:  group.allUsersInGroup])
    }

    /**
     * Allows the user to make changes to the authentication group
     */
    def edit() {
        // We need an id, check for the id
        if (!params.id) {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        Group group = Group.findById(params.id)

        // We also need a group to be able to show something
        if (!group) {
            flash.error = true
            flash.message = g.message(code: 'default.not.found.message', args: [g.message(code: 'group.label'), params.id])
            redirect(uri: eca.createLink(previous: true, noBase: true))
            return
        }

        // The 'save' button was clicked, save all data
        if (request.post) {
            // Save all group related data
            bindData(group, params, [include: ["name", "pages", "enabeled"]], "Group")

            // Make sure that all pages are removed from the group if this needs to happen
            if (!params."Group.pages") {
                group.pages.clear()
            }

            // Remove all users from the group and save all new information
            int i = 0
            group.users.clear()
            group.save(flush: true)
            while (params["User_${i}.id"]?.isLong()) {
                User user = User.findById(params["User_${i}.id"].toLong())

                // A user should only be added to a group once
                if (!group.users?.find { it.id == user.id }) {
                    group.addToUsers(user)
                }

                i++
            }

            // Save the group and redirect to the previous page if everything is ok
            if (group.save(flush: true)) {
                flash.message = g.message(code: 'default.updated.message', args: [g.message(code: 'group.label'), group.toString()])
                if (params['btn_save_close']) {
                    redirect(uri: eca.createLink(previous: true, noBase: true))
                    return
                }
            }
        }

        // Show all group related information
        render(view: "form", model: [   group:              group,
                                        pages:              group.allPagesInGroup,
                                        users:              group.allUsersInGroup,
                                        pagesNotInGroup:    group.allPagesNotInGroup])
    }

    /**
     * Removes the authentication group from the current event date
     */
    def delete() {
        // Of course we need an id of the authentication group
        if (params.id) {
            Group group = Group.findById(params.id)
            group?.softDelete()

            // Try to remove the authentication group, send back a success or failure message
            if (group?.save(flush: true)) {
                flash.message = g.message(code: 'default.deleted.message', args: [g.message(code: 'group.label'), group.toString()])
            }
            else {
                flash.error = true
                flash.message = g.message(code: 'default.not.deleted.message', args: [g.message(code: 'group.label'), group.toString()])
            }
        }
        else {
            flash.error = true
            flash.message = g.message(code: 'default.no.id.message')
        }

        redirect(uri: eca.createLink(action: 'list', noBase: true))
    }
}
