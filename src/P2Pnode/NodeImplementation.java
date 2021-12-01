package P2Pnode;

import common.File;
import common.Node;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class NodeImplementation extends UnicastRemoteObject implements Node {
    //private static final long serialVersionUID = 6529685098267757690L;

    File file = null;
    List<String> clientUsernames = new ArrayList<>();
    List<Node> clientFolders = new ArrayList<>();

    public NodeImplementation(int port) throws RemoteException {
        super();
        this.file = new File(port);
    }

    @Override
    public File getFile() throws RemoteException{
        return this.file;
    }

    @Override
    public Node getFolder() throws RemoteException{
        if (this.clientFolders.isEmpty()){
            return null;
        }else {
            return this.clientFolders.get(0);
        }
    }

    @Override
    public void register(Node clientFolder, String username) throws RemoteException {
        this.clientUsernames.add(username);
        this.clientFolders.add(clientFolder);
    }

    public String toString(){
        return String.format("common.File: %s", this.file);
    }
}
