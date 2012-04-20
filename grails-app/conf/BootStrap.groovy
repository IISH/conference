import java.text.SimpleDateFormat
import org.iisg.eca.domain.Setting

class BootStrap {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM yyyy")
    private static final String LAST_UPDATED = "lastUpdated"

    def init = { servletContext ->
        // Set the last updated date in the database
        Setting lastUpdated = Setting.findByProperty(LAST_UPDATED)
        if (!lastUpdated) {
            lastUpdated = new Setting(property: LAST_UPDATED, value: DATE_FORMAT.format(new Date()))
        }
        else {
            lastUpdated.value = DATE_FORMAT.format(new Date())
        }
        lastUpdated.save(flush: true)
    }

    def destroy = {

    }
}
