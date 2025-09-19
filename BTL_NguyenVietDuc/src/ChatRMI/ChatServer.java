package ChatRMI;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer extends JFrame implements ChatInterface {
    private List<Message> messages;
    private List<ChatInterface> clients;
    private List<String> clientNames;
    private String serverName;
    private String host;
    private int port; 

    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JLabel ipInfoLabel;

    public ChatServer(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        this.messages = new ArrayList<>();
        this.clients = new CopyOnWriteArrayList<>();
        this.clientNames = new CopyOnWriteArrayList<>();
        this.host = getIPv4();

        initializeGUI();
        startServer();
    }

    private String getIPv4() {
        try {
            java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;

                java.util.Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.getAddress().length == 4 && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log("Lỗi khi lấy địa chỉ IP: " + e.getMessage());
        }
        return "127.0.0.1";
    }

    private void initializeGUI() {
        setTitle("Chat Server - " + serverName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Server đang chạy");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        clientCountLabel = new JLabel("Clients: 0");
        clientCountLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        topPanel.add(statusLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(clientCountLabel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipInfoLabel = new JLabel("<html>Địa chỉ IP: <b>" + host + "</b><br>Port: <b>" + port + "</b></html>");
        ipInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ipInfoLabel.setForeground(Color.BLUE);
        bottomPanel.add(ipInfoLabel);

        statusPanel.add(topPanel);
        statusPanel.add(bottomPanel);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);

        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void updateClientCount() {
        SwingUtilities.invokeLater(() -> {
            clientCountLabel.setText("Clients: " + clients.size());
        });
    }

    @Override
    public synchronized void sendMessage(Message message) throws RemoteException {
        messages.add(message);
        log(message.toString());
        
        for (ChatInterface client : clients) {
            try {
                client.sendMessage(message);
            } catch (RemoteException e) {
                log("❌ Lỗi gửi tin nhắn đến client: " + e.getMessage());
                int index = clients.indexOf(client);
                if (index != -1) {
                    clients.remove(index);
                    String disconnectedClient = clientNames.remove(index);
                    log("Client " + disconnectedClient + " đã mất kết nối");
                    updateClientCount();
                    updateAllClientLists();
                }
            }
        }
    }

    @Override
    public List<Message> getMessages() throws RemoteException {
        return new ArrayList<>(messages);
    }

    @Override
    public synchronized void registerClient(ChatInterface client, String clientName) throws RemoteException {
        clients.add(client);
        clientNames.add(clientName);
        
        Message joinMessage = new Message("SYSTEM", 
            clientName + " đã tham gia phòng chat!", MessageType.JOIN);
        sendMessage(joinMessage);
        log(clientName + " đã kết nối");
        updateClientCount();
        updateAllClientLists();
    }

    @Override
    public synchronized void unregisterClient(String clientName) throws RemoteException {
        int index = clientNames.indexOf(clientName);
        if (index != -1) {
            clients.remove(index);
            clientNames.remove(index);
            
            Message leaveMessage = new Message("SYSTEM", 
                clientName + " đã rời phòng chat!", MessageType.LEAVE);
            sendMessage(leaveMessage);
            
            log("🚪 " + clientName + " đã ngắt kết nối");
            updateClientCount();
            updateAllClientLists();
        }
    }

    @Override
    public List<String> getConnectedClients() throws RemoteException {
        return new ArrayList<>(clientNames);
    }

    @Override
    public String getServerName() throws RemoteException {
        return serverName;
    }
    
    @Override
    public void updateClientList(List<String> clients) throws RemoteException {
    }
    
    @Override
    public boolean isAvailable() throws RemoteException {
        return true;
    }

    private void updateAllClientLists() {
        List<String> currentClients = new ArrayList<>(clientNames);
        for (ChatInterface client : clients) {
            try {
                client.updateClientList(currentClients);
            } catch (RemoteException e) {
                log("❌ Lỗi cập nhật danh sách client: " + e.getMessage());
                int index = clients.indexOf(client);
                if (index != -1) {
                    clients.remove(index);
                    String disconnectedClient = clientNames.remove(index);
                    log("Client " + disconnectedClient + " đã mất kết nối");
                    updateClientCount();
                }
            }
        }
    }

    public void startServer() {
        try {
            System.setProperty("java.rmi.server.hostname", host);
            Registry registry = LocateRegistry.createRegistry(port);
            ChatInterface stub = (ChatInterface) UnicastRemoteObject.exportObject(this, port);
            registry.rebind("ChatService", stub);
            Naming.rebind("rmi://" + host + ":" + port + "/ChatService", stub);
            log("=== CHAT SERVER ĐÃ KHỞI ĐỘNG ===");
            log("Địa chỉ: " + host + ":" + port);
            log("Tên server: " + serverName);
            log("=================================");

        } catch (Exception e) {
            log("Lỗi server: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khởi động server: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JTextField serverNameField = new JTextField("Chat Server");
            JTextField portField = new JTextField("1099");

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Tên server:"));
            panel.add(serverNameField);
            panel.add(new JLabel("Port:"));
            panel.add(portField);

            int result = JOptionPane.showConfirmDialog(null, panel, 
                "Tạo Server Chat", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String serverName = serverNameField.getText();
                int port = Integer.parseInt(portField.getText());
                
                JFrame waitingFrame = new JFrame("Server Chat");
                waitingFrame.setSize(400, 200);
                waitingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                waitingFrame.setLocationRelativeTo(null);
                
                String realIP = "Đang tải...";
                try {
                    realIP = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    realIP = "127.0.0.1";
                }
                
                JLabel infoLabel = new JLabel("<html><center>"
                    + "<b>Server Chat đã khởi động</b><br><br>"
                    + "Địa chỉ IP: <b>" + realIP + " </b> <br>"
                    + "Port: <b>" + port + "</b><br>"
                    + "Đang chờ client kết nối..."
                    + "</center></html>", SwingConstants.CENTER);
                
                infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                waitingFrame.add(infoLabel, BorderLayout.CENTER);
                waitingFrame.setVisible(true);
               
                new ChatServer(serverName, port);
                
                Timer timer = new Timer(2000, e -> waitingFrame.dispose());
                timer.setRepeats(false);
                timer.start();
            }
        });
    }
}