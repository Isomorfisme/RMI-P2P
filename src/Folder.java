import java.rmi.Remote;

public interface Folder extends Remote {
    File getFile();
}
