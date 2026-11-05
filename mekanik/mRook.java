package mekanik;

import java.util.ArrayList;
import java.util.List;

/** Benteng: meluncur lurus (atas, bawah, kiri, kanan). */
public final class mRook {
    private mRook() { }

    public static List<int[]> langkah(char[][] k, int r, int c) {
        List<int[]> hasil = new ArrayList<>();
        Papan.meluncur(k, r, c, -1, 0, hasil);
        Papan.meluncur(k, r, c, 1, 0, hasil);
        Papan.meluncur(k, r, c, 0, -1, hasil);
        Papan.meluncur(k, r, c, 0, 1, hasil);
        return hasil;
    }
}
