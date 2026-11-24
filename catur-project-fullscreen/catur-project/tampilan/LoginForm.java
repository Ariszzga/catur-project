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
    final JPanel halaman = new JPanel(new CardLayout());   // isi jendela: halaman "login" / "daftar"
    DaftarForm halamanDaftar;
    boolean diDaftar = false;

    LoginForm() {
        super("Catur - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Layar.siapkan(this);              // layar penuh + tombol F11

        Tema.Latar latar = new Tema.Latar();
        latar.setLayout(new GridBagLayout());

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

        // Mode layar penuh tidak punya tombol X, jadi beri petunjuk cara keluar
        JLabel petunjuk = new JLabel("F11: jendela biasa   |   ESC: keluar", SwingConstants.CENTER);
        petunjuk.setForeground(Tema.TERANG);
        petunjuk.setFont(Tema.font(Font.PLAIN, 11));
        g.gridy = y++;
        g.insets = new Insets(18, 0, 0, 0);
        kartu.add(petunjuk, g);

        latar.add(kartu);

        btnLogin.addActionListener(e -> login());
        btnDaftar.addActionListener(e -> daftar());
        pfPass.addActionListener(e -> login());
        tfNama.addActionListener(e -> pfPass.requestFocus());

        halaman.add(latar, "login");
        setContentPane(halaman);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "keluar");
        getRootPane().getActionMap().put("keluar", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (diDaftar) kembaliKeLogin();      // ESC di halaman daftar = kembali ke login
                else konfirmasiKeluar();
            }
        });
    }

    void konfirmasiKeluar() {
        int p = JOptionPane.showConfirmDialog(this, "Keluar dari aplikasi?", "Keluar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (p == JOptionPane.YES_OPTION) System.exit(0);
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

    // Pindah ke halaman daftar di jendela yang sama (form dibuat baru supaya isian bersih)
    void daftar() {
        if (halamanDaftar != null) halaman.remove(halamanDaftar);
        halamanDaftar = new DaftarForm(this::kembaliKeLogin);
        halaman.add(halamanDaftar, "daftar");
        ((CardLayout) halaman.getLayout()).show(halaman, "daftar");
        diDaftar = true;
    }

    void kembaliKeLogin() {
        ((CardLayout) halaman.getLayout()).show(halaman, "login");
        diDaftar = false;
        tfNama.requestFocusInWindow();
    }

    void pesan(String teks, boolean sukses) {
        Tema.tampilkanStatus(lblStatus, teks, sukses, 260);
    }

    void bukaGame(String nama) {
        CaturAwal.buka();      // jendela game dibuat lebih dulu, baru login ditutup
        dispose();
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