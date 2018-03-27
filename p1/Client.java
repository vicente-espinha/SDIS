import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private Client() {}

    public static void main(String[] args) {
        if( args.length != 4) {
            System.out.println("Number of arguments not correct..");
            return;
        }


        try {
            Registry registry = LocateRegistry.getRegistry(null);
            MessageRMI stub = (MessageRMI) registry.lookup(args[0]);
            String response = stub.backup(args[3]);
            System.out.println("response: " + response);
            response = stub.restore();
            System.out.println("response: " + response);
            response = stub.delete();
            System.out.println("response: " + response);
            response = stub.manage();
            System.out.println("response: " + response);
            response = stub.retrieve();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
