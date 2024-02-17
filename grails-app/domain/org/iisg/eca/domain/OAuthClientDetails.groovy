package org.iisg.eca.domain

class OAuthClientDetails {
	String clientId
	String clientSecret

	Integer accessTokenValiditySeconds
	Integer refreshTokenValiditySeconds

	String event
	String authority
	String grantType

	Set<String> resourceIds = []
	Set<String> scopes = []
	Set<String> autoApproveScopes = []
	Set<String> redirectUris = []

	static transients = ['accessTokenValiditySeconds', 'refreshTokenValiditySeconds',
	                     'additionalInformation', 'authorities', 'authorizedGrantTypes',
	                     'resourceIds', 'scopes', 'autoApproveScopes', 'redirectUris']

	static constraints = {
		clientId blank: false, unique: true
		clientSecret nullable: true

		accessTokenValiditySeconds nullable: true
		refreshTokenValiditySeconds nullable: true

		event nullable: true
		authority blank: false
		grantType blank: false
	}

	static mapping = {
		table 'oauth_client_details'
		version false
	}

	Set<String> getAuthorities() {
		return [authority] as Set<String>
	}

	Set<String> getAuthorizedGrantTypes() {
		return [grantType] as Set<String>
	}

	Map<String, Object> getAdditionalInformation() {
		return ['events': event] as Map<String, Object>
	}
}
