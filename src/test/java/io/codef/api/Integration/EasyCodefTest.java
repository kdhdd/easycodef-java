package io.codef.api.Integration;

import static io.codef.api.fixture.CodefCredentialFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;

@DisplayName("[Integration] EasyCodef Test")
public class EasyCodefTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static EasyCodef easyCodef;

	@BeforeAll
	static void setUp() {
		easyCodef = new EasyCodef();
		easyCodef.setClientInfoForDemo(clientId, clientSecret);
		easyCodef.setPublicKey(publicKey);
	}

	@Nested
	@DisplayName("[isSuccessResponse] 생성이 정상적으로 완료되면 성공")
	class ResponseCases {

		@Test
		@DisplayName("[Success] 상품 요청 응답에 transactionId가 포함되면 성공")
		void requestProduct_success() throws JsonProcessingException {
			String productUrl = "/v1/kr/etc/mt/car-history/flooded-vehicle";
			HashMap<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("organization", "0100");
			parameterMap.put("carNo", "12가1234");

			String response = easyCodef.requestProduct(productUrl, EasyCodefServiceType.DEMO, parameterMap);
			JsonNode root = mapper.readTree(response);

			String transactionId = root.path("result").path("transactionId").asText();

			assertAll(
				() -> assertNotNull(response),
				() -> assertNotNull(transactionId));
		}

		@Test
		@DisplayName("[Success] 토큰 요청 시 exp가 현재 시간(now)보다 크면 성공")
		void requestToken_validExp() throws JsonProcessingException {
			String token = easyCodef.requestToken(EasyCodefServiceType.DEMO);

			long exp = extractExp(token);
			long now = Instant.now().getEpochSecond();

			assertTrue(exp > now);
		}

		@Test
		@DisplayName("[Success] 신규 토큰 발급 시 exp가 갱신되고 현재 시간(now)보다 크면 성공")
		void requestNewToken_updateExp() throws JsonProcessingException {
			String oldToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);
			String newToken = easyCodef.requestNewToken(EasyCodefServiceType.DEMO);

			long oldTokenExp = extractExp(oldToken);
			long newTokenExp = extractExp(newToken);
			long now = Instant.now().getEpochSecond();

			assertAll(
				() -> assertTrue(newTokenExp >= oldTokenExp),
				() -> assertTrue(newTokenExp > now));
		}

		@Test
		@DisplayName("[Success] 토큰 요청 시 캐싱된 토큰 반환 검증")
		void requestToken_verifyLifeCycle() {
			String firstToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);
			String secondToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);

			assertEquals(firstToken, secondToken);
		}

		@Test
		@DisplayName("[Success] 신규 발급 요청 시 새로운 토큰 반환 검증")
		void requestNewToken_verifyLifeCycle() {
			String oldToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);
			String newToken = easyCodef.requestNewToken(EasyCodefServiceType.DEMO);

			assertNotEquals(oldToken, newToken);
		}
	}

	@Nested
	@DisplayName("[Throw Exceptions] 예외처리가 정상 동작하면 성공")
	class ExceptionCases {

		@Test
		@DisplayName("[Exception] TwoWay 정보 없이 인증 요청 시 INVALID_2WAY_INFO 예외처리")
		void requestCertification_INVALID_2WAY_INFO() {
			String productUrl = "/v1/kr/etc/mt/car-history/flooded-vehicle";
			HashMap<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("organization", "0100");
			parameterMap.put("carNo", "12가1234");

			CodefException exception = assertThrows(CodefException.class,
				() -> easyCodef.requestCertification(productUrl, EasyCodefServiceType.DEMO, parameterMap));

			assertEquals(CodefError.INVALID_2WAY_INFO, exception.getCodefError());
		}
	}

	private long extractExp(String token) throws JsonProcessingException {
		String payloadBase64 = token.split("\\.")[1];
		String payloadJson = new String(Base64.getDecoder().decode(payloadBase64));

		Map<?, ?> payloadMap = mapper.readValue(payloadJson, Map.class);

		return ((Number)payloadMap.get("exp")).longValue();
	}
}
