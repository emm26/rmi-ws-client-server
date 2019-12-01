
package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {

	String uploadContent(byte[] content, String title, String description, String password, String fileName) throws RemoteException;

	boolean isContentPasswordProtected(String key) throws RemoteException;

	void addConnectedClient(ClientInterface client) throws RemoteException;

	void removeConnectedClient(ClientInterface client) throws RemoteException;

	void notifyCentralServerStopped() throws RemoteException;

	boolean deleteContent(String password, String key) throws RemoteException;

	boolean renameContent(String password, String key, String newName) throws RemoteException;

	byte[] downloadContentLocallyStored(String key) throws RemoteException;

	byte[] downloadContentNotLocallyStored(String key) throws RemoteException;

	List<DigitalContent> localSearchContentsFromTitle(String title) throws RemoteException;

	List<DigitalContent> localSearchContentsFromDescription(String description) throws RemoteException;

	List<DigitalContent> localSearchContentsFromPartialTitle(String title) throws RemoteException;

	List<DigitalContent> localSearchContentsFromPartialDescription(String description) throws RemoteException;

	List<DigitalContent> listLocalContents() throws RemoteException;

	List<DigitalContent> listGlobalContents() throws RemoteException;

	List<DigitalContent> globalSearchContentsFromTitle(String title) throws RemoteException;

	List<DigitalContent> globalSearchContentsFromDescription(String description) throws RemoteException;

	List<DigitalContent> globalSearchContentsFromPartialTitle(String title) throws RemoteException;

	List<DigitalContent> globalSearchContentsFromPartialDescription(String description) throws RemoteException;

}
