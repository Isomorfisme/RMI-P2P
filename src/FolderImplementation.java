import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FolderImplementation implements Folder, Serializable {
    //private static final long serialVersionUID = 6529685098267757690L;

    File file = null;

    public FolderImplementation(int port) throws RemoteException {
        super();
        this.file = new File(port);
    }

    @Override
    public File getFile() {
        return this.file;
    }

    public String toString(){
        return String.format("File: %s", this.file);
    }
}
