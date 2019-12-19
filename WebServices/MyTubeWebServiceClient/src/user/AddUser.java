
package user;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import sun.net.www.protocol.http.HttpURLConnection;

public class AddUser {
	
	public static void main(String[] args) {
		//POST
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/user");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			// user to add - pass it as bytes from JSON 
			User toAdd = new User("genericuser", "genericpassword");
			System.out.println("Adding user: " + toAdd.getJson());
			OutputStream os = conn.getOutputStream();
			os.write(toAdd.getJson().getBytes());
			os.flush();
			
			if(conn.getResponseCode() != 201) {
				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); 
			}
			
			System.out.println("Success: HTTP code: " + conn.getResponseCode());
			conn.disconnect();
			
		} catch (MalformedURLException e) { 
			e.printStackTrace();
			
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
}
