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
	            jmxEnabled = true
	            initialSize = 0
	            maxActive = 8
	            minIdle = 0
	            maxIdle = 8
	            maxWait = 10000
	            maxAge = 10 * 60000
	            timeBetweenEvictionRunsMillis = 5000
	            minEvictableIdleTimeMillis = 60000
	            validationQuery = "SELECT 1"
	            validationQueryTimeout = 3
	            validationInterval = 15000
	            testOnBorrow = true
	            testWhileIdle = true
	            testOnReturn = true
	            jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
	            defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED
            }
        }
        dataSource_payWay {
            properties {
	            jmxEnabled = true
	            initialSize = 0
	            maxActive = 8
	            minIdle = 0
	            maxIdle = 8
	            maxWait = 10000
	            maxAge = 10 * 60000
	            timeBetweenEvictionRunsMillis = 5000
	            minEvictableIdleTimeMillis = 60000
	            validationQuery = "SELECT 1"
	            validationQueryTimeout = 3
	            validationInterval = 15000
	            testOnBorrow = true
	            testWhileIdle = true
	            testOnReturn = true
	            jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
	            defaultTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED
            }
        }
    }
}
