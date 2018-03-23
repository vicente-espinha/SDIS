import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.lang.String;
import java.io.*;

public class Peer implements MessageRMI {
    private InetAddress group;
    private MulticastSocket msocket;

    public Peer(String[] args) throws IOException{
        try{
            this.group = InetAddress.getByName(args[3]);
            this.msocket = new MulticastSocket(4446);
          System.out.println(args[3]);
        }catch(SocketException e){
          System.out.println("Error opening socket!\n");
        }
        msocket.joinGroup(group);
    }

    private void init(String[] args) throws IOException {



        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        this.msocket.receive(recv);
        System.out.println(buf);
    }

    public String backup() throws IOException {
        String msg = "Backup a file";
        DatagramPacket backup = new DatagramPacket(msg.getBytes(), msg.length(),
                            this.group, 4446);
        this.msocket.send(backup);
        return "Backed a file";
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

    public static void main(String args[]) throws IOException{
        if( args.length != 5) {
            System.out.println("Number of arguments not correct..");
            return;
        }
        //Peer thisPeer = new Peer(args[3]);
        Peer thisPeer = new Peer(args);



                try {

                    MessageRMI stub = (MessageRMI) UnicastRemoteObject.exportObject(thisPeer, 0);

                    // Bind the remote object's stub in the registry
                    Registry registry = LocateRegistry.getRegistry();
                    registry.bind(args[1], stub);

                    System.out.println("Peer ready");
                } catch (Exception e) {
                    System.err.println("Peer exception: " + e.toString());
                    e.printStackTrace();
                }

        thisPeer.init(args);

    }
}
