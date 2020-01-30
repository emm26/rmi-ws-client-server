
package entities;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DigitalContent implements Serializable {

	private int key;
	private String title;
	private String description;
	private String password;
	private int userOwnerKey;
	private int serverOwnerKey;

	public DigitalContent() {

	}

	public DigitalContent(String title, String description, String password, int userOwnerKey) {
		this.key = key;
		this.title = title;
		this.description = description;
		this.password = password;
		this.userOwnerKey = userOwnerKey;
	}

	public DigitalContent(int key, String title, String description, String password, int userOwnerKey, int serverOwnerKey) {
		this.key = key;
		this.title = title;
		this.description = description;
		this.password = password;
		this.userOwnerKey = userOwnerKey;
		this.serverOwnerKey = serverOwnerKey;
	}

	public DigitalContent(String title, String description, String password, int userOwnerKey, int serverOwnerKey) {
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

	public String getJson() {
		return "{\"key\":" + key + "," + "\"title\":\"" + title + "\"," + "\"description\":\"" + description + "\"," + "\"password\":\"" + password + "\"," + "\"userOwnerKey\":" + userOwnerKey + "," + "\"serverOwnerKey\":" + serverOwnerKey + "}";
	}

	public DigitalContent fromJson(String json) {
		return new Gson().fromJson(json, DigitalContent.class);
	}

	public List<DigitalContent> fromJsonContents(String json) {
		return new Gson().fromJson(json, new TypeToken<ArrayList<DigitalContent>>(){}.getType());
	}

	public String toString() {
		return ("\n-------------------------------------------------" +
			   "\n\t KEY: " + key +
			   "\n\t TITLE: " + title +
			   "\n\t DESCRIPTION: " + description +
			   "\n\t USER OWNER KEY: " + userOwnerKey +
			   "\n\t SERVER OWNER KEY: " + serverOwnerKey +
			   "\n-------------------------------------------------");
	}
}
