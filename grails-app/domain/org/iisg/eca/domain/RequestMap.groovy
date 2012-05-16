package org.iisg.eca.domain

class RequestMap {
    String url
    String configAttribute

    static mapping = {
        table 'request_map'
        version false
        cache true

        id              column: 'request_map_id'
        url             column: 'url'
        configAttribute column: 'config_attribute'
    }

    static constraints = {
        url             blank: false, maxSize: 50, unique: true
        configAttribute blank: false, maxSize: 255
    }

    @Override
    String toString() {
        "${url} : ${configAttribute}"
    }
}
