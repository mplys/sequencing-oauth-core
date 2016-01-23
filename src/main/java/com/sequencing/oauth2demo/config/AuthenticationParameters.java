package com.sequencing.oauth2demo.config;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that defines all required configuration parameters needed to carry on authentication
 * against sequencing.com backend 
 */
public class AuthenticationParameters
{
	public static final String DEFAULT_AUTH_URI = "https://sequencing.com/oauth2/authorize"; 
	public static final String DEFAULT_TOKEN_URI = "https://sequencing.com/oauth2/token"; 
	public static final String DEFAULT_API_URI = "https://api.sequencing.com"; 
	public static final String DEFAULT_RESPONSE_TYPE = "code"; 
	public static final String DEFAULT_SCOPE = "demo"; 
	public static final String DEFAULT_GRANT_TYPE = "authorization_code";
	public static final String DEFAULT_GRANT_TYPE_REFRESH = "refresh_token"; 
	
	private static final Logger log = LoggerFactory.getLogger(AuthenticationParameters.class);

	/**
	 * URI of Sequencing oAuth2 where you can request user to authorize your
	 * app.
	 */
	private final String oAuthAuthorizationUri;

	/**
	 * URI of Sequencing oAuth2 where you can obtain access token.
	 */
	private final String oAuthTokenUri;

	/**
	 * Sequencing API endpoint.
	 */
	private final String apiUri;

	/**
	 * Redirect URI of your oauth2 app, where it expects Sequencing oAuth2 to
	 * redirect browser.
	 */
	private final String redirectUri;

	/**
	 * Supply here 'code', which means you want to take the route of
	 * authorization code response.
	 */
	private final String responseType;

	/**
	 * oAuth2 state. It should be some random generated string. State you sent
	 * to authorize URI must match the state you get, when browser is redirected
	 * to the redirect URI you provided.
	 */
	private final String state;

	/**
	 * Id of your oauth2 app (oauth2 client). You will be able to get this value
	 * from Sequencing website.
	 */
	private final String clientId;

	/**
	 * Secret of your oauth2 app (oauth2 client). You will be able to get this
	 * value from Sequencing website. Keep this value private.
	 */
	private final String clientSecret;

	/**
	 * Scopes, access to which you request.
	 */
	private final String scope;

	/**
	 * Supply here 'authorization_code', which means you request to exchange the
	 * authorization code for the aouth2 tokens.
	 */
	private final String grantType;

	/**
	 * Supply here 'refresh_token', which means you request to refresh the
	 * token.
	 */
	private final String grantTypeRefreshToken;

	private AuthenticationParameters(ConfigurationBuilder builder) {
		oAuthAuthorizationUri = builder.oAuthAuthorizationUri;
		oAuthTokenUri = builder.oAuthTokenUri;
		apiUri = builder.apiUri;
		redirectUri = builder.redirectUri;
		responseType = builder.responseType;
		state = builder.state;
		clientId = builder.clientId;
		clientSecret = builder.clientSecret;
		scope = builder.scope;
		grantType = builder.grantType;
		grantTypeRefreshToken = builder.grantTypeRefreshToken;
	}

	public static class ConfigurationBuilder {

		private String redirectUri;
		private String clientId;
		private String clientSecret;
		private String state;
		private String oAuthAuthorizationUri;
		private String oAuthTokenUri;
		private String apiUri;
		private String responseType;
		private String scope;
		private String grantType;
		private String grantTypeRefreshToken;

		public ConfigurationBuilder()
		{
			this
				.withOAuthAuthorizationUri(DEFAULT_AUTH_URI)
				.withOAuthTokenUri(DEFAULT_TOKEN_URI)
				.withApiUri(DEFAULT_API_URI)
				.withResponseType(DEFAULT_RESPONSE_TYPE)
				.withScope(DEFAULT_SCOPE)
				.withGrantType(DEFAULT_GRANT_TYPE)
				.withGrantTypeRefreshToken(DEFAULT_GRANT_TYPE_REFRESH)
				.withState(nextState());
		}

		public ConfigurationBuilder withRedirectUri(String redirectUri) {
			this.redirectUri = redirectUri;
			return this;
		}

		public ConfigurationBuilder withClientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public ConfigurationBuilder withClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
			return this;
		}

		public ConfigurationBuilder withState(String state) {
			this.state = state;
			return this;
		}

		public ConfigurationBuilder withOAuthAuthorizationUri(String oAuthAuthorizationUri) {
			this.oAuthAuthorizationUri = oAuthAuthorizationUri;
			return this;
		}

		public ConfigurationBuilder withOAuthTokenUri(String oAuthTokenUri) {
			this.oAuthTokenUri = oAuthTokenUri;
			return this;
		}

		public ConfigurationBuilder withApiUri(String apiUri) {
			this.apiUri = apiUri;
			return this;
		}

		public ConfigurationBuilder withResponseType(String responseType) {
			this.responseType = responseType;
			return this;
		}

		public ConfigurationBuilder withScope(String scope) {
			this.scope = scope;
			return this;
		}

		public ConfigurationBuilder withGrantType(String grantType) {
			this.grantType = grantType;
			return this;
		}

		public ConfigurationBuilder withGrantTypeRefreshToken(String grantTypeRefreshToken) {
			this.grantTypeRefreshToken = grantTypeRefreshToken;
			return this;
		}

		public AuthenticationParameters build() {
			return new AuthenticationParameters(this);
		}

		/**
		 * Returns MD5 hash from random string
		 */
		public String nextState() {
			SecureRandom random = new SecureRandom();
			String randomStr = new BigInteger(130, random).toString(32);
			
			MessageDigest messageDigest;
			byte[] resultByte = null;
			try {
				messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.update(randomStr.getBytes("UTF8"));
				resultByte = messageDigest.digest();
			} catch (Exception e) {
				log.debug("Error generate md5 hash code", e);
				e.printStackTrace();
			}
			return new String(Hex.encodeHex(resultByte));
		}
	}

	public String getOAuthAuthorizationUri() {
		return oAuthAuthorizationUri;
	}

	public String getOAuthTokenUri() {
		return oAuthTokenUri;
	}

	public String getApiUri() {
		return apiUri;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public String getResponseType() {
		return responseType;
	}

	public String getState() {
		return state;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getScope() {
		return scope;
	}

	public String getGrantType() {
		return grantType;
	}

	public String getGrantTypeRefreshToken() {
		return grantTypeRefreshToken;
	}

}
