package io.codef.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EasyCodefTokenResponse {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private int expiresIn;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("scope")
	private String scope;

	public String getAccessToken() {
		return accessToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getScope() {
		return scope;
	}
}
