
package entities;

import com.google.gson.Gson;

import java.io.Serializable;

public class User implements Serializable {

	private int key;
	private String username;
	private String password;

	public User() {

	}

	public User(String username) {
		this.username = username;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(int key, String username, String password) {
		this.key = key;
		this.username = username;
		this.password = password;
	}

	public int getKey() {
		return key;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJson() {
		return "{\"key\":" + key + "," + "\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
	}

	public User fromJson(String json) {
		return new Gson().fromJson(json, User.class);
	}

}
