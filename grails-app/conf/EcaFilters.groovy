import org.iisg.eca.Page

class EcaFilters {
    def pageInformation

    def filters = {
        all(controller: '*', action: '*', controllerExclude: 'login|logout') {
            before = {
                Page page = Page.findByControllerAndAction(params.controller, params.action)
                if (page) {
                    pageInformation.page = page
                    return true
                }

                return false
            }

            after = { Map model ->
                pageInformation.removePage()
            }

            afterView = { Exception e ->

            }
        }
    }
}
