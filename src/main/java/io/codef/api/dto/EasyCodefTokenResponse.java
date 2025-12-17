package io.codef.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CODEF OAuth 응답을 표현하는 DTO 클래스
 *
 * @version 2.0.0
 */
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
}
