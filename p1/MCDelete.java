import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;

public class MCDelete implements Runnable {

    MC mc;

    public MCDelete(MC mc)  {
        this.mc = mc;
    }

    /**
     * Send DELETE message in MC;
     */
    @Override 
    public void run() {  //TODO Change this method
      /*  try {
            String msg = " "; //temp cuz error in msg.
            DatagramPacket message = new DatagramPacket(msg.getBytes(), msg.length(), this.mc.getGroup(), this.mc.getPort());
            //Peer.MC.msocket.send(message);
            System.out.println("Message sent: " + msg);
        } catch (SocketException e) {
            System.out.println("Error sending packet");
        } catch (IOException e){
            e.printStackTrace();
        }*/
        return;
    }
}