package io.codef.api.unit.http;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.codef.api.error.CodefError;
import io.codef.api.error.CodefException;
import io.codef.api.http.CodefHttpClient;
import io.codef.api.http.HttpRequest;
import io.codef.api.http.HttpRequestBuilder;

@DisplayName("[Unit][HTTP] CodefHttpClient Test")
public class CodefHttpClientTest {

	private CodefHttpClient httpClient;

	@BeforeEach
	void setUp() {
		this.httpClient = new CodefHttpClient();
	}

	@Test
	@DisplayName("[Exception] URL 형식이 올바르지 않은 경우")
	void createConnection_IOException() {
		HttpRequest request = HttpRequestBuilder.builder()
			.url("no-protocol-url")
			.build();

		CodefException exception = assertThrows(CodefException.class,
			() -> httpClient.execute(request));

		assertEquals(CodefError.IO_ERROR, exception.getCodefError());
	}
}
