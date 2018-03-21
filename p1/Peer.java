import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements MessageRMI {

    public Peer() {}

    public String backup() {
        return "Backup a file";
    }

    public String restore() {
        return "Restore a file";
    }

    public String delete() {
        return "Delete a file";
    }

    public String manage() {
        return "Manage a file";
    }

    public String retrieve() {
        return "Retrieve a file";
    }

    public static void main(String args[]) {
        if( args.length != 5) {
            System.out.println("Number of arguments not correct..");
            return;
        }

        try {
            Peer obj = new Peer();
            MessageRMI stub = (MessageRMI) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(args[1], stub);

            System.err.println("Peer ready");
        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
