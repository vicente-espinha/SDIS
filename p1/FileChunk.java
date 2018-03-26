

public class FileChunk{
    
    String fileID;
    int chunkNumber;
    byte[] body;
    int repDegree;

    public FileChunk(String fileID, int chunkNumber, byte[] body, int repDegree){
        this.fileID = fileID;
        this.chunkNumber = chunkNumber;
        this.body = body;
        this.repDegree = repDegree;
    }

    public byte[] getBody(){
        return this.body;
    }

    public int getNumber(){
        return this.chunkNumber;
    }

    public String getFileID(){
        return this.fileID;
    }

    public int getRepDeg(){
        return this.repDegree;
    }

}