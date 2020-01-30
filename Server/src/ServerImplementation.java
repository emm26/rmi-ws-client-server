
import common.CentralServerInterface;
import common.ClientInterface;
import common.ServerInterface;
import entities.DigitalContent;
import entities.User;
import utils.Output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

	/**
	 * HashMap containing <clientIdentifier, ClientStub> of the clients
	 * that connect to the server.
	 */
	private Set<ClientInterface> connectedClients;

	/**
	 * Central server stub.
	 */
	private CentralServerInterface centralServer;

	/**
	 * Integer identifying the server in postgreSQL database used by the Web Services (WS).
	 */
	private int serverId;

	public ServerImplementation(CentralServerInterface centralServer) throws RemoteException {
		this.centralServer = centralServer;
		connectedClients = new HashSet<>();
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	/**
	 * Saves the client stub for further communications server -> client
	 * when a client connects to the server.
	 *
	 * @param client ClientInterface stub.
	 * @throws RemoteException
	 */
	public void addConnectedClient(ClientInterface client) throws RemoteException {
		connectedClients.add(client);
		Output.printInfo("A client has connected. (Connected users: " + connectedClients.size() + ")");
	}

	/**
	 * Deletes the client stub when the client stops its execution.
	 *
	 * @param client ClientInterface stub.
	 * @throws RemoteException
	 */
	public void removeConnectedClient(ClientInterface client) throws RemoteException {
		connectedClients.remove(client);
		Output.printInfo("A client has disconnected. (Connected users: " + connectedClients.size() + ")");
	}

	/**
	 * Given a user (username and password) the server contacts the central server
	 * in order to check if the user can login: if the username and password are correct.
	 * If it cannot login and the username is not taken, the user will register.
	 *
	 * @param userToLoginOrRegister user object containing the username and password.
	 * @return true or false whether the user can log in or register or not.
	 * @throws RemoteException
	 */
	public boolean loginOrRegister(User userToLoginOrRegister) throws RemoteException {
		// try to log in first
		boolean isLoggedIn;
		isLoggedIn = centralServer.canUserLogIn(userToLoginOrRegister);

		if (isLoggedIn) {
			Output.printInfo("User with username: " + userToLoginOrRegister.getUsername() + " has logged in");
			return true;
		}

		// try to register
		boolean isRegistered;
		isRegistered = centralServer.registerUser(userToLoginOrRegister);

		if (isRegistered) {
			Output.printInfo("User with username: " + userToLoginOrRegister.getUsername() + " has registered and logged in");
			return true;
		}

		Output.printInfo("User with username: " + userToLoginOrRegister.getUsername() + " couldn't be logged in, incorrect password");
		return false;
	}

	/**
	 * Lists the contents uploaded/owned by a user given the user's username.
	 *
	 * @param username of the user to get the contents of.
	 * @return the contents uploaded/owned by the user or an empty list by default.
	 * @throws RemoteException
	 */
	public List<DigitalContent> listUserContents(String username) throws RemoteException {
		return this.centralServer.listUserContents(username);
	}

	/**
	 * When the server is about to exit it tries to notify the connected clients
	 * so they can gracefully exit.
	 */
	private void notifyClientsServerStopped() {
		Output.printWarning("Notifying connected users that server stopped");
		for (ClientInterface client : connectedClients) {
			try {
				client.notifyServerExit();
			} catch (RemoteException e) {
				Output.printError("While notifying client that server stopped: " + e.toString());
			}
		}
	}

	public void exit() {
		notifyClientsServerStopped();
	}

	/**
	 * When the server is about to exit it tries to notify the central server.
	 * That way the server can be removed from the list of connectedServers in
	 * the central server.
	 */
	public void notifyCentralServerStopped() throws RemoteException {
		exit();
		new Thread(() -> System.exit(0)).start();
	}

	/**
	 * Passes the content's information to the central server.
	 * Then it saves the actual content locally in the contents folder in a subfolder
	 * named the key of the content.
	 *
	 * @param content      actual content that is saved locally.
	 * @param contentToAdd contents information that is passed to the central server.
	 * @return contents identifier in the db or -1 by default.
	 * @throws RemoteException
	 */
	public int uploadContent(byte[] content, DigitalContent contentToAdd) throws RemoteException {

		Output.printInfo("Got an upload content request");

		// set server owner key with the id of the server
		contentToAdd.setServerOwnerKey(this.serverId);

		// save content key, title, description and password (if existent) on database
		// title of the new content cannot be already existent.
		int contentKey = this.centralServer.addContent(contentToAdd);
		if (contentKey == -1) {
			Output.printError("Content with title: " + contentToAdd.getTitle() + " couldn't be uploaded");
			return -1;
		}

		// save content to folder named the key
		if (saveContent(content, contentToAdd.getTitle(), Integer.toString(contentKey))) {
			Output.printSuccess("New content with key: " + contentKey + " has been uploaded at ./contents/" + contentKey);
			Output.printSuccess("New content with key: " + contentKey + " added to database");
			return contentKey;
		}

		return -1;

	}

	private boolean saveContent(byte[] content, String fileName, String key) {
		createFolder(key);

		try (FileOutputStream fos = new FileOutputStream("./contents/" + key + "/" + fileName)) {
			fos.write(content);
			fos.close();
			return true;

		} catch (Exception e) {
			Output.printError("Couldn't place content in: ./contents/" + key);
			//e.printStackTrace();
			return false;
		}
	}

	private void createFolder(String folderName) {
		try {
			if (!new File("./contents/" + folderName).mkdirs()) {
				Output.printError("Couldn't create directory: ./contents/" + folderName + " for new uploaded content");
			}

		} catch (Exception e) {
			Output.printError("Couldn't create directory: ./contents/" + folderName + " for new uploaded content");
			// e.printStackTrace();
		}

	}

	/**
	 * Tries to download a content given its key by first trying to find it locally.
	 * If it cannot be found locally it calls the central server so it can find the
	 * server the content is stored in and if that server is active then it will
	 * request the content download.
	 *
	 * @param key of the content to download.
	 * @return downloaded content or null by default.
	 * @throws RemoteException
	 */
	public byte[] downloadContent(String key) throws RemoteException {
		DigitalContent toDownload = centralServer.getContentFromKey(key);

		if (toDownload == null) {
			Output.printError("Got a content download request for a content that doesn't exist: content with key: " + key);
			return null;
		}

		Output.printInfo("Got a content download request for content: " + toDownload.toString());

		// check if the content is actually stored by the server
		byte[] locallyStoredContent = downloadContentLocallyStored(toDownload);

		if (locallyStoredContent != null) {
			Output.printSuccess("Sent the following content to client: " + toDownload.toString());
			return locallyStoredContent;
		}

		// if not stored locally, we must contact the central server
		byte[] notLocallyStoredContent = centralServer.downloadContent(key);
		if (notLocallyStoredContent != null) {
			Output.printSuccess("Sent the following content to client: " + toDownload.toString());
			return notLocallyStoredContent;
		}

		Output.printError("Server storing the content is not online");
		return null;
	}


	public byte[] downloadContentLocallyStored(DigitalContent toDownload) throws RemoteException {
		Output.printInfo("Got a content download request for content: " + toDownload.toString());
		String key = Integer.toString(toDownload.getKey());
		byte[] content;
		String fileName;

		try {
			// get the name of the file inside the folder
			fileName = new File("./contents/" + key).listFiles()[0].getName();
		} catch (Exception e) {
			// means the content is not stored locally
			return null;
		}

		try {
			content = Files.readAllBytes(Paths.get("./contents/" + key + "/" + fileName));
		} catch (IOException e) {
			Output.printError("Error while reading file: " + "./contents/" + key + "/" + fileName + ": " + e.toString());
			return null;
		}

		Output.printSuccess("Sent the following content to client: " + toDownload.toString());
		return content;
	}

	public List<DigitalContent> search(String toSearch, boolean partial) throws RemoteException {
		Output.printInfo("Got a partial content search request for content: " + toSearch);
		return centralServer.search(toSearch, partial);
	}

	public boolean doesUserOwnTheContent(int userId, String contentKey) throws RemoteException {
		DigitalContent content = centralServer.getContentFromKey(contentKey);
		return content.getUserOwnerKey() == userId;
	}

	public boolean isContentPasswordProtected(String contentKey) throws RemoteException {
		DigitalContent content = centralServer.getContentFromKey(contentKey);
		return !content.getPassword().equals("null");
	}

	public boolean isContentPasswordCorrect(String password, String contentKey) throws RemoteException {
		DigitalContent content = centralServer.getContentFromKey(contentKey);
		return content.getPassword().equals(password);
	}

	/**
	 * If the given password for the content is correct, it calls the central server
	 * to find the server storing the content to delete it and delete the content's info.
	 *
	 * @param password   of the content to delete.
	 * @param contentKey identifier of the content to delete.
	 * @return true or false whether the removal is successful or not.
	 * @throws RemoteException
	 */
	public boolean deleteContent(String password, String contentKey) throws RemoteException {
		// check if password is correct
		if (!isContentPasswordCorrect(password, contentKey)) {
			Output.printWarning("Removal failed. Client entered the wrong password for content with key: " + contentKey);
			return false;
		}

		return centralServer.deleteContent(password, contentKey);
	}

	public int getUserIdFromUsername(String username) throws RemoteException {
		return this.centralServer.getUserIdFromUsername(username);
	}

	public List<DigitalContent> listContents() throws RemoteException {
		try {
			return centralServer.listContents();
		} catch (Exception e) {
			Output.printError("Whilst listing all contents from all servers: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deletes the content that is locally stored (and the folder that contains it).
	 * This method will be called from the central server once the latter gets a removal
	 * request.
	 *
	 * @param toDelete object containing information about the content to delete.
	 * @return true or false whether the removal is successful or not.
	 * @throws RemoteException
	 */
	public boolean deleteContentLocallyStored(DigitalContent toDelete) throws RemoteException {
		Output.printInfo("Got a content removal request for content: " + toDelete.toString());
		String contentKey = Integer.toString(toDelete.getKey());
		// delete stored content
		if (!deleteFolder(new File("./contents/" + contentKey))) {
			Output.printError("Couldn't delete folder ./contents/" + contentKey);
			return false;
		}

		Output.printSuccess("Deleted the following content from the server: " + toDelete.toString());

		return true;
	}

	private boolean deleteFolder(File folderToDelete) {
		// delete recursively
		File[] contents = folderToDelete.listFiles();
		if (contents != null) {
			for (File file : contents) {
				deleteFolder(file);
			}
		}

		// delete folder
		return folderToDelete.delete();
	}

	/**
	 * If the given password for the content is correct, it calls the central server
	 * to find the server storing the content to rename it.
	 *
	 * @param password   of the content to rename.
	 * @param contentKey identifier of the content to rename.
	 * @param newName    new title/name for the content to be set.
	 * @return true or false whether the renaming operation is successful or not.
	 * @throws RemoteException
	 */
	public boolean renameContent(String password, String contentKey, String newName) throws RemoteException {
		// check if password is correct
		if (!isContentPasswordCorrect(password, contentKey)) {
			Output.printWarning("Renaming failed. Client entered the wrong password for content with key: " + contentKey);
			return false;
		}

		return centralServer.renameContent(password, contentKey, newName);
	}
}
