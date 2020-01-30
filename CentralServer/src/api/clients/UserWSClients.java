
package api.clients;

import entities.User;
import sun.net.www.protocol.http.HttpURLConnection;
import utils.Output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class UserWSClients {

	public UserWSClients() {
	}

	public boolean register(User toRegister) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/user");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			// user to register - pass it as bytes from JSON
			OutputStream os = conn.getOutputStream();
			os.write(toRegister.getJson().getBytes());
			os.flush();

			boolean isRegistered = (conn.getResponseCode() == 201);

			if (isRegistered) {
				Output.printInfo("User with username: " + toRegister.getUsername()  + " has registered and logged in");
			} else {
				Output.printWarning("User with username: " + toRegister.getUsername() + " couldn't be registered, username already exists");
			}

			conn.disconnect();
			return isRegistered;

		} catch (Exception e) {
			Output.printError("register(): " + e.toString());
			e.printStackTrace();
			return false;

		}
	}

	public boolean logIn(User toLogin) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/user/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			// user to login - pass it as bytes from JSON
			OutputStream os = conn.getOutputStream();
			os.write(toLogin.getJson().getBytes());
			os.flush();

			boolean isLoggedIn = (conn.getResponseCode() == 200);

			if (isLoggedIn) {
				Output.printInfo("User with username: " + toLogin.getUsername() + " has logged in");
			} else {
				Output.printWarning("User with username: " + toLogin.getUsername() + " couldn't log in, incorrect password provided");
			}

			conn.disconnect();
			return isLoggedIn;

		} catch (Exception e) {
			Output.printError("login(): " + e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public User getUserFromUsername(String username) {
		try {
			URL url = new URL("http://localhost:8080/MyTubeWebServiceWeb/api/user/username/" + username);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String userInJson = br.readLine();
			conn.disconnect();
			return new User().fromJson(userInJson);

		} catch (Exception e) {
			Output.printError("getUserFromUsername(): " + e.toString());
			e.printStackTrace();
			return new User();
		}
	}

}
