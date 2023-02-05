package rgo.cloud.authentication.boot;

import org.apache.commons.lang3.RandomStringUtils;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static rgo.cloud.common.spring.util.TestCommonUtil.*;

public final class EntityGenerator {
    private EntityGenerator() {
    }

    public static ConfirmationToken createRandomConfirmationToken(Client client) {
        return ConfirmationToken.builder()
                .client(client)
                .build();
    }

    public static ConfirmationToken createRandomFullConfirmationToken(Client client, int tokenLength) {
        return ConfirmationToken.builder()
                .token(generateToken(tokenLength))
                .client(client)
                .expiryDate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private static String generateToken(int tokenLength) {
        return RandomStringUtils.randomNumeric(tokenLength);
    }

    public static Client createRandomClient() {
        return Client.builder()
                .surname(randomString())
                .name(randomString())
                .patronymic(randomString())
                .mail(randomString())
                .password(randomString())
                .build();
    }
}
