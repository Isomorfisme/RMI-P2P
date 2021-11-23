import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FolderImplementation implements Folder, Serializable {
    File file = null;

    public FolderImplementation() throws RemoteException {
        super();
        this.file = new File();
    }

    @Override
    public File getFile() {
        return new File();
    }

    public String toString(){
        return String.format("File: %s", this.file);
    }
}
