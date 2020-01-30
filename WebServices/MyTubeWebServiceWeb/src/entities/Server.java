
package entities;

public class Server {
	
	private int key;
	private String ip;
	private String port;
	private boolean isOnline;
	
	public Server() {
		
	}
	
	public Server(String ip, String port) {
		this.ip = ip;
		this.port = port;
		this.isOnline = false;
	}
	
	public Server(String ip, String port, boolean isOnline) {
		this.ip = ip;
		this.port = port;
		this.isOnline = isOnline;
	}
	
	public Server(int key, String ip, String port, boolean isOnline) {
		this.key = key;
		this.ip = ip;
		this.port = port;
		this.isOnline = isOnline;
	}
	
	public int getKey() {
		return this.key;
	}
	
	public String getIP() {
		return this.ip;
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
	
	public void setIP(String ip) {
		this.ip = ip;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
}
