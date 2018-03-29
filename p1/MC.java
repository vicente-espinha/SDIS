import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class MC implements Runnable {
    InetAddress group;
    int port;
    MulticastSocket msocket;
    String peerID;

    public MC(String address, String port, String peerID) throws IOException {

        try {
            this.peerID = peerID;
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

            try {
                byte[] buf = new byte[1000];

                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                this.msocket.receive(recv);

                byte[] header = Arrays.copyOf(buf, recv.getLength());
                String headerStr = new String(header);
                
                String[] headerArr = headerStr.split("(\\s)+"); //cleans the spaces and divides header into the parameters
                switch(headerArr[0]){
                    case Message.STORED:
                        saveStored(headerArr);
                        System.out.println("Yay");
                        break;
                    default:
                        System.out.println("Nay");
                        break;
                }
                buf = new byte[1000];
                
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

    private void saveStored(String[] header){
        ArrayList<String> peers = Peer.storeCounter.get(header[3] + header[4]);
        if(!peers.contains(header[2])){
            peers.add(header[2]);
            Peer.storeCounter.remove(header[3]+ header[4]);
            Peer.storeCounter.put(header[3]+header[4], peers);
            System.out.println("yahedhadoawd");
        } 
    }
}