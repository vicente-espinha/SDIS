import java.io.*;
import java.nio.*;

public class DataPeerInitializer {

    String pathname;
    String fileID;
    int repDegree;

    public DataPeerInitializer(String pathname,String fileID, int repDegree) {
        this.fileID = fileID;
        this.repDegree = repDegree;
        this.pathname = pathname;
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



}