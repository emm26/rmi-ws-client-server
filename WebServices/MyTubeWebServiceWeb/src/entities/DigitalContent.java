package entities;

import java.util.Objects;

public class DigitalContent {
	private int key;
	private String title;
	private String description;
	private String password;
	private int userOwnerKey;
	private int serverOwnerKey;

	public DigitalContent() {
		
	}
	
	public DigitalContent(int key, String title, String description, String password, int userOwnerKey, int serverOwnerKey) {
		this.key = key;
		this.title = title;
		this.description = description;
		this.password = password;
		this.userOwnerKey = userOwnerKey;
		this.serverOwnerKey = serverOwnerKey;
	}
	
	public DigitalContent( String title, String description, String password, int userOwnerKey, int serverOwnerKey) {
		this.title = title;
		this.description = description;
		this.password = password;
		this.userOwnerKey = userOwnerKey;
		this.serverOwnerKey = serverOwnerKey;
	}

	public boolean isPasswordCorrect(String password) {
		return Objects.equals(this.password, password);
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public int getKey() {
		return this.key;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int getUserOwnerKey() {
		return this.userOwnerKey;
	}
	
	public int getServerOwnerKey() {
		return this.serverOwnerKey;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUserOwnerKey(int userOwnerKey) {
		this.userOwnerKey = userOwnerKey;
	}
	
	public void setServerOwnerKey(int serverOwnerKey) {
		this.serverOwnerKey = serverOwnerKey;
	}

	public String toString() {
		return ("\n-------------------------------------------------" +
			   "\n\t KEY: " + key +
			   "\n\t TITLE: " + title +
			   "\n\t DESCRIPTION: " + description +
			   "\n\t PASSWORD: " + password +
			   "\n\t USER OWNER KEY: " + userOwnerKey +
			   "\n\t SERVER OWNER KEY: " + serverOwnerKey +
			   "\n-------------------------------------------------");
	}
}
