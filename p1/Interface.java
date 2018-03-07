
import java.lang.*;
import java.util.*;

  /*
Backup a file
    The client shall specify the file pathname and the desired replication degree.
Restore a file
    The client shall specify file to restore is specified by the its pathname. 
Delete a file
    The client shall specify file to delete by its pathname. 
Manage local service storage
    The client shall specify the maximum disk space in KBytes (1KByte = 1000 bytes) that can be used for storing chunks. It must be possible to specify a value of 0, thus reclaiming all disk space previously allocated to the service.
Retrieve local service state information
    This operation allows to observe the service state. In response to such a request, the peer shall send to the client the following information:

        For each file whose backup it has initiated:
            The file pathname
            The backup service id of the file
            The desired replication degree
            For each chunk of the file:
                Its id
                Its perceived replication degree
        For each chunk it stores:
            Its id
            Its size (in KBytes)
            Its perceived replication degree
        The peer's storage capacity, i.e. the maximum amount of disk space that can be used to store chunks, and the amount of storage (both in KBytes) used to backup the chunks.
 
  */

public class Interface{


  public Interface(){}


  public void menuOptions(){
    System.out.println(
        "1 -> BackUp a File\n" +
        "2 -> Restore a File\n" +
        "3 -> Delete a File\n" + 
        "4 -> Manage local service storage\n" + 
        "5 -> Retrieve local service information\n"
    );
  }

  public int answer(){
    
    Scanner reader = new Scanner(System.in);  // Reading from System.in

    System.out.println("Enter a number: ");
    
    int input = reader.nextInt(); // Scans the next token of the input as an int 
    
    reader.close();  //close scanner
    
    return input;
  }
}
