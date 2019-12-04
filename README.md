# rmi-client-server
The following project is contextualized in the Distributed Computing Subject at Lleida's University. The goal is to manage the remote upload and download of digital contents using Java Remote Method Invocation mechanism.

# usage
The CentralServer must be ran first, then the Server/s and finally the Client/s. 

 **CentralServer**: java CentralServer <host> <port>
 
 **Server**: java Server <host> <port> <central_server_host> <central_server_port> [contents_db_name] [registry_binded_object_name]
 
 **Client**: java Client <host> <port> [registry_binded_object_name]
