package io.codef.api.unit.root;

import static org.junit.jupiter.api.Assertions.*;
import static io.codef.api.fixture.CodefCredentialFixture.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.codef.api.EasyCodefBuilder;
import io.codef.api.EasyCodefClient;
import io.codef.api.EasyCodefServiceType;

@DisplayName("[Unit][Root] EasyCodefClient Test")
public class EasyCodefClientTest {

	@Test
	@DisplayName("[Success] publicKey 정상 조회")
	void getPublicKey_success() {
		EasyCodefClient client = EasyCodefBuilder.builder()
			.serviceType(EasyCodefServiceType.DEMO)
			.clientId(clientId)
			.clientSecret(clientSecret)
			.publicKey(publicKey)
			.build();

		String propsPublicKey = client.getPublicKey();

		assertEquals(publicKey, propsPublicKey);
	}
}
