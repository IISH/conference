import org.iisg.eca.utils.PageInformation
import org.iisg.eca.utils.CustomPropertyEditorRegistrar

import org.iisg.eca.converter.DateConverter

import org.iisg.eca.security.UserSaltSource
import org.iisg.eca.security.UserDetailsService
import org.iisg.eca.security.SecurityEventListener

import org.springframework.web.servlet.i18n.CookieLocaleResolver

import grails.plugin.springsecurity.SpringSecurityUtils

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
        userPropertyToUse = application.config.grails.plugin.springsecurity.dao.reflectionSaltSourceProperty
    }

    localeResolver(CookieLocaleResolver) {
        cookieName = 'lang'
    }

    customPropertyEditorRegistrar(CustomPropertyEditorRegistrar) {
        messageSource = ref('messageSource')
    }

    /*dateConverter(DateConverter) {
        messageSource = ref('messageSource')
    }*/

    pageInformation(PageInformation)
}
