import java.io.IOException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.nio.charset.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SendGetChunk implements Runnable {

    private DatagramPacket message;

    public SendGetChunk(DatagramPacket packet) throws IOException {
        this.message = packet;
    }

    @Override
    public void run() {

            //sends message
            try {
                Peer.getMC().getSocket().send(this.message);
            } catch (IOException e) {
                e.printStackTrace();
            }        
    }
}