package ChatRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatInterface extends Remote {
    void sendMessage(Message message) throws RemoteException;
    List<Message> getMessages() throws RemoteException;
    void registerClient(ChatInterface client, String clientName) throws RemoteException;
    void unregisterClient(String clientName) throws RemoteException;
    List<String> getConnectedClients() throws RemoteException;
    String getServerName() throws RemoteException;
    void updateClientList(List<String> clients) throws RemoteException;
    boolean isAvailable() throws RemoteException;
}