package io.codef.api.handler;

import static io.codef.api.constant.CodefConstant.*;
import static io.codef.api.error.CodefError.*;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.codef.api.dto.EasyCodefResponse;
import io.codef.api.dto.EasyCodefTokenResponse;
import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;
import io.codef.api.util.JsonUtil;
import io.codef.api.util.UrlUtil;

/**
 * HTTP 응답을 {@link EasyCodefResponse}로 변환하는 유틸리티 클래스
 *
 * @version 2.0.0
 */
public class ResponseHandler {

	private ResponseHandler() {}

	/**
	 * HTTP 응답을 파싱하여 지정된 응답 타입으로 변환
	 *
	 * @param httpResponse CODEF 서버로부터 수신한 HTTP 응답
	 * @param responseType 변환할 응답 클래스 타입
	 * @param <T> 반환될 응답 타입
	 * @return 파싱된 응답 객체
	 */
	public static <T> T processResponse(String httpResponse, Class<T> responseType) {
		String decoded = UrlUtil.decode(httpResponse);
		JsonNode jsonNode = JsonUtil.fromJson(decoded, JsonNode.class);

		return responseType.equals(EasyCodefTokenResponse.class) ? responseType.cast(handleTokenResponse(jsonNode))
			: responseType.cast(handleProductResponse(jsonNode));
	}

	/**
	 * OAuth 토큰 API 응답 처리
	 *
	 * @param jsonNode 파싱된 JSON 응답 노드
	 * @return 파싱된 {@link EasyCodefTokenResponse}
	 */
	private static EasyCodefTokenResponse handleTokenResponse(JsonNode jsonNode) {
		return JsonUtil.convertValue(jsonNode, EasyCodefTokenResponse.class);
	}

	/**
	 * 상품 API 응답 처리
	 *
	 * <p>
	 *     {@code result}, {@code data}, 나머지 필드를 파싱하여 <br>
	 *     {@link EasyCodefResponse}의 각 필드로 매핑
	 * </p>
	 *
	 * @param jsonNode 파싱된 JSON 응답 노드
	 * @return 파싱된 {@link EasyCodefResponse}
	 */
	private static EasyCodefResponse handleProductResponse(JsonNode jsonNode) {
		EasyCodefResponse.Result result = parseResult(jsonNode);
		Object data = parseData(jsonNode);
		Object extraInfo = parseExtraInfo(jsonNode);

		return EasyCodefResponse.of(result, data, extraInfo);
	}

	/**
	 * 응답 JSON에서 {@code result} 필드 파싱
	 *
	 * @param jsonNode JSON 파싱된 응답 객체
	 * @return {@link EasyCodefResponse.Result} 인스턴스
	 * @throws CodefException {@code result} 필드가 없거나 파싱에 실패한 경우 {@link CodefError#PARSE_ERROR}
	 */
	private static EasyCodefResponse.Result parseResult(JsonNode jsonNode) {
		if (!jsonNode.has(RESULT.getValue())) {
			throw CodefException.from(PARSE_ERROR);
		}

		EasyCodefResponse.Result result = JsonUtil.convertValue(
			jsonNode.get(RESULT.getValue()),
			EasyCodefResponse.Result.class);

		if (result == null) {
			throw CodefException.from(PARSE_ERROR);
		}

		return result;
	}

	/**
	 * 응답 JSON에서 {@code data} 필드 파싱
	 *
	 * @param jsonNode JSON 파싱된 응답 객체
	 * @return {@code data}가 Object인 경우 {@link Map}, Array인 경우 {@link List}
	 * @throws CodefException {@code data} 필드가 없거나 형식이 예상과 다른 경우 {@link CodefError#PARSE_ERROR}
	 */
	private static Object parseData(JsonNode jsonNode) {
		if (!jsonNode.has(DATA.getValue())) {
			throw CodefException.from(PARSE_ERROR);
		}

		JsonNode dataNode = jsonNode.get(DATA.getValue());
		if (dataNode == null) {
			throw CodefException.from(PARSE_ERROR);
		}

		if (dataNode.isObject()) {
			return JsonUtil.convertValue(dataNode, Map.class);
		} else if (dataNode.isArray()) {
			return JsonUtil.convertValue(dataNode, List.class);
		}

		throw CodefException.from(PARSE_ERROR);
	}

	/**
	 * 응답 JSON에서 {@code result}, {@code data}를 제외한 부가 정보 필드 파싱
	 *
	 * @param jsonNode JSON 파싱된 응답 객체
	 * @return {@code result}, {@code data}를 제외한 나머지 필드 Map
	 */
	private static Object parseExtraInfo(JsonNode jsonNode) {
		Map<String, Object> jsonMap = JsonUtil.toMap(jsonNode);
		if (jsonMap != null) {
			jsonMap.remove(RESULT.getValue());
			jsonMap.remove(DATA.getValue());
		}

		return jsonMap;
	}
}
