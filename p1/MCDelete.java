import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;

public class MCDelete implements Runnable {

    private MC mc;
    private String filename;

    public MCDelete(MC mc, String filename) {
        this.mc = mc;
        this.filename = filename;
    }

    /**
     * Send DELETE message in MC;
     */
    @Override
    public void run() { //TODO Change this method
        try {
            Message msgaux = new Message("1.0");
            
            byte[] msg = msgaux.generateDeleteReq(this.filename);//generates message

            DatagramPacket message = new DatagramPacket(msg, msg.length, this.mc.getGroup(), this.mc.getPort());
            this.mc.getSocket().send(message); //sends message to mc
        } catch (SocketException e) {
            System.out.println("Error sending packet");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}