
package API;

import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import database.UserTable;
import entities.User;

@Path("/user")
public class UserAPI {

	private UserTable userTable = new UserTable();
	
	@Path("")
	@GET
	@Produces("application/json")
	public List<User> getAllUsers(){
		return userTable.getAllUsers();
	}
	
	@Path("")
	@POST
	public Response signUp(User user) {
		// check if user's username already exists
		if (userTable.doesUsernameExist(user.getUsername())) {
			return Response.status(409).build(); // conflict error
		}
		if (userTable.addUser(user)) {
			return Response.status(200).build();
		}
		return Response.status(500).build();
	}
	
	@Path("/{userKey}")
	@GET
	@Produces("application/json")
	public User getUserFromKey(@PathParam("userKey") int userKey){
		return userTable.getUserFromKey(userKey);
	}
	
	@Path("/username/{username}")
	@GET
	@Produces("application/json")
	public User getUserFromUsername(@PathParam("username") String username){
		return userTable.getUserFromUsername(username);
	}
	
}
