import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class MDRSendChunk implements Runnable {

    DatagramPacket packet;
    String fileID;
    String chunkNo;

    public MDRSendChunk(DatagramPacket packet, String fileID, String chunkNo)  {
        this.packet = packet;
        this.fileID = fileID;
        this.chunkNo = chunkNo;
    }

    /**
     * Send scheduled message in MC;
     */
    @Override 
    public void run() {  
        try {
            for (int i = 0; i < Peer.getGetChunks().size(); i++) {
                if ((this.fileID + this.chunkNo).equals(Peer.getGetChunks().get(i))) {
                    return;
                }
            }
            Peer.getMDR().getSocket().send(this.packet);

        } catch (SocketException e) {
            System.out.println("Error sending packet");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error sending packet");
            e.printStackTrace();
        }
        return;
    }
}