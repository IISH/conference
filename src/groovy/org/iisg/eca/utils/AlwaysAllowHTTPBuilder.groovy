package org.iisg.eca.utils

import groovy.transform.CompileStatic

import java.security.SecureRandom
import java.security.cert.X509Certificate

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import groovyx.net.http.HTTPBuilder
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.ssl.SSLSocketFactory

/**
 * An HTTPBuilder that always allows connections, even though the certificate is not trusted
 */
@CompileStatic
class AlwaysAllowHTTPBuilder extends HTTPBuilder {

	/**
	 * @see groovyx.net.http.HTTPBuilder
	 */
	AlwaysAllowHTTPBuilder() {
		super()

		SSLContext sslContext = SSLContext.getInstance("SSL")
		sslContext.init(null, [new AllTrustManager()] as TrustManager[], new SecureRandom())

		SSLSocketFactory sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
		Scheme httpsScheme = new Scheme('https', sf, 443)

		client.connectionManager.schemeRegistry.register(httpsScheme)
	}

	/**
	 * @see groovyx.net.http.HTTPBuilder
	 */
	AlwaysAllowHTTPBuilder(Object defaultURI) throws URISyntaxException {
		this()
		this.setUri(defaultURI)
	}

	/**
	 * @see groovyx.net.http.HTTPBuilder
	 */
	AlwaysAllowHTTPBuilder(Object defaultURI, Object defaultContentType) throws URISyntaxException {
		this()
		this.setUri(defaultURI)
		this.defaultContentType = defaultContentType
	}

	/**
	 * Trust manager that trusts everything
	 */
	final class AllTrustManager implements X509TrustManager {
		X509Certificate[] getAcceptedIssuers() {
			return null
		}

		void checkClientTrusted(X509Certificate[] certs, String authType) {}

		void checkServerTrusted(X509Certificate[] certs, String authType) {}
	}
}
