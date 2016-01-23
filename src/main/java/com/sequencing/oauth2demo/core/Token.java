package com.sequencing.oauth2demo.core;

/**
 * Class that defines token attributes needed for oauth authentication 
 */
public class Token
{
	private String accessToken;
	private String refreshToken;
	private long lifetime = 0;

	public Token(String accessToken, String refreshToken, long lifetime) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.lifetime = lifetime;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public long getLifeTime() {
		return lifetime;
	}
}
