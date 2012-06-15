import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = org.iisg.eca // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// MySQL driver for reverse engineering
grails.plugin.reveng.jdbcDriverJarDep = 'mysql:mysql-connector-java:5.1.18'

// Make sure grails.config.locations is initialized
if (!grails.config.locations || !(grails.config.locations instanceof Collection)) {
    grails.config.locations = []
}

if (GrailsUtil.getEnvironment() != GrailsApplication.ENV_TEST) {
    // Load properties, like passwords, from another location
    if (System.properties.containsKey("conference.properties")) {
       println("Loading properties from " + System.properties["conference.properties"])
       grails.config.locations << "file:" + System.properties["conference.properties"]
    }
    else if (System.getenv()?.containsKey("CONFERENCE")) {
       println("Loading properties from " + System.getenv().get("CONFERENCE"))
       grails.config.locations << "file:" + System.getenv().get("CONFERENCE")
    }
    else {
       println("FATAL: no conference.properties file set in VM or Environment. \n \
           Add a -Dconference.properties=/path/to/conference.properties argument when starting this application. \n \
           Or set a CONFERENCE=/path/to/conference.properties as environment variable.")
       System.exit(-1)
    }
}

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
        grails.serverURL = "http://localhost:8080/${appName}"
        grails.mail.default.from = "ECA conference application <esshc@iisg.nl>"
        grails.mail.overrideAddress = "Test ECA <kerim.meijer@gmail.com>"
    }
    test {
        grails.mail.disabled = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    trace   'org.hibernate.type'
    debug   'org.hibernate.SQL'

}

// Spring Security Core config
grails.plugins.springsecurity.userLookup.userDomainClassName = 'org.iisg.eca.domain.User'
grails.plugins.springsecurity.userLookup.usernamePropertyName = 'email'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'password'
grails.plugins.springsecurity.userLookup.authoritiesPropertyName = 'roles'
grails.plugins.springsecurity.userLookup.enabledPropertyName = 'enabled'
grails.plugins.springsecurity.userLookup.accountLockedPropertyName = 'deleted'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.iisg.eca.domain.UserRole'

grails.plugins.springsecurity.authority.className = 'org.iisg.eca.domain.Role'
grails.plugins.springsecurity.authority.nameField = 'role'

grails.plugins.springsecurity.successHandler.alwaysUseDefault = true
grails.plugins.springsecurity.successHandler.defaultTargetUrl = '/login/authSuccess'

grails.plugins.springsecurity.password.algorithm = 'SHA-512'
grails.plugins.springsecurity.dao.reflectionSaltSourceProperty = 'salt'

grails.plugins.springsecurity.securityConfigType = grails.plugins.springsecurity.SecurityConfigType.Requestmap

grails.plugins.springsecurity.requestMap.className = 'org.iisg.eca.domain.RequestMap'
grails.plugins.springsecurity.requestMap.urlField = 'url'
grails.plugins.springsecurity.requestMap.configAttributeField = 'configAttribute'

grails.plugins.springsecurity.roleHierarchy = '''
        superAdmin > admin
        admin > user
'''