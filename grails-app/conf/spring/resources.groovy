import org.iisg.eca.utils.PageInformation
import org.iisg.eca.utils.CustomPropertyEditorRegistrar

import org.iisg.eca.security.UserSaltSource
import org.iisg.eca.security.UserDetailsService

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

    customPropertyEditorRegistrar(CustomPropertyEditorRegistrar)

    pageInformation(PageInformation)
}
