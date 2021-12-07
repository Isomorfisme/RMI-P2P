package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;

public interface Node extends Remote {
    Collection<P2PFile> getFiles() throws RemoteException;

    HashMap<String, P2PFile> getContents(Node node) throws RemoteException;

    List<Node> getClientFolders() throws RemoteException;

    Node getServerFolder() throws RemoteException;

    Node getMyFolder() throws RemoteException;

    HashMap<String, P2PFile> getFolderFiles() throws RemoteException;

    String getStringPath() throws RemoteException;

    void recognizeFiles() throws RemoteException;

    void register(Node clientFolder, String username) throws RemoteException;

    void connect(Node serverFolder) throws RemoteException;

    void putMyFolder(Node myFolder) throws RemoteException;

    void updateContents(P2PFile p2PFile) throws RemoteException;

    HashMap<String, P2PFile> getAllContents(Node node) throws RemoteException;

    HashMap<String, P2PFile> getAllContentsFromTop(Node node, HashMap<String, P2PFile> files) throws RemoteException;
}
