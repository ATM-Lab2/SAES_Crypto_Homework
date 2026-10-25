package core;

public class SBox {
    public static final int[][] S_BOX = {
            {0x9, 0x4, 0xA, 0xB},
            {0xD, 0x1, 0x8, 0x5},
            {0x6, 0x2, 0x0, 0x3},
            {0xC, 0xE, 0xF, 0x7}
    };

    public static final int[][] INV_S_BOX = {
            {0xA, 0x5, 0x9, 0xB},
            {0x1, 0x7, 0x8, 0xF},
            {0x6, 0x0, 0x2, 0x3},
            {0xC, 0x4, 0xD, 0xE}
    };

    public static int subNib(int nibble) {
        int row = (nibble >> 2) & 0x3;
        int col = nibble & 0x3;
        return S_BOX[row][col];
    }

    public static int invSubNib(int nibble) {
        int row = (nibble >> 2) & 0x3;
        int col = nibble & 0x3;
        return INV_S_BOX[row][col];
    }
}