package mekanik;

import java.util.List;

/** Ratu: gabungan benteng dan gajah. */
public final class mQueen {
    private mQueen() { }

    public static List<int[]> langkah(char[][] k, int r, int c) {
        List<int[]> hasil = mRook.langkah(k, r, c);
        hasil.addAll(mBishop.langkah(k, r, c));
        return hasil;
    }
}
