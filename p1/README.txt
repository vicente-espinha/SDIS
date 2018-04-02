To compile:

-navigate to the project's directory
-open a terminal in that directory and type the following commands:
        -"javac *.java"


To run peer:

-navigate to the project's directory
-open a terminal in that directory and type the following commands:
        -"rmiregistry &" only needed one time per local host
        -"java Peer <VersionID> <PeerID> <ServerID> <MC_addr> <MC_port> <MDB_addr> <MDB_port> <MDR_addr> <MDR_port>"


To run the client: 
-navigate to the project's directory
-open a terminal in that directory and type the following commands:
        -"java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>"
        where:
            <peer_ap>
                Is the peer's access point.
            <operation>
                Is the operation the peer of the backup service must execute. It can be BACKUP, RESTORE, DELETE, RECLAIM or STATE.
            <opnd_1>
                Is either the path name of the file to backup/restore/delete, for the respective 3 subprotocols, or, in the case of RECLAIM the maximum amount of disk space (in KByte) that the service can use to store the chunks. In the latter case, the peer should execute the RECLAIM protocol, upon deletion of any chunk. The STATE operation takes no operands.
            <opnd_2>
                This operand is an integer that specifies the desired replication degree and applies only to the backup protocol 