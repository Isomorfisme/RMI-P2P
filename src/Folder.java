import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Folder extends Remote {
    File getFile() throws RemoteException;

    Folder getFolder() throws RemoteException;

    void register(Folder clientFolder, String username) throws RemoteException;
}
