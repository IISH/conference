package org.iisg.eca

class NetworkController {
    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost')
    }
}
