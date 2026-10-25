package core;

public class KeyExpansion {
    private static int g(int w) {
        int highNib = (w >> 4) & 0xF;
        int lowNib = w & 0xF;
        int rot = (lowNib << 4) | highNib;
        
        int subHigh = SBox.subNib((rot >> 4) & 0xF);
        int subLow = SBox.subNib(rot & 0xF);
        int sub = (subHigh << 4) | subLow;
        return sub;
    }

    public static int[] expand(int key16) {
        int[] w = new int[6];
        w[0] = (key16 >> 8) & 0xFF;
        w[1] = key16 & 0xFF;

        int RC1 = 0x80;
        int RC2 = 0x30;

        w[2] = w[0] ^ g(w[1]) ^ RC1;
        w[3] = w[2] ^ w[1];

        w[4] = w[2] ^ g(w[3]) ^ RC2;
        w[5] = w[4] ^ w[3];

        return w;
    }
}