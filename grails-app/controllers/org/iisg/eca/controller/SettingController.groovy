package org.iisg.eca.controller

class SettingController {
    def index() {
        redirect(action: 'list', params: params)
    }

    def list() {
        forward(controller: 'dynamicPage', action: 'get', params: params)
    }

    def edit() {
        forward(controller: 'dynamicPage', action: 'getAndPost', params: params)
    }
}
