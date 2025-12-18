package io.codef.api.unit.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.dto.EasyCodefResponse;

@DisplayName("[Unit][DTO] EasyCodefResponse Test")
public class EasyCodefResponseTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	private EasyCodefResponse.Result result;
	private Map<String, Object> data;
	private Map<String, Object> extraInfo;

	@BeforeEach
	void setup() {
		this.result = new EasyCodefResponse.Result(
			"CF-00000", "", "성공", "test-transactionId");
		this.data = Collections.singletonMap("data", "dataVal");
		this.extraInfo = Collections.singletonMap("extraInfo", "extraVal");
	}

	@Nested
	@DisplayName("[FactoryMethod] 정적 팩토리 메서드 / 생성자 테스트")
	class FactoryMethod {

		@Test
		@DisplayName("[Success] of 팩토리 메서드가 정상동작하면 성공")
		void of_success() {
			EasyCodefResponse response = EasyCodefResponse.of(result, data, extraInfo);

			assertAll(
				() -> assertNotNull(response),
				() -> assertEquals(result, response.getResult()),
				() -> assertEquals(data, response.getData()),
				() -> assertEquals(extraInfo, response.getExtraInfo()));
		}
	}

	@Nested
	@DisplayName("[isSuccessResponse] 정상적으로 완료되면 성공")
	class ResponseCases {

		@Test
		@DisplayName("[Success] extraInfo가 정상적으로 포함된 경우")
		void toString_with_extraInfo() throws JsonProcessingException {
			EasyCodefResponse response = EasyCodefResponse.of(result, data, extraInfo);

			String jsonString = response.toString();
			Map<?, ?> jsonMap = mapper.readValue(jsonString, Map.class);

			assertAll(
				() -> assertTrue(jsonMap.containsKey("result")),
				() -> assertTrue(jsonMap.containsKey("data")),
				() -> assertTrue(jsonMap.containsKey("extraInfo")));
		}

		@Test
		@DisplayName("[Success] extraInfo 값이 비어있을 경우")
		void toString_without_extraInfo() throws JsonProcessingException {
			Map<String, Object> extraInfo = Collections.emptyMap();

			EasyCodefResponse response = EasyCodefResponse.of(result, data, extraInfo);

			String jsonString = response.toString();
			Map<?, ?> jsonMap = mapper.readValue(jsonString, Map.class);

			assertAll(
				() -> assertTrue(jsonMap.containsKey("result")),
				() -> assertTrue(jsonMap.containsKey("data")),
				() -> assertFalse(jsonMap.containsKey("extraInfo")));
		}

		@Test
		@DisplayName("[Success] extraInfo가 null인 경우")
		void toString_null_extraInfo() throws JsonProcessingException {
			EasyCodefResponse response = EasyCodefResponse.of(result, data, null);

			String jsonString = response.toString();
			Map<?, ?> jsonMap = mapper.readValue(jsonString, Map.class);

			assertAll(
				() -> assertNull(response.getExtraInfo()),
				() -> assertTrue(jsonMap.containsKey("result")),
				() -> assertTrue(jsonMap.containsKey("data")),
				() -> assertFalse(jsonMap.containsKey("extraInfo")));
		}
	}
}
