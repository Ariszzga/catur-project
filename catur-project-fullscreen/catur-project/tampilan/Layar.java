package tampilan;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

// Pengatur mode layar penuh untuk semua jendela (login, daftar, game).
// Status disimpan global supaya mode layar terbawa dari satu halaman ke halaman lain.
final class Layar {
    private Layar() { }

    static boolean penuh = true;                      // mulai dalam mode layar penuh
    static final Dimension UKURAN_NORMAL = new Dimension(1000, 780);

    static Rectangle batasLayar(Window w) {
        GraphicsConfiguration gc = (w != null && w.getGraphicsConfiguration() != null)
                ? w.getGraphicsConfiguration()
                : GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration();
        return gc.getBounds();
    }

    // Panggil di constructor JFrame, sebelum jendela ditampilkan
    static void siapkan(JFrame f) {
        f.setResizable(true);
        terapkan(f, false);
        pasangF11(f);
    }

    // Terapkan mode (penuh/normal) pada jendela. Kalau jendela sudah tampil, dibuat ulang dulu.
    private static void terapkan(JFrame f, boolean sudahTampil) {
        if (sudahTampil) f.dispose();                 // undecorated hanya bisa diubah saat jendela tidak tampil
        f.setUndecorated(penuh);
        if (penuh) {
            f.setBounds(batasLayar(f));
        } else {
            f.setSize(UKURAN_NORMAL);
            f.setLocationRelativeTo(null);
        }
        if (sudahTampil) {
            f.setVisible(true);
            f.toFront();
            f.requestFocus();
        }
    }

    static void ganti(JFrame f) {
        penuh = !penuh;
        terapkan(f, true);
    }

    private static void pasangF11(JFrame f) {
        JRootPane rp = f.getRootPane();
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "layarPenuh");
        rp.getActionMap().put("layarPenuh", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { ganti(f); }
        });
    }
}
