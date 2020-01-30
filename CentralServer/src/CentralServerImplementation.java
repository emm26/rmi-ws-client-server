
import api.clients.DigitalContentWSClients;
import api.clients.ServerWSClients;
import api.clients.UserWSClients;
import common.CentralServerInterface;
import common.ServerInterface;
import entities.DigitalContent;
import entities.Server;
import entities.User;
import utils.Output;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CentralServerImplementation extends UnicastRemoteObject implements CentralServerInterface {

	private Map<Integer, ServerInterface> connectedServers;
	private ServerWSClients serverWSClients;
	private UserWSClients userWSClients;
	private DigitalContentWSClients contentWSClients;

	public CentralServerImplementation() throws RemoteException {
		connectedServers = new HashMap<>();
		serverWSClients = new ServerWSClients();
		userWSClients = new UserWSClients();
		contentWSClients = new DigitalContentWSClients();
	}

	/**
	 * Adds a server to database using WS.
	 * Also saves the ServerInterface stub in the connectedServers arrayList.
	 *
	 * @param serverToAdd object containing all the info about the server that is about to be saved.
	 * @param server      stub, will be used for interaction between central server and server.
	 * @return true or false whether the server info has been saved in the database through the WS.
	 * @throws RemoteException
	 */
	public boolean addConnectedServer(Server serverToAdd, ServerInterface server) throws RemoteException {
		// add server to the database using the WS
		boolean isAdded = serverWSClients.loginOrRegister(serverToAdd);

		if (isAdded) {
			// save server identifier along with the server stub
			int serverKey = serverWSClients.getServerFromIPPort(serverToAdd.getIP(), serverToAdd.getPort()).getKey();
			connectedServers.put(serverKey, server);
			Output.printSuccess("Got a server connection. (Connected servers: " + connectedServers.size() + ")");
		} else {
			Output.printError("Couldn't add server info to WS, check Postgres and the WS are up");
		}

		return isAdded;
	}

	/**
	 * Sets server isOnline to false in the database using WS.
	 * Also deletes the ServerInterface stub from the connectedServers arrayList.
	 *
	 * @param serverToRemove object containing all the info about the server that is about to be setOnline to false of.
	 * @param server         stub, will be used for interaction between central server and server.
	 * @return true or false whether isOnline has been set to false for server in the database through the WS or not.
	 * @throws RemoteException
	 */
	public boolean removeConnectedServer(Server serverToRemove, ServerInterface server) throws RemoteException {
		// sets isOnline to false in the database using WS.
		boolean isRemoved = serverWSClients.logOut(serverToRemove);

		if (isRemoved) {
			// remove server stub
			connectedServers.remove(serverToRemove.getKey());
			Output.printWarning("A server has disconnected  (Connected servers: " + connectedServers.size() + ")");
		} else {
			Output.printError("Couldn't remove server info from WS, check Postgres and the WS are up");
		}

		return isRemoved;
	}

	/**
	 * Obtains the server id (or key in the database) through the WS.
	 *
	 * @param IP   address of the server to get the id of.
	 * @param port of the server to get the id of.
	 * @return the id of the server with IP @IP and port @port (if it exists in the db) or -1 by default.
	 * @throws RemoteException
	 */
	public int getServerIdFromIPPort(String IP, String port) throws RemoteException {
		return serverWSClients.getServerFromIPPort(IP, port).getKey();
	}

	/**
	 * Obtains the user id (or key in the database) through the WS.
	 *
	 * @param username of the user to get the id of.
	 * @return the id of the user with username @username (if it exists in the db) or -1 by default.
	 * @throws RemoteException
	 */
	public int getUserIdFromUsername(String username) throws RemoteException {
		return userWSClients.getUserFromUsername(username).getKey();
	}

	/**
	 * Checks if the given user exists in the database through the WS.
	 *
	 * @param userToLogin object containing all the info of the user to verify its existance in the db (username, password).
	 * @return true or false whether the user exists in the db or not.
	 */
	public boolean canUserLogIn(User userToLogin) {
		return userWSClients.logIn(userToLogin);
	}

	/**
	 * Adds a user in the database through the WS.
	 *
	 * @param userToRegister object containing all the info of the user to add in the db (username, password).
	 * @return true or false whether the user is successully registered or not
	 * (will return false, for example, when a user with the given username already exists in the db).
	 */
	public boolean registerUser(User userToRegister) {
		return userWSClients.register(userToRegister);
	}

	/**
	 * Adds content's info to database using the WS.
	 *
	 * @param contentToAdd object containing all the info of the content to add in the db (title, description, password, userOwnerKey, serverOwnerKey).
	 * @return the content identifier in the database or -1 by default.
	 * @throws RemoteException
	 */
	public int addContent(DigitalContent contentToAdd) throws RemoteException {
		return contentWSClients.addContent(contentToAdd);
	}

	/**
	 * Gets all contents stored in the db through a request to the WS.
	 *
	 * @return list of objects containing all the info about the contents stored or null by default.
	 * @throws RemoteException
	 */
	public List<DigitalContent> listContents() throws RemoteException {
		Output.printInfo("Got a list all contents request");
		return contentWSClients.listAllContents();
	}

	/**
	 * Gets the content matching the provided key.
	 *
	 * @param key of the content to get.
	 * @return object containing the info about the content or null by default.
	 * @throws RemoteException
	 */
	public DigitalContent getContentFromKey(String key) throws RemoteException {
		Output.printInfo("Got a get content from key request");
		return contentWSClients.getContentFromKey(key);
	}

	/**
	 * Downloads the content specified by the key.
	 * Firstly it checks that the content exists in the db through the WS.
	 * <p>
	 * If it indeed exists, the next step is to get the server identifier of the
	 * server that is storing the content. If that server isOnline then we can
	 * contact it and obtain the content.
	 *
	 * @param key of the content to download.
	 * @return the bytes of the content itself or null by default.
	 * @throws RemoteException
	 */
	public byte[] downloadContent(String key) throws RemoteException {

		DigitalContent contentToDownload = contentWSClients.getContentFromKey(key);

		// check if content with the given key exists
		if (contentToDownload == null) {
			Output.printError("Got a download content from key request for a content that doesn't exist");
			return null;
		}

		Output.printInfo("Got a content download request for content: " + contentToDownload.toString());

		// check if server storing the content isOnline
		ServerInterface serverStoringTheContent = connectedServers.get(contentToDownload.getServerOwnerKey());
		if (!serverWSClients.getServerFromKey(key).getIsOnline()) {
			Output.printError("Looks like the server storing the content to download is not online");
			return null;
		}

		// contact the server stub that is storing the content
		Output.printInfo("Contacting server that stores the content to download; server identifier is :" + contentToDownload.getServerOwnerKey());
		return serverStoringTheContent.downloadContentLocallyStored(contentToDownload);
	}

	/**
	 * Gets the contents with exact/partial that match on title/description with the given string.
	 *
	 * @param toSearch the string to search a match of.
	 * @param partial  whether partial match is allowed or not.
	 * @return a list of objects containing info about the matched contents or an empty one by default.
	 * @throws RemoteException
	 */
	public List<DigitalContent> search(String toSearch, boolean partial) throws RemoteException {
		Output.printInfo("Got a partial content search request for content: " + toSearch);
		return contentWSClients.search(toSearch, partial);
	}

	public boolean renameContent(String password, String contentKey, String newName) throws RemoteException {
		DigitalContent contentToRename = contentWSClients.getContentFromKey(contentKey);

		// check if content with the given key exists
		if (contentToRename == null) {
			Output.printError("Got content renaming request for a content that doesn't exist");
			return false;
		}

		Output.printInfo("Got a content renaming request for content: " + contentToRename.toString());

		// rename from db through the WS
		DigitalContent modifiedContent = contentToRename;
		modifiedContent.setTitle(newName);
		return contentWSClients.renameContent(contentKey, modifiedContent);
	}


	public boolean deleteContent(String password, String contentKey) throws RemoteException {
		DigitalContent contentToDelete = contentWSClients.getContentFromKey(contentKey);

		// check if content with the given key exists
		if (contentToDelete == null) {
			Output.printError("Got a delete content request for a content that doesn't exist");
			return false;
		}

		Output.printInfo("Got a delete content request for content: " + contentToDelete.toString());

		// check if server storing the content isOnline
		int serverOwnerKey = contentToDelete.getServerOwnerKey();
		ServerInterface serverStoringTheContent = connectedServers.get(serverOwnerKey);
		if (!serverWSClients.getServerFromKey(Integer.toString(serverOwnerKey)).getIsOnline()) {
			Output.printError("Looks like the server storing the content to delete is not online");
			return false;
		}

		// contact the server stub that is storing the content
		Output.printInfo("Contacting server that stores the content to delete; server identifier is :" + contentToDelete.getServerOwnerKey());
		if (!serverStoringTheContent.deleteContentLocallyStored(contentToDelete)) {
			Output.printError("Server owning the content couldn't delete the content");
			return false;
		}

		// delete from db through the WS
		return contentWSClients.deleteContent(contentKey, password);
	}

	public List<DigitalContent> listUserContents(String username) throws RemoteException {
		return contentWSClients.listUserContents(username);
	}

	public void notifyCentralServerStopped() {
		Output.printWarning("Notifying connected servers that central server stopped");
		for (Map.Entry<Integer, ServerInterface> server : connectedServers.entrySet()) {
			try {
				server.getValue().notifyCentralServerStopped();
			} catch (Exception e) {
				Output.printError("While notifying central server stopped: " + e.toString());
			}
		}
	}
}
