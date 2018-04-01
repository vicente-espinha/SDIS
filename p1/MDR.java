import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

/*

Multicast Data Recovery Channel

*/
public class MDR implements Runnable {
    private InetAddress group;
    private int port;
    private MulticastSocket msocket;

    public MDR(String address, String port) throws IOException {

        try {
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            msocket.joinGroup(group);
            this.msocket.setTimeToLive(2);
            System.out.println("Multicast Data Recovery Channel (MDR) open on " + address + ":" + this.port);

        } catch (SocketException e) {
            System.out.println("Error opening MDR socket!\n");
            e.printStackTrace();
        }

    }

    public InetAddress getGroup() {
        return this.group;
    }

    public int getPort() {
        return this.port;
    }

    public MulticastSocket getSocket() {
        return this.msocket;
    }

    public void sendMessage(String msg) throws IOException {
        try {

            DatagramPacket message = new DatagramPacket(msg.getBytes(), msg.length(), this.group, this.port);
            this.msocket.send(message);
            System.out.println("Message sent: " + msg);
        } catch (SocketException e) {
            System.out.println("Error sending packet (MDR)");
        }
        return;
    }

    @Override
    public void run() {
        while (true) {

            try {

                byte[] buffer = new byte[65000];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                this.msocket.receive(recv);
                byte[] temp = Arrays.copyOf(buffer, recv.getLength());
                Peer.executer.execute(new ParserMessages(temp, "MDR"));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}