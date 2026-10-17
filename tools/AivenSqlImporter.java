import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

/** Temporary, standalone importer. It is not part of Spring Boot startup. */
public final class AivenSqlImporter {
    private static final String DATABASE = "bd_techservices";
    private static final String SQL_FILE = "sql/01_init.sql";

    public static void main(String[] args) throws Exception {
        Map<String, String> env = loadEnv(Path.of(".env.aiven"));
        required(env, "DB_URL", "DB_USERNAME", "DB_PASSWORD", "AIVEN_TRUSTSTORE_URL", "AIVEN_TRUSTSTORE_PASSWORD");
        String url = env.get("DB_URL");
        Properties props = new Properties();
        props.setProperty("user", env.get("DB_USERNAME"));
        props.setProperty("password", env.get("DB_PASSWORD"));
        props.setProperty("sslMode", "VERIFY_IDENTITY");
        props.setProperty("trustCertificateKeyStoreUrl", env.get("AIVEN_TRUSTSTORE_URL"));
        props.setProperty("trustCertificateKeyStorePassword", env.get("AIVEN_TRUSTSTORE_PASSWORD"));
        props.setProperty("trustCertificateKeyStoreType", "PKCS12");

        String serverUrl = url.replaceFirst("(?i)(://[^/]+)(/[^?]*)?", "$1/");
        try (Connection server = DriverManager.getConnection(serverUrl, props)) {
            try (Statement s = server.createStatement()) {
                s.execute("CREATE DATABASE IF NOT EXISTS " + DATABASE);
            }
        }

        try (Connection db = DriverManager.getConnection(url, props)) {
            if (hasData(db, "clientes") || hasData(db, "usuarios_api")) {
                throw new IllegalStateException("La base remota ya contiene datos; importación cancelada sin modificarla.");
            }
            executeSqlFile(db, Path.of(SQL_FILE));
            int clientes = count(db, "clientes");
            int usuarios = count(db, "usuarios_api");
            if (clientes != 5 || usuarios != 2) throw new IllegalStateException("Conteos remotos inesperados.");
            System.out.println("clientes=" + clientes);
            System.out.println("usuarios=" + usuarios);
        }
    }

    private static boolean hasData(Connection c, String table) throws SQLException {
        if (!tableExists(c, table)) return false;
        return count(c, table) > 0;
    }
    private static boolean tableExists(Connection c, String table) throws SQLException {
        DatabaseMetaData m = c.getMetaData();
        try (ResultSet r = m.getTables(DATABASE, null, table, new String[]{"TABLE"})) { return r.next(); }
    }
    private static int count(Connection c, String table) throws SQLException {
        try (Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            r.next(); return r.getInt(1);
        }
    }
    private static void executeSqlFile(Connection c, Path path) throws Exception {
        StringBuilder statement = new StringBuilder();
        for (String line : Files.readAllLines(path)) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("--")) continue;
            statement.append(line).append('\n');
            if (t.endsWith(";")) {
                try (Statement s = c.createStatement()) { s.execute(statement.toString()); }
                statement.setLength(0);
            }
        }
    }
    private static Map<String, String> loadEnv(Path path) throws Exception {
        if (!Files.exists(path)) throw new IllegalStateException("Falta .env.aiven. Copia .env.aiven.example y completa la contraseña localmente.");
        Map<String, String> result = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            String t = line.trim(); if (t.isEmpty() || t.startsWith("#")) continue;
            int equals = t.indexOf('='); if (equals < 1) continue;
            String value = t.substring(equals + 1).trim();
            if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'")))) value = value.substring(1, value.length() - 1);
            result.put(t.substring(0, equals).trim(), value);
        }
        return result;
    }
    private static void required(Map<String, String> env, String... names) {
        for (String name : names) if (!env.containsKey(name) || env.get(name).isBlank()) throw new IllegalStateException("Completa " + name + " en .env.aiven.");
    }
}
