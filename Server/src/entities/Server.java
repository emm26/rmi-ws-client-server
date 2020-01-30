
package entities;

import com.google.gson.Gson;

import java.io.Serializable;

public class Server implements Serializable {

	private int key;
	private String IP;
	private String port;
	private boolean isOnline;

	public Server() {

	}

	public Server(String IP, String port) {
		this.IP = IP;
		this.port = port;
	}

	public Server(int key, String IP, String port) {
		this.key = key;
		this.IP = IP;
		this.port = port;
	}

	public Server(String IP, String port, boolean isOnline) {
		this.IP = IP;
		this.port = port;
		this.isOnline = isOnline;
	}

	public Server(int key, String IP, String port, boolean isOnline) {
		this.key = key;
		this.IP = IP;
		this.port = port;
		this.isOnline = isOnline;
	}

	public int getKey() {
		return this.key;
	}

	public String getIP() {
		return this.IP;
	}

	public String getPort() {
		return this.port;
	}

	public boolean getIsOnline() {
		return this.isOnline;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Server fromJson(String json) {
		return new Gson().fromJson(json, Server.class);
	}

	public String getJson() {
		return "{\"key\":" + key + "," + "\"ip\":\""+ IP + "\"," + "\"port\":\"" + port + "\",\"isOnline\":\"" + isOnline  + "\"}";
	}
}
