package rgo.cloud.authentication.boot.storage.query;

public final class ClientQuery {
    private static final String TABLE_NAME = "CLIENT";

    private ClientQuery() {
    }

    public static String findAll() {
        return "SELECT * FROM " + TABLE_NAME;
    }

    public static String findById() {
        return "SELECT * FROM " + TABLE_NAME + " WHERE entity_id = :entity_id";
    }

    public static String findByMail() {
        return "SELECT * FROM " + TABLE_NAME + " WHERE mail = :mail";
    }

    public static String save() {
        return "INSERT INTO " + TABLE_NAME + "(surname, name, patronymic, mail, password) VALUES (:surname, :name, :patronymic, :mail, :password)";
    }

    public static String update() {
        return  "UPDATE " + TABLE_NAME + " " +
                "SET surname = :surname," +
                "    name = :name," +
                "    patronymic = :patronymic," +
                "    password = :password," +
                "    last_modified_date = :lmd " +
                "WHERE entity_id = :entity_id";
    }

    public static String updateStatus() {
        return  "UPDATE " + TABLE_NAME + " SET is_active = :active, last_modified_date = :lmd WHERE entity_id = :entity_id";
    }

    public static String resetPassword() {
        return  "UPDATE " + TABLE_NAME + " SET password = :password, last_modified_date = :lmd WHERE mail = :mail";
    }
}
