package mekanik;

import java.util.ArrayList;
import java.util.List;

public final class mKing {
    private mKing() { }

    public static List<int[]> langkah(char[][] k, int r, int c) {
        List<int[]> hasil = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr != 0 || dc != 0) Papan.tunggal(k, r, c, dr, dc, hasil);
            }
        }
        return hasil;
    }
}
