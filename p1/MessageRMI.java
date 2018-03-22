import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;

public interface MessageRMI extends Remote {
    String backup() throws RemoteException, IOException;;
    String restore() throws RemoteException;
    String delete() throws RemoteException;
    String manage() throws RemoteException;
    String retrieve() throws RemoteException;
}
