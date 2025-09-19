package ChatRMI;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JTextField serverAddressField;
    private boolean loginSuccess = false;
    private String selectedServer;

    public LoginDialog(Frame parent) {
        super(parent, "Đăng nhập Chat", true);
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        usernameField = new JTextField("User" + (int)(Math.random() * 1000));
        serverAddressField = new JTextField("localhost:1099");

        panel.add(new JLabel("Tên người dùng:"));
        panel.add(usernameField);
        panel.add(new JLabel("Địa chỉ server:"));
        panel.add(serverAddressField);

        JButton loginButton = new JButton("Kết nối");
        JButton cancelButton = new JButton("Hủy");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            String serverAddress = serverAddressField.getText().trim();
            if (!serverAddress.isEmpty()) {
                selectedServer = serverAddress;
                loginSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ server", 
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getServerAddress() {
        return selectedServer;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}