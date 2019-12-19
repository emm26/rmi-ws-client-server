
package content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import sun.net.www.protocol.http.HttpURLConnection;

public class ModifyContent {
	
	public static void main(String[] args) {
        //PUT
        try {
        	// content to modify is content with Key = 1, as specified in the URL
            URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/content/4");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");

            // pass bytes from password, if wrong password server will return 401 
        	// and will not delete the content
            DigitalContent modified = new DigitalContent("modifiedTitle", "modifiedDescription", "", 1, 1);
            System.out.println("modifying:" + modified.getJson());
            OutputStream os = conn.getOutputStream();
            os.write(modified.getJson().getBytes());
            os.flush();
            
            System.out.println("Server responded code: " + conn.getResponseCode());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
