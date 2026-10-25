package core;

public class GFMath {
    public static int multiply(int a, int b) {
        int p = 0;
        for (int i = 0; i < 4; i++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            boolean hiBitSet = (a & 0x8) != 0;
            a <<= 1;
            if (hiBitSet) {
                a ^= 0x13;
            }
            a &= 0xF;
            b >>= 1;
        }
        return p;
    }
}