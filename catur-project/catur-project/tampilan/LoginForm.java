package tampilan;

import database.Database;
import database.UserDAO;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

public class LoginForm extends JFrame {
    final Tema.Teks tfNama = new Tema.Teks(16);
    final Tema.Sandi pfPass = new Tema.Sandi(16);
    final JLabel lblStatus = new JLabel(" ", SwingConstants.CENTER);

    LoginForm() {
        super("Catur - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Tema.Latar latar = new Tema.Latar();
        latar.setLayout(new GridBagLayout());
        latar.setPreferredSize(new Dimension(480, 640));

        Tema.Kartu kartu = new Tema.Kartu();
        kartu.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        int y = 0;

        g.gridy = y++;
        g.insets = new Insets(0, 0, 22, 0);
        kartu.add(Tema.header("MASTER BIDAK", "Masuk untuk mulai bermain"), g);

        g.gridy = y++;
        g.insets = new Insets(0, 0, 4, 0);
        kartu.add(Tema.label("Nama"), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 14, 0);
        kartu.add(tfNama, g);

        g.gridy = y++;
        g.insets = new Insets(0, 0, 4, 0);
        kartu.add(Tema.label("Password"), g);
        g.gridy = y++;
        g.insets = new Insets(0, 0, 6, 0);
        kartu.add(pfPass, g);

        lblStatus.setFont(Tema.font(Font.PLAIN, 12));
        g.gridy = y++;
        g.insets = new Insets(4, 0, 8, 0);
        kartu.add(lblStatus, g);

        Tema.Tombol btnLogin = Tema.tombolUtama("Masuk");
        g.gridy = y++;
        g.insets = new Insets(0, 0, 16, 0);
        kartu.add(btnLogin, g);

        JLabel belum = new JLabel("Belum punya akun?", SwingConstants.CENTER);
        belum.setForeground(Tema.TERANG);
        belum.setFont(Tema.font(Font.PLAIN, 12));
        g.gridy = y++;
        g.insets = new Insets(0, 0, 6, 0);
        kartu.add(belum, g);

        Tema.Tombol btnDaftar = Tema.tombolSekunder("Buat Akun Baru");
        g.gridy = y++;
        g.insets = new Insets(0, 0, 0, 0);
        kartu.add(btnDaftar, g);

        latar.add(kartu);

        btnLogin.addActionListener(e -> login());
        btnDaftar.addActionListener(e -> daftar());
        pfPass.addActionListener(e -> login());
        tfNama.addActionListener(e -> pfPass.requestFocus());

        setContentPane(latar);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    void login() {
        String nama = tfNama.getText().trim();
        String pass = new String(pfPass.getPassword());
        if (nama.isEmpty() || pass.isEmpty()) {
            pesan("Nama dan password wajib diisi.", false);
            return;
        }
        try {
            int id = UserDAO.login(nama, pass);
            if (id > 0) bukaGame(nama);
            else pesan("Nama atau password salah.", false);
        } catch (SQLException e) {
            pesan("Gagal terhubung ke database.", false);
            e.printStackTrace();
        }
    }

    void daftar() {
        new DaftarForm(this).setVisible(true);
    }

    void pesan(String teks, boolean sukses) {
        Tema.tampilkanStatus(lblStatus, teks, sukses, 260);
    }

    void bukaGame(String nama) {
        dispose();
        JFrame f = new JFrame("Catur");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new CaturAwal(f));
        f.pack();
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            Database.init();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database gagal dibuat:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}