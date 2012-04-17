package org.iisg.eca

class EventDateController {
    def index() {
        redirect(action: 'create', params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }
}
