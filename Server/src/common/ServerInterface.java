
package common;

import entities.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {

	int uploadContent(byte[] content, DigitalContent contentToUpload) throws RemoteException;

	void addConnectedClient(ClientInterface client) throws RemoteException;

	void removeConnectedClient(ClientInterface client) throws RemoteException;

	int getUserIdFromUsername(String username) throws RemoteException;

	void notifyCentralServerStopped() throws RemoteException;

	boolean loginOrRegister(User userToLoginOrRegister) throws RemoteException;

	List<DigitalContent> listUserContents(String username) throws RemoteException;

	List<DigitalContent> listContents() throws RemoteException;

	byte[] downloadContentLocallyStored(DigitalContent toDownload) throws RemoteException;

	List<DigitalContent> search(String toSearch, boolean partial) throws RemoteException;

	boolean doesUserOwnTheContent(int userId, String contentKey) throws RemoteException;

	boolean isContentPasswordProtected(String key) throws RemoteException;

	boolean renameContent(String password, String key, String newName) throws RemoteException;

	boolean deleteContent(String password, String key) throws RemoteException;

	boolean deleteContentLocallyStored(DigitalContent toDelete) throws RemoteException;

}
