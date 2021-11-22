import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FolderImplementation extends UnicastRemoteObject implements Folder{
    public FolderImplementation() throws RemoteException {
    }

    @Override
    public File getFile() {
        return new File();
    }
}
