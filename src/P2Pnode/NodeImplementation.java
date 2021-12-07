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
    HashMap<String, P2PFile> folderFiles =  new HashMap<>();
    Path folderPath;
    String stringPath;
    Node serverFolder;
    Node myFolder;

    public NodeImplementation(int port) throws IOException {
        super();
        String path = new File(".").getCanonicalPath();
        folderPath = Files.createDirectories(Paths.get(path + "\\files" + port));
        stringPath = String.valueOf(folderPath);
    }

    @Override
    public Collection<P2PFile> getFiles() throws RemoteException{
        return folderFiles.values();
    }

    @Override
    public List<Node> getClientFolders() throws RemoteException{
        return clientFolders;
    }

    @Override
    public Node getServerFolder() throws RemoteException{
        return serverFolder;
    }

    @Override
    public Node getMyFolder() throws RemoteException{
        return myFolder;
    }

    @Override
    public HashMap<String, P2PFile> getFolderFiles() throws RemoteException{
        return folderFiles;
    }

    @Override
    public String getStringPath() throws RemoteException{
        return stringPath;
    }

    @Override
    public void recognizeFiles() throws RemoteException{
        if(folderFiles.isEmpty()){
            try (Stream<Path> paths = Files.walk(Paths.get(stringPath))) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            file = new File(String.valueOf(path));
                            try {
                                folderFiles.put(file.getName(), new P2PFile(path, file.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public HashMap<String, P2PFile> getContents(Node node) throws RemoteException {
       return node.getFolderFiles();
    }

    @Override
    public void register(Node clientFolder, String username) throws RemoteException {
        this.clientUsernames.add(username);
        this.clientFolders.add(clientFolder);
    }

    @Override
    public void connect(Node serverFolder) throws RemoteException{
        this.serverFolder = serverFolder;
    }

    @Override
    public void putMyFolder(Node myFolder) throws RemoteException{
        this.myFolder = myFolder;
    }

    @Override
    public void updateContents(P2PFile p2PFile) throws RemoteException{
        String name = p2PFile.getFilename();
        folderFiles.replace(name, folderFiles.get(name), p2PFile);
    }

    @Override
    public HashMap<String, P2PFile> getAllContents(Node node) throws RemoteException {
        if(node.getServerFolder() == null){
            HashMap<String, P2PFile> files = new HashMap<>();
            return getAllContentsFromTop(node.getMyFolder(), files);
        }else{
            return getAllContents(node.getServerFolder());
        }
    }

    @Override
    public HashMap<String, P2PFile> getAllContentsFromTop(Node node, HashMap<String, P2PFile> files) throws RemoteException {
        HashMap<String, P2PFile> actualNodeFiles = node.getContents(node);
        /*Collection<P2PFile> allFiles = files.values();
        if(!files.isEmpty()){
            for(P2PFile nodeFile: actualNodeFiles.values()){
                for (P2PFile file: allFiles) {
                    if(nodeFile.getHash().equals(file.getHash())){

                    }
                }
            }
        }*/
        files.putAll(actualNodeFiles);
        System.out.println(node);
        //System.out.println(files.values());
        List<Node> clients = node.getClientFolders();
        if(!clients.isEmpty()){
            System.out.println("!");
            for (Node client:clients) {
                files.putAll(getAllContentsFromTop(client, files));
            }
        }
        return files;
    }

    public String toString(){
        return String.format(String.valueOf(folderPath));
    }
}
