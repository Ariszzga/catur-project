package mekanik;

import java.util.ArrayList;
import java.util.List;

/** Kuda: loncat berbentuk L, boleh melompati bidak lain. */
public final class mKnight {
    private static final int[][] LONCAT = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

    private mKnight() { }

    public static List<int[]> langkah(char[][] k, int r, int c) {
        List<int[]> hasil = new ArrayList<>();
        for (int[] d : LONCAT) Papan.tunggal(k, r, c, d[0], d[1], hasil);
        return hasil;
    }
}
