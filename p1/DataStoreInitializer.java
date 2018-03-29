import java.io.*;
import java.nio.*;

public class DataStoreInitializer {

    String fileID;
    int size;//KBytes
    int repDegree;

    public DataStoreInitializer(String fileID,int size, int repDegree) {
        this.fileID = fileID;
        this.repDegree = repDegree;
        this.size = size;
    }


    public String getFileID() {
        return this.fileID;
    }

    public int getSize() {
        return this.size;
    }


    public int getRepDeg() {
        return this.repDegree;
    }



}