package mekanik;

import java.util.ArrayList;
import java.util.List;

public final class Skak {
    private Skak() { }

    public enum Status { AMAN, SKAK, SKAKMAT, STALEMATE }

    // Status untuk pemain yang AKAN melangkah
    public static Status cekStatus(char[][] k, boolean putih) {
        boolean skak = sedangSkak(k, putih);
        boolean adaLangkah = adaLangkahLegal(k, putih);

        if (skak && !adaLangkah) return Status.SKAKMAT;
        if (skak) return Status.SKAK;
        if (!adaLangkah) return Status.STALEMATE;
        return Status.AMAN;
    }

    // Apakah raja warna ini sedang diserang?
    public static boolean sedangSkak(char[][] k, boolean putih) {
        char raja = putih ? 'K' : 'k';
        int rr = -1, rc = -1;
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (k[r][c] == raja) { rr = r; rc = c; }
        if (rr == -1) return false; // raja tidak ditemukan

        // Cek apakah ada bidak lawan yang langkahnya mengenai petak raja
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char p = k[r][c];
                if (p == '.' || Papan.putih(p) == putih) continue;
                for (int[] m : Papan.langkah(k, r, c)) {
                    if (m[0] == rr && m[1] == rc) return true;
                }
            }
        }
        return false;
    }

    // Langkah legal satu bidak (langkah yang tidak membuat raja sendiri skak)
    public static List<int[]> langkahLegal(char[][] k, int r, int c) {
        List<int[]> hasil = new ArrayList<>();
        char p = k[r][c];
        if (p == '.') return hasil;
        boolean putih = Papan.putih(p);

        for (int[] m : Papan.langkah(k, r, c)) {
            char tujuan = k[m[0]][m[1]];
            k[m[0]][m[1]] = p;      // simulasi langkah
            k[r][c] = '.';
            boolean aman = !sedangSkak(k, putih);
            k[r][c] = p;            // kembalikan
            k[m[0]][m[1]] = tujuan;
            if (aman) hasil.add(m);
        }
        return hasil;
    }

    // Apakah pemain punya minimal satu langkah legal?
    public static boolean adaLangkahLegal(char[][] k, boolean putih) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char p = k[r][c];
                if (p == '.' || Papan.putih(p) != putih) continue;
                if (!langkahLegal(k, r, c).isEmpty()) return true;
            }
        }
        return false;
    }
}