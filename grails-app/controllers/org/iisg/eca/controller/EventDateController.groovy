package org.iisg.eca.controller

class EventDateController {
    def pageInformation

    def index() {
        redirect(uri: eca.createLink(action: 'create', noBase: true), params: params)
    }

    def create() {
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def show() {
        params.id = pageInformation.date.id
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }

    def edit() {
        params.id = pageInformation.date.id
        forward(controller: 'dynamicPage', action: 'dynamic', params: params)
    }
}
