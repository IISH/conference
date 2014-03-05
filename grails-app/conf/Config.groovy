import grails.util.Environment

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

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [
        all:           '*/*',
        atom:          'application/atom+xml',
        css:           'text/css',
        csv:           'text/csv',
        form:          'application/x-www-form-urlencoded',
        html:          ['text/html','application/xhtml+xml'],
        js:            'text/javascript',
        json:          ['application/json', 'text/json'],
        multipartForm: 'multipart/form-data',
        rss:           'application/rss+xml',
        text:          'text/plain',
        hal:           ['application/hal+json','application/hal+xml'],
        xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default, if false, you have to specify 'cache: true' in the query to cache queries
grails.hibernate.cache.queries = false

// Set to true to fallback to the Spring binder instead
grails.databinding.useSpringBinder = true

// Make sure grails.config.locations is initialized
if (!grails.config.locations || !(grails.config.locations instanceof Collection)) {
    grails.config.locations = []
}

if (Environment.current != Environment.TEST) {
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

// Quartz configuration
quartz {
    autoStartup = true
    jdbcStore = false
}

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
        grails.mail.default.from = "ECA conference application <testeca1@knoex.com>"
        grails.mail.overrideAddress = "Test ECA <testeca1@knoex.com>"
        grails.mail.disabled = true
        grails.plugin.springsecurity.debug.useFilter = true
    }
    test {
        grails.mail.disabled = true
        quartz.autoStartup = false
    }
    production {
        grails.logging.jul.usebridge = false
        grails.mail.disabled = false
    }
}

// log4j configuration
// We assume the production environment is running in an tomcat container. If not we use the application path's target folder.
final String catalinaBase = System.properties.getProperty('catalina.base', './target') + "/logs"
File logFile = new File(catalinaBase)
logFile.mkdirs()
println("log directory: " + logFile.absolutePath)

String loglevel = System.properties.getProperty('loglevel', 'warn')
log4j = {
	appenders {
		console name: 'StackTrace'
		rollingFile name: 'stacktrace', maxFileSize: 1024,
				file: logFile.absolutePath + '/stacktrace.log'
	}

	root {
		"$loglevel"()
	}

	"$loglevel" 'org.codehaus.groovy.grails.web.servlet',  //  controllers
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
}

// Spring Security Core config
grails {
    plugin {
        springsecurity {
            userLookup {
                userDomainClassName = 'org.iisg.eca.domain.User'
                usernamePropertyName = 'email'
                passwordPropertyName = 'password'
                authoritiesPropertyName = 'roles'
                enabledPropertyName = 'enabled'
                accountLockedPropertyName = 'deleted'
                authorityJoinClassName = 'org.iisg.eca.domain.UserRole'
            }

            authority {
                className = 'org.iisg.eca.domain.Role'
                nameField = 'role'
            }

            requestMap {
                className = 'org.iisg.eca.domain.RequestMap'
                urlField = 'url'
                configAttributeField = 'configAttribute'
                httpMethodField = 'httpMethod'
            }

            password {
                algorithm = 'SHA-512'
                hash.iterations = 1
            }

            logout.postOnly = false
            useSecurityEventListener = true
            dao.reflectionSaltSourceProperty = 'salt'
            securityConfigType = grails.plugin.springsecurity.SecurityConfigType.Requestmap
            roleHierarchy = 'superAdmin > admin admin > user'
            controllerAnnotations.staticRules = [
                    '/':                              ['permitAll'],
                    '/index':                         ['permitAll'],
                    '/index.gsp':                     ['permitAll'],
                    '/**/js/**':                      ['permitAll'],
                    '/**/css/**':                     ['permitAll'],
                    '/**/images/**':                  ['permitAll'],
                    '/**/favicon.ico':                ['permitAll']
            ]
            providerNames = ['daoAuthenticationProvider']
			if (Environment.current != Environment.TEST) {
		        providerNames << 'clientCredentialsAuthenticationProvider'
	        }

	        oauthProvider {
                grantTypes {
                    authorizationCode = false
                    implicit = false
                    refreshToken = false
                    clientCredentials = true
                    password = false
                }

                tokenServices {
                    accessTokenValiditySeconds = 60 * 60 * 12 // default 12 hours
                }
            }
        }
    }
}
