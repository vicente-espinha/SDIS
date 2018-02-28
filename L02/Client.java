import java.lang.String;
import java.net.*;
import java.lang.Integer;
import java.io.IOException;


public class Client{

  private DatagramSocket socket;
  private MulticastSocket multisocket;

  public static void main(String[] args)throws IOException{

    if (args.length != 5 && args.length != 4) {
      System.out.println("Usage: java Client <mcast_addr> <mcast_port> <operation> <operand>*");
      return;
    }

    String address = args[1];
    InetAddress mcast_addr = InetAddress.getByName(address);
    int mcast_port = Integer.parseInt(args[2]);
    String oper = args[2];
    String plate = args[3];
    String request = new String();


    Client client = new Client(mcast_port);

    //Multicast

    client.multisocket.joinGroup(mcast_addr);

    byte[] multibuf = new byte[512];
    DatagramPacket recv = new DatagramPacket(multibuf,multibuf.length);
    client.receivePacket(recv);

    InetAddress db_address = recv.getAddress();
    String db_port = new String(recv.getData());

    client.multisocket.leaveGroup(mcast_addr);

    //UniCast

    if(oper.equals("REGISTER")){
      String owner = args[4];
      request = oper + ";" + plate + ";" + owner;
    }else if(oper.equals("LOOKUP")){
      request = oper + ";" + plate;
    }else{
      System.out.println("Operation invalid !");
      System.exit(-1);
    }

    //sending message
    byte[] sbuf = request.getBytes();
    DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, db_address, Integer.parseInt(db_port));
    client.sendPacket(packet);

    //get response
    byte[] rbuf = new byte[512];
    DatagramPacket packetreceived = new DatagramPacket(rbuf, rbuf.length);
    client.receivePacket(packetreceived);

    // display response
    String received = new String(packetreceived.getData());
    if(received.contains(";")){
      String[] parts = received.split(";");
      System.out.println(parts[0] + " belongs to " + parts[1]);

    } else {
      if(received.equals("-1"))
        System.out.println("Error in request");
      else
        System.out.println("Registed with success. " + received + " vehicles in database");
    }
    client.socket.close();

  }

  public Client(int port) throws IOException{
    try{
      this.socket = new DatagramSocket();
      this.multisocket = new MulticastSocket(port);
    }catch(SocketException e){
      System.out.println("Error opening socket!\n");
    }
  }

  public void sendPacket(DatagramPacket packet) throws IOException{
    try{
      this.socket.send(packet);
    }catch(SocketException e){
      System.out.println("Error sending packet to socket!\n");
    }
  }

  public void receivePacket(DatagramPacket packet)throws IOException{
    try{
      this.socket.receive(packet);
    }catch(SocketException e){
      System.out.println("Error sending packet to socket!\n");
    }
  }
}
