import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NodeFunctionalitiesImplementation implements NodeFunctionalities{
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

    @Override
    public void connectClient(String[] args) throws RemoteException{
        int port = Integer.parseInt(args[1]);
        String host = (args.length < 3) ? null : args[2];
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            NodeFunctionalities stub = (NodeFunctionalities) registry.lookup("Hello");
            String response = stub.hello();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString()); e.printStackTrace();
        }
    }

    @Override
    public void startServer(String[] args) throws RemoteException{
        try {
            int port = Integer.parseInt(args[0]);
            NodeFunctionalitiesImplementation obj = new NodeFunctionalitiesImplementation();
            NodeFunctionalities remoteobj = (NodeFunctionalities) UnicastRemoteObject.exportObject(obj, port);
            Registry registry = startRegistry(port);
            registry.bind("Hello", remoteobj);
            System.err.println("Server ready 4, rmi_registry started automatically");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString()); e.printStackTrace();
        }
    }

    @Override
    public String hello() throws RemoteException{
        return "client connected";
    }

}
