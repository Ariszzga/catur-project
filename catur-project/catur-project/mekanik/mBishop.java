package mekanik;

import java.util.ArrayList;
import java.util.List;

public final class mBishop {
    private mBishop() { }

    public static List<int[]> langkah(char[][] k, int r, int c) {
        List<int[]> hasil = new ArrayList<>();
        Papan.meluncur(k, r, c, -1, -1, hasil);
        Papan.meluncur(k, r, c, -1, 1, hasil);
        Papan.meluncur(k, r, c, 1, -1, hasil);
        Papan.meluncur(k, r, c, 1, 1, hasil);
        return hasil;
    }
}
