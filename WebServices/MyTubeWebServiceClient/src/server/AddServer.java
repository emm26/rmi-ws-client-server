
package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import sun.net.www.protocol.http.HttpURLConnection;

public class AddServer {
	
	public static void main(String[] args) {
		//POST
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/server");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			// server to add - pass it as bytes from JSON 
			Server toAdd = new Server("127.0.0.127", "1234");
			System.out.println("Adding server:" + toAdd.getJson());
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
