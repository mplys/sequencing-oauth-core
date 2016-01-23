package com.sequencing.oauth2demo.core;

import java.util.Map;

import com.sequencing.oauth2demo.config.AuthenticationParameters;
import com.sequencing.oauth2demo.exception.FailureBasicAuthentication;
import com.sequencing.oauth2demo.exception.InvalidStateException;

/**
 * Interface that defines basic methods needed for oauth 
 */
public interface SequencingOAuth2Client
{
	/**
	 * Returns map of parameters needed for initial GET redirect to sequencing authentication endpoint
	 * @return Map<String, String> map with parameters, where key is parameter name
	 */
	public Map<String, String> getHttpParametersForRedirect();

	/**
	 * Returns query needed for initial redirect to sequencing authentication endpoint
	 */
	public String getLoginRedirectUrl();

	/**
	 * Create token for user authentication
	 * @return Token token with access, refresh and lifetime value
	 */
	public Token authorize(String responseCode, String responseState) throws InvalidStateException, FailureBasicAuthentication ;
	
	/**
	 * Returns token state - whether it's still effective or not
	 */
	public boolean isAuthorized();
	
	/**
	 * Returns configuration parameters needed to carry on authentication
	 * against sequencing.com backend
	 */
	public AuthenticationParameters getAuthenticationParameters();
	
	/**
	 * Returns current token
	 */
	public Token getToken();
}
