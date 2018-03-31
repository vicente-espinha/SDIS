import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.nio.charset.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SendChunk implements Runnable {

    private DatagramPacket message;
    private int time, timesSent, chunkNumber, repDeg;
    private String fileID;

    public SendChunk(String fileID, int chunkNumber, int repDeg, DatagramPacket message) throws IOException {
        this.fileID = fileID;
        this.chunkNumber = chunkNumber;
        this.repDeg = repDeg;
        this.message = message;
        this.time = 1000;
        this.timesSent = 0;
    }

    @Override
    public void run() {

        ArrayList<String> list = Peer.storeCounter.get(this.fileID + this.chunkNumber);

        if (list.size() >= this.repDeg) {
            return; //TODO check if needs to be erased something
        } else if (this.timesSent < 5) {
            //sends message
            try {
                Peer.getMDB().getSocket().send(this.message);
            } catch (IOException e) {
                e.printStackTrace();
                Random rand = new Random();
                int randomNum = rand.nextInt(500);
                Peer.executer.schedule(this, randomNum, TimeUnit.MILLISECONDS);
                return;
            }

            this.timesSent++;
            int tmpTime = this.time;
            this.time *= 2;
            Peer.executer.schedule(this, tmpTime, TimeUnit.MILLISECONDS);

        }
    }
}