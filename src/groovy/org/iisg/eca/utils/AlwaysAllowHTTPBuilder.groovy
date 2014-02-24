package org.iisg.eca.utils

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
class AlwaysAllowHTTPBuilder extends HTTPBuilder {

	/**
	 * @see groovyx.net.http.HTTPBuilder
	 */
	public AlwaysAllowHTTPBuilder() {
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
	public AlwaysAllowHTTPBuilder(java.lang.Object defaultURI) throws java.net.URISyntaxException {
		this()
		this.setUri(defaultURI)
	}

	/**
	 * @see groovyx.net.http.HTTPBuilder
	 */
	public AlwaysAllowHTTPBuilder(java.lang.Object defaultURI, java.lang.Object defaultContentType)
			throws java.net.URISyntaxException {
		this()
		this.setUri(defaultURI)
		this.defaultContentType = defaultContentType
	}

	/**
	 * Trust manager that trusts everything
	 */
	final class AllTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	}
}
