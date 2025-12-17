package io.codef.api.unit.root;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.codef.api.EasyCodefBuilder;
import io.codef.api.EasyCodefClient;
import io.codef.api.EasyCodefServiceType;

@DisplayName("[Root Layer] EasyCodefClient Test")
public class EasyCodefClientTest {

	private static final String clientId = System.getenv("CLIENT_ID");
	private static final String clientSecret = System.getenv("CLIENT_SECRET");
	private static final String publicKey = System.getenv("PUBLIC_KEY");

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

		assertAll(
			() -> assertEquals(publicKey, propsPublicKey)
		);
	}
}
