import org.iisg.eca.filter.HibernateFilterHelper
import org.iisg.eca.security.UserSaltSource
import org.iisg.eca.security.UserDetailsService
import org.iisg.eca.security.SecurityEventListener
import org.iisg.eca.security.MyUsernamePasswordAuthenticationFilter

import org.iisg.eca.utils.PageInformation
import org.iisg.eca.utils.CustomPropertyEditorRegistrar

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

	authenticationProcessingFilter(MyUsernamePasswordAuthenticationFilter) {
		def conf = SpringSecurityUtils.securityConfig
		authenticationManager = ref('authenticationManager')
		sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
		authenticationSuccessHandler = ref('authenticationSuccessHandler')
		authenticationFailureHandler = ref('authenticationFailureHandler')
		rememberMeServices = ref('rememberMeServices')
		authenticationDetailsSource = ref('authenticationDetailsSource')
		requiresAuthenticationRequestMatcher = ref('filterProcessUrlRequestMatcher')
		usernameParameter = conf.apf.usernameParameter // j_username
		passwordParameter = conf.apf.passwordParameter // j_password
		continueChainBeforeSuccessfulAuthentication = conf.apf.continueChainBeforeSuccessfulAuthentication // false
		allowSessionCreation = conf.apf.allowSessionCreation // true
		postOnly = conf.apf.postOnly // true
		storeLastUsername = conf.apf.storeLastUsername // false
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

	hibernateFilterHelper(HibernateFilterHelper) {
		sessionFactory = ref('sessionFactory')
	}
}
