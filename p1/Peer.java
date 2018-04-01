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
    public static String peerID;
    public static MC mc;
    public static MDB mdb;
    private static MDR mdr;
    public static ScheduledThreadPoolExecutor executer;
    public static Vector<DataStoreInitializer> dataStoredHash;
    public static Vector<DataPeerInitializer> dataPeerHash;
    public static Hashtable<String, ArrayList<String>> storeCounter;

    public Peer(String[] args) throws IOException {

        peerID = args[1];
        mc = new MC(args[3], args[4]);
        //here will initiate the mdr and the mdb
        mdb = new MDB(args[5], args[6]);
        //this.mdr = new MDR(addr,port);

        executer = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(300);
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

        mdb.sendMessage(filename, repDegree); 
        return;
    }

    public void restore(String filename) throws IOException {

        //String msg = "Restored a file";
        //mc.sendMessage(msg);
        //here sends with the mdr
        return;
    }

    public void delete(String filename) throws IOException {

        MCDelete channel = new MCDelete(filename);
        executer.execute(channel);
        //mc.sendMessage(msg);
        return;
    }

    public void reclaim(String storage) throws IOException {
        String msg = "Managed a file";
        //mc.sendMessage(msg);
        return;
    }

    public String state() throws IOException {
        String state = "State of Peer " + peerID + ":\n";
        state += "Number of files whose backup was initiated: " + dataPeerHash.size() + "\n";

        for (DataPeerInitializer p : dataPeerHash) {
            state += "-> " + p.getPathname() + "\n\tID: " + p.getFileID() + "\n\tReplication Degree: " + p.getRepDeg()
                    + "\n";
            for (int i = 1; i <= p.getNumChunks(); i++) {
                ArrayList<String> peers = storeCounter.get(p.getFileID() + i);
                state += "\t\t Chunk " + i + " - Saved on " + peers.size() + " Peers\n";
            }
        }
        state += "Number of chunks currently backing up: " + dataStoredHash.size() + "\n";
        for (DataStoreInitializer s : dataStoredHash) {
            state += "-> ID: " + s.getFileID() + "\n\tChunk: " + s.getNumber() + "\n\tSize: " + s.getSize() + " KBytes\n\tPerceived Replication Degree: "
                    + storeCounter.get(s.getFileID() + s.getNumber()).size() + "\n";
            
        }
        return state;
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
