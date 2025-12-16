package io.codef.api.unit.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.http.CodefHttpClient;
import io.codef.api.http.CodefHttpRequest;
import io.codef.api.http.HttpRequestBuilder;

public class CodefHttpClientTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private CodefHttpRequest request;
	private CodefHttpClient httpClient;

	@BeforeEach
	void setUp() throws JsonProcessingException {
		this.httpClient = new CodefHttpClient();

		Map<String, Object> body = new HashMap<>();
		body.put("name", "CODEF");
		body.put("purpose", "http client test");

		String jsonBody = mapper.writeValueAsString(body);

		this.request = HttpRequestBuilder.builder()
			.url("https://postman-echo.com/post")
			.header("Content-Type", "application/json")
			.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
			.body(jsonBody)
			.build();
	}

	@Test
	void execute_success() {
		String response = httpClient.execute(request);

		System.out.println(response);

		assertNotNull(response);
	}
}
