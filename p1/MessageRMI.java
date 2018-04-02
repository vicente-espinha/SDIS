import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;

public interface MessageRMI extends Remote {
    void backup(String filename, String repDegree) throws RemoteException, IOException;
    String restore(String filename) throws RemoteException, IOException;
    void delete(String filename) throws RemoteException, IOException;
    void reclaim(String storage) throws RemoteException, IOException;
    String state() throws RemoteException, IOException;
}
