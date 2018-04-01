
import java.io.*; 
import java.nio.*; 
 
public class DataStoreInitializer { 
 
    String fileID; 
    int number; 
    double size;//KBytes 
    int repDegree; 
    String filename;
 
    public DataStoreInitializer(String fileID, int number, int size, int repDegree,String filename) { 
        this.fileID = fileID; 
        this.number = number; 
        this.repDegree = repDegree; 
        this.size = size / 1000.0; 
        this.filename = filename;
    } 
 
 
    public String getFileID() { 
        return this.fileID; 
    } 
 
    public int getNumber() { 
        return this.number; 
    } 
 
    public double getSize() { 
        return this.size; 
    } 
 
 
    public int getRepDeg() { 
        return this.repDegree; 
    } 
    
    public String getFileName(){
        return this.filename;
    }
 
 
}