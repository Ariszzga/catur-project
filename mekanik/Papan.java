package mekanik;

import java.util.ArrayList;
import java.util.List;

/**
 * Fungsi bantu bersama untuk semua bidak.
 * Papan = char[8][8]; huruf besar = putih, huruf kecil = hitam, '.' = kosong.
 * Baris 0 adalah rank 8 (atas), baris 7 adalah rank 1 (bawah).
 * Setiap langkah dikembalikan sebagai int[]{baris, kolom}.
 */
public final class Papan {
    private Papan() { }

    public static boolean putih(char p) { return Character.isUpperCase(p); }

    public static boolean dalam(int r, int c) { return r >= 0 && r < 8 && c >= 0 && c < 8; }

    /** true bila b adalah bidak lawan dari a. */
    public static boolean musuh(char a, char b) { return b != '.' && putih(a) != putih(b); }

    /** Meluncur searah (dr,dc) sampai terhalang; boleh memakan bidak lawan pertama yang ditemui. */
    public static void meluncur(char[][] k, int r, int c, int dr, int dc, List<int[]> hasil) {
        char p = k[r][c];
        int rr = r + dr, cc = c + dc;
        while (dalam(rr, cc)) {
            if (k[rr][cc] == '.') {
                hasil.add(new int[]{rr, cc});
            } else {
                if (musuh(p, k[rr][cc])) hasil.add(new int[]{rr, cc});
                break;
            }
            rr += dr;
            cc += dc;
        }
    }

    /** Satu loncatan/langkah ke (r+dr, c+dc): sah bila di dalam papan dan kosong atau lawan. */
    public static void tunggal(char[][] k, int r, int c, int dr, int dc, List<int[]> hasil) {
        int rr = r + dr, cc = c + dc;
        if (dalam(rr, cc) && (k[rr][cc] == '.' || musuh(k[r][c], k[rr][cc]))) hasil.add(new int[]{rr, cc});
    }

    /** Semua langkah bidak di (r,c) sesuai jenisnya. Daftar kosong bila petak kosong. */
    public static List<int[]> langkah(char[][] k, int r, int c) {
        return switch (Character.toUpperCase(k[r][c])) {
            case 'P' -> mPawn.langkah(k, r, c);
            case 'R' -> mRook.langkah(k, r, c);
            case 'N' -> mKnight.langkah(k, r, c);
            case 'B' -> mBishop.langkah(k, r, c);
            case 'Q' -> mQueen.langkah(k, r, c);
            case 'K' -> mKing.langkah(k, r, c);
            default -> new ArrayList<>();
        };
    }
}
