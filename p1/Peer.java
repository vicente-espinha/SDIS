import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.lang.String;
import java.io.*;
import java.util.concurrent.*;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Peer implements MessageRMI {
    private String peerID;
    public static MC mc;
    public static MDB mdb;
    private static MDR mdr;
    public static ScheduledThreadPoolExecutor executer;
    public static Vector<DataStoreInitializer> dataStoredHash;
    public static Vector<DataPeerInitializer> dataPeerHash;
    public static Hashtable<String, ArrayList<String>> storeCounter;

    public Peer(String[] args) throws IOException {

        this.peerID = args[1];
        mc = new MC(args[3], args[4], this.peerID);
        //here will initiate the mdr and the mdb
        mdb = new MDB(this.peerID, args[5], args[6]);
        //this.mdr = new MDR(addr,port);

        executer = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(15);
        dataStoredHash = new Vector<DataStoreInitializer>();
        dataPeerHash = new Vector<DataPeerInitializer>();
        storeCounter = new Hashtable<String, ArrayList<String>>();
    }

    public void getMessage() throws IOException {

        executer.execute(mc);
        executer.execute(mdb);
        //executer.execute(this.mdr);
    }

    public void backup(String filename, String repDegree) throws IOException {

        //String msg = "Backup a file";
        //this.mc.sendMessage(msg);
        //String msg = "Backup a file";
        //this.mc.sendMessage(msg);

        mdb.sendMessage(filename, repDegree);
        //here sends to the mdb  
        return;
    }

    public void restore(String filename) throws IOException {

        //String msg = "Restored a file";
        //mc.sendMessage(msg);
        //here sends with the mdr
        return;
    }

    public void delete(String filename) throws IOException {

        String msg = "Deleted a file";
        //mc.sendMessage(msg);
        return;
    }

    public void reclaim(String storage) throws IOException {
        String msg = "Managed a file";
        //mc.sendMessage(msg);
        return;
    }

    public String state() throws IOException {
        String msg = "Retrieved a file";
        //mc.sendMessage(msg);
        return "Retrieve a file";
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 7) {
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

    public static Vector<DataStoreInitializer> getDataStoreInitializerVector() {
        return dataStoredHash;
    }

    public static Vector<DataPeerInitializer> getDataPeerInitializerVector() {
        return dataPeerHash;
    }

    public static MC getMC() {
        return mc;
    }

    public static MDB getMDB() {
        return mdb;
    }
}
