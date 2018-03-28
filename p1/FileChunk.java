
public class FileChunk {

    byte[] fileID;
    int chunkNumber;
    byte[] body;
    int repDegree;

    public FileChunk(byte[] fileID, int chunkNumber, byte[] body, int repDegree) {
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

    public byte[] getFileID() {
        return this.fileID;
    }

    public int getRepDeg() {
        return this.repDegree;
    }

    public void save(String peerID) {

        //opens file with filename = fileID + chunknumber
        FileOutputStream out = new FileOutputStream(peerID + String(this.fileID) + Integer.toString(this.chunkNumber));
        out.write(this.body); //writes in bynary file
        out.close(); //closes output stream
    }

}