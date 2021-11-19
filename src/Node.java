import java.rmi.RemoteException;

public class Node extends NodeFunctionalitiesImplementation {
    public static void main(String[] args) throws RemoteException {
        startServer();
        if(args.length >= 2) {
            connectClient(args);
        }
    }
}
