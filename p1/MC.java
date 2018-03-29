import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;

public class MC implements Runnable {
    InetAddress group;
    int port;
    MulticastSocket msocket;

    public MC(String address, String port) throws IOException {

        try {
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            msocket.joinGroup(group);
            System.out.println("Multicast Socket open on " + address + ":" + this.port);

        } catch (SocketException e) {
            System.out.println("Error opening socket!\n");
        }

    }

    public void sendMessage(String msg) throws IOException {
        try {

            DatagramPacket message = new DatagramPacket(msg.getBytes(), msg.length(), this.group, this.port);
            this.msocket.send(message);
            System.out.println("Message sent: " + msg);
        } catch (SocketException e) {
            System.out.println("Error sending packet");
        }
        return;
    }

    @Override
    public void run() {
        while (true) {

            byte[] buf = new byte[1000];
            try {

                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                this.msocket.receive(recv);
                String msg = new String(recv.getData());
                System.out.println(msg);
            } catch (SocketException e) {
                System.out.println("Error receiving packet");
            }catch(IOException e){
                e.printStackTrace();
            }

            //new thread here to process the received message
            Random rand = new Random();
            int randomNum = rand.nextInt(400);
            //execute.schedule(classe que processa,randomNum,TimeOut.MILLISECONDS);
        }
    }
}