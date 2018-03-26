import java.io.IOException;
import java.net.*;

public class MC implements Runnable{
    private InetAddress group;
    private int port;
    private MulticastSocket msocket;

    public MC(String address, String port) throws IOException{

        try{
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            msocket.joinGroup(group);
            System.out.println("Multicast Socket open on " + address + ":" + this.port);

        }catch(SocketException e){
            System.out.println("Error opening socket!\n");
        }

    }

    public void sendMessage(String msg) throws IOException{
        try{

            DatagramPacket message = new DatagramPacket(msg.getBytes(), msg.length(), this.group, this.port);
            this.msocket.send(message);
            System.out.println("Message sent: " + msg);
        } catch(SocketException e){
            System.out.println("Error sending packet");
        }
        return;
    }

    
    public void receiveMessage() throws IOException{
        while(true) {
        
            byte[] buf = new byte[1000];
            try {

                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                this.msocket.receive(recv);
                String msg = new String(recv.getData());
                System.out.println(msg);
            } catch(SocketException e) {
                System.out.println("Error receiving packet");
            }
        }
    }


    @Override
    public void run(){

    }
}