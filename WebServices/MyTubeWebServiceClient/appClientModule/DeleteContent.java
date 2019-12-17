
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import sun.net.www.protocol.http.HttpURLConnection;

public class DeleteContent {
	public static void main(String[] args) {
		//DELETE
		try {
			URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/content/12");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			
			OutputStream os = conn.getOutputStream();
            os.write("12345".getBytes());
            os.flush();
            
			System.out.println(conn.getResponseCode());
			conn.disconnect();
			
		} catch (MalformedURLException e) { 
			e.printStackTrace();
			
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
}
