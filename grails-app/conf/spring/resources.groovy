import org.iisg.eca.utils.PageInformation
import org.iisg.eca.utils.CustomPropertyEditorRegistrar

import org.iisg.eca.security.UserSaltSource
import org.iisg.eca.security.UserDetailsService
import org.iisg.eca.security.SecurityEventListener

import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

// Place your Spring DSL code here
beans = {
    userDetailsService(UserDetailsService) {
        grailsApplication = ref('grailsApplication')
    }

    authenticationSuccessHandler(SecurityEventListener) {
            def conf = SpringSecurityUtils.securityConfig
            requestCache = ref('requestCache')
            defaultTargetUrl = conf.successHandler.defaultTargetUrl
            alwaysUseDefaultTargetUrl = conf.successHandler.alwaysUseDefault
            targetUrlParameter = conf.successHandler.targetUrlParameter
            useReferer = conf.successHandler.useReferer
            redirectStrategy = ref('redirectStrategy')
        }

    saltSource(UserSaltSource) {
        userPropertyToUse = application.config.grails.plugins.springsecurity.dao.reflectionSaltSourceProperty
    }

    localeResolver(CookieLocaleResolver) {
        cookieName = 'lang'
    }

    customPropertyEditorRegistrar(CustomPropertyEditorRegistrar) {
        messageSource = ref('messageSource')
    }

    pageInformation(PageInformation)
}
