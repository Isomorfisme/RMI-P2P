package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;

public interface Node extends Remote {
    Collection<P2PFile> getFiles() throws RemoteException;

    HashMap<String, P2PFile> getContents() throws RemoteException;

    void register(Node clientFolder, String username) throws RemoteException;
}
