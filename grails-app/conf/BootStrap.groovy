import java.text.SimpleDateFormat
import org.iisg.eca.domain.Setting

class BootStrap {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM yyyy")
    private static final String LAST_UPDATED = "lastUpdated"

    def grailsApplication

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

    /**
     * Replaces the redirect method of the given controller with support for different events and dates
     * @param controller The controller of which to replace the redirect method
     */
    void replaceRedirect(controller) {
        def oldRedirect = controller.metaClass.pickMethod("redirect", [Map, Closure] as Class[])
        controller.metaClass.redirect = { Map params, Closure closure ->
            if (!params.params) {
                params.params = [:]
            }

            // Add event (date) information
            if (delegate?.params?.event && delegate?.params?.date) {
                params.params.event = delegate.params.event
                params.params.date = delegate.params.date
            }
            if (params.event && params.date) {
                params.params.event = params.event
                params.params.date = params.date
            }

            // Add parameters for moving back to the current page
            params.params.prevController = params.controller
            params.params.prevAction = params.action
            if (params.id) {
                params.params.prevId = params.id
            }

            // Invoke the default render method
            oldRedirect.invoke(delegate, [params, closure])
        }
    }
}
