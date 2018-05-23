import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer{

    Integer port;
    SSLServerSocket s;

    public static void main(String[] args) {
        
        try{
            SSLServer sslServer = new SSLServer(args);

        } catch(IOException e){
            e.printStackTrace();
        }
            
        
    }


    public SSLServer(String[] args) throws IOException {
        String[] cipherSuites = new String[args.length - 1];
        this.port = Integer.parseInt(args[0]);

        for(int i = 1; i < args.length; i++)
            cipherSuites[i - 1] = args[i];
 
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();  
            
        try {  
            this.s = (SSLServerSocket) ssf.createServerSocket(this.port);  
        }  
        catch( IOException e) {  
            System.out.println("Server - Failed to create SSLServerSocket");  
            e.getMessage();  
            return;  
        } 
       
        this.s.setNeedClientAuth(true);
        if(cipherSuites.length > 0)
            this.s.setEnabledCipherSuites(cipherSuites);
        else
            this.s.setEnabledCipherSuites(cipherSuites); 

    }

    public void receiveRequest() {
        while(true){
            SSLSocket request = (SSLSocket) this.s.accept();

        }
    }


}

