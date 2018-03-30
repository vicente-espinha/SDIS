import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class MCSendScheduled implements Runnable {

    DatagramPacket packet;

    public MCSendScheduled(DatagramPacket packet)  {
        this.packet = packet;
    }

    /**
     * Send scheduled message in MC;
     */
    @Override 
    public void run() {  
        try {
            Peer.getMC().getSocket().send(this.packet);

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