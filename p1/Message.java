import java.io.File;
import java.io.IOException;
import java.security.*;
import java.nio.charset.*;

public class Message{

    static final String PUTCHUNK = "PUTCHUNK";
    static final String STORED = "STORED";
    static final String GETCHUNK = "GETCHUNK";
    static final String CHUNK = "CHUNK";
    static final String DELETE = "DELETE";
    static final String REMOVED = "REMOVED";
    static final String SPACE = " ";
    static final String CRLF = "\r\n";
    
    String version;
    public Message(String version){
        this.version = version;
    }

    public byte[] generateBackupReq(String senderID, FileChunk chunk){

        String reqMsg = generateHeader(PUTCHUNK, senderID);
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getFileID());

        reqMsg = SPACE + chunk.getNumber() + SPACE + chunk.getRepDeg() + SPACE + CRLF + CRLF;
        byte[] reqMsgArr1 = joinArrays(reqMsg.getBytes(), chunk.getBody());

        return joinArrays(reqMsgArr, reqMsgArr1);
    }

    public byte[] generateBackupAnswer(String senderID, FileChunk chunk){

        String reqMsg = generateHeader(STORED, senderID); 
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getFileID());

        reqMsg = SPACE + chunk.getNumber() + SPACE + CRLF + CRLF;
        
        return joinArrays(reqMsgArr, reqMsg.getBytes());
    }

    public byte[] generateRestoreReq(String senderID, FileChunk chunk){

        String reqMsg = generateHeader(GETCHUNK, senderID);
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getFileID());


        reqMsg = SPACE + chunk.getNumber() + SPACE + CRLF + CRLF;

        return joinArrays(reqMsgArr, reqMsg.getBytes());
    }

    public byte[] generateRestoreAnswer(String senderID, FileChunk chunk){

        String reqMsg = generateHeader(CHUNK, senderID);
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getFileID());


        reqMsg = SPACE + chunk.getNumber() + SPACE + chunk.getRepDeg() + SPACE + CRLF + CRLF;
        byte[] reqMsgArr1 = joinArrays(reqMsg.getBytes(), chunk.getBody());

        return joinArrays(reqMsgArr, reqMsgArr1);
    }

    public byte[] generateDeleteReq(String senderID, FileChunk chunk){
        
        String reqMsg = generateHeader(DELETE, senderID);
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getFileID());

        reqMsg = SPACE + CRLF + CRLF;
        return joinArrays(reqMsgArr, reqMsg.getBytes());
    }

    public byte[] generateRemovedAnswer(String senderID, FileChunk chunk){
        
        String reqMsg = generateHeader(DELETE, senderID);
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getFileID());

        reqMsg = SPACE + chunk.getNumber() + SPACE + CRLF + CRLF;
        return joinArrays(reqMsgArr, reqMsg.getBytes());
    }

    public String generateHeader(String type, String senderID){
        String header;
        header = type + SPACE + this.version + SPACE + senderID + SPACE;
        return header;
    }

    public byte[] joinArrays(byte[] array1, byte[] array2){
        
        byte[] joined = new byte[array1.length + array2.length];

        System.arraycopy(array1, 0, joined, 0, array1.length);
        System.arraycopy(array2, 0, joined, array1.length, array2.length);

        return joined;
    }

    public byte[] generateFileID(String fileName) throws IOException{
        File file = new File(fileName);
        String fileID = fileName + file.lastModified();
        
        try{

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileID.getBytes(StandardCharsets.UTF_8));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating fileID");
            throw new IOException();
        }
    }

}