
package common;

import entities.DigitalContent;
import entities.Server;
import entities.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface CentralServerInterface extends Remote {

	boolean addConnectedServer(Server servertoAdd, ServerInterface server) throws RemoteException;

	boolean removeConnectedServer(Server servertoRemove, ServerInterface server) throws RemoteException;

	int getServerIdFromIPPort(String IP, String port) throws RemoteException;

	int getUserIdFromUsername(String username) throws RemoteException;

	boolean canUserLogIn(User userToLogin) throws RemoteException;

	boolean registerUser(User userToRegister) throws RemoteException;

	int addContent(DigitalContent contentToAdd) throws RemoteException;

	byte[] downloadContent(String key) throws RemoteException;

	boolean renameContent(String password, String contentKey, String newName) throws RemoteException;

	boolean deleteContent(String password, String contentKey) throws RemoteException;

	List<DigitalContent> listUserContents(String username) throws RemoteException;

	List<DigitalContent> listContents() throws RemoteException;

	DigitalContent getContentFromKey(String key) throws RemoteException;

	List<DigitalContent> search(String toSearch, boolean partial) throws RemoteException;

}

