
package common;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
	boolean uploadContent(byte[] content, String title, String description, String password) throws RemoteException;
	boolean isContentPasswordProtected(int key) throws RemoteException;
	void addConnectedClient(ClientInterface client) throws RemoteException;
	void removeConnectedClient(ClientInterface client) throws RemoteException;
	boolean deleteContent(String password, int key) throws RemoteException;
	byte[] downloadContent(String password, int key) throws RemoteException;
	/*
	void modifyContentTitle() throws RemoteException;

	*/
	List<DigitalContent> listContents() throws RemoteException;
	DigitalContent getContentFromKey() throws RemoteException;
}
