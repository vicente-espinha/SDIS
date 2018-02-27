import java.io.*;
import java.net.*;
import java.util.Date;

public class Server {
    private int quote = 0;


    public static void main(String[] args) throws IOException {
        new Server();
    }

    public Server() throws IOException {
        this("Server");
    }

    public Server(String name) throws IOException {

        DatagramSocket socket = new DatagramSocket(4445);

        try {
            BufferedReader in = new BufferedReader(new FileReader("one-liners.txt"));

            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String dString = null;
            if (in == null)
              dString = new Date().toString();
            else
              dString = in.readLine();
              buf = dString.getBytes();

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        }
        catch (FileNotFoundException e){
            System.err.println("Couldn't open quote file.  Serving time instead.");
        }

        socket.close();
    }


}
