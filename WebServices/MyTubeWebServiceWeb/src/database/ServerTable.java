
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import utils.Server;
import utils.Output;


public class ServerTable extends ConnectionManager {

	public ServerTable() {
		this.createServerTable();
	}
	
	private void createServerTable() {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "CREATE TABLE IF NOT EXISTS " +
				   "server (Key SERIAL PRIMARY KEY," +
				   "IP TEXT NOT NULL, " +
				   "Port TEXT NOT NULL, " +
				   "Online BOOLEAN DEFAULT FALSE);";
			st.executeUpdate(query);
			st.close();

		} catch (SQLException e) {
			Output.printError("Couldn't create table server in database: " + e.toString());
			closeConnection();
			System.exit(1);
		}
		closeConnection();
	}
	
	public List<Server> getAllServers() {
		String query = "SELECT * FROM server;";
		return this.queryAndObtainServers(query);
	}
	
	public List<Server> queryAndObtainServers(String query) {
		openConnection();
		List<Server> servers = new ArrayList<>();
		try {
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				Server toAdd = new Server(result.getInt("Key"),
					   result.getString("IP"),
					   result.getString("Port"),
					   result.getBoolean("isOnline"));
				servers.add(toAdd);
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("While querying: " + query + ": " + e.toString());
		}
		closeConnection();
		return servers;
	}
	
	public Server queryAndObtainServer(String query) {
		openConnection();
		Server server = null;
		try {
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				server = new Server(result.getInt("Key"),
					   result.getString("IP"),
					   result.getString("Port"),
					   result.getBoolean("isOnline"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("While querying: " + query + ": " + e.toString());
		}
		closeConnection();
		return server;
	}
	
	public boolean addServer(Server serverToAdd) {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "INSERT INTO server(IP, Port) " +
				   "VALUES ( '" + serverToAdd.getIP() + "', '" + serverToAdd.getPort() + "');";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't add server to database: " + e.toString());
			return false;
		}
		closeConnection();
		return true;
	}
	
	public Server getServerFromKey(int key) {
		String query = "SELECT * FROM server WHERE Key = '" + key + "';";
		return this.queryAndObtainServer(query);
	}
	
	public Server getServerFromHostPort(String host, String port) {
		String query = "SELECT * FROM server WHERE IP = '" + host + "' AND Port = '" + port + "';";
		return this.queryAndObtainServer(query);
	}
}
