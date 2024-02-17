package org.iisg.eca.domain

class OAuthAccessToken {
	String authenticationKey
	byte[] authentication

	String username
	String clientId

	String value
	String tokenType

	Date expiration
	Map<String, Object> additionalInformation

	String refreshToken
	Set<String> scope

	static transients = ['additionalInformation', 'scope']

	static constraints = {
		authenticationKey nullable: false, blank: false, unique: true
		authentication nullable: false, minSize: 1, maxSize: 1024 * 4

		username nullable: true
		clientId nullable: false, blank: false

		value nullable: false, blank: false, unique: true
		tokenType nullable: false, blank: false

		expiration nullable: false

		refreshToken nullable: true
	}

	static mapping = {
		table 'oauth_access_token'
		version false
	}
}
