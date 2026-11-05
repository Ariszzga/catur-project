package mekanik;

import java.util.ArrayList;
import java.util.List;

/**
 * Pion: maju 1 (2 dari posisi awal) bila petak depan kosong, makan serong 1 petak.
 * Putih maju ke atas (baris mengecil), hitam ke bawah. (Belum ada en passant.)
 */
public final class mPawn {
    private mPawn() { }

    public static List<int[]> langkah(char[][] k, int r, int c) {
        List<int[]> hasil = new ArrayList<>();
        boolean putih = Papan.putih(k[r][c]);
        int arah = putih ? -1 : 1;
        int barisAwal = putih ? 6 : 1;

        int depan = r + arah;
        if (Papan.dalam(depan, c) && k[depan][c] == '.') {
            hasil.add(new int[]{depan, c});
            int depan2 = r + 2 * arah;
            if (r == barisAwal && k[depan2][c] == '.') hasil.add(new int[]{depan2, c});
        }
        for (int dc = -1; dc <= 1; dc += 2) {
            int cc = c + dc;
            if (Papan.dalam(depan, cc) && Papan.musuh(k[r][c], k[depan][cc])) hasil.add(new int[]{depan, cc});
        }
        return hasil;
    }
}
