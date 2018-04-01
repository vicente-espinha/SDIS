import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class MC implements Runnable {
    private InetAddress group;
    private int port;
    private MulticastSocket msocket;

    public MC(String address, String port) throws IOException {

        try {
            this.group = InetAddress.getByName(address);
            this.port = Integer.parseInt(port);
            this.msocket = new MulticastSocket(this.port);
            this.msocket.joinGroup(this.group);
            this.msocket.setTimeToLive(2);
            System.out.println("Multicast Socket open on " + address + ":" + port);

        } catch (SocketException e) {
            System.out.println("Error opening socket!\n");
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

    public void sendMessage(String fileName) throws IOException {

        try {

            Message msg = new Message("1.0");
            String fileID = msg.generateFileID(fileName);

            for (int i = 0; i < Peer.getDataPeerInitializerVector().size(); i++) {
                if (Peer.getDataPeerInitializerVector().get(i).getFileID().equals(fileID)) {

                    int nChunks = Peer.getDataPeerInitializerVector().get(i).getNumChunks(); //saves the number of chunks of the file

                    for (int j = 1; j < nChunks; i++) {

                        byte[] msgArr = msg.generateRestoreReq(fileID, j);

                        DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, this.group, this.port);

                        SendGetChunk sender = new SendGetChunk(message);

                        Random rand = new Random();
                        int randomNum = rand.nextInt(400);
                        Peer.executer.schedule(sender, randomNum, TimeUnit.MILLISECONDS);
                    }
                }
            }
        } catch (IOException e) {
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

                Peer.executer.execute(new ParserMessages(temp, "MC"));

            } catch (SocketException e) {
                System.out.println("Error receiving packet");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}