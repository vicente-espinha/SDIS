import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;

public class MCStored implements Runnable {

    MC mc;

    public MCStored(MC mc)  {
        this.mc = mc;
    }

    /**
     * Send STORED message in MC;
     */
    @Override 
    public void run() {  //TODO Change this method
        try {
            String msg = " "; //temp cuz error in msg.
            DatagramPacket message = new DatagramPacket(msg.getBytes(), msg.length(), this.mc.group, this.mc.port);
            this.mc.msocket.send(message);
            System.out.println("Message sent: " + msg);
        } catch (SocketException e) {
            System.out.println("Error sending packet");
        }catch (IOException e){
            e.printStackTrace();
        }
        return;
    }
}