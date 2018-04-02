import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.net.*;
import java.util.Random;
import java.util.Vector;
import java.util.ArrayList;
import java.io.File;
import java.io.*;

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
            if (this.channel.equals("MDR")) {
                processChunk();
            }
            break;
        case Message.GETCHUNK:
            if (this.channel.equals("MC")) {
                processGetChunk();
            }
            break;
        case Message.DELETE:
            if (this.channel.equals("MC")) {
                processDelete();
            }
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
            if (this.headerArgs[FILEID].equals(Peer.getDataStoreInitializerVector().get(i).getFileID()) && Integer
                    .parseInt(this.headerArgs[CHUNKNO]) == Peer.getDataStoreInitializerVector().get(i).getNumber()) {
                exists = true;
                break;
            }
        }

        FileChunk chunk = new FileChunk(this.headerArgs[FILEID], Integer.parseInt(this.headerArgs[CHUNKNO]), this.body,
                Integer.parseInt(this.headerArgs[REPDEG]));

        //saves the chunk
        if (!exists) {
            chunk.save(this.headerArgs[SENDERID]);
            Peer.getDataStoreInitializerVector().add(new DataStoreInitializer(chunk.getFileID(), chunk.getNumber(),
                    chunk.getBody().length, chunk.getRepDeg(), chunk.getFileName()));
            ArrayList<String> peers = Peer.storeCounter.get(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO]);
            if (peers == null) {
                peers = new ArrayList<String>();
                Peer.storeCounter.put(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO], peers);
            }
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
        } else if (peers == null) {
            peers = new ArrayList<String>();
            peers.add(this.headerArgs[SENDERID]);
            Peer.storeCounter.put(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO], peers);
        }
    }

    public void processDelete() {
        Vector<DataStoreInitializer> removing = new Vector<DataStoreInitializer>();
        for (DataStoreInitializer d : Peer.getDataStoreInitializerVector()) {
            if (d.getFileID().equals(headerArgs[FILEID])) { //searches for all the chunks that peer has backed up

                try {

                    File file = new File(d.getFileName());
                    if (file.exists()) {

                        if (file.delete()) { //deletes file
                            System.out.println(d.getFileName() + " is deleted!");
                            removing.add(d); //removes chunk from the vector containing the chunks backed up
                        } else {
                            System.out.println("Delete operation failed.");
                        }
                    } else
                        removing.add(d); //removes chunk from the vector containing the chunks backed up

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        for (DataStoreInitializer s : removing) {
            Peer.getDataStoreInitializerVector().remove(s);
        }
    }

    public void processGetChunk() {
        //check if chunk exists in this peer
        Boolean exists = false;
        int idx = -1;
        for (int i = 0; i < Peer.getDataStoreInitializerVector().size(); i++) {
            if (this.headerArgs[FILEID].equals(Peer.getDataStoreInitializerVector().get(i).getFileID()) && Integer
                    .parseInt(this.headerArgs[CHUNKNO]) == Peer.getDataStoreInitializerVector().get(i).getNumber()) {
                exists = true;
                idx = i;
                break;
            }
        }
        if (exists && idx > -1) {
            try {

                byte[] buffer = new byte[64000];
                FileInputStream inputStream = new FileInputStream(
                        this.headerArgs[SENDERID] + this.headerArgs[FILEID] + this.headerArgs[CHUNKNO]);
                int nRead;
                if ((nRead = inputStream.read(buffer)) != -1) {

                    Message msg = new Message("1.0");
                    byte[] buffer2 = Arrays.copyOf(buffer, nRead);
                    FileChunk fchunk = new FileChunk(this.headerArgs[FILEID],
                            Integer.parseInt(this.headerArgs[CHUNKNO]), buffer2, 0);
                    byte[] msgArr = msg.generateRestoreAnswer(fchunk);

                    DatagramPacket message = new DatagramPacket(msgArr, msgArr.length, Peer.getMDR().getGroup(),
                            Peer.getMDR().getPort());
                    MDRSendChunk sendchunk = new MDRSendChunk(message, this.headerArgs[FILEID],
                            this.headerArgs[CHUNKNO]);

                    Random rand = new Random();
                    int randomNum = rand.nextInt(400);
                    Peer.executer.schedule(sendchunk, randomNum, TimeUnit.MILLISECONDS);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error opening file");
                e.printStackTrace();

            } catch (IOException e) {
                System.out.println("Error reading file");
                e.printStackTrace();
            }
            return;
        }
    }

    public void addGetChunk() {
        for (int i = 0; i < Peer.getGetChunks().size(); i++) {
            if ((this.headerArgs[FILEID] + this.headerArgs[CHUNKNO]).equals(Peer.getGetChunks().get(i))) {
                return;
            }
        }
        if (!Peer.peerID.equals(this.headerArgs[SENDERID]))
            Peer.getGetChunks().add(this.headerArgs[FILEID] + this.headerArgs[CHUNKNO]);
    }

    public void processChunk() {
        addGetChunk();
        if (Peer.getCurrentlyRestoring()) {
            for (int i = 0; i < Peer.getDataPeerInitializerVector().size(); i++) {
                if (Peer.getDataPeerInitializerVector().get(i).getFileID().equals(this.headerArgs[FILEID])
                        && Peer.getFileRestoring().equals(Peer.getDataPeerInitializerVector().get(i).getPathname())) {
                    System.out.println("RRRRRRRRRRRRRRRRRRR");
                    saveRestoredChunk(i);
                    break;
                }
            }
        }
    }

    public void saveRestoredChunk(int i) {

        if (Peer.getRestoreTemp().get(Integer.parseInt(this.headerArgs[CHUNKNO]) - 1).length == 0) {

            this.body = Arrays.copyOfRange(this.message, this.index + (Message.CRLF + Message.CRLF).length(),
                    this.message.length); //separates the chunk body

            Peer.getRestoreTemp().set(Integer.parseInt(headerArgs[CHUNKNO]) - 1, this.body);

        }

        Boolean exists = false;
        for (int j = 0; j < Peer.getRestoreTemp().size(); j++) {
            if (Peer.getRestoreTemp().get(j).length == 0) {
                exists = true;
                break;
            }
        }
        if (!exists) {

            try {

                FileOutputStream out = new FileOutputStream(Peer.getFileRestoring(), false);
                for (int c = 0; c < Peer.getRestoreTemp().size(); c++) {
                    out.write(Peer.getRestoreTemp().get(c)); //writes in bynary file
                }
                out.close(); //closes output stream

                Peer.fileRestoring = "";
                Peer.currentlyRestoring = false;
                Peer.restoreTemp = new ArrayList<byte[]>();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void run() {
        splitHeader();
        processMessageType();
    }

}