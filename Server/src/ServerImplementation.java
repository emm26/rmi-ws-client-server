
import common.ClientInterface;
import common.DigitalContent;
import common.Output;
import common.ServerInterface;
import sun.security.pkcs11.wrapper.CK_DESTROYMUTEX;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

	private ContentDatabase contentsDb;
	private Set<ClientInterface> connectedClients;

	public ServerImplementation() throws RemoteException {
		connectedClients = new HashSet<>();
		contentsDb = new ContentDatabase("contentsDB");
	}

	public void addConnectedClient(ClientInterface client) throws RemoteException {
		connectedClients.add(client);
		Output.printInfo("A client has connected. (Connected clients: " + connectedClients.size() + " )");
	}

	public void removeConnectedClient(ClientInterface client) throws RemoteException {
		connectedClients.remove(client);
		Output.printInfo("A client has disconnected. (Connected clients: " + connectedClients.size() + ")");
	}

	private void notifyClientsServerStopped() {
		for (ClientInterface client : this.connectedClients) {
			try {
				client.notifyServerExit();
			} catch (RemoteException e) {
				Output.printError("While notifying that server stopped: " + e.toString());
			}
		}
	}

	public void exit() {
		this.notifyClientsServerStopped();
		contentsDb.disconnectFromDb();
	}

	public boolean uploadContent(byte[] content, String title, String description, String password) throws RemoteException {

		Output.printInfo("Got an upload content request");
		// save content key, title, description and password (if existent) on database
		// title of the new content cannot be already existent.
		if(!contentsDb.addContent(title, description, password)){
			return false;
		}


		int key = contentsDb.getContentFromTitle(title).getKey();

		// save content to folder named the key
		if (saveContent(content, title, key)){
			Output.printSuccess("New content with key: " + key + " has been uploaded at ./contents/" + key);
			Output.printSuccess("New content with key: " + key + " added to database");
			return true;
		} else {
			contentsDb.deleteContent(key);
			return false;
		}

	}

	private boolean saveContent(byte[] content, String title, int key) {
		this.createFolder(String.valueOf(key));

		try (FileOutputStream fos = new FileOutputStream("./contents/" + key + "/" + title)) {
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
			if (new File("./contents/" + folderName).mkdirs()){
				Output.printSuccess("Created directory: ./contents/" + folderName + " for new uploaded content");
			} else {
				Output.printError("Couldn't create directory: ./contents/" + folderName + " for new uploaded content");
			}

		} catch (Exception e) {
			Output.printError("Couldn't create directory: ./contents/" + folderName + " for new uploaded content");
			// e.printStackTrace();
		}

	}

	public boolean isContentPasswordProtected(int key) throws RemoteException{
		return contentsDb.isContentPasswordProtected(key);
	}

	private boolean isContentPasswordCorrect(String password, int key) {
		return contentsDb.isContentPasswordCorrect(password, key);
	}

	public boolean deleteContent(String password, int key) throws RemoteException {

		Output.printInfo("Got a content removal request");

		// check if password is correct
		if (!this.isContentPasswordCorrect(password, key)){
			Output.printWarning("Client entered the wrong password for content with key: " + key + ", removal failed");
			String correctPassword = null;
			correctPassword = this.contentsDb.getContentFromKey(key).getPassword();
			Output.printInfo("Correct password for file with key: " + key + " is: " + correctPassword );
			return false;
		}


		// delete from database
		if (!this.contentsDb.deleteContent(key)){
			Output.printError("Couldn't delete content with key: " + key + " from database");
			return false;
		}
		Output.printSuccess("Deleted content with key: " + key + " from database");

		// delete stored content
		if (!this.deleteFolder(new File("./contents/" + key))){
			Output.printError("Couldn't delete folder ./contents/" + key);
			return false;
		}
		Output.printSuccess("Deleted directory: ./contents/" + key );
		return true;
	}

	private boolean deleteFolder(File folderToDelete){

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

	/*


	}

	public void modifyContentTitle() throws RemoteException {

	}

	public byte[] downloadContentByTitle(String[] title) throws RemoteException {
		return (byte[]) 0x00;
	}

	public byte[] downloadContentByDescription(String[] description) throws RemoteException {

	}
	*/

	public List<DigitalContent> listContents() throws RemoteException {
		return contentsDb.getAllContents();
	}

}
