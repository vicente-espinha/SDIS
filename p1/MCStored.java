import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;

public class MCStored implements Runnable {

    MC mc;
    FileChunk chunk;

    public MCStored(FileChunk chunk)  {
        this.mc = Peer.getMC();
        this.chunk = chunk;
    }

    /**
     * Send STORED message in MC;
     */
    @Override 
    public void run() {  //TODO Change this method
        try {
            Message msg = new Message("1.0");
            byte[] answer = msg.generateBackupAnswer(this.mc.peerID, this.chunk);
            DatagramPacket message = new DatagramPacket(answer, answer.length, this.mc.group, this.mc.port);
            this.mc.msocket.send(message);
        } catch (SocketException e) {
            System.out.println("Error sending packet");
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return;
    }
}