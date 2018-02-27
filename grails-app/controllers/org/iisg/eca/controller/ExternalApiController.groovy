package org.iisg.eca.controller

import org.iisg.eca.domain.Setting

class ExternalApiController {
    def bookExportService

    def programme() {
        if (Setting.getSetting(Setting.ACCESS_TOKEN).value.equalsIgnoreCase(params.access_token)) {
            response.contentType = "application/xml"
            response.outputStream << bookExportService.getSessionsXml(true)
        }
        else {
            response.sendError(403)
            return false
        }
    }
}
