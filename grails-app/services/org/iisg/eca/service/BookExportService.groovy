package org.iisg.eca.service

import java.text.DateFormat
import java.text.SimpleDateFormat

import groovy.xml.MarkupBuilder

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.User
import org.iisg.eca.domain.SessionDateTime

/**
 * Service creating the XML for the program book
 */
class BookExportService {
    def pageInformation

    String getDaysXml() {
        DateFormat weekDayFormatter = new SimpleDateFormat('EEE', Locale.US)
        DateFormat dayFormatter = new SimpleDateFormat('dd', Locale.US)
        DateFormat monthFormatter = new SimpleDateFormat('MMMMM', Locale.US)

        getXml("Export of Days for ${getEventCode()} Programbook (English)") { builder ->
            builder.days {
                Day.executeQuery('''
                    SELECT d, sdt
                    FROM Day AS d
                    INNER JOIN d.sessionDateTimes AS sdt
                    ORDER BY d.id, sdt.indexNumber
                ''').each { results ->
                    builder.day {
                        Day d = results[0]
                        SessionDateTime sdt = results[1]

                        builder.weekday(weekDayFormatter.format(d.day))
                        builder.day(dayFormatter.format(d.day))
                        builder.month(monthFormatter.format(d.day))
                        builder.time(sdt.period.split('-')[0].trim())
                    }
                }
            }
        }
    }

    String getConcordanceXml() {
        getXml("Export of Concordance for ${getEventCode()} Programbook") { builder ->
            builder.names {
                User.executeQuery('''
                    SELECT u
                    FROM ParticipantDate AS pd
                    INNER JOIN pd.user AS u
                    WHERE pd.state.id = 2
                    AND u.enabled = true
                    AND u.deleted = false
                    AND pd.enabled = true
                ''').each { user ->
                    builder.name {
                        builder.lastname(user.lastName)
                        builder.firstname(user.firstName)
                        builder.email(user.email)
                    }
                }
            }
        }
    }

    private String getEventCode() {
        "${pageInformation.date.event.shortName}${pageInformation.date.yearCode}"
    }

    private String getXml(String description, Closure body) {
        DateFormat formatter = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss', Locale.US)
        StringWriter writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.doubleQuotes = true

        writer.write('<?xml version="1.0" encoding="utf-8" ?>\n')

        xml.data {
            xml.description(description)
            xml.exportDate(formatter.format(new Date()))

            body(xml)
        }

        writer.toString()
    }
}
