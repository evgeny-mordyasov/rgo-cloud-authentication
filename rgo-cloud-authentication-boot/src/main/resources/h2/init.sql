CREATE TABLE IF NOT EXISTS CLIENT (
    ENTITY_ID          IDENTITY PRIMARY KEY,
    SURNAME            VARCHAR(128) NOT NULL,
    NAME               VARCHAR(128) NOT NULL,
    PATRONYMIC         VARCHAR(128) NOT NULL,
    MAIL               VARCHAR(128) NOT NULL,
    PASSWORD           VARCHAR(512) NOT NULL,
    CREATED_DATE       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LAST_MODIFIED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IS_ACTIVE          BOOLEAN DEFAULT FALSE,
    ROLE               VARCHAR(16) DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS CONFIRMATION_TOKEN (
    ENTITY_ID   IDENTITY PRIMARY KEY,
    TOKEN       VARCHAR(6) NOT NULL,
    EXPIRY_DATE TIMESTAMP NOT NULL,
    CLIENT_ID   BIGINT NOT NULL REFERENCES CLIENT(ENTITY_ID)
);