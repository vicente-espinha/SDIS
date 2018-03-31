import java.io.*;
import java.nio.*;

public class DataPeerInitializer {

    String pathname;
    String fileID;
    int repDegree;
    int numChunks;

    public DataPeerInitializer(String pathname,String fileID, int repDegree, int numChunks) {
        this.fileID = fileID;
        this.repDegree = repDegree;
        this.pathname = pathname;
        this.numChunks = numChunks;
    }


    public String getFileID() {
        return this.fileID;
    }

    public String getPathname() {
        return this.pathname;
    }


    public int getRepDeg() {
        return this.repDegree;
    }

    public int getNumChunks() {
        return this.numChunks;
    }



}