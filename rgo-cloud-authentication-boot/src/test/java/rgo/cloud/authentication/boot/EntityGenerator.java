package rgo.cloud.authentication.boot;

import org.apache.commons.lang3.RandomStringUtils;
import rgo.cloud.authentication.internal.api.storage.Client;
import rgo.cloud.authentication.internal.api.storage.ConfirmationToken;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static rgo.cloud.authentication.boot.TestCommonUtil.generateId;
import static rgo.cloud.authentication.boot.TestCommonUtil.randomString;

public final class EntityGenerator {
    private static final int TOKEN_LENGTH = 6;

    private EntityGenerator() {
    }

    public static ConfirmationToken createRandomConfirmationToken() {
        return ConfirmationToken.builder()
                .token(generateToken())
                .clientId(generateId())
                .expiryDate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    private static String generateToken() {
        return RandomStringUtils.randomNumeric(TOKEN_LENGTH);
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
