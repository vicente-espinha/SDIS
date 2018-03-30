import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.net.*;
import java.util.Random;
import java.util.ArrayList;

public class ParserMessages implements Runnable {
    static final int TYPE = 0;
    static final int VERSION = 1;
    static final int SENDERID = 2;
    static final int FILEID = 3;
    static final int CHUNKNO = 4;
    static final int REPDEG = 5;

    byte[] message;
    String header, channel;
    String[] headerArgs;
    int index;
    byte[] body;

    public ParserMessages(byte[] message, String channel) {
        this.message = message;
        this.channel = channel;
    }

    public void splitHeader() {
        String crlf = Message.CRLF + Message.CRLF;
        this.index = Utils.indexOf(this.message, crlf.getBytes()); //Gets the index of the CRLF in the packet received

        byte[] head = Arrays.copyOf(this.message, this.index); //separates the header
        this.header = new String(head);

        this.headerArgs = this.header.split("(\\s)+"); //cleans the spaces and divides header into the parameters
    }

    public void processMessageType() {
        switch (this.headerArgs[TYPE]) {
        case Message.PUTCHUNK:
            if (this.channel.equals("MDB")) {
                processPutchunk();
            }
            break;
        case Message.STORED:
            if (this.channel.equals("MC")) {
                processStored();
            }
            break;
        case Message.CHUNK:
            break;
        case Message.GETCHUNK:
            break;
        case Message.DELETE:
            break;
        case Message.REMOVED:
            break;
        default:
            break;
        }
    }

    public void processPutchunk() {
        if (Peer.peerID.equals(headerArgs[SENDERID])) 
            return;
        
        System.out.println("Chunk number: " + headerArgs[CHUNKNO]);
        

        this.body = Arrays.copyOfRange(this.message, this.index + (Message.CRLF + Message.CRLF).length(),
                this.message.length); //separates the chunk body

        //check if chunk already exists in this peer
        Boolean exists = false;
        for (int i = 0; i < Peer.getDataStoreInitializerVector().size(); i++) {
            if (this.headerArgs[FILEID].equals(Peer.getDataStoreInitializerVector().get(i).getFileID()) && Integer.parseInt(this.headerArgs[CHUNKNO]) == Peer.getDataStoreInitializerVector().get(i).getNumber()) {
                exists = true;
            }
        }

        FileChunk chunk = new FileChunk(this.headerArgs[FILEID], Integer.parseInt(this.headerArgs[CHUNKNO]), this.body,
                Integer.parseInt(this.headerArgs[REPDEG]));

        //saves the chunk
        if (!exists) {
            chunk.save(this.headerArgs[SENDERID]);
            Peer.getDataStoreInitializerVector()
                    .add(new DataStoreInitializer(chunk.getFileID(), chunk.getNumber(), chunk.getBody().length, chunk.getRepDeg()));
            
            processStored();

        }

        Message msg = new Message(this.headerArgs[VERSION]);
        byte[] answer = msg.generateBackupAnswer(chunk);
        DatagramPacket packet = new DatagramPacket(answer, answer.length, Peer.getMC().getGroup(),
                Peer.getMC().getPort());

        Random rand = new Random();
        int randomNum = rand.nextInt(400);
        //new thread here to process the received message (random between 0 and 400ms)
        Peer.executer.schedule(new MCSendScheduled(packet), randomNum, TimeUnit.MILLISECONDS);

    }

    public void processStored() {
        ArrayList<String> peers = Peer.storeCounter.get(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO]);
        if (peers != null && !peers.contains(this.headerArgs[SENDERID])) {
            peers.add(this.headerArgs[SENDERID]);
            Peer.storeCounter.remove(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO]);
            Peer.storeCounter.put(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO], peers);
            System.out.println("yahedhadoawd + " + peers.size());
        } else if(peers == null) {
            peers = new ArrayList<String>();
            peers.add(this.headerArgs[SENDERID]);
            Peer.storeCounter.put(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO], peers);
        }
    }

    public void run() {
        splitHeader();
        processMessageType();
    }

}