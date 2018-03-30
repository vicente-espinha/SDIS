import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class MC implements Runnable {
    private InetAddress group;
    private int port;
    private MulticastSocket msocket;

    public MC(String address, String port) throws IOException {

        try {
            System.out.println(address + " " + port + " " + Peer.peerID);
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            this.msocket.joinGroup(this.group);
            System.out.println("Multicast Socket open on " + address + ":" + port);

        } catch (SocketException e) {
            System.out.println("Error opening socket!\n");
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

    public void sendMessage(DatagramPacket message) throws IOException {
        try {

            this.msocket.send(message);

            System.out.println("Message sent: " + message.toString());
        } catch (SocketException e) {
            System.out.println("Error sending packet");
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(Peer.peerID + " aaaa " + Peer.storeCounter.size());
            try {

                byte[] buffer = new byte[65000];

                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                this.msocket.receive(recv);

                byte[] temp = Arrays.copyOf(buffer, recv.getLength());

                Peer.executer.execute(new ParserMessages(temp, "MC"));

            } catch (SocketException e) {
                System.out.println("Error receiving packet");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}