package io.codef.api;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.commons.codec.binary.Base64;

import io.codef.api.dto.EasyCodefTokenResponse;
import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;
import io.codef.api.service.EasyCodefOAuthService;
import io.codef.api.util.AuthorizationUtil;

/**
 * CODEF OAuth Access Token 관리를 위한 클래스
 *
 * @version 2.0.0
 */
public class EasyCodefToken {

	private final EasyCodefOAuthService oAuthService;
	private final String oauthToken;

	private String accessToken;
	private LocalDateTime expiresAt;

	/**
	 * EasyCodefToken 생성자
	 *
	 * <p>
	 *     OAuth 토큰을 생성하고, Access Token을 발급하여 초기화
	 * </p>
	 *
	 * @param clientId        CODEF 클라이언트 아이디
	 * @param clientSecret    CODEF 클라이언트 시크릿
	 * @param oAuthService    OAuth 토큰 발급 요청 담당 서비스
	 */
	EasyCodefToken(String clientId, String clientSecret, EasyCodefOAuthService oAuthService) {
		this.oauthToken = encodeClientCredentials(clientId, clientSecret);
		this.oAuthService = oAuthService;

		requestAccessToken();
	}

	/**
	 * 유효한 Access Token 반환
	 *
	 * @return "Bearer {accessToken}" 형식의 인증 문자열
	 */
	String getBearerAccessToken() {
		validateExpiring();

		return AuthorizationUtil.createBearerAuth(accessToken);
	}

	/**
	 * 저장된 Access Token 반환
	 *
	 * @return Access Token 문자열
	 */
	String getAccessToken() {
		validateExpiring();

		return accessToken;
	}

	/**
	 * 신규 Access Token 발급 후 반환
	 *
	 * @return 신규 Access Token 문자열
	 */
	String forceNewAccessToken() {
		requestAccessToken();

		return accessToken;
	}

	/**
	 * Access Token 만료 여부 검증, 필요시 재발급
	 */
	private void validateExpiring() {
		if (expiresAt == null || isTokenExpiringSoon(expiresAt)) {
			requestAccessToken();
		}
	}

	/**
	 * 클라이언트 아이디, 시크릿을 이용해 OAuth 토큰 생성
	 *
	 * @param clientId        CODEF 클라이언트 아이디
	 * @param clientSecret    CODEF 클라이언트 시크릿
	 * @return Base64 인코딩된 OAuth 토큰 문자열
	 */
	private String encodeClientCredentials(String clientId, String clientSecret) {
		String auth = clientId + ":" + clientSecret;
		byte[] authEncBytes = Base64.encodeBase64(auth.getBytes());

		return new String(authEncBytes, StandardCharsets.UTF_8);
	}

	/**
	 * CODEF OAuth API로부터 Access Token 발급하여 초기화
	 */
	private void requestAccessToken() {
		String basicToken = AuthorizationUtil.createBasicAuth(oauthToken);
		EasyCodefTokenResponse response = oAuthService.requestToken(basicToken);

		this.accessToken = response.getAccessToken();
		this.expiresAt = LocalDateTime.now()
			.plusSeconds(response.getExpiresIn());
	}

	/**
	 * 토큰 만료 시간 임박 여부 확인
	 *
	 * @param expiry 토큰 만료 시각
	 * @return 만료까지 24시간 미만이면 {@code true}
	 */
	private boolean isTokenExpiringSoon(LocalDateTime expiry) {
		return expiry.isBefore(LocalDateTime.now().plusHours(24));
	}
}
