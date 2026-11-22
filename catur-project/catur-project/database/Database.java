package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private Database() { }

    private static final String URL =
        "jdbc:mysql://localhost:3306/chess?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "gabriel16";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Memastikan koneksi berhasil dan tabel tersedia
    public static void init() throws SQLException {
        String users = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nama VARCHAR(100) NOT NULL UNIQUE,
                jenis_kelamin VARCHAR(20) NOT NULL,
                tanggal_lahir DATE NULL,
                alamat TEXT NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )""";
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute(users);
        }
    }
}