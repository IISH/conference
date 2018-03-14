import java.util.TimeZone
import java.text.SimpleDateFormat

SimpleDateFormat formatter = new SimpleDateFormat("d MMMMM yyyy HH:mm:ss", Locale.US)
formatter.setTimeZone(TimeZone.getTimeZone('CET'))
formatter.format(new Date()) + ' CET'