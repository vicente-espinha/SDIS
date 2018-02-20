import java.lang.String;
import java.net.*;
import java.lang.Integer;
import java.io.IOException;

public class Server{

  private DatagramSocket socket;

  public static void main(String[] args) throws IOException{

    if(args.length == 1){
      Server server = new Server();
      String cars = "";
      String port = args[0];
      System.out.println("Opened server in port " + port);

      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);

      server.receivePacket(packet);

      String[] msg = (new String(packet.getData())).split(":");
      String answer = new String();
      if(msg[0] == "register"){
        answer = server.register(cars, msg[1], msg[2]);

      } else if(msg[0] == "lookup") {
        answer = server.lookup(cars, msg[1]);
      } else {
        answer = "-1";
      }

      InetAddress address = packet.getAddress();
      DatagramPacket packetsend = new DatagramPacket(answer.getBytes(), answer.length(), address, Integer.parseInt(port));
      server.sendPacket(packetsend);
    }
  }

  public Server(){
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

  public String register(String cars, String plate, String owner){
    if(cars.length() != 0)
      cars += "%";
    cars += plate + ";" + owner;
    String[] carsArray = cars.split("%");
    return "" + carsArray.length;
  }

  public String lookup(String cars, String plate){
    int idx = cars.indexOf(plate);
    if(idx > -1){
      int idx2 = cars.indexOf("%", idx);
      String car = cars.substring(idx, idx2);
      return car;
    }
    return "-1";
  }


}
