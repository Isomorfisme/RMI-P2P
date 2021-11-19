import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeFunctionalities extends Remote {
    //void connectClient(String[] args) throws RemoteException;
    //void startServer() throws RemoteException;
    String hello() throws RemoteException;
}
