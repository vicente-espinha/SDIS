import java.lang.String;
import java.net.*;


public class Client{

  public Client(){
    private static String host;
    private static String port;
    private String oper;
    private String opnd;
  }

  public static void main(String[] args){

    if(args.length == 4){
      this.host = args[0];
      this.port = args[1];
      this.oper = args[2];
      this.opnd = args[3];
      //private DatagramSocket socket = new DatagramSocket(port.)
      start();
    }
    else{
      System.out.println("Usage : java Client <host_name> <port_number> <oper> <opnd>*");
      return;
    }



  }

  public void start(){

  }

}
