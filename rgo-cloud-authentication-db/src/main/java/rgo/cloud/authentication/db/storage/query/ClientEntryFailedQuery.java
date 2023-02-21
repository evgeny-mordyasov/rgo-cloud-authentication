package rgo.cloud.authentication.db.storage.query;

public final class ClientEntryFailedQuery {
    private static final String TABLE_NAME = "client_entry_failed";

    private ClientEntryFailedQuery() {
    }

    public static String findByMail() {
        return  "SELECT * FROM " + TABLE_NAME +
                " WHERE mail = :mail";
    }

    public static String save() {
        return "INSERT INTO " + TABLE_NAME + "(mail) VALUES (:mail)";
    }

    public static String updateAttempts() {
        return  "UPDATE " + TABLE_NAME + " " +
                "SET attempts = :attempts " +
                "WHERE mail = :mail";
    }

    public static String resetAttempts() {
        return  "UPDATE " + TABLE_NAME + " " +
                "SET attempts = 0 " +
                "WHERE mail = :mail";
    }

    public static String block() {
        return  "UPDATE " + TABLE_NAME + " " +
                "SET block_date = :block_date," +
                "    attempts = 0 " +
                "WHERE mail = :mail";
    }
}
