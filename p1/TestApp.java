import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

    private TestApp() {
    }

    public static void main(String[] args) {
        if (args.length < 2 && args.length > 4) {
            System.out.println("Number of arguments not correct. It should be invoked as: \n"
                    + "$ java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2> \n"
                    + "Note: <peer_ap> should be sent as <host>/<server_access_point>\n"
                    + "Note2: <opnd_2> is only appliable to the backup protocol!");
            return;
        }

        String[] split = args[0].split("/");
        if (split.length != 2) {
            System.out.println("<peer_ap> should be sent as <host>/<server_access_point>\n");
            return;
        }
        try {

            Registry registry = LocateRegistry.getRegistry(split[0]);
            MessageRMI stub = (MessageRMI) registry.lookup(split[1]);
            switch (args[1]) {
            case "BACKUP":
                if (args.length == 4) {
                    String response = stub.backup(args[2], args[3]);
                    System.out.println(response);
                } else
                    System.out.println("Missing Arguments. For the backup protocol provide all the arguments");
                break;
            case "RESTORE":
                if (args.length == 3) {
                    String response = stub.restore(args[2]);
                    System.out.println(response);
                } else
                    System.out.println("Wrong number of arguments");
                break;
            case "DELETE":
                if (args.length == 3)
                    stub.delete(args[2]);
                else
                    System.out.println("Wrong number of arguments");
                break;
            case "RECLAIM":
                if (args.length == 3)
                    stub.reclaim(args[2]);
                else
                    System.out.println("Wrong number of arguments");
                break;
            case "STATE":
                if (args.length == 2) {
                    String response = stub.state();
                    System.out.println(response);
                } else
                    System.out.println("Wrong number of arguments");
                break;
            default:
                System.out.println("Protocol " + args[1] + " doesn't exist.");
                break;
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
