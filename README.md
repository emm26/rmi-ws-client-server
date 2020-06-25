# rmi-client-server
The following project is contextualized in the Distributed Computing Subject at Lleida's University. The goal is to manage the remote upload and download of digital contents using Java Remote Method Invocation mechanism and Web Services.

# dependencies

- PostgreSQL must be running on port 5432.
- gson-2.6.2.jar: java library to convert json to java objects. It is included in the source code.

# usage

PostgreSQL must be ran first, then the server defining the Web Services using RedHat CodeReady Studio, for instance.

Then the CentralServer, Server and Client can be ran, in that particular order.

 **CentralServer**: java CentralServer <central_server_host> <central_server_port>
 
 **Server**: java Server <server_host> <server_port> <central_server_host> <central_server_port> [registry_binded_object_name]
 
 **Client**: java Client <server_host> <server_port> [registry_binded_object_name]
