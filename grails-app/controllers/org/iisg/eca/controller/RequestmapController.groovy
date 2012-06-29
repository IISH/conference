package org.iisg.eca.controller

/**
 * Controller responsible for handling requests on request maps
 */
class RequestmapController {

    /**
      * Index action, redirects to the list action
      */
     def index() {
         redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
     }

     /**
      * Shows all data on a particular request map
      */
     def show() {
         forward(controller: 'dynamicPage', action: 'dynamic', params: params)
     }

     /**
      * Shows a list of all request maps for the current event date
      */
     def list() {
         forward(controller: 'dynamicPage', action: 'dynamic', params: params)
     }

     /**
      * Allows the user to create a new request map for the current event date
      */
     def create() {
         forward(controller: 'dynamicPage', action: 'dynamic', params: params)
     }

     /**
      * Allows the user to make changes to the request map
      */
     def edit() {
         forward(controller: 'dynamicPage', action: 'dynamic', params: params)
     }
}
