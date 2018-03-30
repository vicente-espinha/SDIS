import java.io.File;
import java.io.IOException;
import java.security.*;
import java.nio.charset.*;

public class Message {

    static final String PUTCHUNK = "PUTCHUNK";
    static final String STORED = "STORED";
    static final String GETCHUNK = "GETCHUNK";
    static final String CHUNK = "CHUNK";
    static final String DELETE = "DELETE";
    static final String REMOVED = "REMOVED";
    static final String SPACE = " ";
    static final String CRLF = "\r\n";

    String version;

    public Message(String version) {
        this.version = version;
    }

    public byte[] generateBackupReq(FileChunk chunk) {
        String reqMsg = generateHeader(PUTCHUNK) + chunk.getFileID() + SPACE + chunk.getNumber() + SPACE
                + chunk.getRepDeg() + SPACE + CRLF + CRLF;

        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getBody());

        return reqMsgArr;
    }

    public byte[] generateBackupAnswer(FileChunk chunk) {

        String reqMsg = generateHeader(STORED) + chunk.getFileID() + SPACE + chunk.getNumber() + SPACE + CRLF
                + CRLF;

        return reqMsg.getBytes();
    }

    public byte[] generateRestoreReq(FileChunk chunk) {

        String reqMsg = generateHeader(GETCHUNK) + chunk.getFileID() + SPACE + chunk.getNumber() + SPACE
                + CRLF + CRLF;

        return reqMsg.getBytes();
    }

    public byte[] generateRestoreAnswer(FileChunk chunk) {

        String reqMsg = generateHeader(CHUNK) + chunk.getFileID() + SPACE + chunk.getNumber() + SPACE
                + chunk.getRepDeg() + SPACE + CRLF + CRLF;
        byte[] reqMsgArr = joinArrays(reqMsg.getBytes(), chunk.getBody());

        return reqMsgArr;
    }

    public byte[] generateDeleteReq(FileChunk chunk) {

        String reqMsg = generateHeader(DELETE) + chunk.getFileID() + SPACE + CRLF + CRLF;
        return reqMsg.getBytes();
    }

    public byte[] generateRemovedAnswer(FileChunk chunk) {

        String reqMsg = generateHeader(DELETE) + chunk.getFileID() + SPACE + chunk.getNumber() + SPACE + CRLF
                + CRLF;
        return reqMsg.getBytes();
    }

    public String generateHeader(String type) {
        String header;
        header = type + SPACE + this.version + SPACE + Peer.peerID + SPACE;
        return header;
    }

    public byte[] joinArrays(byte[] array1, byte[] array2) {

        byte[] joined = new byte[array1.length + array2.length];

        System.arraycopy(array1, 0, joined, 0, array1.length);
        System.arraycopy(array2, 0, joined, array1.length, array2.length);

        return joined;
    }

    public String generateFileID(String fileName) throws IOException {
        File file = new File(fileName);
        String fileIDtemp = fileName + file.lastModified();

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileIDtemp.getBytes("UTF-8"));
            StringBuffer fileID = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    fileID.append('0');
                fileID.append(hex);
            }

            return fileID.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating fileID");
            throw new IOException();
        }
    }

}