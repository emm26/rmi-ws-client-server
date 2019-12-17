
package API;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import java.util.List;
import utils.DigitalContent;
import database.ContentTable;

@Path("/content")
public class ContentAPI {
	
	private ContentTable contentTable = new ContentTable();
	
    @Path("")
    @GET
    @Produces("application/json")
    public List<DigitalContent> getAllContents(){
        return contentTable.getAllContents();
    }
    
    @Path("/{contentKey}")
    @GET
    @Produces("application/json")
    public DigitalContent getContentFromKey(@PathParam("contentKey") int contentKey){
    	return contentTable.getContentFromKey(contentKey);
    }
    
    @Path("/search/{toSearch}")
    @GET
    @Produces("application/json")
    public List<DigitalContent> searchContents(@PathParam("toSearch") String contentToSearch, @QueryParam("partial") boolean isPartialSearchAllowed){
    	if (isPartialSearchAllowed == true) {
    		return contentTable.partialSearch(contentToSearch);
    	}
    	return contentTable.exactSearch(contentToSearch);
    }
    
    @Path("/user/{userKey}")
    @GET
    @Produces("application/json")
    public List<DigitalContent> getUserContents(@PathParam("userKey") int userKey){
        return contentTable.getUserContents(userKey);
    }
    
    
    @Path("")
    @POST
    public Response uploadContent(DigitalContent content){
        if (contentTable.addContent(content)) {
        	return Response.status(201).build();
        }
        return Response.status(500).build();
    }
    
    @Path("/{contentKey}")
    @PUT
    public Response modifyContent(@PathParam("contentKey") int contentKey, DigitalContent modifiedContent){
    	
    	String contentPassword = modifiedContent.getPassword();
    	
    	// check invalid content password
    	if (contentTable.isContentPasswordProtected(contentKey) && !contentTable.isContentPasswordCorrect(contentPassword, contentKey)) {
    		return Response.status(401).build();
    	}
    	
    	if (contentTable.modifyContent(contentKey, modifiedContent)) {
    		return Response.status(200).build();
    	}
    	
    	return Response.status(500).build();
    }
    
    @Path("/{contentKey}")
    @DELETE
    public Response deleteContent(@PathParam("contentKey") int contentKey, String contentPassword){
    	// check invalid content password
    	if (contentTable.isContentPasswordProtected(contentKey) && !contentTable.isContentPasswordCorrect(contentPassword, contentKey)) {
    		return Response.status(401).build();
    	}
    	
        if (contentTable.deleteContent(contentKey)) {
        	return Response.status(200).build();
        }
        return Response.status(500).build();
    }
    
}