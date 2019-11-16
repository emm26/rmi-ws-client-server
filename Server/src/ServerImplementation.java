
import common.DigitalContent;
import common.Output;
import common.ServerInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

	private List<DigitalContent> contents;
	private List<String> keys;

	public ServerImplementation() throws RemoteException {
		contents = new ArrayList<>();
		keys = new ArrayList<>();
	}

	public String uploadContent(byte[] content, String userId, String host, int port, String title, String description) throws RemoteException {
		String key = generateKey();
		DigitalContent contentObj = new DigitalContent(content, key, title, description);
		contents.add(contentObj);

		// save content to folder named the key
		if (!saveContent(content, title, key)) {
			return "Uploaded failed";
		}

		return "Successfully uploaded";

	}

	private boolean saveContent(byte[] content, String title, String key) {
		createFolder(key);

		try (FileOutputStream fos = new FileOutputStream("/contents/" + key + "/" + title)) {
			fos.write(content);
			fos.close();
			return true;

		} catch (Exception e) {
			Output.printError("Couldn't place content in: /contents/" + key);
			e.printStackTrace();
			return false;
		}
	}

	private static void createFolder(String folderName) {
		try {
			new File("/contents/" + folderName).mkdirs();
			Output.printSuccess("Created directory: /contents/" + folderName + " for new uploaded content");

		} catch (Exception e) {
			Output.printError("Couldn't create directory: /contents/" + folderName + " for new uploaded content");
			// e.printStackTrace();
		}

	}

	/*
	public void deleteContent(String id) throws RemoteException {

	}

	public void modifyContentTitle() throws RemoteException {

	}

	public byte[] downloadContentByTitle(String[] title) throws RemoteException {
		return (byte[]) 0x00;
	}

	public byte[] downloadContentByDescription(String[] description) throws RemoteException {

	}
	*/

	public List<String> listContents() throws RemoteException {

		List<String> contentsTitles = new ArrayList<>();

		for (DigitalContent content : contents) {
			contentsTitles.add(content.getTitle());
		}
		return contentsTitles;
	}


	private String generateKey() {
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		try {
			while (!keys.isEmpty() && keys.contains(key)) {
				key = UUID.randomUUID().toString().replaceAll("-", "");
			}
			keys.add(key);

		} catch (Exception e) {
			Output.printError("Error while generating key: " + e.toString());
		}
		return key;
	}
}
