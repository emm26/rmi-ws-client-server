
package api.clients;

import entities.DigitalContent;
import sun.net.www.protocol.http.HttpURLConnection;
import utils.Output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class DigitalContentWSClients {

	public DigitalContentWSClients(){ }

	public DigitalContent getContentFromKey(String contentKey){
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content/" + contentKey);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String contentInJson = br.readLine();
			conn.disconnect();

			return new DigitalContent().fromJson(contentInJson);

		} catch (Exception e) {
			Output.printError("listUserContents(): " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public int addContent(DigitalContent toAdd) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(toAdd.getJson().getBytes());
			os.flush();

			boolean isAdded = (conn.getResponseCode() == HttpURLConnection.HTTP_CREATED);

			conn.disconnect();

			if (!isAdded){
				return -1;
			}

			// return the added content's identifier
			return this.search(toAdd.getTitle(), false).get(0).getKey();

		} catch (Exception e) {
			Output.printError("addContent(): " + e.toString());
			e.printStackTrace();
			return -1;
		}
	}

	public List<DigitalContent> listUserContents(String username) {

		int userKey = new UserWSClients().getUserFromUsername(username).getKey();

		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content/user/" + userKey);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String contentsInJson = br.readLine();
			conn.disconnect();
			return new DigitalContent().fromJsonContents(contentsInJson);

		} catch (Exception e) {
			Output.printError("listUserContents(): " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public List<DigitalContent> listAllContents() {

		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content"  );
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String contentsInJson = br.readLine();
			conn.disconnect();

			return new DigitalContent().fromJsonContents(contentsInJson);

		} catch (Exception e) {
			Output.printError("listAllContents(): " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public List<DigitalContent> search(String toSearch, boolean partialSearch) {
		try {
			toSearch = toSearch.replaceAll("\\s","\\+");
			URL url;
			if (partialSearch){
				url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content/search/" + toSearch + "?partial=true");
			} else {
				url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content/search/" + toSearch);
			}
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String contentsInJson = br.readLine();
			conn.disconnect();
			return new DigitalContent().fromJsonContents(contentsInJson);

		} catch (Exception e) {
			Output.printError("exactSearch(): " + e.toString());
			// e.printStackTrace();
			return null;
		}
	}

	public boolean deleteContent(String contentKey, String password){
		//DELETE
		try {
			// content to delete is content with Key = 4, as specified in the URL
			URL url = new URL ("http://localhost:8080/MyTubeWebServiceWeb/api/content/" + contentKey);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");

			// pass bytes from password, if wrong password server will return 401
			// and will not delete the content
			OutputStream os = conn.getOutputStream();
			os.write(password.getBytes());
			os.flush();

			boolean isRemoved = (conn.getResponseCode() == 200);

			conn.disconnect();

			return isRemoved;

		} catch (Exception e) {
			Output.printError("deleteContent(): " + e.toString());
			// e.printStackTrace();
			return false;

		}
	}

	public boolean renameContent(String contentKey, DigitalContent modifiedContent) {
		try {
			// content to modify is content with Key = 1, as specified in the URL
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/content/" + contentKey);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(modifiedContent.getJson().getBytes());
			os.flush();

			boolean isRenamed = (conn.getResponseCode() == 200);

			conn.disconnect();

			return isRenamed;

		} catch (Exception e) {
			Output.printError("renameContent(): " + e.toString());
			// e.printStackTrace();
			return false;
		}
	}
}
