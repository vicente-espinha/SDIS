import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.lang.String;
import java.io.*;

public class Peer implements MessageRMI {
    private String peerID;
    private MC mc;
    private MDB mdb;
    private MDR mdr;

    public Peer(String[] args) throws IOException{
        
        this.peerID = args[1];
        this.mc = new MC(args[3], args[4]);
        //here will initiate the mdr and the mdb
        this.mdb = new MDB(this.peerID, args[5], args[6]);
        //this.mdr = new MDR(addr,port);       
       
    }

    public void getMessage() throws IOException {

        this.mdb.receiveMessage();
    }

    public String backup(String filename) throws IOException {

        //String msg = "Backup a file";
        //this.mc.sendMessage(msg);
        //String msg = "Backup a file";
        //this.mc.sendMessage(msg);
        
        this.mdb.sendMessage(filename);
        //here sends to the mdb  
        return "Backed a file";
    }

    public String restore() throws IOException {

        String msg = "Restored a file";
        this.mc.sendMessage(msg);
        //here sends with the mdr
        return "Restore a file";
    }

    public String delete() throws IOException {

        String msg = "Deleted a file";
        this.mc.sendMessage(msg);
        return "Delete a file";
    }

    public String manage() throws IOException {
        String msg = "Managed a file";
        this.mc.sendMessage(msg);
        return "Manage a file";
    }

    public String retrieve() throws IOException {
        String msg = "Retrieved a file";
        this.mc.sendMessage(msg);
        return "Retrieve a file";
    }

    public static void main(String args[]) throws IOException{
        if( args.length != 7) {
            System.out.println("Number of arguments not correct..");
            return;
        }

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

        thisPeer.getMessage();

    }
}
