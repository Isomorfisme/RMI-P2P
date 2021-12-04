package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface Node extends Remote {
    P2PFile getFile() throws RemoteException;

    HashMap<String, P2PFile> getContents() throws RemoteException;

    void register(Node clientFolder, String username) throws RemoteException;
}
