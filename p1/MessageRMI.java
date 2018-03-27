import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;

public interface MessageRMI extends Remote {
    String backup(String filename) throws RemoteException, IOException;
    String restore() throws RemoteException, IOException;
    String delete() throws RemoteException, IOException;
    String manage() throws RemoteException, IOException;
    String retrieve() throws RemoteException, IOException;
}
