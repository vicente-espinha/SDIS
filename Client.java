import java.lang.String;
import java.net.*;


public class Client{

  public void main(String[] args){
    if (args.length != 4 || args.length != 3) {
      System.out.println("Usage: java Client <hostname> <port> <operation> <operand>+");
      return;
    }

    String host = args[0];
    String port = args[1];
    String oper = args[2];
    String opnd = "";
    if(args.length == 4)
       opnd = args[3];
    DatagramSocket socket = new DatagramSocket();
    String request = oper + ":" + opnd;
    byte[] sbuf = request.getBytes();
    InetAddress address = InetAddress.getByName(host);
    DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
    socket.send(packet);

    //get response
    byte[] rbuf = new byte[sbuf.length];
    packet = new DatagramPacket(rbuf, rbuf.length);
    socket.receive(packet);

    // display response
    String received = new String(packet.getData());
    if(received.contains(";")){
      String[] parts = received.split(";");
      System.out.println(parts[0] + " belongs to " + parts[1]);

    } else {
      if(received == -1)
        System.out.println("Error in request");
      else
        System.out.println("Registed with success. " + received + " vehicles in database");
    }
    socket.close();
        start();

  }

  public void start(){

  }

}
