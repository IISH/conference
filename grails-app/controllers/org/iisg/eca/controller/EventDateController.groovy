package org.iisg.eca.controller

class EventDateController {
    def index() {
        redirect(uri: eca.createLink(action: 'create', noBase: true), params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
}
