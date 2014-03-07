import org.iisg.eca.security.UserSaltSource
import org.iisg.eca.security.UserDetailsService
import org.iisg.eca.security.SecurityEventListener

import org.iisg.eca.utils.PageInformation
import org.iisg.eca.utils.CustomPropertyEditorRegistrar

import org.iisg.eca.converter.DateConverter

import org.springframework.security.oauth2.provider.token.JdbcTokenStore
import org.springframework.security.oauth2.provider.JdbcClientDetailsService

import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.web.servlet.i18n.CookieLocaleResolver

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

    tokenStore(JdbcTokenStore, ref('dataSource'))

	clientDetailsService(JdbcClientDetailsService, ref('dataSource'))

    saltSource(UserSaltSource) {
        userPropertyToUse = application.config.grails.plugin.springsecurity.dao.reflectionSaltSourceProperty
    }

    localeResolver(CookieLocaleResolver) {
        cookieName = 'lang'
    }

    customPropertyEditorRegistrar(CustomPropertyEditorRegistrar) {
        messageSource = ref('messageSource')
    }

    dateConverter(DateConverter) {
        messageSource = ref('messageSource')
    }

    pageInformation(PageInformation)
}
