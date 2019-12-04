
package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CentralServerInterface extends Remote {

	void addConnectedServer(ServerInterface server) throws RemoteException;

	int getConnectedServerIdentifier() throws RemoteException;

	void removeConnectedServer(ServerInterface server) throws RemoteException;

	byte[] downloadContent(String key) throws RemoteException;

	List<DigitalContent> searchContentsFromTitle(String title) throws RemoteException;

	List<DigitalContent> searchContentsFromDescription(String description) throws RemoteException;

	List<DigitalContent> searchContentsFromPartialTitle(String title) throws RemoteException;

	List<DigitalContent> searchContentsFromPartialDescription(String description) throws RemoteException;

	List<DigitalContent> listContents() throws RemoteException;
}
