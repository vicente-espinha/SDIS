import java.lang.String;
import java.net.*;
import java.lang.Integer;
import java.io.IOException;
import java.util.Hashtable;

public class Server{

  private DatagramSocket socket;
  private Hashtable<String,String> dataBase;

  public static void main(String[] args) throws IOException{

    if(args.length == 3){

      String port = args[0];
      String mcast_addr = args[1];
      String mcast_port = args[2];
      InetAddress group = InetAddress.getByName(mcast_addr);
      MulticastSocket ms = new MulticastSocket(mcast_port);
      ms.joinGroup(group);
      DatagramPacket adv = new DatagramPacket(port.getBytes(), port.length(), group, mcast_port);
      Server server = new Server(port);
      System.out.println("Opened server in port " + port);

      while(true){
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf,512);

        server.receivePacket(packet);
        String[] msg = (new String(packet.getData())).split(";");

        String answer = new String();
        System.out.println(msg[0] + " + " + msg[1]);
        if(msg[0] == "REGISTER"){
          answer = server.register(msg[1], msg[2]); //processes register
        } else if(msg[0].equals("LOOKUP")) {
          answer = server.lookup(msg[1]);  //processes lookup
        } else {
          answer = "-1";
        }

        DatagramPacket packetsend = new DatagramPacket(answer.getBytes(), answer.getBytes().length, packet.getAddress(), packet.getPort());  //creates packet
        server.sendPacket(packetsend); //sends packet

      }
    } else {
      System.out.println("Usage: java Client <srvc_port> <mcast_addr> <mcast_port>");
      return;
    }
  }

  public Server(String port){
    try{
      this.socket = new DatagramSocket(Integer.parseInt(port));
      this.dataBase = new Hashtable<>();
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

  public String register(String plate, String owner){

    System.out.println("PLATE: " + plate + "  " + "Owner:" + owner);

      if(this.dataBase.get(plate) == null){  //checks if the plate already exists in dataBase
        this.dataBase.put(plate,owner);
        return Integer.toString(this.dataBase.size());          //return the size od the dataBase
      }
      else{
        System.out.println("Already Registered!");
        return "-1";
      }
  }

  public String lookup(String plate){

    System.out.println("PLATE: " + plate);

    if(this.dataBase.get(plate) == null){  //checks if the plate already exists in dataBase
      System.out.println("Owner not Found!");
      return "NOT_FOUND";                   //returns not found if the plate does not exist in the dataBase
    }
    else{
      return this.dataBase.get(plate);          //return the owner
    }

  }


}
