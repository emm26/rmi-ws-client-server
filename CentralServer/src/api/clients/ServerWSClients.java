package api.clients;

import entities.Server;
import sun.net.www.protocol.http.HttpURLConnection;
import utils.Output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class ServerWSClients {

	public ServerWSClients() {

	}

	public Server getServerFromIPPort(String IP, String port) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/server/host/" + IP + "/port/" + port);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String serverInJson = br.readLine();
			conn.disconnect();
			return new Server().fromJson(serverInJson);

		} catch (Exception e) {
			Output.printError("getServerFromIPPort(): " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public Server getServerFromKey(String key) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/server/" + key);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String serverInJson = br.readLine();
			conn.disconnect();
			return new Server().fromJson(serverInJson);

		} catch (Exception e) {
			Output.printError("getServerFromKey(): " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public boolean loginOrRegister(Server server) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/server");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");


			OutputStream os = conn.getOutputStream();
			os.write(server.getJson().getBytes());
			os.flush();

			int responseCode = conn.getResponseCode();
			boolean isRegistered = (responseCode == 201) || (responseCode == 409);

			conn.disconnect();
			return isRegistered;

		} catch (Exception e) {
			Output.printError("register(): " + e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public boolean logOut(Server server) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/server");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(server.getJson().getBytes());
			os.flush();

			boolean isLoggedOut = (conn.getResponseCode() == 200);

			conn.disconnect();
			return isLoggedOut;

		} catch (Exception e) {
			Output.printError("logOut(): " + e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
