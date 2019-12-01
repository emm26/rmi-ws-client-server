
import common.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

	private ContentDatabase contentsDb;
	private Set<ClientInterface> connectedClients;
	private CentralServerInterface centralServer;
	private int serverIdentifier;

	public ServerImplementation(String contentsDBName, int serverIdentifier, CentralServerInterface centralServer) throws RemoteException {
		this.centralServer = centralServer;
		this.serverIdentifier = serverIdentifier;
		connectedClients = new HashSet<>();
		contentsDb = new ContentDatabase(contentsDBName, serverIdentifier);
	}


	public void addConnectedClient(ClientInterface client) throws RemoteException {
		connectedClients.add(client);
		Output.printInfo("A client has connected. (Connected clients: " + connectedClients.size() + ")");
	}

	public void removeConnectedClient(ClientInterface client) throws RemoteException {
		connectedClients.remove(client);
		Output.printInfo("A client has disconnected. (Connected clients: " + connectedClients.size() + ")");
	}

	private void notifyClientsServerStopped() {
		Output.printWarning("Notifying connected clients that server stopped");
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
		contentsDb.disconnectFromDb();
	}

	public void notifyCentralServerStopped() throws RemoteException {
		exit();
		new Thread(() -> System.exit(0)).start();
	}

	public String uploadContent(byte[] content, String title, String description, String password, String fileName) throws RemoteException {

		Output.printInfo("Got an upload content request");
		// save content key, title, description and password (if existent) on database
		// title of the new content cannot be already existent.

		String key = contentsDb.addContent(title, description, password);
		if (key == null) {
			return null;
		}

		// save content to folder named the key
		if (saveContent(content, fileName, key)) {
			Output.printSuccess("New content with key: " + key + " has been uploaded at ./contents/" + key);
			Output.printSuccess("New content with key: " + key + " added to database");
			return key;
		} else {
			contentsDb.deleteContent(key);
			return null;
		}

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

	public boolean isContentPasswordProtected(String key) throws RemoteException {
		return (contentsDb.isContentPasswordProtected(key));
	}

	private boolean isContentPasswordCorrect(String password, String key) {
		boolean isCorrect = contentsDb.isContentPasswordCorrect(password, key);
		if (!isCorrect) {
			DigitalContent content = contentsDb.getContentFromKey(key);
			Output.printWarning("Removal failed. Client entered the wrong password for content: " + content.toString());
			String correctPassword = content.getPassword();
			Output.printInfo("Correct password for content with key: " + key + " is: " + correctPassword);
			return false;
		}
		Output.printSuccess("Client entered the correct password for content with key: " + key);
		return true;
	}

	public boolean deleteContent(String password, String key) throws RemoteException {
		DigitalContent toDelete = contentsDb.getContentFromKey(key);
		Output.printInfo("Got a content removal request for content: " + toDelete.toString());

		// check if content exists
		if (toDelete == null) {
			return false;
		}
		// check if password is correct
		if (!isContentPasswordCorrect(password, key)) {
			return false;
		}

		// delete from database
		if (!contentsDb.deleteContent(key)) {
			Output.printError("Couldn't delete the following content from the database: " + toDelete.toString());
			return false;
		}

		// delete stored content
		if (!deleteFolder(new File("./contents/" + key))) {
			Output.printError("Couldn't delete folder ./contents/" + key);
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

	public boolean renameContent(String password, String key, String newName) throws RemoteException {
		DigitalContent toRename = contentsDb.getContentFromKey(key);
		Output.printInfo("Got a content renaming request for content: " + toRename.toString());

		// check if content exists
		if (toRename == null) {
			return false;
		}
		// check if password is correct
		if (!isContentPasswordCorrect(password, key)) {
			return false;
		}

		// rename on database
		if (!contentsDb.renameContent(key, newName)) {
			Output.printError("Couldn't rename the following content on the database: " + toRename.toString());
			return false;
		}

		Output.printSuccess("Renamed the following content from the server: " + toRename.toString());

		return true;
	}

	public byte[] downloadContentLocallyStored(String key) throws RemoteException {
		byte[] content;
		DigitalContent toDownload;
		String fileName;

		try {
			toDownload = contentsDb.getContentFromKey(key);
			Output.printInfo("Got a content download request for content: " + toDownload.toString());
			// get the name of the file inside the folder
			fileName = new File("./contents/" + key).listFiles()[0].getName();
		} catch (Exception e){
			// means the content is not stored locally, must perform a global search
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

	public byte[] downloadContentNotLocallyStored(String key) throws RemoteException {
		return centralServer.downloadContent(key);
	}


	public List<DigitalContent> localSearchContentsFromTitle(String title) throws RemoteException {
		return contentsDb.getContentsFromTitle(title);
	}

	public List<DigitalContent> localSearchContentsFromDescription(String description) throws RemoteException {
		return contentsDb.getContentsFromDescription(description);
	}

	public List<DigitalContent> localSearchContentsFromPartialTitle(String title) throws RemoteException {
		return contentsDb.getContentsFromPartialTitle(title);
	}

	public List<DigitalContent> localSearchContentsFromPartialDescription(String description) throws RemoteException {
		return contentsDb.getContentsFromPartialDescription(description);
	}

	public List<DigitalContent> listLocalContents() throws RemoteException {
		return contentsDb.getAllContents();
	}

	public List<DigitalContent> listGlobalContents() throws RemoteException {
		List<DigitalContent> contents = new ArrayList<>();
		try {
			contents = centralServer.listContents();
		} catch (Exception e) {
			Output.printError("Whilst listing all contents from all servers: " + e.toString());
			e.printStackTrace();
		}
		return contents;
	}

	public List<DigitalContent> globalSearchContentsFromTitle(String title) throws RemoteException {
		return centralServer.searchContentsFromTitle(title);
	}

	public List<DigitalContent> globalSearchContentsFromDescription(String description) throws RemoteException {
		return centralServer.searchContentsFromDescription(description);
	}

	public List<DigitalContent> globalSearchContentsFromPartialTitle(String title) throws RemoteException {
		return centralServer.searchContentsFromPartialTitle(title);
	}

	public List<DigitalContent> globalSearchContentsFromPartialDescription(String description) throws RemoteException {
		return centralServer.searchContentsFromPartialDescription(description);
	}

}
