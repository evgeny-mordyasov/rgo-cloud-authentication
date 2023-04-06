package rgo.cloud.authentication.db.utils;

import org.apache.commons.lang3.RandomStringUtils;
import rgo.cloud.authentication.db.api.entity.Client;
import rgo.cloud.authentication.db.api.entity.ClientEntryFailed;
import rgo.cloud.authentication.db.api.entity.ConfirmationToken;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static rgo.cloud.common.spring.util.TestCommonUtil.*;

public final class EntityGenerator {
    private static final int TOKEN_LENGTH = 6;

    private EntityGenerator() {
    }

    public static ConfirmationToken createRandomConfirmationToken(Client client) {
        return ConfirmationToken.builder()
                .client(client)
                .build();
    }

    public static ConfirmationToken createRandomFullConfirmationToken(Client client) {
        return ConfirmationToken.builder()
                .token(generateToken())
                .client(client)
                .expiryDate(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }

    public static ConfirmationToken createRandomFullConfirmationToken(Client client, LocalDateTime date) {
        return ConfirmationToken.builder()
                .token(generateToken())
                .client(client)
                .expiryDate(date)
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

    public static ClientEntryFailed createRandomClientEntryFailed() {
        return ClientEntryFailed.builder()
                .mail(randomString())
                .build();
    }
}
