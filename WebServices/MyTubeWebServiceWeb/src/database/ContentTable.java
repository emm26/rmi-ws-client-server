
package database;

import utils.DigitalContent;
import utils.Output;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentTable extends ConnectionManager {

	public ContentTable() {
		// must create server and user tables before creating content table (foreign keys)
		new ServerTable();
		new UserTable();
		
		this.createContentTable();
	}

	private void createContentTable() {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "CREATE TABLE IF NOT EXISTS " +
				   "content (Key SERIAL PRIMARY KEY," +
				   "Title TEXT NOT NULL UNIQUE, " +
				   "Description TEXT NOT NULL, " +
				   "Password TEXT," + 
				   "UserOwnerKey INTEGER REFERENCES users(Key), " +
				   "ServerOwnerKey INTEGER REFERENCES server(Key)); ";
			st.executeUpdate(query);
			st.close();

		} catch (SQLException e) {
			Output.printError("Couldn't create table contents in database: " + e.toString());
			closeConnection();
			System.exit(1);
		}
		closeConnection();
	}
	
	public boolean addContent(DigitalContent contentToAdd) {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "INSERT INTO content(Title, Description, Password, userOwnerKey, serverOwnerKey) " +
				   "VALUES ( '" + contentToAdd.getTitle() + "', '" + contentToAdd.getDescription() + "', '" + contentToAdd.getPassword() + "', '" + contentToAdd.getUserOwnerKey() + "', '" + contentToAdd.getServerOwnerKey() + "');";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't add content to database: " + e.toString());
			return false;
		}
		closeConnection();
		return true;
	}

	public boolean deleteContent(int key) {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "DELETE FROM content WHERE Key = '" + key + "';";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't delete content from database: " + e.toString());
			closeConnection();
			return false;
		}
		closeConnection();
		return true;
	}

	public boolean modifyContent(int key, DigitalContent modifiedContent) {
		openConnection();
		try {
			Statement st = conn.createStatement();
			String query = "UPDATE content SET Title = '" + modifiedContent.getTitle() + "', Description = '" + modifiedContent.getDescription() + "' WHERE Key = '" + key + "';";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't modify content from database: " + e.toString());
			closeConnection();
			return false;
		}
		closeConnection();
		return true;
	}

	public int getNumOfContents(){
		openConnection();
		String query = "SELECT COUNT(*) FROM content;";
		int numOfContents = 0;

		try {
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			numOfContents = result.getInt("COUNT(*)");
			result.close();
			st.close();

		} catch (SQLException e) {
			Output.printError("While querying: " + query + ": " + e.toString());
		}
		closeConnection();
		return numOfContents;
	}

	public DigitalContent getContentFromKey(int key) {
		String query = "SELECT * FROM content WHERE Key = '" + key + "';";
		return this.queryAndObtainContent(query);
	}

	public List<DigitalContent> exactSearch(String toSearch) {
		String query = "SELECT * FROM content WHERE Title = '" + toSearch + "' OR Description = '" + toSearch + "';";
		return this.queryAndObtainContents(query);
	}
	
	public List<DigitalContent> partialSearch(String toSearch) {
		String query = "SELECT * FROM content WHERE Title LIKE '%" + toSearch + "%' OR Description LIKE '%" + toSearch + "%';";
		return this.queryAndObtainContents(query);
	}
	
	public List<DigitalContent> getUserContents(int userKey){
		String query = "SELECT * FROM content WHERE OwnerKey = '" + userKey + "';";
		return this.queryAndObtainContents(query);
	}
	
	private List<DigitalContent> queryAndObtainContents(String query) {
		openConnection();
		List<DigitalContent> contents = new ArrayList<>();
		try {
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				DigitalContent toAdd = new DigitalContent(result.getInt("Key"),
					   result.getString("Title"),
					   result.getString("Description"),
					   result.getString("Password"),
					   result.getInt("UserOwnerKey"),
					   result.getInt("ServerOwnerKey"));
				contents.add(toAdd);
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("While querying: " + query + ": " + e.toString());
		}
		closeConnection();
		return contents;
	}

	private DigitalContent queryAndObtainContent(String query) {
		openConnection();
		DigitalContent content = null;
		try {
			Statement st = conn.createStatement();
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				content = new DigitalContent(result.getInt("Key"),
						   result.getString("Title"),
						   result.getString("Description"),
						   result.getString("Password"),
						   result.getInt("UserOwnerKey"),
						   result.getInt("ServerOwnerKey"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("While querying: " + query + ": " + e.toString());
		}
		closeConnection();
		return content;
	}

	public List<DigitalContent> getAllContents() {
		String query = "SELECT * FROM content;";
		return this.queryAndObtainContents(query);
	}

	public boolean isContentPasswordProtected(int key) {
		openConnection();
		boolean isProtected = true;
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content WHERE Key = '" + key + "';";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				isProtected = !Objects.equals(result.getString("Password"), "null") && !Objects.equals(result.getString("Password"), "");
			}
		} catch (SQLException e) {
			Output.printError("Couldn't check if content is password protected: " + e.toString());
		}
		closeConnection();
		return isProtected;
	}

	public boolean isContentPasswordCorrect(String password, int key) {
		openConnection();
		boolean isCorrect = false;
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content WHERE Key = '" + key + "';";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				isCorrect = Objects.equals(result.getString("Password"), password);
			}
		} catch (SQLException e) {
			Output.printError("Couldn't check if password is correct: " + e.toString());
		}
		closeConnection();
		return isCorrect;
	}

}
