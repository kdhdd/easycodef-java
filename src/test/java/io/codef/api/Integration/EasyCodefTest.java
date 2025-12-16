package io.codef.api.Integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
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
}
