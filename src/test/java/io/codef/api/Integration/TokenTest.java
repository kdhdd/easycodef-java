package io.codef.api.Integration;

import static io.codef.api.fixture.CodefCredentialFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.codef.api.http.CodefHttpClient;
import io.codef.api.service.EasyCodefOAuthService;

@DisplayName("[Integration] Token Test")
public class TokenTest {

	private EasyCodefOAuthService oAuthService;

	@BeforeEach
	void setUp() {
		CodefHttpClient httpClient = new CodefHttpClient();
		this.oAuthService = new EasyCodefOAuthService(httpClient);
	}

	@Test
	void validateExpiring_isTokenExpiringSoon_with_reflection() throws Exception {
		LocalDateTime refreshedExpiresAt = invokeGetAccessTokenWithExpiresAt(LocalDateTime.now());

		assertTrue(refreshedExpiresAt.isAfter(LocalDateTime.now().plusHours(24)));
	}

	@Test
	void validateExpiring_null_with_reflection() throws Exception {
		LocalDateTime refreshedExpiresAt = invokeGetAccessTokenWithExpiresAt(null);

		assertTrue(refreshedExpiresAt.isAfter(LocalDateTime.now().plusHours(24)));
	}

	private LocalDateTime invokeGetAccessTokenWithExpiresAt(LocalDateTime initialExpiresAt) throws Exception {
		Class<?> tokenClass = Class.forName("io.codef.api.EasyCodefToken");

		Constructor<?> constructor = tokenClass.getDeclaredConstructor(String.class, String.class,
			EasyCodefOAuthService.class);
		constructor.setAccessible(true);

		Object tokenInstance = constructor.newInstance(clientId, clientSecret, oAuthService);

		Field expiresAtField = tokenClass.getDeclaredField("expiresAt");
		expiresAtField.setAccessible(true);
		expiresAtField.set(tokenInstance, initialExpiresAt);

		Method getAccessTokenMethod = tokenClass.getDeclaredMethod("getAccessToken");
		getAccessTokenMethod.setAccessible(true);
		getAccessTokenMethod.invoke(tokenInstance);

		return (LocalDateTime)expiresAtField.get(tokenInstance);
	}
}
