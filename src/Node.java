import java.rmi.RemoteException;

public class Node extends NodeFunctionalitiesImplementation {
    public static void main(String[] args) throws RemoteException {
        Node node = new Node();
        node.startServer();
        node.connectClient(args);

    }
}
