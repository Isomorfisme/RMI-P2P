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
import java.util.*;
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

    //Method used only when a file is downloaded to add it node
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

    //Recognizes the files in the current node's folder
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

    //Needed to add myFolder and server folder cause of recursive search
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

    //Updates P2Pfile arguments by replacing it
    @Override
    public void updateContents(P2PFile p2PFile) throws RemoteException{
        String name = p2PFile.getFilename();
        folderFiles.replace(name, p2PFile);
    }

    //Gets all the contents of the network by going to the first node and then searching all clients
    @Override
    public HashMap<String, P2PFile> getAllContents(Node node) throws RemoteException {
        if(node.getServerFolder() == null){
            HashMap<String, P2PFile> files = new HashMap<>();
            files = getAllContentsFromTop(node.getMyFolder(), files);
            Iterator<Map.Entry<String, P2PFile>> names = files.entrySet().iterator();
            HashSet<P2PFile> filesSet = new HashSet<P2PFile>();
            while(names.hasNext()){
                Map.Entry<String, P2PFile> next = names.next();
                if(!filesSet.add(next.getValue())){
                    files.remove(next.getKey(), next.getValue());
                    //names.remove();;
                }
            }
            ArrayList<P2PFile> filesToDelete = new ArrayList<>();
            for (P2PFile file: files.values()) {
                for (P2PFile fileToCompare: files.values()) {
                    if(file.getHash().equals(fileToCompare.getHash()) && file != fileToCompare){
                        P2PFile fileToDelete = new P2PFile();
                        if(file.getNames().size() >= fileToCompare.getNames().size()){
                            fileToDelete = fileToCompare;
                        }else{
                            fileToDelete = file;
                        }
                        filesToDelete.add(fileToDelete);
                    }
                }
            }
            for(P2PFile file:filesToDelete){
                files.remove(file.getFilename(),file);
            }
            return files;
        }else{
            return getAllContents(node.getServerFolder());
        }
    }

    //Search all clients from the first node
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
        List<Node> clients = node.getClientFolders();
        if(!clients.isEmpty()){
            for (Node client:clients) {
                files.putAll(getAllContentsFromTop(client, files));
            }
        }
        return files;
    }

    //Downloads file if it's in the network not concurrent
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
                    fileToDownload.newName(name);
                    foldersWithFile.addAll(file.getFolders());
                }
            }
        }
        if(nameIsInNetwork){
            //download
            myFolder.addFile(myFolder, name, fileToDownload, fileToDownload.getBytes());
        }else{
            System.out.println("Name: " + name + " is not in the network.");
        }
    }

    //Print file arguments by name
    @Override
    public void showFile(String name) throws RemoteException {
        Collection<P2PFile> files = getAllContents(myFolder).values();
        boolean show = false;
        for (P2PFile file:files) {
            for (String fname : file.getNames()) {
                if (fname.contains(name)) {
                    show = true;
                    break;
                }
            }
            if(show){
                System.out.println(file);
                show = false;
            }
        }
    }

    //Delete file by name, only in current node folder
    @Override
    public void deleteFile(String name) throws RemoteException{
        Collection<P2PFile> folderFiles = getFiles();
        boolean fileExists = false;
        String fileToDelete = "";
        for (P2PFile p2pfile:folderFiles) {
            if (name.equals(p2pfile.getFilename())) {
                fileExists = true;
                fileToDelete = file.getName();
                File file = new File(myFolder.getStringPath(), name);
                if (file.delete()) {
                    System.out.println("Deleted the file: " + file.getName());
                } else {
                    System.out.println("Failed to delete the file.");
                }
            }
        }
        if(fileExists){
            this.folderFiles.remove(fileToDelete);
        }else{
            System.out.println("This file is not in your folder");
        }
    }

    //Changes file filename
    @Override
    public void changeFilename(String filename, String newFilename) throws RemoteException {
        File file = new File(getStringPath(), filename);
        File newFile = new File(getStringPath(), newFilename);
        file.renameTo(newFile);
        newFile.delete();
    }

    public String toString(){
        return String.format(String.valueOf(folderPath));
    }
}
