package tampilan;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import mekanik.Papan;
import mekanik.Skak;

public class CaturAwal extends JPanel {
    static final int UKURAN = 80;   // ukuran kotak awal; selanjutnya mengikuti ukuran jendela
    static final String[] AWAL = {
        "rnbqkbnr",
        "pppppppp",
        "........",
        "........",
        "........",
        "........",
        "PPPPPPPP",
        "RNBQKBNR"
    };

    final char[][] k = new char[8][8];
    int pilihBaris = -1, pilihKolom = -1;
    List<int[]> tujuan = new ArrayList<>();
    boolean giliranPutih = true;
    boolean selesai = false;
    final JFrame jendela;

    // ukuran kotak dan posisi papan dihitung ulang setiap digambar
    int ukuran = UKURAN, offsetX = 0, offsetY = 0;
    String statusTeks = "";                     // ditampilkan kecil di pojok kiri atas
    final Tema.Tombol btnJeda = Tema.tombolSekunder("Jeda");

    CaturAwal(JFrame jendela) {
        this.jendela = jendela;
        for (int r = 0; r < 8; r++) k[r] = AWAL[r].toCharArray();
        setOpaque(true);
        setBackground(Tema.GELAP_PEKAT);
        setLayout(null);                            // tombol jeda diletakkan manual di pojok kanan atas

        btnJeda.setFont(Tema.font(Font.BOLD, 12));
        btnJeda.setBorder(new EmptyBorder(6, 16, 6, 16));
        btnJeda.setFocusable(false);                // supaya papan tidak kehilangan fokus keyboard
        btnJeda.addActionListener(e -> jeda());
        add(btnJeda);
        perbaruiJudul();

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                hitungUkuran();
                int x = e.getX() - offsetX, y = e.getY() - offsetY;
                if (x < 0 || y < 0) return;
                klik(y / ukuran, x / ukuran);
            }
        });

        // ESC = jeda (F11 dipasang oleh Layar pada jendela)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "jeda");
        getActionMap().put("jeda", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { jeda(); }
        });
    }

    // Membuat jendela game lengkap: papan di tengah, panel info di kanan
    static void buka() {
        JFrame f = new JFrame("Catur");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Layar.siapkan(f);
        CaturAwal papan = new CaturAwal(f);
        f.add(papan);
        f.setVisible(true);
    }

    // Tombol jeda kecil menempel di pojok kanan atas
    @Override
    public void doLayout() {
        Dimension d = btnJeda.getPreferredSize();
        btnJeda.setBounds(getWidth() - d.width - 16, 16, d.width, d.height);
    }

    void hitungUkuran() {
        ukuran = Math.max(1, Math.min(getWidth(), getHeight()) / 8);
        offsetX = (getWidth() - ukuran * 8) / 2;
        offsetY = (getHeight() - ukuran * 8) / 2;
    }

    // Dialog jeda: Lanjutkan atau Keluar (kembali ke halaman login)
    void jeda() {
        Object[] pilihan = {"Lanjutkan", "Keluar"};
        int p = JOptionPane.showOptionDialog(
            this, "Permainan dijeda.", "Pause",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, pilihan, pilihan[0]);
        if (p == 1) {
            new LoginForm().setVisible(true);   // login dibuka dulu, baru game ditutup
            jendela.dispose();
        }
    }

    void klik(int r, int c) {
        if (selesai || !Papan.dalam(r, c)) return;

        if (pilihBaris >= 0) {
            for (int[] t : tujuan) {
                if (t[0] == r && t[1] == c) { pindah(r, c); return; }
            }
        }
        char p = k[r][c];
        if (p != '.' && Papan.putih(p) == giliranPutih) {
            pilihBaris = r;
            pilihKolom = c;
            tujuan = Skak.langkahLegal(k, r, c);
        } else {
            batalPilih();
        }
        repaint();
    }

    void pindah(int r, int c) {
        char p = k[pilihBaris][pilihKolom];
        k[pilihBaris][pilihKolom] = '.';
        // promosi otomatis menjadi ratu
        if (p == 'P' && r == 0) p = 'Q';
        if (p == 'p' && r == 7) p = 'q';
        k[r][c] = p;
        batalPilih();

        giliranPutih = !giliranPutih;
        perbaruiJudul();
        repaint();
        periksaStatus();
    }

    void batalPilih() {
        pilihBaris = -1;
        pilihKolom = -1;
        tujuan = new ArrayList<>();
    }

    void periksaStatus() {
        String nama = giliranPutih ? "Putih" : "Hitam";
        String lawan = giliranPutih ? "Hitam" : "Putih";

        switch (Skak.cekStatus(k, giliranPutih)) {
            case SKAK -> {
                setStatus("Raja " + nama + " SKAK!");
                JOptionPane.showMessageDialog(this,
                    "Raja " + nama + " sedang SKAK!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
            case SKAKMAT -> {
                selesai = true;
                setStatus("SKAKMAT! " + lawan + " menang");
                JOptionPane.showMessageDialog(this,
                    "SKAKMAT! Raja " + nama + " tidak punya jalan keluar.\n" + lawan + " menang!",
                    "Permainan Selesai", JOptionPane.INFORMATION_MESSAGE);
            }
            case STALEMATE -> {
                selesai = true;
                setStatus("Seri (stalemate)");
                JOptionPane.showMessageDialog(this,
                    "Stalemate! Permainan berakhir seri.",
                    "Permainan Selesai", JOptionPane.INFORMATION_MESSAGE);
            }
            default -> { }
        }
    }

    // Status tampil di title bar (mode biasa) dan di panel kanan (mode layar penuh)
    void setStatus(String teks) {
        jendela.setTitle("Catur - " + teks);
        statusTeks = teks;
        repaint();
    }

    void perbaruiJudul() { setStatus("Giliran " + (giliranPutih ? "Putih" : "Hitam")); }

    String simbol(char p) {
        int i = "KQRBNP".indexOf(Character.toUpperCase(p));
        return i >= 0 ? String.valueOf("♚♛♜♝♞♟".charAt(i)) : "";
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        hitungUkuran();
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("SansSerif", Font.PLAIN, ukuran * 7 / 10));
        FontMetrics fm = g.getFontMetrics();
        int geser = Math.max(1, ukuran / 10);
        int tepi = Math.max(1, ukuran / 80);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                boolean terang = (r + c) % 2 == 0;
                g.setColor(terang ? new Color(0xC9B99A) : new Color(0x4A3F36));
                if (r == pilihBaris && c == pilihKolom) g.setColor(new Color(0xE8892F));
                g.fillRect(offsetX + c * ukuran, offsetY + r * ukuran, ukuran, ukuran);

                String s = simbol(k[r][c]);
                if (s.isEmpty()) continue;
                int x = offsetX + c * ukuran + (ukuran - fm.stringWidth(s)) / 2;
                int y = offsetY + r * ukuran + (ukuran + fm.getAscent()) / 2 - geser;
                boolean putih = Character.isUpperCase(k[r][c]);
                g.setColor(putih ? Color.BLACK : new Color(0xC9B99A));   // garis tepi
                g.drawString(s, x + tepi, y + tepi);
                g.drawString(s, x - tepi, y - tepi);
                g.setColor(putih ? new Color(0xF5EFE0) : Color.BLACK);   // isi bidak
                g.drawString(s, x, y);
            }
        }
        g.setFont(Tema.font(Font.BOLD, 16));
        g.setColor(Tema.TERANG);
        g.drawString(statusTeks, 20, 16 + g.getFontMetrics().getAscent());

        for (int[] t : tujuan) {
            int cx = offsetX + t[1] * ukuran + ukuran / 2;
            int cy = offsetY + t[0] * ukuran + ukuran / 2;
            if (k[t[0]][t[1]] == '.') {
                int rad = ukuran / 8;
                g.setColor(new Color(0xE8892F));
                g.fillOval(cx - rad, cy - rad, rad * 2, rad * 2);
            } else {
                int rad = ukuran * 85 / 200;
                g.setColor(new Color(200, 40, 40));
                g.setStroke(new BasicStroke(Math.max(2f, ukuran / 16f)));
                g.drawOval(cx - rad, cy - rad, rad * 2, rad * 2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CaturAwal::buka);
    }
}
