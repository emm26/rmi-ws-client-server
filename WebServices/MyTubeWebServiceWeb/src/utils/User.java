
package utils;

public class User {
	
	private int key;
	private String username;
	private String password;
	
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
}
