dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    dbCreate = "validate"
    configClass = org.grails.plugin.hibernate.filter.HibernateFilterDomainConfiguration.class
}
dataSource_payWay {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    dialect = org.hibernate.dialect.MySQL5InnoDBDialect
    dbCreate = "validate"
    configClass = org.grails.plugin.hibernate.filter.HibernateFilterDomainConfiguration.class
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    //cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'net.sf.ehcache.hibernate.SingletonEhCacheRegionFactory' // Hibernate 3 singleton
    //cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    //cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4 singleton
}
// environment specific settings
environments {
    test {
        dataSource {
            driverClassName = "org.h2.Driver"
            dialect = org.hibernate.dialect.H2Dialect
            username = "sa"
            password = ""
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
        dataSource_payWay {
            driverClassName = "org.h2.Driver"
            dialect = org.hibernate.dialect.H2Dialect
            username = "sa"
            password = ""
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
    }
    production {
        dataSource {
            properties {
	            maxActive = 8
	            maxIdle = 8
	            minIdle = 0
	            minEvictableIdleTimeMillis = 1800000
	            timeBetweenEvictionRunsMillis = 1800000
	            numTestsPerEvictionRun = 3
	            testOnBorrow = true
	            testWhileIdle = true
	            testOnReturn = true
	            validationQuery = "SELECT 1"
	            jdbcInterceptors = "ConnectionState"
            }
        }
	    dataSource_payWay {
            properties {
                maxActive = 8
                maxIdle = 8
                minIdle = 0
                minEvictableIdleTimeMillis = 1800000
                timeBetweenEvictionRunsMillis = 1800000
                numTestsPerEvictionRun = 3
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = true
                validationQuery = "SELECT 1"
                jdbcInterceptors = "ConnectionState"
            }
	    }
    }
}
