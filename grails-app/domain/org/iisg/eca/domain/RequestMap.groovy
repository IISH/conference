package org.iisg.eca.domain

import org.springframework.http.HttpMethod

class RequestMap {
    String url
    String configAttribute
    HttpMethod httpMethod

    static mapping = {
        table 'request_map'
        version false

        id              column: 'request_map_id'
        url             column: 'url'
        configAttribute column: 'config_attribute'
        httpMethod      column: 'http_method'
    }

    static constraints = {
        url             blank: false,   maxSize: 50,  unique: 'httpMethod'
        configAttribute blank: false,   maxSize: 255
        httpMethod      nullable: true
    }

    @Override
    String toString() {
        "${url} : ${configAttribute}"
    }
}
