import java.rmi.RemoteException;

public class Node extends NodeFunctionalities {
    //args[0] -> port; args[1] -> port to connect, args[2] -> IP to connect
    public static void main(String[] args) throws RemoteException {
        Node node = new Node();
        node.startServer(args);
        if(args.length > 1){  //There is a port to connect
            node.connectClient(args);
        }
    }
}
