package io.codef.api.unit.util;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;
import io.codef.api.util.JsonUtil;

@DisplayName("[Util Layer] JsonUtil Unit Test")
public class JsonUtilTest {

	@Nested
	@DisplayName("[isSuccessResponse] 정상적으로 암호화하면 성공")
	class ResponseCases {

		@Test
		@DisplayName("[Success] Class 타입 변환하면 성공")
		void convertValue_class() {
			Map<String, Object> map = new HashMap<>();
			map.put("key", "value");

			JsonNode jsonNode = JsonUtil.convertValue(map, JsonNode.class);

			assertThat(jsonNode).isInstanceOf(JsonNode.class);
		}

		@Test
		@DisplayName("[Success] TypeReference 타입 변환하면 성공")
		void convertValue_typeReference() {
			Map<String, Object> map = new HashMap<>();
			map.put("key", "value");

			JsonNode jsonNode = JsonUtil.convertValue(map, new TypeReference<JsonNode>() {
			});

			assertThat(jsonNode).isInstanceOf(JsonNode.class);
		}

		@Test
		@DisplayName("[Success] Map 타입 변환하면 성공")
		void toMap_success() throws JsonProcessingException {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree("{\"key\":\"value\"}");

			Map<String, Object> map = JsonUtil.toMap(node);

			assertThat(map).isInstanceOf(Map.class);
		}
	}

	@Nested
	@DisplayName("[Throw Exception] 예외처리가 정상 동작하면 성공")
	class ExceptionCases {

		@Test
		@DisplayName("[Exception] 파라미터가 null인 경우 JSON_PARSE_ERROR 예외처리")
		void method_null() {
			assertAll(
				() -> assertNull(JsonUtil.toJson(null)),
				() -> assertNull(JsonUtil.fromJson(null, Map.class)),
				() -> assertNull(JsonUtil.convertValue(null, Map.class)),
				() -> assertNull(JsonUtil.convertValue(null, new TypeReference<Object>() {
				}))
			);
		}

		@Test
		@DisplayName("[Exception] 순환 구조를 직렬화 시도하면 JSON_PARSE_ERROR 예외처리")
		void toJson_selfReferenceMap() {
			Map<String, Object> map = new HashMap<>();
			map.put("self", map);

			CodefException exception = assertThrows(
				CodefException.class,
				() -> JsonUtil.toJson(map)
			);

			assertEquals(CodefError.JSON_PARSE_ERROR, exception.getCodefError());
		}

		@Test
		@DisplayName("[Exception] 유효하지 않은 문자열을 역직렬화 시도하면 JSON_PARSE_ERROR 예외처리")
		void fromJson_invalidJson() {
			String invalidJson = "{ \"name\": \"codef\" ";

			CodefException exception = assertThrows(
				CodefException.class,
				() -> JsonUtil.fromJson(invalidJson, JsonNode.class)
			);

			assertEquals(CodefError.JSON_PARSE_ERROR, exception.getCodefError());
		}
	}
}
