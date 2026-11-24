package database;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.time.LocalDate;

public final class UserDAO {
    private UserDAO() { }

    // Mengembalikan null jika berhasil, atau pesan error jika gagal
    public static String daftar(String nama, String jenisKelamin, LocalDate tanggalLahir,
                                String alamat, String email, String password, String konfirmasi) {
        if (nama.length() < 3) return "Nama minimal 3 karakter.";
        if (jenisKelamin == null || jenisKelamin.isEmpty()) return "Pilih jenis kelamin.";
        if (tanggalLahir == null) return "Tanggal lahir tidak valid.";
        if (tanggalLahir.isAfter(LocalDate.now())) return "Tanggal lahir tidak boleh di masa depan.";
        if (alamat.isEmpty()) return "Alamat wajib diisi.";
        if (email.isEmpty() || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            return "Format email tidak valid.";
        if (password.length() < 4) return "Password minimal 4 karakter.";
        if (!password.equals(konfirmasi)) return "Konfirmasi password tidak cocok.";

        String sql = "INSERT INTO users (nama, jenis_kelamin, tanggal_lahir, alamat, email, password_hash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, jenisKelamin);
            ps.setDate(3, java.sql.Date.valueOf(tanggalLahir));
            ps.setString(4, alamat);
            ps.setString(5, email);
            ps.setString(6, buatHash(password));
            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            String pesan = e.getMessage();
            if (pesan != null && pesan.contains("Duplicate")) {
                int i = pesan.lastIndexOf("for key");        // bagian setelah ini menyebut kolom yang kembar
                String kunci = i >= 0 ? pesan.substring(i) : "";
                return kunci.contains("email") ? "Email sudah dipakai." : "Nama sudah dipakai.";
            }
            return "Gagal mendaftar: " + e.getMessage();
        } catch (Exception e) {
            return "Terjadi kesalahan: " + e.getMessage();
        }
    }

    // Mengembalikan id user jika berhasil, -1 jika nama/password salah
    public static int login(String nama, String password) throws SQLException {
        String sql = "SELECT id, password_hash FROM users WHERE nama = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && cocok(password, rs.getString("password_hash")))
                    return rs.getInt("id");
            }
        }
        return -1;
    }

    // ---------- hashing password (PBKDF2 + salt acak) ----------

    private static String buatHash(String password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" +
               Base64.getEncoder().encodeToString(hash);
    }

    private static boolean cocok(String password, String tersimpan) {
        try {
            String[] bagian = tersimpan.split(":");
            byte[] salt = Base64.getDecoder().decode(bagian[0]);
            byte[] hashAsli = Base64.getDecoder().decode(bagian[1]);
            return MessageDigest.isEqual(hashAsli, pbkdf2(password, salt));
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).getEncoded();
    }
}