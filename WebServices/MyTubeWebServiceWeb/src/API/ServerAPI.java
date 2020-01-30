
package API;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import database.ServerTable;
import entities.Server;


@Path("/server")
public class ServerAPI {
	
	private ServerTable serverTable = new ServerTable();
	
	@Path("")
    @GET
    @Produces("application/json")
    public List<Server> getAllServers(){
        return serverTable.getAllServers();
    }
	
	@Path("/{serverKey}")
    @GET
    @Produces("application/json")
    public Server getServerFromKey(@PathParam("serverKey") int serverKey){
        return serverTable.getServerFromKey(serverKey);
    }
	
	@Path("/host/{host}/port/{port}")
    @GET
    @Produces("application/json")
    public Server getServerFromHostPort(@PathParam("host") String host, @PathParam("port") String port){
        return serverTable.getServerFromHostPort(host, port);
    }
	
	@Path("")
    @POST
    public Response addServer(Server serverToAdd){
		// if there exists a server with the same host and port 
		if (serverTable.doesServerExist(serverToAdd)) {
			serverTable.setIsServerOnline(serverToAdd, "true");
			return Response.status(409).build();
		}
		
        if (serverTable.addServer(serverToAdd)) {
        	return Response.status(201).build();
        }
        
        return Response.status(500).build();
    }
	
	@Path("")
    @DELETE
    public Response setServerToOffline(Server serverToAdd){
		// if there exists a server with the same host and port 
		if (serverTable.doesServerExist(serverToAdd)) {
			serverTable.setIsServerOnline(serverToAdd, "false");
			return Response.status(200).build();
		}
        
        return Response.status(400).build();
    }
	
}
