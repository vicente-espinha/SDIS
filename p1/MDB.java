import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.nio.charset.*;
/*

Multicast Data Backup Channel

*/
public class MDB implements Runnable {
    private InetAddress group;
    private int port;
    private MulticastSocket msocket;
    private String peerID;

    public MDB(String peerID, String address, String port) throws IOException {

        try {
            this.peerID = peerID;
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            msocket.joinGroup(group);
            System.out.println("Multicast Data Backup Channel open on " + address + ":" + this.port);

        } catch (SocketException e) {
            System.out.println("Error opening MDB socket!\n");
        }

    }

    public void sendMessage(String fileName) throws IOException {
        try {
            byte[] buffer = new byte[64000];
            FileInputStream inputStream = new FileInputStream(fileName);
            Message msg = new Message("1.0");
            FileChunk chunk;
            int chunkNumber = 1;
            String fileID = msg.generateFileID(fileName);
            int nRead = 0;

            while ((nRead = inputStream.read(buffer)) != -1) {
                if(nRead == 64000) {
                    chunk = new FileChunk(fileID, chunkNumber, buffer, 1); //change RepDegree
                } else {
                    byte[] buffer2 = Arrays.copyOf(buffer, nRead);
                    chunk = new FileChunk(fileID, chunkNumber, buffer2, 1); //change RepDegree
                }
                System.out.println("nRead = " + nRead);
                System.out.print("chunk : ");
                System.out.println(chunk.getBody());
                byte[] msgArr = msg.generateBackupReq(this.peerID, chunk);
                DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);
                this.msocket.send(message);
                System.out.println(" --> length : " + msgArr.length);

                buffer = new byte[64000];
                chunkNumber++;
            }

            inputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error opening file \"" + fileName + "\"");

        } catch (IOException e) {
            System.out.println("Error reading file \"" + fileName + "\"");
            e.printStackTrace();
        }
        return;
    }

    public void receiveMessage() throws IOException {
        while (true) {

            try {
                byte[] buffer = new byte[65000];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                this.msocket.receive(recv);
                String crlf = Message.CRLF + Message.CRLF;
                int index = Utils.indexOf(buffer, crlf.getBytes());
                System.out.println("index: " + index);
                byte[] header = Arrays.copyOf(buffer, index);
                String headerStr = new String(header);
                System.out.println("header: " + headerStr);
                System.out.println("size: " + recv.getLength());
                byte[] body = Arrays.copyOfRange(buffer, index + crlf.length(), recv.getLength());
                System.out.println("body: " + body);
                String[] headerArr = headerStr.split("(\\s)+");
                if(headerArr[0] != Message.PUTCHUNK)
                    throw new IOException();
                FileChunk chunk = new FileChunk(headerArr[3], headerArr[4], body, headerArr[5]);
                chunk.save(headerArr[2]);


                /*FileChunk chunk;
                int chunkNumber = 1;
                byte[] fileID = msg.generateFileID(fileName); 
                int nRead = 0;
                
                while((nRead = inputStream.read(buffer))!= -1){
                
                    chunk = new FileChunk(fileID, chunkNumber, buffer, 1); //change RepDegree
                    System.out.println("chunk");
                System.out.println(chunk.getBody());
                byte[] msgArr = msg.generateBackupReq(this.peerID, chunk);
                DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);
                this.msocket.send(message);
                
                buffer = new byte[64000];
                chunkNumber++;
                }
                
                inputStream.close();*/

            } catch (FileNotFoundException e) {
                // System.out.println("Error opening file \"" + fileName + "\"");  
                System.out.println("Error");
            } catch (IOException e) {
                //System.out.println("Error reading file \"" + fileName + "\"");                  
                System.out.println("Error");
            }
        }
        /*
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
        }*/
    }

    @Override
    public void run() {

    }
}