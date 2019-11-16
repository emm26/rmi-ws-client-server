
package common;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
	String uploadContent(byte[] content, String userId, String host, int port, String title, String description) throws RemoteException;
	/*
	void deleteContent(String id) throws RemoteException;
	void modifyContentTitle() throws RemoteException;
	byte[] downloadContentByTitle(String[] title) throws RemoteException;
	byte[] downloadContentByDescription(String [] description) throws RemoteException;
	*/
	List<String> listContents() throws RemoteException;
}
