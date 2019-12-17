
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import utils.User;
import utils.Output;

public class UserTable extends ConnectionManager {

	public UserTable() {
		this.createUsersTable();
	}

	private void createUsersTable() {
		try {
			openConnection();
			Statement st = conn.createStatement();
			String query = "CREATE TABLE IF NOT EXISTS " +
				   "users (Key SERIAL PRIMARY KEY," +
				   "Username TEXT NOT NULL UNIQUE, " +
				   "Password TEXT NOT NULL);"; 
			st.executeUpdate(query);
			st.close();
			closeConnection();
		} catch (SQLException e) {
			Output.printError("Couldn't create table users in database: " + e.toString());
			closeConnection();
			System.exit(1);
		}
	}
	
	public boolean addUser(User userToAdd) {
		try {
			openConnection();
			Statement st = conn.createStatement();
			String query = "INSERT INTO users(Username, Password) " +
				   "VALUES ('" + userToAdd.getUsername() + "', '" + userToAdd.getPassword() +  "');";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't add user to database: " + e.toString());
			closeConnection();
			return false;
		}
		closeConnection();
		return true;
	}
	
	public boolean deleteUser(int key) {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "DELETE FROM users WHERE Key = '" + key + "';";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't delete user from database: " + e.toString());
			closeConnection();
			return false;
		}
		closeConnection();
		return true;
	}
	
	public boolean modifyUser(int key, User modifiedUser) {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "UPDATE users SET Username = '" + modifiedUser.getUsername() + "', Password = '" + modifiedUser.getPassword() + "' WHERE Key = '" + key + "';";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't modify user from database: " + e.toString());
			closeConnection();
			return false;
		}
		closeConnection();
		return true;
	}
	
	public List<User> getAllUsers() {
		String query = "SELECT * FROM users;";
		return this.queryAndObtainUsers(query);
	}
	
	public User getUserFromKey(int key) {
		String query = "SELECT * FROM users WHERE Key = '" + key + "';";
		return this.queryAndObtainUser(query);
	}
	
	public User getUserFromUsername(String username) {
		String query = "SELECT * FROM users WHERE Username = '" + username + "';";
		return this.queryAndObtainUser(query);
	}

	public boolean isUserValid(int userKey, User user) {
		boolean doesExist = false;
		try {
			openConnection();
			Statement st = conn.createStatement();
			String query = "SELECT * FROM users WHERE Key = '" + userKey + "' AND Username = '" + user.getUsername()+ "' AND Password = '" + user.getPassword() + "';";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				doesExist = !Objects.equals(result.getString("Username"), "null");
			}

		} catch (SQLException e) {
			Output.printError("Couldn't check user's existence in database: " + e.toString());
		}
		closeConnection();
		return doesExist;
	}
	
	private User queryAndObtainUser(String query) {
		User user = null;
		try {
			openConnection();
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				user = new User(result.getInt("Key"),
					   result.getString("Username"),
					   result.getString("Password"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("While querying users database: " + query + ": " + e.toString());
		}
		closeConnection();
		return user;
	}
	
	private List<User> queryAndObtainUsers(String query) {
		openConnection();
		List<User> users = new ArrayList<>();
		try {
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				User user = new User(result.getInt("Key"),
					   result.getString("Username"),
					   result.getString("Password"));
				users.add(user);
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("While querying users database: " + query + ": " + e.toString());
		}
		closeConnection();
		return users;
	}
}
