package org.iisg.eca

/**
 * Tag libs involving <code>Conference</code> data
 */
class ConferencesTagLib {
    static namespace = "eca"

    /**
     * Tag listing all conferences by date
     */
    def listConferences = { attrs ->
        out << "<ul id=\"conference-list\">"
        attrs.conferenceList.each {
            out << "<li>"
            boolean first = true
            attrs.dateList.each { date ->
                 if (date.conference.equals(it)) {
                    if (first) {
                        out << "<a href=\"\">${it.shortName} ${date.yearCode}</a> "
                        first = false
                    }
                    else {
                        out << "<a href=\"\">${date.yearCode}</a> "
                    }
                }
            }
            out << "</li> <br />"
        }
        out << "</ul>"
    }
}
