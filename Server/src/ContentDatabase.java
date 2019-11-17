
import common.DigitalContent;
import common.Output;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentDatabase {

	private Connection conn;

	public ContentDatabase(String databaseName) {
		this.connectToDb(databaseName);
		this.createContentTable();
	}

	private void connectToDb(String databaseName) {
		try {
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
			this.conn.setAutoCommit(false);

		} catch (Exception e) {
			Output.printError("Couldn't connect to database " + databaseName + ": " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
		Output.printSuccess("Connected to " + databaseName + " database");
	}

	public void disconnectFromDb() {
		try {
			conn.close();
		} catch (SQLException e) {
			Output.printError("Couldn't disconnect from database: " + e.toString());
		}
		Output.printSuccess("Disconnected from database");
	}

	private void createContentTable() {
		try {
			Statement st = conn.createStatement();
			String query = "CREATE TABLE IF NOT EXISTS " +
				   "content (Key INTEGER PRIMARY KEY AUTOINCREMENT," +
				   "Title TEXT UNIQUE NOT NULL, " +
				   "Description TEXT NOT NULL, " +
				   "Password TEXT);";
			st.executeUpdate(query);
			st.close();

		} catch (SQLException e) {
			Output.printError("Couldn't create table in database: " + e.toString());
			System.exit(1);
		}
	}

	public boolean addContent(String title, String description, String password) {
		try {
			Statement st = conn.createStatement();
			String query = "INSERT INTO content(Title, Description, Password) " +
				   "VALUES ('" + title + "', '" + description + "', '" + password + "');";
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't add content to database: " + e.toString());
			return false;
		}
		return true;
	}

	public boolean deleteContent(int key){
		try {
			Statement st = conn.createStatement();
			String query = "DELETE FROM content WHERE key = " + key + ";" ;
			st.executeUpdate(query);
			st.close();
			conn.commit();

		} catch (SQLException e) {
			Output.printError("Couldn't delete content from database: " + e.toString());
			return false;
		}
		return true;
	}

	public DigitalContent getContentFromKey(int key) {
		DigitalContent content = null;
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content WHERE Key = " + key + ";";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				content = new DigitalContent(result.getInt("Key"),
					   result.getString("Title"),
					   result.getString("Description"),
					   result.getString("Password"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("Couldn't get content from key: " + e.toString());
		}
		return content;
	}


	public DigitalContent getContentFromTitle(String title) {
		DigitalContent content = null;
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content WHERE Title = '" + title + "';";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				content = new DigitalContent(result.getInt("Key"),
					   result.getString("Title"),
					   result.getString("Description"),
					   result.getString("Password"));
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("Couldn't get content from title: " + e.toString());
		}
		return content;
	}

	public List<DigitalContent> getAllContents() {
		List<DigitalContent> contents = new ArrayList<>();
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content;";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				DigitalContent toAdd = new DigitalContent(result.getInt("Key"),
					   result.getString("Title"),
					   result.getString("Description"),
					   result.getString("Password"));

				contents.add(toAdd);
			}
			result.close();
			st.close();
		} catch (SQLException e) {
			Output.printError("Couldn't get contents from database: " + e.toString());
		}

		return contents;
	}

	public boolean isContentPasswordProtected(int key) {
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content WHERE key = " + key + ";";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				return (!Objects.equals(result.getString("Password"), "null"));
			}
		} catch (SQLException e) {
			Output.printError("Couldn't check if content is password protected: " + e.toString());
		}
		return true;
	}

	public boolean isContentPasswordCorrect(String password, int key) {
		try {
			Statement st = conn.createStatement();
			String query = "SELECT * FROM content WHERE key = " + key + ";";
			ResultSet result = st.executeQuery(query);
			while (result.next()) {
				return (Objects.equals(result.getString("Password"), password));
			}
		} catch (SQLException e) {
			Output.printError("Couldn't check if password is correct: " + e.toString());
		}
		return false;
	}

}
