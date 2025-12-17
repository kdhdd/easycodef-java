package io.codef.api.Integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;

public class EasyCodefTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final String clientId = System.getenv("CLIENT_ID");
	private static final String clientSecret = System.getenv("CLIENT_SECRET");
	private static final String publicKey = System.getenv("PUBLIC_KEY");

	private static EasyCodef easyCodef;

	@BeforeAll
	static void setUp() {
		easyCodef = new EasyCodef();
		easyCodef.setClientInfoForDemo(clientId, clientSecret);
		easyCodef.setPublicKey(publicKey);
	}

	@Test
	void requestProduct_success() throws JsonProcessingException {
		String productUrl = "/v1/kr/etc/mt/car-history/flooded-vehicle";
		Map<String, Object> parameterMap = new HashMap<>();
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
	void requestToken_validExp() throws JsonProcessingException {
		String token = easyCodef.requestToken(EasyCodefServiceType.DEMO);

		long exp = extractExp(token);
		long now = Instant.now().getEpochSecond();

		assertAll(
			() -> assertTrue(exp > now)
		);
	}

	@Test
	void requestNewToken_updateExp() throws JsonProcessingException {
		String oldToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);
		String newToken = easyCodef.requestNewToken(EasyCodefServiceType.DEMO);

		long oldTokenExp = extractExp(oldToken);
		long newTokenExp = extractExp(newToken);
		long now = Instant.now().getEpochSecond();

		assertAll(
			() -> assertTrue(newTokenExp >= oldTokenExp),
			() -> assertTrue(newTokenExp > now)
		);
	}

	@Test
	@DisplayName("[Success] 토큰 요청 시 캐싱된 토큰 반환 검증")
	void requestToken_verifyLifeCycle() {
		String firstToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);
		String secondToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);

		assertAll(
			() -> assertSame(firstToken, secondToken)
		);
	}

	@Test
	@DisplayName("[Success] 신규 발급 요청 시 새로운 토큰 반환 검증")
	void requestNewToken_verifyLifeCycle() {
		String oldToken = easyCodef.requestToken(EasyCodefServiceType.DEMO);
		String newToken = easyCodef.requestNewToken(EasyCodefServiceType.DEMO);

		assertAll(
			() -> assertNotSame(oldToken, newToken)
		);
	}

	private long extractExp(String token) throws JsonProcessingException {
		String payloadBase64 = token.split("\\.")[1];
		String payloadJson = new String(Base64.getDecoder().decode(payloadBase64));

		Map<?, ?> payloadMap = mapper.readValue(payloadJson, Map.class);

		return ((Number) payloadMap.get("exp")).longValue();
	}
}
