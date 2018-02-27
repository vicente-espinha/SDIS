import java.net.*;
import java.io.*;


public class Client {
  public static void main(String[] args) throws IOException{
    if (args.length != 1) {
      System.out.println("Usage: java Client <hostname>");
      return;
    }
    // send request
    DatagramSocket socket = new DatagramSocket();
    byte[] sbuf = new byte[256];
    InetAddress address = InetAddress.getByName(args[0]);
    DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, 4445);
    socket.send(packet);

    packet = new DatagramPacket(sbuf, sbuf.length);
    socket.receive(packet);
    // display response
    String received = new String(packet.getData(), 0, packet.getLength());
    System.out.println("Echoed Message: " + received);
    //socket.close();
  }
}
