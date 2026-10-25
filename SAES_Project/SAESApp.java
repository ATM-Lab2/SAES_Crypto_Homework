import core.SAESCipher;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SAESApp extends JFrame {
    private JTextField txtInput, txtKey1, txtKey2, txtKey3;
    private JTextArea txtLog;

    public SAESApp() {
        // 1. 应用现代 UI 主题 (FlatLaf 浅色模式)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Button.default.background", new Color(0, 120, 215));
            UIManager.put("Button.default.foreground", Color.WHITE);
        } catch (Exception ex) {
            System.err.println("UI库未加载，请确保同目录下有 flatlaf-3.4.jar");
        }

        // 2. 设置全局统一字体，彻底解决乱码问题
        Font globalFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        UIManager.put("Label.font", globalFont);
        UIManager.put("Button.font", new Font("Microsoft YaHei", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("Consolas", Font.PLAIN, 16));
        UIManager.put("TextArea.font", new Font("Microsoft YaHei", Font.PLAIN, 14));

        setTitle(" S-AES 现代密码学演示系统");
        setSize(750, 650); // 稍微加高，容纳更多输入框
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        getContentPane().setBackground(new Color(245, 245, 245));

        // ========== 顶部：输入控制台 (现在是4行) ==========
        JPanel topPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        topPanel.setBackground(new Color(245, 245, 245));
        
        topPanel.add(new JLabel(" 输入数据 (16进制明文/密文/字符串):"));
        txtInput = new JTextField("6A6A");
        topPanel.add(txtInput);

        topPanel.add(new JLabel(" 主密钥 K1 (16位 Hex):"));
        txtKey1 = new JTextField("2D55");
        topPanel.add(txtKey1);

        topPanel.add(new JLabel(" 辅助密钥 K2 (双重/三重加密用):"));
        txtKey2 = new JTextField("1234");
        topPanel.add(txtKey2);

        topPanel.add(new JLabel(" 辅助密钥 K3 (三重加密用):"));
        txtKey3 = new JTextField("ABCD");
        topPanel.add(txtKey3);

        add(topPanel, BorderLayout.NORTH);

        // ========== 中间：功能选项卡 ==========
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(0, 20, 0, 20));
        tabbedPane.setBackground(new Color(245, 245, 245));
        
        tabbedPane.addTab(" 基础测试", createBasicTab());
        tabbedPane.addTab(" 字符串扩展", createExtTab());
        tabbedPane.addTab(" 多重加密与攻击", createDoubleTab()); // 这里包含双重、三重、破解
        tabbedPane.addTab(" CBC 模式", createCBCTab());

        add(tabbedPane, BorderLayout.CENTER);

        // ========== 底部：控制台输出 ==========
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bottomPanel.setBackground(new Color(245, 245, 245));
        
        JLabel lblLog = new JLabel(" 系统运行日志 / 测试报告:");
        lblLog.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        bottomPanel.add(lblLog, BorderLayout.NORTH);
        
        txtLog = new JTextArea(8, 50);
        txtLog.setEditable(false);
        txtLog.setBackground(Color.WHITE); 
        txtLog.setForeground(new Color(50, 50, 50)); 
        txtLog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- Tab 1：基础加解密 ---
    private JPanel createBasicTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        panel.setBackground(new Color(245, 245, 245));
        
        JButton btnEnc = new JButton(" 基础加密");
        JButton btnDec = new JButton(" 基础解密");
        btnEnc.setPreferredSize(new Dimension(150, 45));
        btnDec.setPreferredSize(new Dimension(150, 45));
        
        btnEnc.addActionListener(e -> {
            try {
                int pt = Integer.parseInt(txtInput.getText().trim(), 16);
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int ct = SAESCipher.encrypt(pt, k1);
                txtLog.append(">> 基础加密: " + txtInput.getText() + " -> " + String.format("%04X", ct) + "\n");
            } catch (Exception ex) { showErr(); }
        });

        btnDec.addActionListener(e -> {
            try {
                int ct = Integer.parseInt(txtInput.getText().trim(), 16);
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int pt = SAESCipher.decrypt(ct, k1);
                txtLog.append(">> 基础解密: " + txtInput.getText() + " -> " + String.format("%04X", pt) + "\n");
            } catch (Exception ex) { showErr(); }
        });

        panel.add(btnEnc);
        panel.add(btnDec);
        return panel;
    }

    // --- Tab 2：ASCII 扩展 ---
    private JPanel createExtTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        panel.setBackground(new Color(245, 245, 245));
        
        JButton btnStringEnc = new JButton(" 加密 ASCII 字符串");
        btnStringEnc.setPreferredSize(new Dimension(200, 45));
        
        btnStringEnc.addActionListener(e -> {
            String text = txtInput.getText();
            if(text.length() % 2 != 0) text += "\0"; 
            try {
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < text.length(); i += 2) {
                    int block = (text.charAt(i) << 8) | text.charAt(i+1);
                    int ct = SAESCipher.encrypt(block, k1);
                    sb.append(String.format("%04X ", ct));
                }
                txtLog.append(">> 字符串扩展加密: \n  明文: " + txtInput.getText() + "\n  密文: " + sb.toString() + "\n");
            } catch (Exception ex) { showErr(); }
        });

        panel.add(btnStringEnc);
        return panel;
    }

    // --- Tab 3：多重加密与攻击 ---
    private JPanel createDoubleTab() {
        // 改成 3 行 2 列，容纳三重加密
        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 20));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        panel.setBackground(new Color(245, 245, 245));

        JButton btnDoubleEnc = new JButton(" 双重加密 (K1+K2)");
        JButton btnDoubleDec = new JButton(" 双重解密 (K1+K2)");
        JButton btnTripleEnc = new JButton(" 三重加密 (K1+K2+K3)");
        JButton btnTripleDec = new JButton(" 三重解密 (K1+K2+K3)");
        JButton btnAttack = new JButton(" 中间相遇攻击 (暴力破解)");
        
        btnAttack.setBackground(new Color(220, 53, 69));
        btnAttack.setForeground(Color.WHITE);

        // 双重加密
        btnDoubleEnc.addActionListener(e -> {
            try {
                int pt = Integer.parseInt(txtInput.getText().trim(), 16);
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int k2 = Integer.parseInt(txtKey2.getText().trim(), 16);
                int ct = SAESCipher.encrypt(SAESCipher.encrypt(pt, k1), k2);
                txtLog.append(">> 双重加密: 明文 " + String.format("%04X", pt) + " -> 密文 " + String.format("%04X", ct) + "\n");
                txtInput.setText(String.format("%04X", ct)); 
            } catch (Exception ex) { showErr(); }
        });

        // 双重解密
        btnDoubleDec.addActionListener(e -> {
            try {
                int ct = Integer.parseInt(txtInput.getText().trim(), 16);
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int k2 = Integer.parseInt(txtKey2.getText().trim(), 16);
                int pt = SAESCipher.decrypt(SAESCipher.decrypt(ct, k2), k1);
                txtLog.append(">> 双重解密: 密文 " + String.format("%04X", ct) + " -> 明文 " + String.format("%04X", pt) + "\n");
                txtInput.setText(String.format("%04X", pt));
            } catch (Exception ex) { showErr(); }
        });

        // 三重加密 (E_k3( D_k2( E_k1(P) ) ))
        btnTripleEnc.addActionListener(e -> {
            try {
                int pt = Integer.parseInt(txtInput.getText().trim(), 16);
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int k2 = Integer.parseInt(txtKey2.getText().trim(), 16);
                int k3 = Integer.parseInt(txtKey3.getText().trim(), 16);
                
                int temp1 = SAESCipher.encrypt(pt, k1);
                int temp2 = SAESCipher.decrypt(temp1, k2); // 中间是解密
                int ct = SAESCipher.encrypt(temp2, k3);
                
                txtLog.append(">> 三重加密: 明文 " + String.format("%04X", pt) + " -> 密文 " + String.format("%04X", ct) + "\n");
                txtInput.setText(String.format("%04X", ct)); 
            } catch (Exception ex) { showErr(); }
        });

        // 三重解密 (D_k1( E_k2( D_k3(C) ) ))
        btnTripleDec.addActionListener(e -> {
            try {
                int ct = Integer.parseInt(txtInput.getText().trim(), 16);
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int k2 = Integer.parseInt(txtKey2.getText().trim(), 16);
                int k3 = Integer.parseInt(txtKey3.getText().trim(), 16);
                
                int temp1 = SAESCipher.decrypt(ct, k3);
                int temp2 = SAESCipher.encrypt(temp1, k2);
                int pt = SAESCipher.decrypt(temp2, k1);
                
                txtLog.append(">> 三重解密: 密文 " + String.format("%04X", ct) + " -> 明文 " + String.format("%04X", pt) + "\n");
                txtInput.setText(String.format("%04X", pt));
            } catch (Exception ex) { showErr(); }
        });

        // 中间相遇攻击
        btnAttack.addActionListener(e -> {
            txtLog.append(">> 开始中间相遇攻击...\n");
            try {
                int pt = Integer.parseInt(txtInput.getText().trim(), 16);
                int ct = Integer.parseInt(txtKey1.getText().trim(), 16); 
                Map<Integer, Integer> map = new HashMap<>();
                
                for (int k = 0; k <= 0xFFFF; k++) {
                    map.put(SAESCipher.encrypt(pt, k), k);
                }
                
                for (int k = 0; k <= 0xFFFF; k++) {
                    int mid = SAESCipher.decrypt(ct, k);
                    if (map.containsKey(mid)) {
                        int foundK1 = map.get(mid);
                        txtLog.append("   [!] 破解成功! K1 = " + String.format("%04X", foundK1) + ", K2 = " + String.format("%04X", k) + "\n");
                        return;
                    }
                }
                txtLog.append("   破解失败，未找到匹配密钥。\n");
            } catch (Exception ex) { 
                txtLog.append("   请检查：输入框填明文，密钥1框填密文！\n");
            }
        });

        panel.add(btnDoubleEnc);
        panel.add(btnDoubleDec);
        panel.add(btnTripleEnc);
        panel.add(btnTripleDec);
        panel.add(new JLabel("")); 
        panel.add(btnAttack);
        return panel;
    }

    // --- Tab 4：CBC 模式 ---
    private JPanel createCBCTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        panel.setBackground(new Color(245, 245, 245));
        
        JButton btnCBC = new JButton(" 执行 CBC 模式与篡改测试");
        btnCBC.setPreferredSize(new Dimension(250, 45));

        btnCBC.addActionListener(e -> {
            try {
                txtLog.append(">> 开始 CBC 模式测试:\n");
                int k1 = Integer.parseInt(txtKey1.getText().trim(), 16);
                int iv = 0xABCD; 
                int p1 = 0x1122, p2 = 0x3344, p3 = 0x5566; 
                
                int c1 = SAESCipher.encrypt(p1 ^ iv, k1);
                int c2 = SAESCipher.encrypt(p2 ^ c1, k1);
                int c3 = SAESCipher.encrypt(p3 ^ c2, k1);
                txtLog.append("   明文: 1122 3344 5566\n   密文: " + String.format("%04X %04X %04X", c1, c2, c3) + "\n");
                
                int tampered_c1 = c1 ^ 0x8000; 
                txtLog.append("   篡改C1最高位，新密文: " + String.format("%04X %04X %04X", tampered_c1, c2, c3) + "\n");
                
                int dec_p1 = SAESCipher.decrypt(tampered_c1, k1) ^ iv;
                int dec_p2 = SAESCipher.decrypt(c2, k1) ^ tampered_c1;
                int dec_p3 = SAESCipher.decrypt(c3, k1) ^ c2;
                txtLog.append("   解密篡改后的明文: " + String.format("%04X %04X %04X", dec_p1, dec_p2, dec_p3) + "\n");
                txtLog.append("   [注意观察] 由于雪崩效应，P1和P2已完全乱码!\n");
            } catch (Exception ex) { showErr(); }
        });

        panel.add(btnCBC);
        return panel;
    }

    private void showErr() {
        JOptionPane.showMessageDialog(this, "请输入正确的 16进制数 (如 6A6A)！", "输入错误", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SAESApp().setVisible(true));
    }
}