

public class Message{
    public enum MessageType {
        PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
    }
    private static final String CRLF = "\r\n";
    
    String version;
    public Message(String version){
        this.version = version;
    }

    public String generateBackupReq(String senderID, String fileName, FileChunk chunk){
        String reqMsg;
        reqMsg = MessageType.PUTCHUNK + " " + generateHeader(senderID, fileName) 
        + " " + chunk.getNumber() + " " + chunk.getRepDeg() + " " + CRLF + CRLF + chunk.getBody();
        return reqMsg;
    }

    public String generateBackupAnswer(String senderID, String fileName, FileChunk chunk){
        String reqMsg;
        reqMsg = MessageType.STORED + " " + generateHeader(senderID, fileName) 
        + " " + chunk.getNumber() + " " + CRLF + CRLF;
        return reqMsg;
    }

    public String generateRestoreReq(String senderID, String fileName, FileChunk chunk){
        String reqMsg;
        reqMsg = MessageType.GETCHUNK + " " + generateHeader(senderID, fileName) 
        + " " + chunk.getNumber() + " " + CRLF + CRLF;
        return reqMsg;
    }

    public String generateRestoreAnswer(String senderID, String fileName, FileChunk chunk){
        String reqMsg;
        reqMsg = MessageType.CHUNK + " " + generateHeader(senderID, fileName) 
        + " " + chunk.getNumber() + " " + CRLF + CRLF + chunk.getBody();
        return reqMsg;
    }

    public String generateDeleteReq(String senderID, String fileName, FileChunk chunk){
        String reqMsg;
        reqMsg = MessageType.DELETE + " " + generateHeader(senderID, fileName) 
        + " " + CRLF + CRLF;
        return reqMsg;
    }

    public String generateRemovedAnswer(String senderID, String fileName, FileChunk chunk){
        String reqMsg;
        reqMsg = MessageType.DELETE + " " + generateHeader(senderID, fileName) 
        + " " + chunk.getNumber() + " " + CRLF + CRLF;
        return reqMsg;
    }

    private String generateHeader(String senderID, String fileName){
        String header;
        header = this.version + " " + senderID + " " + generateFileID(fileName);
        return header;
    }

    private String generateFileID(String fileName) {
        //TODO encryption SHA256 for fileID
        return fileName;
    }

}