import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Folder extends Remote {
    File getFile() throws RemoteException;
}
