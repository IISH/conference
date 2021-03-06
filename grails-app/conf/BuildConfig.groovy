grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [test: false]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()

        // uncomment these to enable remote dependency resolution from public Maven repositories
        mavenRepo "https://repo1.maven.org/maven2/"
        mavenRepo "https://snapshots.repository.codehaus.org"
        mavenRepo "https://repository.codehaus.org"
        mavenRepo "https://download.java.net/maven/2/"
        mavenRepo "https://repository.jboss.com/maven2/"
        mavenRepo "https://repo.spring.io/milestone/"
        mavenRepo "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/"
        mavenRepo "https://repo.adobe.com/nexus/content/repositories/public/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        compile 'net.sf.ehcache:ehcache-core:2.6.11'
        compile 'net.sourceforge.jexcelapi:jxl:2.6.12'
        runtime 'mysql:mysql-connector-java:5.1.47'
	    runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.7') {
		    excludes 'xalan'
		    excludes 'xml-apis'
		    excludes 'groovy'
	    }
    }

    plugins {
        // plugins for the build system only
	    build ':tomcat:7.0.50.1'

        // plugins for the compile step
        compile ":hibernate-filter:0.3.2"
        compile ":mail:1.0.7"
        compile ":quartz:1.0.2"
        compile ":spring-security-core:2.0.0"
        compile ":spring-security-oauth2-provider:2.0-RC5"
        compile ":cache:1.1.8"
        compile ":csv:0.3.1"

        // plugins needed at runtime but not for compilation
	    runtime(":hibernate:3.6.10.8") { // Hibernate 4 instead: ":hibernate4:4.3.1.2"
            excludes 'ehcache-core'
        }
        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.1"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0.1"
        //runtime ":cached-resources:1.1"
        //runtime ":yui-minify-resources:0.1.5"
    }
}
