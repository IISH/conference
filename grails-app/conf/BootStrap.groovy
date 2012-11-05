import java.text.SimpleDateFormat

import org.iisg.eca.domain.Setting
import org.iisg.eca.domain.DynamicPage

class BootStrap {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd")

    def init = { servletContext ->
        // Set the last updated date in the database
        Setting lastUpdated = Setting.findByProperty(Setting.LAST_UPDATED)
        if (!lastUpdated) {
            lastUpdated = new Setting(property: Setting.LAST_UPDATED, value: DATE_FORMAT.format(new Date()))
        }
        else {
            lastUpdated.value = DATE_FORMAT.format(new Date())
        }
        lastUpdated.save()
        
        // Make sure the cache of the dynamic pages are emptied
        DynamicPage.list().each {
            it.cache = null
            it.save()
        }
    }
    
    def destroy = {
        
    }
}
