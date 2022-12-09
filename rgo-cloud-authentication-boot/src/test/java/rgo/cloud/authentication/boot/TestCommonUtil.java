package rgo.cloud.authentication.boot;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class TestCommonUtil {
    private TestCommonUtil() {
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static Long generateId() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static void runScript(String path, DataSource ds) {
        DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(new ClassPathResource(path)), ds);
    }
}
