package rgo.cloud.authentication.boot.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import rgo.cloud.authentication.boot.config.properties.DbProperties;
import rgo.cloud.authentication.boot.storage.repository.ClientRepository;
import rgo.cloud.authentication.boot.storage.repository.ConfirmationTokenRepository;
import rgo.cloud.common.spring.storage.DbTxManager;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig {

    @Bean
    @Profile("test")
    public DataSource h2() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("auth-h2")
                .addScript("classpath:h2/init.sql")
                .build();
    }

    @Bean
    public DataSource pg(DbProperties dbProp) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbProp.getUrl());
        ds.setSchema(dbProp.getScheme());
        ds.setUsername(dbProp.getUsername());
        ds.setPassword(dbProp.getPassword());
        ds.setMaximumPoolSize(dbProp.getMaxPoolSize());
        ds.setAutoCommit(false);

        return ds;
    }

    @Bean
    public DbTxManager dbTxManager(DataSource ds) {
        return new DbTxManager(ds);
    }

    @Bean
    public ClientRepository clientRepository(DbTxManager dbTxManager) {
        return new ClientRepository(dbTxManager);
    }

    @Bean
    public ConfirmationTokenRepository confirmationTokenRepository(DbTxManager dbTxManager) {
        return new ConfirmationTokenRepository(dbTxManager);
    }
}