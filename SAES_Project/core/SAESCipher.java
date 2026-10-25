package core;

public class SAESCipher {
    private static int addRoundKey(int state, int key) {
        return state ^ key;
    }

    private static int subNibbles(int state) {
        int n0 = SBox.subNib((state >> 12) & 0xF);
        int n1 = SBox.subNib((state >> 8) & 0xF);
        int n2 = SBox.subNib((state >> 4) & 0xF);
        int n3 = SBox.subNib(state & 0xF);
        return (n0 << 12) | (n1 << 8) | (n2 << 4) | n3;
    }

    private static int invSubNibbles(int state) {
        int n0 = SBox.invSubNib((state >> 12) & 0xF);
        int n1 = SBox.invSubNib((state >> 8) & 0xF);
        int n2 = SBox.invSubNib((state >> 4) & 0xF);
        int n3 = SBox.invSubNib(state & 0xF);
        return (n0 << 12) | (n1 << 8) | (n2 << 4) | n3;
    }

    private static int shiftRows(int state) {
        int n0 = (state >> 12) & 0xF;
        int n1 = (state >> 8) & 0xF;
        int n2 = (state >> 4) & 0xF;
        int n3 = state & 0xF;
        return (n0 << 12) | (n1 << 8) | (n3 << 4) | n2;
    }

    private static int mixColumns(int state) {
        int s0 = (state >> 12) & 0xF;
        int s1 = (state >> 8) & 0xF;
        int s2 = (state >> 4) & 0xF;
        int s3 = state & 0xF;

        int s0_new = s0 ^ GFMath.multiply(4, s2);
        int s2_new = GFMath.multiply(4, s0) ^ s2;
        int s1_new = s1 ^ GFMath.multiply(4, s3);
        int s3_new = GFMath.multiply(4, s1) ^ s3;

        return (s0_new << 12) | (s1_new << 8) | (s2_new << 4) | s3_new;
    }

    private static int invMixColumns(int state) {
        int s0 = (state >> 12) & 0xF;
        int s1 = (state >> 8) & 0xF;
        int s2 = (state >> 4) & 0xF;
        int s3 = state & 0xF;

        int s0_new = GFMath.multiply(9, s0) ^ GFMath.multiply(2, s2);
        int s2_new = GFMath.multiply(2, s0) ^ GFMath.multiply(9, s2);
        int s1_new = GFMath.multiply(9, s1) ^ GFMath.multiply(2, s3);
        int s3_new = GFMath.multiply(2, s1) ^ GFMath.multiply(9, s3);

        return (s0_new << 12) | (s1_new << 8) | (s2_new << 4) | s3_new;
    }

    public static int encrypt(int plaintext, int key16) {
        int[] w = KeyExpansion.expand(key16);
        int key0 = (w[0] << 8) | w[1];
        int state = addRoundKey(plaintext, key0);

        state = subNibbles(state);
        state = shiftRows(state);
        state = mixColumns(state);
        int key1 = (w[2] << 8) | w[3];
        state = addRoundKey(state, key1);

        state = subNibbles(state);
        state = shiftRows(state);
        int key2 = (w[4] << 8) | w[5];
        state = addRoundKey(state, key2);

        return state & 0xFFFF;
    }

    public static int decrypt(int ciphertext, int key16) {
        int[] w = KeyExpansion.expand(key16);
        int key2 = (w[4] << 8) | w[5];
        int state = addRoundKey(ciphertext, key2);

        state = shiftRows(state);
        state = invSubNibbles(state);

        int key1 = (w[2] << 8) | w[3];
        state = addRoundKey(state, key1);
        state = invMixColumns(state);
        state = shiftRows(state);
        state = invSubNibbles(state);

        int key0 = (w[0] << 8) | w[1];
        state = addRoundKey(state, key0);

        return state & 0xFFFF;
    }
}