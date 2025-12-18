package io.codef.api.unit.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.http.HttpRequest;

@DisplayName("[Unit][HTTP] CodefHttpRequest Test")
public class HttpRequestTest {

	@Test
	@DisplayName("[Success] CodefHttpRequest 생성자 테스트")
	void constructor_success() throws JsonProcessingException {
		String url = "https://api.codef.io/v1/test";

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer token");

		Map<String, Object> bodyMap = new HashMap<>();
		bodyMap.put("param", "value");
		String body = new ObjectMapper().writeValueAsString(bodyMap);

		HttpRequest request = new HttpRequest(url, headers, body);

		assertAll(
			() -> assertNotNull(request),
			() -> assertEquals(url, request.getUrl()),
			() -> assertEquals(headers, request.getHeaders()),
			() -> assertEquals(body, request.getBody()));
	}
}
