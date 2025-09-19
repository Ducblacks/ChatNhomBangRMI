package ChatRMI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JComboBox<String> serverComboBox;
    private boolean loginSuccess = false;
    private String selectedServer;

    public LoginDialog(Frame parent) {
        super(parent, "Đăng nhập Chat", true);
        initialize();
        discoverServers();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        usernameField = new JTextField("User" + (int)(Math.random() * 1000));
        serverComboBox = new JComboBox<>();
        serverComboBox.addItem("Đang tìm server...");

        panel.add(new JLabel("Tên người dùng:"));
        panel.add(usernameField);
        panel.add(new JLabel("Chọn server:"));
        panel.add(serverComboBox);

        JButton refreshButton = new JButton("Làm mới");
        JButton loginButton = new JButton("Kết nối");
        JButton cancelButton = new JButton("Hủy");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        refreshButton.addActionListener(e -> discoverServers());
        loginButton.addActionListener(e -> {
            if (serverComboBox.getSelectedIndex() > 0) {
                selectedServer = (String) serverComboBox.getSelectedItem();
                loginSuccess = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một server", 
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void discoverServers() {
        new Thread(() -> {
            serverComboBox.removeAllItems();
            serverComboBox.addItem("Đang tìm kiếm server...");
            
            List<String> servers = DiscoveryService.discoverServers();
            
            SwingUtilities.invokeLater(() -> {
                serverComboBox.removeAllItems();
                
                if (servers.isEmpty()) {
                    serverComboBox.addItem("Không tìm thấy server nào");
                } else {
                    for (String server : servers) {
                        serverComboBox.addItem(server);
                    }
                }
            });
        }).start();
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