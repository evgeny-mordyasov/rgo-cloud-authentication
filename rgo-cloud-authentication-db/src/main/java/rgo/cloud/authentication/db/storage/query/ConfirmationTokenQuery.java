package rgo.cloud.authentication.db.storage.query;

public final class ConfirmationTokenQuery {
    private static final String TABLE_NAME = "confirmation_token";

    private ConfirmationTokenQuery() {
    }

    public static String findByClientId() {
        return  "SELECT cf.entity_id," +
                "       cf.token, " +
                "       cf.expiry_date, " +
                "       cf.client_id, " +
                "       c.entity_id AS client_id, " +
                "       c.surname AS client_surname, " +
                "       c.name AS client_name, " +
                "       c.patronymic AS client_patronymic, " +
                "       c.mail AS client_mail, " +
                "       c.password AS client_password, " +
                "       c.created_date AS client_created_date, " +
                "       c.last_modified_date AS client_last_modified_date, " +
                "       c.is_active AS client_is_active, " +
                "       c.role AS client_role " +
                "FROM " + TABLE_NAME + " AS cf " +
                "JOIN client AS c " +
                "   ON cf.client_id = c.entity_id" +
                "      AND c.entity_id = :client_id";
    }

    public static String save() {
        return  "INSERT INTO " + TABLE_NAME + "(token, expiry_date, client_id) " +
                "VALUES (:token, :expiry_date, :client_id)";
    }

    public static String update() {
        return  "UPDATE " + TABLE_NAME + " " +
                "SET token = :token, " +
                "    expiry_date = :expiry_date " +
                "WHERE client_id = :client_id";
    }
}
