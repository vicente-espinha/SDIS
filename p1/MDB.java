import java.io.IOException;
import java.net.*;
import java.io.*;

/*

Multicast Data Backup Channel

*/
public class MDB implements Runnable{
    private InetAddress group;
    private int port;
    private MulticastSocket msocket;
    private String peerID;

    public MDB(String peerID, String address, String port) throws IOException{

        try{
            this.peerID = peerID;
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            msocket.joinGroup(group);
            System.out.println("Multicast Data Backup Channel open on " + address + ":" + this.port);

        }catch(SocketException e){
            System.out.println("Error opening MDB socket!\n");
        }

    }

    public void sendMessage(String fileName) throws IOException{
        try{
            byte[] buffer = new byte[64000];
            FileInputStream inputStream = new FileInputStream(fileName);
            Message msg = new Message("1.0");
            FileChunk chunk;
            int chunkNumber = 1;
            byte[] fileID = msg.generateFileID(fileName); 
            int nRead = 0;
    
            while((nRead = inputStream.read(buffer))!= -1){
    
                chunk = new FileChunk(fileID, chunkNumber, buffer, 1); //change RepDegree
                byte[] msgArr = msg.generateBackupReq(this.peerID, chunk);
                DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);
                this.msocket.send(message);
                
    
                chunkNumber++;
            }
    
            inputStream.close();
            
        } catch(FileNotFoundException e) {
            System.out.println("Error opening file \"" + fileName + "\"");  
    
        } catch(IOException e) {
            System.out.println("Error reading file \"" + fileName + "\"");                  
    
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
                System.out.println("Error receiving packet (MDB)");
            }
        }
    }


    @Override
    public void run(){

    }
}