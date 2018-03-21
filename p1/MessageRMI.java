import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageRMI extends Remote {
    String backup() throws RemoteException;
    String restore() throws RemoteException;
    String delete() throws RemoteException;
    String manage() throws RemoteException;
    String retrieve() throws RemoteException;
}
