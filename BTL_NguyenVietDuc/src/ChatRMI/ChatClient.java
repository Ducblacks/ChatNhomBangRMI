package ChatRMI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChatClient extends JFrame implements ChatInterface {
    private ChatInterface server;
    private String clientName;
    private boolean connected;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JLabel statusLabel;

    public ChatClient(String clientName, String serverAddress) {
        this.clientName = clientName;
        initializeGUI();
        connectToServer(serverAddress);
    }

    private void initializeGUI() {
        setTitle("Chat Client - " + clientName);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane chatScroll = new JScrollPane(chatArea);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(200, 0));

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Gửi");

        messageField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        statusLabel = new JLabel("Kết nối... ");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chatScroll, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(mainPanel);
        splitPane.setRightComponent(userScroll);

        add(splitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void appendToChat(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void updateUserList(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        });
    }

    private void updateStatus(String status, boolean isConnected) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(" " + status + " ");
            statusLabel.setForeground(isConnected ? Color.GREEN.darker() : Color.RED);
            sendButton.setEnabled(isConnected);
            messageField.setEnabled(isConnected);
        });
    }

    private void connectToServer(String serverAddress) {
        new Thread(() -> {
            try {
                updateStatus("Đang kết nối...", false);
                server = DiscoveryService.connectToServer(serverAddress);
                
                ChatInterface clientStub = (ChatInterface) UnicastRemoteObject.exportObject(this, 0);
                server.registerClient(clientStub, clientName);
                connected = true;
                updateStatus("Đã kết nối đến server", true);
                appendToChat("✅ Đã kết nối đến server: " + server.getServerName());
                updateUserList(server.getConnectedClients());

            } catch (Exception e) {
                updateStatus("Kết nối thất bại", false);
                appendToChat("Lỗi kết nối: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Không thể kết nối đến server: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void disconnect() {
        if (connected) {
            try {
                server.unregisterClient(clientName);
                connected = false;
                updateStatus("Đã ngắt kết nối", false);
            } catch (RemoteException e) {
                appendToChat("Lỗi khi ngắt kết nối: " + e.getMessage());
            }
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && connected) {
            try {
                Message msg = new Message(clientName, message, MessageType.TEXT);
                server.sendMessage(msg);
                messageField.setText("");
            } catch (RemoteException e) {
                appendToChat("Lỗi khi gửi tin nhắn: " + e.getMessage());
                connected = false;
                updateStatus("Mất kết nối", false);
            }
        }
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        appendToChat(message.toString());
    }

    @Override
    public List<Message> getMessages() throws RemoteException {
        return null;
    }

    @Override
    public void registerClient(ChatInterface client, String clientName) throws RemoteException {
        // Client không cần xử lý phương thức này
    }

    @Override
    public void unregisterClient(String clientName) throws RemoteException {
        // Client không cần xử lý phương thức này
    }

    @Override
    public List<String> getConnectedClients() throws RemoteException {
        return null;
    }

    @Override
    public String getServerName() throws RemoteException {
        return "Client: " + clientName;
    }
    
    @Override
    public void updateClientList(List<String> clients) throws RemoteException {
        updateUserList(clients);
    }
    
	@Override
	public boolean isAvailable() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);

            if (loginDialog.isLoginSuccess()) {
                new ChatClient(
                        loginDialog.getUsername(),
                        loginDialog.getServerAddress()
                );
            } else {
                System.exit(0);
            }
        });
    }
}