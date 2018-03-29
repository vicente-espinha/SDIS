import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.nio.charset.*;
import java.util.Random;

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
                if (nRead == 64000) {
                    chunk = new FileChunk(fileID, chunkNumber, buffer, 1); //TODO change RepDegree
                } else {
                    byte[] buffer2 = Arrays.copyOf(buffer, nRead);
                    chunk = new FileChunk(fileID, chunkNumber, buffer2, 1); //TODO change RepDegree
                }

                byte[] msgArr = msg.generateBackupReq(this.peerID, chunk);
                DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);
                this.msocket.send(message);

                buffer = new byte[64000];
                chunkNumber++;
            }

            inputStream.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error opening file \"" + fileName + "\"");
            e.printStackTrace();

        } catch (IOException e) {
            System.out.println("Error reading file \"" + fileName + "\"");
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void run() {
        while (true) {

            try {

                byte[] buffer = new byte[65000];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                this.msocket.receive(recv);

                String crlf = Message.CRLF + Message.CRLF;
                int index = Utils.indexOf(buffer, crlf.getBytes()); //Gets the index of the CRLF in the packet received

                byte[] header = Arrays.copyOf(buffer, index); //separates the header
                String headerStr = new String(header);

                byte[] body = Arrays.copyOfRange(buffer, index + crlf.length(), recv.getLength()); //separates the chunk body

                String[] headerArr = headerStr.split("(\\s)+"); //cleans the spaces and divides header into the parameters

                if (!headerArr[0].equals(Message.PUTCHUNK)) {
                    System.out.println("Expected PUTCHUNK got: " + headerArr[0]);
                    continue;
                }

                //saves the chunk
                FileChunk chunk = new FileChunk(headerArr[3], Integer.parseInt(headerArr[4]), body,
                        Integer.parseInt(headerArr[5]));
                chunk.save(headerArr[2]);

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            //new thread here to process the received message (random between 0 and 400ms)
            Random rand = new Random();
            int randomNum = rand.nextInt(400);
            //execute.schedule(classe que processa,randomNum,TimeOut.MILLISECONDS);
        }

    }
}