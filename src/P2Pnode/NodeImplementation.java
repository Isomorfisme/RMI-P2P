package P2Pnode;

import common.P2PFile;
import common.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    Integer myPort;
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
    public void addFile(Node myFolder, String name, P2PFile file, byte[] fileBytes) throws RemoteException{
        folderFiles.put(name, file);
        File newFile = new File(myFolder.getStringPath(), name);
        try (FileOutputStream stream = new FileOutputStream(newFile)) {
            stream.write(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                                folderFiles.put(file.getName(), new P2PFile(path, file.getName(), myFolder));
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
    public void putMyFolder(Node myFolder, Integer myPort) throws RemoteException{
        this.myFolder = myFolder;
        this.myPort = myPort;
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
        Collection<P2PFile> allFiles = files.values();
        HashMap<String, P2PFile> repeatedFiles = new HashMap<>();
        if(!files.isEmpty()){
            for(P2PFile nodeFile: actualNodeFiles.values()){
                for (P2PFile file: allFiles) {
                    if(nodeFile.getHash().equals(file.getHash())){
                        file.addFolder(node.getMyFolder());
                        file.addFilename(nodeFile.getFilename());
                        file.addName(nodeFile.getNames());
                        file.addKeywords(nodeFile.getKeywords());
                        file.addDescription(nodeFile.getDescription());
                        repeatedFiles.put(nodeFile.getName(), file);
                    }
                }
            }
        }
        files.putAll(actualNodeFiles);
        files.putAll(repeatedFiles);
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

    @Override
    public void downloadFile(String name) throws RemoteException{
        Collection<P2PFile> files = getAllContents(myFolder).values();
        P2PFile fileToDownload = new P2PFile();
        boolean nameIsInNetwork = false;
        ArrayList<Node> foldersWithFile = new ArrayList<>();
        for (P2PFile file:files) {
            for (String fname:file.getNames()) {
                if(name.equals(fname)){
                    nameIsInNetwork = true;
                    name = file.getName();
                    fileToDownload = file;
                    foldersWithFile.addAll(file.getFolders());
                }
            }
        }
        if(nameIsInNetwork){
            //download
            myFolder.addFile(myFolder, name, fileToDownload, fileToDownload.getBytes());
            System.out.println(foldersWithFile);
        }else{
            System.out.println("Name: " + name + " is not in the network.");
        }
    }

    public String toString(){
        return String.format(String.valueOf(folderPath));
    }
}
