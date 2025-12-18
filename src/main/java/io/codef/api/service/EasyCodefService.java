package io.codef.api.service;

import io.codef.api.handler.ResponseHandler;
import io.codef.api.http.CodefHttpClient;
import io.codef.api.http.HttpRequest;

/**
 * CODEF API 요청 전송 공통 서비스 추상 클래스
 *
 * @version 2.0.0
 */
public abstract class EasyCodefService {

	private final CodefHttpClient httpClient;

	/**
	 * EasyCodefService 생성자
	 *
	 * @param httpClient HTTP 요청 실행을 담당
	 */
	EasyCodefService(CodefHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * CODEF API 요청 실행 및 결과 반환
	 *
	 * @param request 전송할 HTTP 요청 정보
	 * @param responseType 응답을 변환할 클래스 타입
	 * @param <T> 반환될 응답 타입
	 * @return 파싱 및 변환된 API 응답 객체
	 */
	<T> T sendRequest(HttpRequest request, Class<T> responseType) {
		String httpResponse = httpClient.execute(request);

		return ResponseHandler.processResponse(httpResponse, responseType);
	}
}
