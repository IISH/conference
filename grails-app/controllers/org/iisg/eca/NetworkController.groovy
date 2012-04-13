package org.iisg.eca

class NetworkController {
    def show() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get')
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }
}
