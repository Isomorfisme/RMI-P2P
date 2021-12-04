package P2Pnode;

import common.P2PFile;
import common.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class NodeImplementation extends UnicastRemoteObject implements Node {
    //private static final long serialVersionUID = 6529685098267757690L;

    P2PFile file = null;
    List<String> clientUsernames = new ArrayList<>();
    List<Node> clientFolders = new ArrayList<>();
    HashMap<String, P2PFile> files =  new HashMap<>();
    Path folderPath;

    public NodeImplementation(int port) throws IOException {
        super();
        String path = new File(".").getCanonicalPath();
        folderPath = Files.createDirectories(Paths.get(path + "\\files" + port));
        this.file = new P2PFile(port);
    }

    @Override
    public P2PFile getFile() throws RemoteException{
        return this.file;
    }

    @Override
    public HashMap<String, P2PFile> getContents() throws RemoteException {
        try (Stream<Path> paths = Files.walk(Paths.get(String.valueOf(folderPath)))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
                   /* .forEach(files.put(); */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register(Node clientFolder, String username) throws RemoteException {
        this.clientUsernames.add(username);
        this.clientFolders.add(clientFolder);
    }

    public String toString(){
        return String.format(String.valueOf(folderPath));
    }
}
