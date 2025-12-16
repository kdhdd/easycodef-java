package io.codef.api.dto;

import java.util.HashMap;
import java.util.Map;

import io.codef.api.util.JsonUtil;

/**
 * CODEF API 응답을 표현하는 DTO 클래스
 *
 * @version 2.0.0
 */
public class EasyCodefResponse {

	private final Result result;
	private final Object data;
	private final Object extraInfo;

	private EasyCodefResponse(Result result, Object data, Object extraInfo) {
		this.result = result;
		this.data = data;
		this.extraInfo = extraInfo;
	}

	/**
	 * 전체 응답 요소를 지정하여 {@link EasyCodefResponse}를 생성
	 *
	 * @param result    메타 정보
	 * @param data      응답 데이터
	 * @param extraInfo 부가 정보
	 * @return 생성된 {@link EasyCodefResponse} 인스턴스
	 */
	public static EasyCodefResponse of(Result result, Object data, Object extraInfo) {
		return new EasyCodefResponse(result, data, extraInfo);
	}

	public Result getResult() {
		return result;
	}

	public Object getData() {
		return data;
	}

	public Object getExtraInfo() {
		return extraInfo;
	}

	/**
	 * 응답 전체를 JSON 문자열로 직렬화
	 *
	 * @return JSON 문자열 표현
	 */
	@Override
	public String toString() {
		Map<String, Object> root = new HashMap<>();

		root.put("result", result);
		root.put("data", data);

		if (extraInfo != null) {
			Map<String, Object> extraMap = JsonUtil.toMap(extraInfo);
			if (extraMap != null) {
				root.putAll(extraMap);
			}
		}

		return JsonUtil.toJson(root);
	}

	/**
	 * CODEF 응답의 result 영역을 표현하는 내부 클래스
	 *
	 * <p>
	 *     CODEF 응답의 {@code result} 필드를 그대로 매핑하며, <br>
	 *     응답 코드, 메시지, 트랜잭션 ID 정보를 포함
	 * </p>
	 */
	public static class Result {
		private String code;
		private String extraMessage;
		private String message;
		private String transactionId;

		public Result() {}

		/**
		 * 메타 정보 생성
		 *
		 * @param code          응답 코드
		 * @param extraMessage  추가 메시지
		 * @param message       기본 메시지
		 * @param transactionId CODEF 거래 식별자
		 */
		public Result(String code,
			String extraMessage,
			String message,
			String transactionId) {
			this.code = code;
			this.extraMessage = extraMessage;
			this.message = message;
			this.transactionId = transactionId;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setExtraMessage(String extraMessage) {
			this.extraMessage = extraMessage;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setTransactionId(String transactionId) {
			this.transactionId = transactionId;
		}

		public String getCode() {
			return code;
		}

		public String getExtraMessage() {
			return extraMessage;
		}

		public String getMessage() {
			return message;
		}

		public String getTransactionId() {
			return transactionId;
		}
	}
}
