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

    public MDB(String address, String port) throws IOException {

        try {
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            this.msocket.joinGroup(group);
            this.msocket.setTimeToLive(2);
            System.out.println("Multicast Data Backup Channel open on " + address + ":" + this.port);

        } catch (SocketException e) {
            System.out.println("Error opening MDB socket!\n");
            e.printStackTrace();
        }

    }

    public InetAddress getGroup() {
        return this.group;
    }

    public int getPort() {
        return this.port;
    }

    public MulticastSocket getSocket() {
        return this.msocket;
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
            ArrayList<FileChunk> temphash = new ArrayList<FileChunk>();

            while ((nRead = inputStream.read(buffer)) != -1) {
                if (nRead == 64000) {
                    chunk = new FileChunk(fileID, chunkNumber, buffer, Integer.parseInt(repDegree));
                } else {
                    byte[] buffer2 = Arrays.copyOf(buffer, nRead);
                    chunk = new FileChunk(fileID, chunkNumber, buffer2, Integer.parseInt(repDegree));
                }
                temphash.add(chunk);
                Peer.storeCounter.put(fileID + chunkNumber, new ArrayList<String>());

                buffer = new byte[64000];
                chunkNumber++;
            }

            //send all chunks of file
            for (FileChunk key : temphash) {
                byte[] msgArr = msg.generateBackupReq(key);
                DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);
                SendChunk sendchunk = new SendChunk(key.getFileID(), key.getNumber(), key.getRepDeg(), message);

                Peer.executer.execute(sendchunk);

            }
            Peer.getDataPeerInitializerVector().add(new DataPeerInitializer(fileName, temphash.get(0).getFileID(),
                    Integer.parseInt(repDegree), temphash.size()));

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
                byte[] temp = Arrays.copyOf(buffer, recv.getLength());
                Peer.executer.execute(new ParserMessages(temp, "MDB"));

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}