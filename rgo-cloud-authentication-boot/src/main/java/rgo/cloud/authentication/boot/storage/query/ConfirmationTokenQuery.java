package rgo.cloud.authentication.boot.storage.query;

public final class ConfirmationTokenQuery {
    private static final String TABLE_NAME = "CONFIRMATION_TOKEN";

    private ConfirmationTokenQuery() {
    }

    public static String findByClientId() {
        return "SELECT cf.entity_id," +
                "      cf.token, " +
                "      cf.expiry_date, " +
                "      cf.client_id, " +
                "      c.entity_id as client_id, " +
                "      c.surname as client_surname, " +
                "      c.name as client_name, " +
                "      c.patronymic as client_patronymic, " +
                "      c.mail as client_mail, " +
                "      c.password as client_password, " +
                "      c.created_date as client_created_date, " +
                "      c.last_modified_date as client_last_modified_date, " +
                "      c.is_active as client_is_active, " +
                "      c.role as client_role " +
                "FROM " + TABLE_NAME + " as cf " +
                "JOIN CLIENT as c ON cf.client_id = c.entity_id " +
                "WHERE client_id = :client_id";
    }

    public static String save() {
        return "INSERT INTO " + TABLE_NAME + "(token, expiry_date, client_id) VALUES (:token, :expiry_date, :client_id)";
    }

    public static String update() {
        return "UPDATE " + TABLE_NAME + " SET token = :token, expiry_date = :expiry_date WHERE client_id = :client_id";
    }
}
