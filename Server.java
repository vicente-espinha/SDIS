import java.lang.String;
import java.net.*;

public class Server{


  public static void main(String[] args){

    if(args.length == 1){
      String cars = "";
      String port = args[0];
      System.out.println("Opened server in port " + port);
      DatagramSocket socket = new DatagramSocket();

      byte[] buf = new byte[256];
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      socket.receive(packet);

      String[] msg = (new String(packet.getData())).split(":");
      String answer;
      if(msg[0] == "register"){
        answer = register(cars, msg[1], msg[2]);

      } else if(msg[0] == "lookup") {
        answer = lookup(cars, msg[1]);
      } else {
        answer = "-1";
      }

      InetAddress address = packet.getAddress();
      packet = new DatagramPacket(answer, answer.length, address, port);
      socket.send(packet);
    }
  }

  public String register(String cars, String plate, String owner){
    if(cars.length() != 0)
      cars += "%";
    cars += plate + ";" + owner;
    return cars.split("%").length();
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
