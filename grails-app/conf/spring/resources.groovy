import org.iisg.eca.UserSaltSource

import org.iisg.eca.UserDetailsService
import org.springframework.web.servlet.i18n.CookieLocaleResolver

// Place your Spring DSL code here
beans = {
    userDetailsService(UserDetailsService) {
        grailsApplication = ref('grailsApplication')
    }
	
    saltSource(UserSaltSource) {
        userPropertyToUse = application.config.grails.plugins.springsecurity.dao.reflectionSaltSourceProperty
    }

    localeResolver(CookieLocaleResolver) {
        cookieName = 'lang'
    }
}
