import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;
import java.util.Random;
import java.util.Vector;

public class MCDelete implements Runnable {

    private String filename;

    public MCDelete(String filename) {
        this.filename = filename;
    }

    /**
     * Send DELETE message in MC;
     */
    @Override
    public void run() { //TODO Change this method
        try {
            Message msgaux = new Message("1.0");
            
            byte[] msg = msgaux.generateDeleteReq(this.filename);//generates message

            Vector<DataPeerInitializer> removing = new Vector<DataPeerInitializer>();
            for( DataPeerInitializer s : Peer.getDataPeerInitializerVector()){
            
                if(s.getPathname().equals(this.filename) && msgaux.generateFileID(this.filename).equals(s.getFileID())){
                    
                    removing.add(s);
                    
                    DatagramPacket message = new DatagramPacket(msg, msg.length, Peer.getMC().getGroup(),
                        Peer.getMC().getPort());
                    Peer.getMC().getSocket().send(message); //sends message to mc
                }
            }
            if(removing.isEmpty())
                System.out.println("File isn't backed up so delete is not available!");
            else{
                for(DataPeerInitializer s : removing){
                    Peer.getDataPeerInitializerVector().remove(s);
                }
            }
        } catch (SocketException e) {
            System.out.println("Error sending packet");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }
}