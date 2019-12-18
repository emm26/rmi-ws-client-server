
package content;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import sun.net.www.protocol.http.HttpURLConnection;

public class DeleteContent {
	
	public static void main(String[] args) {
		//DELETE
		try {
			// content to delete is content with Key = 12, as specified in the URL
			URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/content/12");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			
			// pass bytes from password, if wrong password server will return 401 
			// and will not delete the content
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
