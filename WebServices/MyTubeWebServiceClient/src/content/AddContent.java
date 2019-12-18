
package content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.OutputStream;
import sun.net.www.protocol.http.HttpURLConnection;

public class AddContent {
	
	public static void main(String[] args) {
		//POST
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/content");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			
			// content to add - pass it as bytes from JSON 
			DigitalContent toAdd = new DigitalContent("title12", "description", "", 2, 1);
			System.out.print(toAdd.getJson());
			OutputStream os = conn.getOutputStream();
			os.write(toAdd.getJson().getBytes());
			os.flush();
			
			if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode()); 
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output;
			while((output = br.readLine()) != null) {
				System.out.println("\nClient POST. Answer: " + output );
			}
			
			conn.disconnect();
			
		} catch (MalformedURLException e) { 
			e.printStackTrace();
			
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
}
