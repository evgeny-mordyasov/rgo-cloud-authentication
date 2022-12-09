package rgo.cloud.authentication.boot.storage.query;

public final class ConfirmationTokenQuery {
    private static final String TABLE_NAME = "CONFIRMATION_TOKEN";

    private ConfirmationTokenQuery() {
    }

    public static String findByClientIdAndToken() {
        return "SELECT * FROM " + TABLE_NAME + " WHERE client_id = :client_id AND token = :token";
    }

    public static String save() {
        return "INSERT INTO " + TABLE_NAME + "(token, expiry_date, client_id) VALUES (:token, :expiry_date, :client_id)";
    }
}
