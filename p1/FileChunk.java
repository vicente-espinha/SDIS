import java.io.*;
import java.nio.*;
import java.nio.file.Files;

public class FileChunk {

    String fileID;
    int chunkNumber;
    byte[] body;
    int repDegree;
    String filename;

    public FileChunk(String fileID, int chunkNumber, byte[] body, int repDegree) {
        this.fileID = fileID;
        this.chunkNumber = chunkNumber;
        this.body = body;
        this.repDegree = repDegree;
    }

    public byte[] getBody() {
        return this.body;
    }

    public int getNumber() {
        return this.chunkNumber;
    }

    public String getFileID() {
        return this.fileID;
    }

    public int getRepDeg() {
        return this.repDegree;
    }

    public String getFileName() {
        return this.filename;
    }


    //saves chunk localy
    public void save(String peerID) {
        File f = new File(peerID + this.fileID + this.chunkNumber);
        if (!f.exists() && !f.isDirectory()) {
            try {
                this.filename = peerID + this.fileID + this.chunkNumber;
                FileOutputStream out = new FileOutputStream(this.filename);
                out.write(this.body); //writes in bynary file
                out.close(); //closes output stream
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}