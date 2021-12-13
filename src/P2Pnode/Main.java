package P2Pnode;

import common.P2PFile;
import common.Node;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class Main{
    private static Registry startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry= LocateRegistry.getRegistry(RMIPortNum);
            registry.list( );
            // The above call will throw an exception
            // if the registry does not already exist
            return registry;
        }
        catch (RemoteException ex) {
            // No valid registry at that port.
            System.out.println(
                    "RMI registry cannot be located at port " + RMIPortNum);
            Registry registry= LocateRegistry.createRegistry(RMIPortNum);
            System.out.println(
                    "RMI registry created at port " + RMIPortNum);
            return registry;
        }
    }

    public static int myPort = 0;
    public static Node myFolder = null;
    public static Registry myRegistry = null;
    public static Node serverFolder = null;
    public static Registry serverRegistry = null;
    public static String[] params;

    //args[0] -> port; args[1] -> port to connect, args[2] -> IP to connect
    public static void main(String[] args) throws RemoteException, InterruptedException {
        params = args;
        startServer();
        if (args.length > 1) {  //There is a port to connect
            connectToServer();
        }
        myFolder.recognizeFiles();
        Thread.sleep(50); //To start (print) later than the rmi_registry start
        while (true) {
            getInstruction();
        }
    }

    //Opens the node registry and binds the node
    public static void startServer() throws RemoteException{
        try {
            myPort = Integer.parseInt(params[0]);
            myFolder = new NodeImplementation(myPort);
            myRegistry = startRegistry(myPort);
            myRegistry.bind("Folder", myFolder);
            myFolder.putMyFolder(myFolder, myPort);
            System.err.println("Server ready 4, rmi_registry started automatically");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }

    //Connect current node to another
    public static void connectToServer() throws RemoteException{
        int port = Integer.parseInt(params[1]);
        String host = (params.length < 3) ? null : params[2];
        try {
            serverRegistry = LocateRegistry.getRegistry(host, port);
            serverFolder = (Node) serverRegistry.lookup("Folder");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Write your username: ");
            String username = scanner.nextLine();
            System.out.println("Register folder with username: " + username);
            serverFolder.register(myFolder, username);
            myFolder.connect(serverFolder);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); e.printStackTrace();
        }
    }

    //Lists all files in network
    public static void listFiles(){
        try{
            HashMap<String, P2PFile> contents = myFolder.getAllContents(myFolder);
            Collection<P2PFile> allFiles = contents.values();
            for (P2PFile file:allFiles) {
                System.out.println(file);
            }
        } catch (Exception e) {
            System.err.println(("Client exception: " + e.toString())); e.printStackTrace();
        }
    }

    //Scans and performs the instruction wanted
    public static void getInstruction() throws RemoteException {
        List instructions = Arrays.asList("listfiles", "setname", "setkeywords", "setfilename", "setdescription", "downloadfile", "showfile", "deletefile");
        System.out.println("Write one instruction: " + instructions);
        Scanner scanner = new Scanner(System.in);
        String instruction = scanner.nextLine();
        if(instructions.contains(instruction)){
            if(instruction.equals("listfiles")){
                listFiles();
            }else
            if(instruction.equals("setname") || instruction.equals("setkeywords") || instruction.equals("setfilename") || instruction.equals("setdescription")){
                System.out.println("Write the filename of the file you want to rename");
                String filename = scanner.nextLine();
                HashMap<String, P2PFile> files = myFolder.getContents(myFolder);
                if(files.containsKey(filename)) {
                    if(instruction.equals("setname")){
                        files.get(filename).setName();
                    }else if(instruction.equals("setkeywords")){
                        files.get(filename).setKeywords();
                    }else if(instruction.equals("setfilename")){
                        String newFilename = files.get(filename).setFilename();
                        myFolder.changeFilename(filename, newFilename);
                    }else{ //if(instruction.equals("setdescription")){ (not necessary cause of upper if)
                        files.get(filename).setDescription();
                    }
                    myFolder.updateContents(files.get(filename));
                }else{
                    System.out.println("This file does not exist in this folder");
                }
            }else
            if(instruction.equals("downloadfile")){
                System.out.println("Write the name of the file you want to download");
                String name = scanner.nextLine();
                myFolder.downloadFile(name);
            }else
            if(instruction.equals("showfile")){
                System.out.println("Write the name of the file you want to show (works with partial search)");
                String name = scanner.nextLine();
                myFolder.showFile(name);
            }else
            if(instruction.equals("deletefile")){
                System.out.println("Write the filename of the file you want to delete");
                String name = scanner.nextLine();
                myFolder.deleteFile(name);
            }
        }else{
            System.out.println("Invalid instruction!");
        }
    }
}



