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
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
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

        List<Page> pages = Page.withCriteria {
            groups {
                eq('id', group.id)
            }
            order('titleDefault', "asc")
        }

        List<User> users = User.withCriteria {
            groups {
                eq('id', group.id)
            }
            order('lastName', "asc")
            order('firstName', "asc")
        }

        // Show all group related information
        render(view: "show", model: [   group:  group,
                                        pages:  pages,
                                        users:  users])
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
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    /**
     * Allows the user to make changes to the authentication group
     */
    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
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
