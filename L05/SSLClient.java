

public class SSLClient {

    public static void main(String[] args) {
        //TODO number of args

        SSLClient sslClient = new SSLClient(args);
        sslClient.sendRequest();
    }

    public SSLClient(String[] args){
        this.address = InetAddress.getByName(args[0]);
        this.port = args[1];
        
    }
}