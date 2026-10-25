import core.SAESCipher;

public class Main {
    public static void main(String[] args) {
        int plaintext = 0x6A6A; 
        int key = 0x2D55;
        
        System.out.println("明文: " + String.format("%04X", plaintext));
        System.out.println("密钥: " + String.format("%04X", key));

        int ciphertext = SAESCipher.encrypt(plaintext, key);
        System.out.println("加密得到密文: " + String.format("%04X", ciphertext));

        int decrypted = SAESCipher.decrypt(ciphertext, key);
        System.out.println("解密得到明文: " + String.format("%04X", decrypted));
        
        if (decrypted == plaintext) {
            System.out.println("✅ 测试通过！算法没问题！");
        } else {
            System.out.println("❌ 算法有 Bug！");
        }
    }
}