package com.sequencing.oauth2demo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.sequencing.oauth2demo.config.AuthenticationParameters;
import com.sequencing.oauth2demo.exception.FailureBasicAuthentication;
import com.sequencing.oauth2demo.exception.InvalidStateException;
import com.sequencing.oauth2demo.helper.HttpHelper;
import com.sequencing.oauth2demo.helper.JsonHelper;

/**
 * Class is a default implementation of SequencingOAuth2Client interface
 */
public class DefaultSequencingOAuth2Client implements SequencingOAuth2Client 
{
	private AuthenticationParameters parameters;
	private volatile Token token;
	private static final Logger log = LoggerFactory.getLogger(DefaultSequencingOAuth2Client.class);
	
	/**
	 * Attribute for value of redirect url 
	 */
	private static final String ATTR_REDIRECT_URL = "redirect_uri";
	
	/**
	 * Attribute for value of response type
	 */
	private static final String ATTR_RESPONSE_TYPE = "response_type";
	
	/**
	 * Attribute for value state
	 */
	private static final String ATTR_STATE = "state";
	
	/**
	 * Attribute for value client id
	 */
	private static final String ATTR_CLIENT_ID = "client_id";
	
	/**
	 * Attribute for value scope
	 */
	private static final String ATTR_SCOPE = "scope";
	
	/**
	 * Attribute for value code
	 */
	private static final String ATTR_CODE = "code";
	
	/**
	 * Attribute for value refresh token
	 */
	private static final String ATTR_REFRESH_TOKEN = "refresh_token";
	
	/**
	 * Attribute for access token
	 */
	private static final String ATTR_ACCESS_TOKE = "access_token";
	
	/**
	 * Attribute for value grant type
	 */
	private static final String ATTR_GRANT_TYPE = "grant_type";
	
	/**
	 * Attribute for value expires in
	 */
	private static final String ATTR_GEXPRIES_IN = "expires_in";
	
	/**
	 * Executor that handles token refresh
	 */
	private ScheduledExecutorService tokenUpdateExecutor = Executors.newSingleThreadScheduledExecutor();
	
	public DefaultSequencingOAuth2Client(AuthenticationParameters parameters){
		this.parameters = parameters;
	}
	
	@Override
	public Map<String, String> getHttpParametersForRedirect() {
		Map<String, String> attribures = new HashMap<String, String>(5);
		attribures.put(ATTR_REDIRECT_URL, parameters.getRedirectUri());
		attribures.put(ATTR_RESPONSE_TYPE, parameters.getResponseType());
		attribures.put(ATTR_STATE, parameters.getState());
		attribures.put(ATTR_CLIENT_ID, parameters.getClientId());
		attribures.put(ATTR_SCOPE, parameters.getScope());
		return attribures;
	}
	
	@Override
	public String getLoginRedirectUrl() {
		return String.format("%s?%s", parameters.getOAuthAuthorizationUri(), Joiner.on("&").join(getAttributesForRedirectAsList()));
	}

	@Override
	public Token authorize(String responseCode, String responseState) throws InvalidStateException, FailureBasicAuthentication
	{
		if (responseState.equals(parameters.getState()) == false)
			throw new InvalidStateException("Invalid state parametr");
			
		// You are to save these 2 tokens somewhere in a permanent storage, such as
        // database. When access token expires, you will be able to use refresh
        // token to fetch a new access token without need of re-authorization by
        // user.
			
		Map<String, String> params = new HashMap<String, String>();
		params.put(ATTR_GRANT_TYPE,   parameters.getGrantType());
		params.put(ATTR_CODE,		  responseCode);
		params.put(ATTR_REDIRECT_URL, parameters.getRedirectUri());

		Map<String, String> headers = HttpHelper.getBasicAuthenticationHeader(
				parameters.getClientId(),
				parameters.getClientSecret());

		String uri = parameters.getOAuthTokenUri();
		String result = HttpHelper.doPost(uri, headers, params);

		if (result == null) {
			throw new FailureBasicAuthentication("Failure authentication");
		}
			
		String accessToken = JsonHelper.getField(result, ATTR_ACCESS_TOKE);
		String refreshToken = JsonHelper.getField(result, ATTR_REFRESH_TOKEN);
		long timelife = Long.parseLong(JsonHelper.getField(result, ATTR_GEXPRIES_IN));
			
		token = new Token(accessToken, refreshToken, timelife);
		
		runRefreshTokenExcutor();
		
		return token;
	}
	
	@Override
	public boolean isAuthorized()
	{
		return token != null && token.getLifeTime() != 0;
	}
	
	@Override
	public AuthenticationParameters getAuthenticationParameters() {
		return parameters;
	}

	@Override
	public Token getToken() {
		return token;
	}

	protected void refreshToken() throws FailureBasicAuthentication
	{
		log.debug("Going to refresh OAuth token");
		
		Map<String, String> params = new HashMap<String, String>(2);
		params.put(ATTR_GRANT_TYPE, parameters.getGrantTypeRefreshToken());
		params.put(ATTR_REFRESH_TOKEN, token.getRefreshToken());

		Map<String, String> headers = HttpHelper.getBasicAuthenticationHeader(
				parameters.getClientId(),
				parameters.getClientSecret());

		String uri = parameters.getOAuthTokenUri();
		String result = HttpHelper.doPost(uri, headers, params);

		if (result == null) {
			throw new FailureBasicAuthentication("Failure authentication");
		}
		
		String accessToken = JsonHelper.getField(result, ATTR_ACCESS_TOKE);
		long timelife = Long.parseLong(JsonHelper.getField(result, ATTR_GEXPRIES_IN));
		
		token = new Token(accessToken, token.getRefreshToken(), timelife);
		log.debug("Token has been refreshed! New value " + token.getAccessToken());
	}
	
	/**
	 * Runs executor for refreshing token 
	 */
	private void runRefreshTokenExcutor(){
		tokenUpdateExecutor.scheduleWithFixedDelay(new TokenRefreshTask(), 0, token.getLifeTime() - 60, TimeUnit.SECONDS);
	}
	
	class TokenRefreshTask implements Runnable
	{
		public void run() {
			try {
				refreshToken();
			} catch (FailureBasicAuthentication e) {
				log.debug("Error to refresh token", e.getMessage());
			}
		}
	}
	
	private List<String> getAttributesForRedirectAsList() {
		Map<String, String> attributes = getHttpParametersForRedirect();
		List<String> result = new ArrayList<String>(attributes.size());
		
		for (Entry<String, String> e : attributes.entrySet())
			result.add(String.format("%s=%s", e.getKey(), e.getValue()));
		
		return result;
	}
}
