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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class NodeImplementation extends UnicastRemoteObject implements Node {
    //private static final long serialVersionUID = 6529685098267757690L;

    File file = null;
    List<String> clientUsernames = new ArrayList<>();
    List<Node> clientFolders = new ArrayList<>();
    HashMap<String, P2PFile> files =  new HashMap<>();
    Path folderPath;

    public NodeImplementation(int port) throws IOException {
        super();
        String path = new File(".").getCanonicalPath();
        folderPath = Files.createDirectories(Paths.get(path + "\\files" + port));
    }

    @Override
    public Collection<P2PFile> getFiles() throws RemoteException{
        return files.values();
    }

    @Override
    public HashMap<String, P2PFile> getContents() throws RemoteException {
        try (Stream<Path> paths = Files.walk(Paths.get(String.valueOf(folderPath)))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        file = new File(String.valueOf(path));
                        try {
                            files.put(file.getName(), new P2PFile(path, file.getName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    @Override
    public void register(Node clientFolder, String username) throws RemoteException {
        this.clientUsernames.add(username);
        this.clientFolders.add(clientFolder);
    }

    @Override
    public void updateContents(P2PFile p2PFile) {
        System.out.println(p2PFile);
        String name = p2PFile.getFile().getName();
        files.replace(name, files.get(name), p2PFile);
        System.out.println(files);
    }

    public String toString(){
        return String.format(String.valueOf(folderPath));
    }
}
