import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class FolderImplementation extends UnicastRemoteObject implements Folder{
    //private static final long serialVersionUID = 6529685098267757690L;

    File file = null;
    List<String> clientUsernames = new ArrayList<>();
    List<Folder> clientFolders = new ArrayList<>();

    public FolderImplementation(int port) throws RemoteException {
        super();
        this.file = new File(port);
    }

    @Override
    public File getFile() throws RemoteException{
        return this.file;
    }

    @Override
    public Folder getFolder() throws RemoteException{
        if (this.clientFolders.isEmpty()){
            return null;
        }else {
            return this.clientFolders.get(0);
        }
    }

    @Override
    public void register(Folder clientFolder, String username) throws RemoteException {
        this.clientUsernames.add(username);
        this.clientFolders.add(clientFolder);
    }

    public String toString(){
        return String.format("File: %s", this.file);
    }
}
