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
    public static MDR mdr;
    public static ScheduledThreadPoolExecutor executer;
    public static Vector<DataStoreInitializer> dataStoredHash;
    public static Vector<DataPeerInitializer> dataPeerHash;
    public static Hashtable<String, ArrayList<String>> storeCounter;
    public static ArrayList<byte[]> restoreTemp;
    public static Boolean currentlyRestoring;
    public static String fileRestoring;
    public static ArrayList<String> getchunks;

    public Peer(String[] args) throws IOException {

        peerID = args[1];
        mc = new MC(args[3], args[4]);
        mdb = new MDB(args[5], args[6]);
        mdr = new MDR(args[7], args[8]);

        executer = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(300);
        dataStoredHash = new Vector<DataStoreInitializer>();
        dataPeerHash = new Vector<DataPeerInitializer>();
        storeCounter = new Hashtable<String, ArrayList<String>>();
        restoreTemp = new ArrayList<byte[]>();
        currentlyRestoring = false;
        fileRestoring = "";
        getchunks = new ArrayList<String>();
    }

    public void getMessage() throws IOException {

        executer.execute(mc);
        executer.execute(mdb);
        executer.execute(mdr);
    }

    public String backup(String filename, String repDegree) throws IOException {
        for (int i = 0; i < Peer.getDataPeerInitializerVector().size(); i++) {
            if (Peer.getDataPeerInitializerVector().get(i).getPathname().equals(filename))
                return filename + " is already backed up!";
        }
        mdb.sendMessage(filename, repDegree);
        return "Backing up " + filename;
    }

    public String restore(String filename) throws IOException {
        if (!currentlyRestoring) {
            for (int i = 0; i < dataPeerHash.size(); i++) {
                if (dataPeerHash.get(i).getPathname().equals(filename)) {

                    fileRestoring = filename;
                    currentlyRestoring = true;
                    mc.sendMessage(i);
                    return "Restoring file " + filename;
                }
            }
            return "File " + filename + " not backed up!";
        } else {
            return "Already restoring the file " + fileRestoring + "!";
        }
    }

    public void delete(String filename) throws IOException {

        MCDelete channel = new MCDelete(filename);
        executer.execute(channel);
        return;
    }

    public void reclaim(String storage) throws IOException {
        
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
        double occupied = 0.0;
        for (DataStoreInitializer s : dataStoredHash) {
            state += "-> ID: " + s.getFileID() + "\n\tChunk: " + s.getNumber() + "\n\tSize: " + s.getSize()
                    + " KBytes\n\tPerceived Replication Degree: "
                    + storeCounter.get(s.getFileID() + s.getNumber()).size() + "\n";
            occupied += s.getSize();

        }
        state += "Space occupied by all chunks: " + occupied + " KBytes.";
        return state;
    }

    public static void main(String args[]) throws IOException {
        if (args.length != 9) {
            System.out.println("Number of arguments not correct..\n" + "java Peer <VersionID> <PeerID> <AccessPoint>"
                    + " <MC_addr> <MC_port> <MDB_addr> <MDB_port> <MDR_addr> <MDR_port>");
            return;
        }
        if(!args[1].matches("[0-9]+")){
            System.out.println("Peer id must be only numbers!");
            return; 
        }

        Peer thisPeer = new Peer(args);

        try {

            MessageRMI stub = (MessageRMI) UnicastRemoteObject.exportObject(thisPeer, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(args[2], stub);

            System.out.println("Peer ready");
            thisPeer.getMessage();
            
        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }


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

    public static MDR getMDR() {
        return mdr;
    }

    public static ArrayList<byte[]> getRestoreTemp() {
        return restoreTemp;
    }

    public static Boolean getCurrentlyRestoring() {
        return currentlyRestoring;
    }

    public static String getFileRestoring() {
        return fileRestoring;
    }

    public static ArrayList<String> getGetChunks() {
        return getchunks;
    }
}
