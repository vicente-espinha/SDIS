import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;

public class MCDelete implements Runnable {

    MC mc;

    public MCDelete(MC mc)  {
        this.mc = mc
    }

    /**
     * Send DELETE message in MC;
     */
    @Override 
    public void run() {  //TODO Change this method
        try {

            DatagramPacket message = new DatagramPacket(msg.getBytes(), msg.length(), this.mc.group, this.mc.port);
            this.mc.msocket.send(message);
            System.out.println("Message sent: " + msg);
        } catch (SocketException e) {
            System.out.println("Error sending packet");
        }
        return;
    }
}