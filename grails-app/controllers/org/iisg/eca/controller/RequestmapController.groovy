package org.iisg.eca.controller

class RequestmapController {
    def index() {
        redirect(uri: eca.createLink(action: 'list', noBase: true), params: params)
    }

    def show() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }
}
