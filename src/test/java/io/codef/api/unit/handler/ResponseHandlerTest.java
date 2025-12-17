package io.codef.api.unit.handler;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.dto.EasyCodefResponse;
import io.codef.api.dto.EasyCodefTokenResponse;
import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;
import io.codef.api.handler.ResponseHandler;

@DisplayName("[Handler Layer] ResponseHandler Test")
public class ResponseHandlerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	@DisplayName("[Success] data가 List 형식인 경우 성공")
	void parseData_array() throws JsonProcessingException {
		List<Map<String, Object>> data = new ArrayList<>();
		Map<String, Object> item = new HashMap<>();
		item.put("k", "v");
		data.add(item);

		Map<String, Object> request = new HashMap<>();
		request.put("result", createSuccessResultMap());
		request.put("data", data);

		String httpResponse = mapper.writeValueAsString(request);

		EasyCodefResponse response = ResponseHandler.processResponse(httpResponse, EasyCodefResponse.class);

		assertThat(response.getData()).isInstanceOf(List.class);
	}

	@Nested
	@DisplayName("[Throw Exceptions] 예외처리가 정상 동작하면 성공")
	class ExceptionCases {

		@Test
		@DisplayName("[Exception] 토큰 응답이 문자열 \"null\"인 경우 OAUTH_ERROR 예외처리")
		void handleTokenResponse_null() {
			String httpResponse = "null";

			CodefException exception = assertThrows(CodefException.class,
				() -> ResponseHandler.processResponse(httpResponse, EasyCodefTokenResponse.class));

			assertEquals(CodefError.OAUTH_ERROR, exception.getCodefError());
		}

		@Test
		@DisplayName("[Exception] result 필드가 없는 경우 PARSE_ERROR 예외처리")
		void parseResult_missing_result() throws JsonProcessingException {
			assertParseError(new HashMap<>());
		}

		@Test
		@DisplayName("[Exception] result 필드 값이 null인 경우 PARSE_ERROR 예외처리")
		void parseResult_null_result() throws JsonProcessingException {
			Map<String, Object> request = new HashMap<>();
			request.put("result", null);

			assertParseError(request);
		}

		@Test
		@DisplayName("[Exception] data 필드가 없는 경우 PARSE_ERROR 예외처리")
		void parseData_missing() throws JsonProcessingException {
			Map<String, Object> request = new HashMap<>();
			request.put("result", createSuccessResultMap());

			assertParseError(request);
		}

		@Test
		@DisplayName("[Exception] data 필드 값이 null인 경우 PARSE_ERROR 예외처리")
		void parseData_null() throws JsonProcessingException {
			Map<String, Object> response = new HashMap<>();
			response.put("result", createSuccessResultMap());
			response.put("data", null);

			assertParseError(response);
		}

		@Test
		@DisplayName("[Exception] data 필드 형식이 Object나 Array이 아닌 경우 PARSE_ERROR 예외처리")
		void parseData_not_object_or_array() throws JsonProcessingException {
			Map<String, Object> response = new HashMap<>();
			response.put("result", createSuccessResultMap());
			response.put("data", "abc");

			assertParseError(response);
		}
	}

	private Map<String, Object> createSuccessResultMap() {
		Map<String, Object> result = new HashMap<>();
		result.put("code", "CF-00000");
		return result;
	}

	private void assertParseError(Map<String, Object> responseMap) throws JsonProcessingException {
		String httpResponse = mapper.writeValueAsString(responseMap);

		CodefException exception = assertThrows(CodefException.class,
			() -> ResponseHandler.processResponse(httpResponse, EasyCodefResponse.class));

		assertEquals(CodefError.PARSE_ERROR, exception.getCodefError());
	}
}
