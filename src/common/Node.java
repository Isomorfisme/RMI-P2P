package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {
    File getFile() throws RemoteException;

    Node getFolder() throws RemoteException;

    void register(Node clientFolder, String username) throws RemoteException;
}
