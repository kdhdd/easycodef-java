package io.codef.api.Integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;
import io.codef.api.http.CodefHttpClient;
import io.codef.api.http.CodefHttpRequest;
import io.codef.api.http.HttpRequestBuilder;

public class CodefHttpClientTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private CodefHttpClient httpClient;

	@BeforeEach
	void setUp() {
		this.httpClient = new CodefHttpClient();
	}

	@Nested
	@DisplayName("[isSuccessResponse] 생성이 정상적으로 완료되면 성공")
	class ResponseCases {

		@Test
		@DisplayName("[Success] body가 존재하는 경우")
		void execute_with_body() throws JsonProcessingException {
			Map<String, Object> body = new HashMap<>();
			body.put("name", "CODEF");
			body.put("purpose", "http client test");

			String jsonBody = mapper.writeValueAsString(body);

			CodefHttpRequest request = HttpRequestBuilder.builder()
				.url("https://postman-echo.com/post")
				.header("Content-Type", "application/json")
				.header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
				.body(jsonBody)
				.build();

			String response = httpClient.execute(request);

			assertNotNull(response);
		}

		@Test
		@DisplayName("[Success] body가 존재하지 않는 경우")
		void execute_without_body() {
			CodefHttpRequest request = HttpRequestBuilder.builder()
				.url("https://postman-echo.com/post")
				.header("Content-Type", "application/json")
				.header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
				.build();

			String response = httpClient.execute(request);

			assertNotNull(response);
		}

		@Test
		@DisplayName("[Success] body가 비어있는 값인 경우")
		void execute_empty_body() {
			CodefHttpRequest request = HttpRequestBuilder.builder()
				.url("https://postman-echo.com/post")
				.header("Content-Type", "application/json")
				.header("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
				.body("")
				.build();

			String response = httpClient.execute(request);

			assertNotNull(response);
		}
	}

	@Nested
	@DisplayName("[Throw Exceptions] 예외처리가 정상 동작하면 성공")
	class ExceptionCases {

		@Test
		@DisplayName("[Exception] 해당 주소(서버)에 연결할 수 없는 경우")
		void getResponse_IOException() {
			CodefHttpRequest request = HttpRequestBuilder.builder()
				.url("http://127.0.0.1:59999")
				.build();

			CodefException exception = assertThrows(CodefException.class,
				() -> httpClient.execute(request));

			assertEquals(CodefError.IO_ERROR, exception.getCodefError());
		}
	}
}
