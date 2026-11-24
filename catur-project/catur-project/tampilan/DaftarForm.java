package tampilan;

import database.UserDAO;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DaftarForm extends JDialog {
    static final String[] BULAN = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    final Tema.Teks tfNama = new Tema.Teks(16, "Nama kamu");
    final Tema.Pil rbLaki = new Tema.Pil("Laki-laki");
    final Tema.Pil rbPerempuan = new Tema.Pil("Perempuan");
    final Tema.Pilihan<Integer> cbTanggal = new Tema.Pilihan<>();
    final Tema.Pilihan<String> cbBulan = new Tema.Pilihan<>(BULAN);
    final Tema.Pilihan<Integer> cbTahun = new Tema.Pilihan<>();
    final Tema.Alamat taAlamat = new Tema.Alamat();
    final Tema.Teks tfEmail = new Tema.Teks(16, "nama@email.com");
    final Tema.Sandi pfPass = new Tema.Sandi(16, "Minimal 4 karakter");
    final Tema.Sandi pfKonfirmasi = new Tema.Sandi(16, "Ketik ulang password");
    final JLabel lblStatus = new JLabel(" ", SwingConstants.CENTER);

    DaftarForm(JFrame induk) {
        super(induk, "Catur - Pendaftaran", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (int i = 1; i <= 31; i++) cbTanggal.addItem(i);
        int tahunIni = LocalDate.now().getYear();
        for (int y = tahunIni; y >= tahunIni - 100; y--) cbTahun.addItem(y);
        cbTahun.setSelectedItem(tahunIni - 15);

        ButtonGroup grup = new ButtonGroup();
        grup.add(rbLaki);
        grup.add(rbPerempuan);

        // kolom kiri 
        JPanel kiri = kolom();
        baris(kiri, 0, "Tulis nama kamu", tfNama);

        JPanel panelJk = new JPanel(new GridLayout(1, 2, 8, 0));
        panelJk.setOpaque(false);
        panelJk.add(rbLaki);
        panelJk.add(rbPerempuan);
        baris(kiri, 1, "Kamu seorang...", panelJk);

        JPanel panelTgl = new JPanel(new GridLayout(1, 3, 6, 0));
        panelTgl.setOpaque(false);
        panelTgl.add(cbTanggal);
        panelTgl.add(cbBulan);
        panelTgl.add(cbTahun);
        baris(kiri, 2, "Kapan ulang tahunmu?", panelTgl);
        baris(kiri, 3, "Alamat email kamu", tfEmail);

        //kolom kanan
        JPanel kanan = kolom();
        baris(kanan, 0, "Tinggal di mana?", taAlamat);
        baris(kanan, 1, "Buat password", pfPass);
        baris(kanan, 2, "Ulangi password", pfKonfirmasi);

        //susun kartu
        Tema.Kartu kartu = new Tema.Kartu();
        kartu.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        g.insets = new Insets(0, 0, 22, 0);
        kartu.add(Tema.header("MASTER BIDAK", "Isi data singkat ini, lalu langsung main catur"), g);

        g.gridwidth = 1; g.gridy = 1; g.weightx = 1;
        g.insets = new Insets(0, 0, 0, 16);
        kartu.add(kiri, g);
        g.gridx = 1;
        g.insets = new Insets(0, 16, 0, 0);
        kartu.add(kanan, g);

        lblStatus.setFont(Tema.font(Font.PLAIN, 12));
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        g.insets = new Insets(16, 0, 10, 0);
        kartu.add(lblStatus, g);

        Tema.Tombol btnDaftar = Tema.tombolUtama("Gabung Sekarang");
        Tema.Tombol btnBatal = Tema.tombolSekunder("Nanti Saja");
        JPanel tombol = new JPanel(new GridLayout(1, 2, 12, 0));
        tombol.setOpaque(false);
        tombol.add(btnDaftar);
        tombol.add(btnBatal);
        g.gridy = 3;
        g.insets = new Insets(0, 0, 0, 0);
        kartu.add(tombol, g);

        btnDaftar.addActionListener(e -> daftar());
        btnBatal.addActionListener(e -> dispose());

        Tema.Latar latar = new Tema.Latar();
        latar.setLayout(new GridBagLayout());
        latar.setBorder(new EmptyBorder(24, 24, 24, 24));
        latar.add(kartu);

        setContentPane(latar);
        pack();
        setResizable(false);
        setLocationRelativeTo(induk);
    }

    JPanel kolom() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        return p;
    }

    void baris(JPanel kolom, int nomor, String teks, JComponent isi) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        g.gridy = nomor * 2;
        g.insets = new Insets(nomor == 0 ? 0 : 16, 4, 5, 0);
        kolom.add(Tema.label(teks), g);

        g.gridy = nomor * 2 + 1;
        g.insets = new Insets(0, 0, 0, 0);
        kolom.add(isi, g);
    }

    // Mengembalikan null kalau tanggalnya tidak ada di kalender (misalnya 31 Februari)
    LocalDate ambilTanggalLahir() {
        try {
            return LocalDate.of(
                (Integer) cbTahun.getSelectedItem(),
                cbBulan.getSelectedIndex() + 1,
                (Integer) cbTanggal.getSelectedItem());
        } catch (DateTimeException e) {
            return null;
        }
    }

    void daftar() {
        String jk = rbLaki.isSelected() ? "Laki-laki" : rbPerempuan.isSelected() ? "Perempuan" : "";
        String error = UserDAO.daftar(
            tfNama.getText().trim(), jk,
            ambilTanggalLahir(),
            taAlamat.getText().trim(),
            tfEmail.getText().trim(),
            new String(pfPass.getPassword()),
            new String(pfKonfirmasi.getPassword()));

        if (error == null) {
            JOptionPane.showMessageDialog(this, "Akun berhasil dibuat! Selamat bergabung, silakan login.");
            dispose();
        } else {
            Tema.tampilkanStatus(lblStatus, error, false, 480);
        }
    }
}