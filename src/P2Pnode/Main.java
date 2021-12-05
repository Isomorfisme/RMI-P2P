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
        System.out.println("Node will list all files from all registered nodes");
        while (true) {
            getInstruction();
        }
    }

    public static void startServer() throws RemoteException{
        try {
            myPort = Integer.parseInt(params[0]);
            myFolder = new NodeImplementation(myPort);
            myRegistry = startRegistry(myPort);
            myRegistry.bind("Folder", myFolder);
            System.err.println("Server ready 4, rmi_registry started automatically");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }

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
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); e.printStackTrace();
        }
    }

    public static void listFiles(){
        try{
            HashMap<String, P2PFile> contents = myFolder.getContents();
            Collection <P2PFile> files = myFolder.getFiles();
            System.out.println("Folder: " + myFolder);
            System.out.println("File Names: " + files);
            for (P2PFile file:files) {
                System.out.println(file.name);
                System.out.println(file.hash);
            }
        } catch (Exception e) {
            System.err.println(("Client exception: " + e.toString())); e.printStackTrace();
        }
    }

    public static void getInstruction() throws RemoteException {
        Scanner scanner = new Scanner(System.in);
        List instructions = Arrays.asList("listfiles", "setname", "setkeywords", "setdescription");
        System.out.println("Write one instruction: " + instructions);
        String instruction = scanner.nextLine();
        if(instructions.contains(instruction)){
            if(instruction.equals("listfiles")){
                listFiles();
            }else
            if(instruction.equals("setname")){
                System.out.println("Write the filename of the file you want to rename");
                String filename = scanner.nextLine();
                HashMap<String, P2PFile> files = myFolder.getContents();
                if(files.containsKey(filename)) {
                    P2PFile file = files.get(filename);
                    file.setName();
                }else{
                    System.out.println("This file does not exist in this folder");
                }
            }
        }else{
            System.out.println("Invalid instruction!");
        }
    }
}



