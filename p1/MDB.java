import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.nio.charset.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    public void sendMessage(String fileName, String repDegree) throws IOException {
        try {
            byte[] buffer = new byte[64000];
            FileInputStream inputStream = new FileInputStream(fileName);
            Message msg = new Message("1.0");
            FileChunk chunk;
            int chunkNumber = 1;
            String fileID = msg.generateFileID(fileName);
            int nRead = 0;
            ArrayList<FileChunk>temphash = new ArrayList();

            while ((nRead = inputStream.read(buffer)) != -1) {
                if (nRead == 64000) {
                    chunk = new FileChunk(fileID, chunkNumber, buffer, repDegree); //TODO change RepDegree
                } else {
                    byte[] buffer2 = Arrays.copyOf(buffer, nRead);
                    chunk = new FileChunk(fileID, chunkNumber, buffer2, repDegree); //TODO change RepDegree
                }  
                temphash.add(chunk);

                buffer = new byte[64000];
                chunkNumber++;
            }   

            //send all chunks of file
            for(FileChunk key : temphash){
                byte[] msgArr = msg.generateBackupReq(this.peerID, key);
                DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);
                this.msocket.send(message);
                Peer.getDataPeerInitializerVector()
                        .add(new DataPeerInitializer(new File(fileName).getAbsolutePath(), chunk.getFileID(), repDegree));//TODO change RepDegree
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

                //check if chunk already eists in this peer
                Boolean exists = false;
                for (int i = 0; i < Peer.getDataStoreInitializerVector().size(); i++) {
                    if (headerArr[3].equals(Peer.getDataStoreInitializerVector().get(i).getFileID())) {
                        exists = true;
                    }
                }

                FileChunk chunk = new FileChunk(headerArr[3], Integer.parseInt(headerArr[4]), body,
                            Integer.parseInt(headerArr[5]));

                //saves the chunk
                if (!exists) {
                    chunk.save(headerArr[2]);
                    Peer.getDataStoreInitializerVector()
                            .add(new DataStoreInitializer(chunk.getFileID(), chunk.getBody().length, chunk.getRepDeg()));//TODO change RepDegree

                }

                MCStored mcstored = new MCStored(chunk);
                Random rand = new Random();
                int randomNum = rand.nextInt(400);

                //new thread here to process the received message (random between 0 and 400ms)
                Peer.executer.schedule(mcstored, randomNum, TimeUnit.MILLISECONDS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}