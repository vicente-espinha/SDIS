import java.io.*;
import java.nio.*;

public class DataStoreInitializer {

    String fileID;
    int number;
    int size;//KBytes
    int repDegree;

    public DataStoreInitializer(String fileID, int number, int size, int repDegree) {
        this.fileID = fileID;
        this.number = number;
        this.repDegree = repDegree;
        this.size = size;
    }


    public String getFileID() {
        return this.fileID;
    }

    public int getNumber() {
        return this.number;
    }

    public int getSize() {
        return this.size;
    }


    public int getRepDeg() {
        return this.repDegree;
    }



}