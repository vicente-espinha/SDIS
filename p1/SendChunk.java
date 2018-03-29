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

    private FileChunk chunk;
    private MulticastSocket socket;
    private DatagramPacket message;
    private int time;
    private int timesSent;

    public SendChunk(FileChunk chunk, MulticastSocket socket, DatagramPacket message) throws IOException {
        this.chunk = chunk;
        this.socket = socket;
        this.message = message;
        this.time = 1000;
        this.timesSent = 0;
    }

    @Override
    public void run() {

        ArrayList<String> list = Peer.storeCounter.get(this.chunk.getFileID() + this.chunk.getNumber());

        if (list.size() >= this.chunk.getRepDeg()) {
            return;//TODO check if needs to be erased something
        } else if (this.timesSent < 5) {
            //sends message
            try {
                this.socket.send(this.message);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.timesSent++;
            int tmpTime = this.time;
            this.time *= 2;
            Peer.executer.schedule(this, tmpTime, TimeUnit.MILLISECONDS);

        }
    }
}