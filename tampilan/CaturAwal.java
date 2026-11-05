package tampilan;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import mekanik.Papan;

public class CaturAwal extends JPanel {
    static final int UKURAN = 80;
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

    CaturAwal(JFrame jendela) {
        this.jendela = jendela;
        for (int r = 0; r < 8; r++) k[r] = AWAL[r].toCharArray();
        setPreferredSize(new Dimension(8 * UKURAN, 8 * UKURAN));
        perbaruiJudul();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { klik(e.getY() / UKURAN, e.getX() / UKURAN); }
        });
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
            tujuan = Papan.langkah(k, r, c);
        } else {
            batalPilih();
        }
        repaint();
    }

    void pindah(int r, int c) {
        char p = k[pilihBaris][pilihKolom];
        char dimakan = k[r][c];
        k[pilihBaris][pilihKolom] = '.';
        // promosi otomatis menjadi ratu
        if (p == 'P' && r == 0) p = 'Q';
        if (p == 'p' && r == 7) p = 'q';
        k[r][c] = p;
        batalPilih();

        if (Character.toUpperCase(dimakan) == 'K') {
            selesai = true;
            repaint();
            String pemenang = giliranPutih ? "Putih" : "Hitam";
            jendela.setTitle("Catur - " + pemenang + " menang!");
            JOptionPane.showMessageDialog(this, pemenang + " menang (raja lawan dimakan).");
            return;
        }
        giliranPutih = !giliranPutih;
        perbaruiJudul();
        repaint();
    }

    void batalPilih() {
        pilihBaris = -1;
        pilihKolom = -1;
        tujuan = new ArrayList<>();
    }

    void perbaruiJudul() { jendela.setTitle("Catur - Giliran " + (giliranPutih ? "Putih" : "Hitam")); }

    String simbol(char p) {
        int i = "KQRBNP".indexOf(Character.toUpperCase(p));
        return i >= 0 ? String.valueOf("♚♛♜♝♞♟".charAt(i)) : "";
    }

    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(new Font("SansSerif", Font.PLAIN, 56));
        FontMetrics fm = g.getFontMetrics();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                boolean terang = (r + c) % 2 == 0;
                g.setColor(terang ? new Color(0xC9B99A) : new Color(0x4A3F36));
                if (r == pilihBaris && c == pilihKolom) g.setColor(new Color(0xE8892F));
                g.fillRect(c * UKURAN, r * UKURAN, UKURAN, UKURAN);

                String s = simbol(k[r][c]);
                if (s.isEmpty()) continue;
                int x = c * UKURAN + (UKURAN - fm.stringWidth(s)) / 2;
                int y = r * UKURAN + (UKURAN + fm.getAscent()) / 2 - 8;
                boolean putih = Character.isUpperCase(k[r][c]);
                g.setColor(putih ? Color.BLACK : new Color(0xC9B99A));   // garis tepi
                g.drawString(s, x + 1, y + 1);
                g.drawString(s, x - 1, y - 1);
                g.setColor(putih ? new Color(0xF5EFE0) : Color.BLACK);   // isi bidak
                g.drawString(s, x, y);
            }
        }
        for (int[] t : tujuan) {
            int cx = t[1] * UKURAN + UKURAN / 2, cy = t[0] * UKURAN + UKURAN / 2;
            if (k[t[0]][t[1]] == '.') {
                g.setColor(new Color(0xE8892F));
                g.fillOval(cx - 10, cy - 10, 20, 20);
            } else {
                g.setColor(new Color(200, 40, 40));
                g.setStroke(new BasicStroke(5f));
                g.drawOval(cx - 34, cy - 34, 68, 68);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Catur");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new CaturAwal(f));
            f.pack();
            f.setResizable(false);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}

//ppp