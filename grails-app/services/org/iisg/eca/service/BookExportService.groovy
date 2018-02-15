package org.iisg.eca.service

import org.iisg.eca.export.ConcordanceXmlExport
import org.iisg.eca.export.DaysXmlExport
import org.iisg.eca.export.SessionsXmlExport

import org.iisg.eca.utils.EmailFilter

/**
 * Service creating the XML for the program book
 */
class BookExportService {
    def pageInformation

    /**
     * Creates an XML with all the conference days
     * @return A string representing the XML
     */
    String getDaysXml() {
        DaysXmlExport daysXmlExport =
                new DaysXmlExport("Export of Days for ${getEventCode()} Programbook (English)")
        daysXmlExport.toString()
    }

    /**
     * Creates an XML with all the participants
     * @return A string representing the XML
     */
    String getConcordanceXml() {
        EmailFilter emailFilter = new EmailFilter(pageInformation.date.event)
        ConcordanceXmlExport concordanceXmlExport =
                new ConcordanceXmlExport("Export of Concordance for ${getEventCode()} Programbook", emailFilter)
        concordanceXmlExport.toString()
    }

    /**
     * Creates an XML with all the sessions
     * @return A string representing the XML
     */
    String getSessionsXml() {
        SessionsXmlExport sessionsXmlExport =
                new SessionsXmlExport("Export ${getEventCode()} Programbook (Sessions) (English)")
        sessionsXmlExport.toString()
    }

    /**
     * The event code to use in the header of the XML
     * @return A string representing the event in question
     */
    private String getEventCode() {
        "${pageInformation.date.event.shortName}${pageInformation.date.yearCode}"
    }
}
