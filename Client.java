import java.lang.String;
import java.net.*;
import java.lang.Integer;
import java.io.IOException;


public class Client{

  private DatagramSocket socket;

  public static void main(String[] args)throws IOException{

    if (args.length != 5 && args.length != 4) {
      System.out.println("Usage: java Client <hostname> <port> <operation> <operand>+");
      return;
    }
    Client client = new Client();

    String host = args[0];
    String port = args[1];
    String oper = args[2];
    String opnd = "";
    if(args.length == 4)
       opnd = args[3];
    String request = oper + ";" + opnd;
    byte[] sbuf = request.getBytes();
    InetAddress address = InetAddress.getByName(host);
    DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, Integer.parseInt(port));
    client.sendPacket(packet);

    //get response
    byte[] rbuf = new byte[256];
    DatagramPacket packetreceived = new DatagramPacket(rbuf, rbuf.length);
    System.out.println("AAA");
    client.receivePacket(packetreceived);
    System.out.println("CCC");
    // display response
    String received = new String(packetreceived.getData());
    if(received.contains(";")){
      String[] parts = received.split(";");
      System.out.println(parts[0] + " belongs to " + parts[1]);

    } else {
      if(received == "-1")
        System.out.println("Error in request");
      else
        System.out.println("Registed with success. " + received + " vehicles in database");
    }
    client.socket.close();

  }

  public Client(){
    try{
      this.socket = new DatagramSocket();
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
