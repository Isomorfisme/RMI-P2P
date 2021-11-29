import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Scanner;

public class NodeFunctionalities {
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

    int myPort = 0;
    public Folder myFolder = null;
    public Registry myRegistry = null;
    public Folder serverFolder = null;
    public Registry serverRegistry = null;

    public void connectToServer(String[] args) throws RemoteException{
        int port = Integer.parseInt(args[1]);
        String host = (args.length < 3) ? null : args[2];
        try {
            this.serverRegistry = LocateRegistry.getRegistry(host, port);
            this.serverFolder = (Folder) this.serverRegistry.lookup("Folder" + port);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Write your username: ");
            String username = scanner.nextLine();
            System.out.println("Register folder with username: " + username);
            this.serverFolder.register(this.myFolder, username);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); e.printStackTrace();
        }
    }

    public void listFiles(){
        try{
            Folder clientFolder = this.myFolder.getFolder();
            File file = this.myFolder.getFile();
            System.out.println(this.myFolder);
            System.out.println(clientFolder);
            System.out.println("File: " + file);
        } catch (Exception e) {
            System.err.println(("Client exception: " + e.toString())); e.printStackTrace();
        }
    }

    public void startServer(String[] args) throws RemoteException{
        try {
            this.myPort = Integer.parseInt(args[0]);
            this.myFolder = new FolderImplementation(this.myPort);
            this.myRegistry = startRegistry(this.myPort);
            this.myRegistry.bind("Folder" + this.myPort, this.myFolder);
            System.err.println("Server ready 4, rmi_registry started automatically");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }

    /*public void notify_clients(){
        for (int port : this.clientRegistry.keySet()){
            try {
                System.out.println("Server port: "+ this.myPort+ " Calling the client with the port: "+ port);
                this.clientFolders.get(port).getFile(port);
            }catch(RemoteException e){
                System.out.println("error in call");
                this.clientRegistry.remove(port);
                this.clientFolders.remove(port);
            }
        }
    }*/
}
