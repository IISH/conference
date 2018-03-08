package org.iisg.eca.export

import org.iisg.eca.domain.Day
import org.iisg.eca.domain.SessionDateTime

import java.text.DateFormat
import java.text.SimpleDateFormat

class DaysXmlExport extends XmlExport {
    private static final DateFormat WEEKDAY_FORMATTER = new SimpleDateFormat('EEEEE', Locale.US)
    private static final DateFormat DAY_FORMATTER = new SimpleDateFormat('dd', Locale.US)
    private static final DateFormat MONTH_FORMATTER = new SimpleDateFormat('MMMMM', Locale.US)

    DaysXmlExport(String description) {
        super(description)
        getDaysXml()
    }

    private void getDaysXml() {
        getXml { builder ->
            builder.days {
                Day.executeQuery('''
					SELECT d, sdt
					FROM Day AS d
					INNER JOIN d.sessionDateTimes AS sdt
					WHERE sdt.deleted = false
					ORDER BY d.id, sdt.indexNumber
                ''').each { results ->
                    builder.day {
                        Day d = results[0]
                        SessionDateTime sdt = results[1]

                        builder.weekday(WEEKDAY_FORMATTER.format(d.day))
                        builder.day(DAY_FORMATTER.format(d.day))
                        builder.month(MONTH_FORMATTER.format(d.day))

                        String[] times = sdt.period.split('-')
                        builder.starttime(times[0].trim())
                        builder.endtime((times.length >= 2) ? times[1].trim() : null)
                    }
                }
            }
        }
    }
}
