package org.iisg.eca.controller

import grails.converters.JSON

/**
 * Controller responsible for sending translated messages to the client side
 */
class MessageController {

    /**
     * Returns localized messages called by client-side scripts
     * (AJAX call)
     */
    def index() {
        // Must be called via AJAX
        if (request.xhr) {
            // Look up the message and send it back in JSON format
            Map message = [message: g.message(code: params.code)]
            render message as JSON
        }
    }
}

