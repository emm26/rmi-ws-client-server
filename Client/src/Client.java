import common.DigitalContent;
import common.Output;
import common.ServerInterface;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class Client {
	private String host;
	private int port;
	private String registryName;
	private Registry registry;
	private ServerInterface stub;
	private ClientImplementation clientImp;

	public Client(String host, int port, String registryName) {
		this.host = host;
		this.port = port;
		this.registryName = registryName;
	}

	private void contactServer() {
		try {
			this.registry = LocateRegistry.getRegistry(this.host, this.port);
			this.stub = (ServerInterface) registry.lookup(registryName);
			this.clientImp = new ClientImplementation();
			this.stub.addConnectedClient(this.clientImp);

		} catch (Exception e) {
			Output.printError("Couldn't contact server: " + e.toString());
			// e.printStackTrace();
			System.exit(1);
		}
	}

	private void serviceLoop() {
		Output.print("Choose one of the following:");
		BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
		while (true) {

			Output.simplePrint("		   Press 1 to list contents");
			Output.simplePrint("		   Press 2 to search a content by its title");
			Output.simplePrint("		   Press 3 to search a content by its description");
			Output.simplePrint("		   Press 4 to search a content by its partial title");
			Output.simplePrint("		   Press 5 to search a content by its partial description");
			Output.simplePrint("		   Press 6 to upload a new content");
			Output.simplePrint("		   Press 7 to download a content");
			Output.simplePrint("		   Press 8 to delete a content");
			Output.simplePrint("		   Press 0 to exit");

			String chosenNum = "-1";

			try {
				chosenNum = sc.readLine();
			} catch (IOException e) {
				Output.printError("Error while reading/parsing input. Try again ");
				continue;
			}

			if (Objects.equals(chosenNum, "1")) {
				this.manageListContentsRequest();
			} else if (Objects.equals(chosenNum, "2")) {
				this.manageSearchContentFromTitleRequest();
			} else if (Objects.equals(chosenNum, "3")) {
				this.manageSearchContentFromDescriptionRequest();
			} else if (Objects.equals(chosenNum, "4")) {
				this.manageSearchContentsFromPartialTitleRequest();
			} else if (Objects.equals(chosenNum, "5")) {
				this.manageSearchContentFromPartialDescriptionRequest();
			} else if (Objects.equals(chosenNum, "6")) {
				this.manageUploadContentRequest();
			} else if (Objects.equals(chosenNum, "7")) {
				this.manageDownloadContentRequest();
			} else if (Objects.equals(chosenNum, "8")) {
				this.manageDeleteContentRequest();
			} else if (Objects.equals(chosenNum, "0")) {
				this.manageExitRequest();
			} else {
				Output.printError("Wrong option chosen: " + chosenNum + ". Try again");
			}

			Output.print("Choose one of the following:");

		}
	}

	private void manageListContentsRequest() {
		try {
			List<DigitalContent> contents = this.stub.listContents();

			if (contents.isEmpty()) {
				Output.printInfo("There are no contents available");
			} else {
				Output.printInfo("Listing contents");

				for (DigitalContent content : contents) {
					Output.simplePrint(content.toString());
				}
			}

		} catch (RemoteException e) {
			Output.printError("Caught a RemoteException whilst listing contents: " + e.toString());
		}
	}

	private void manageSearchContentFromTitleRequest() {
		try {
			Output.print("Enter the title of the content to search: ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			String title = s.readLine();

			DigitalContent matching = this.stub.searchContentFromTitle(title);

			if (matching == null) {
				Output.printError("Content with title: " + title + " does not exist");
			} else {
				Output.printSuccess("Content with title " + title + "found: \n" + matching.toString());
			}

		} catch (Exception e) {
			Output.printError("Whilst reading input: " + e.toString());
		}
	}

	private void manageSearchContentFromDescriptionRequest() {
		try {
			Output.print("Enter the description of the content to search: ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			String description = s.readLine();

			List<DigitalContent> matching = this.stub.searchContentsFromDescription(description);

			if (matching.isEmpty()) {
				Output.printError("There is not any content with description: " + description);
			} else {
				Output.printSuccess("There is " + matching.size() + " contents found: ");
				for (DigitalContent content : matching) {
					Output.simplePrint(content.toString());
				}
			}
		} catch (Exception e) {
			Output.printError("Whilst reading input: " + e.toString());
		}

	}

	private void manageSearchContentsFromPartialTitleRequest() {
		try {
			Output.print("Enter the partial title of the content to search: ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			String title = s.readLine();

			List<DigitalContent> matching = this.stub.searchContentsFromPartialTitle(title);

			if (matching.isEmpty()) {
				Output.printError("There is not any content with partial title: " + title);
			} else {
				Output.printSuccess("There is " + matching.size() + " contents found: ");
				for (DigitalContent content : matching) {
					Output.simplePrint(content.toString());
				}
			}
		} catch (Exception e) {
			Output.printError("Whilst reading input: " + e.toString());
		}

	}

	private void manageSearchContentFromPartialDescriptionRequest(){
		try {
			Output.print("Enter the partial description of the content to search: ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			String description = s.readLine();

			List<DigitalContent> matching = this.stub.searchContentsFromPartialDescription(description);

			if (matching.isEmpty()) {
				Output.printError("There is not any content with partial description: " + description);
			} else {
				Output.printSuccess("There is " + matching.size() + " contents found: ");
				for (DigitalContent content : matching) {
					Output.simplePrint(content.toString());
				}
			}
		} catch (Exception e) {
			Output.printError("Whilst reading input: " + e.toString());
		}
	}

	private void manageUploadContentRequest() {
		try {
			// ask for the name of the file to upload
			Output.print("Enter the NAME of the content to upload (example: file.txt): ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			String name = s.readLine();

			// ask for the path of the file to upload
			Output.print("Enter the full PATH to directory the content to upload is located in (example: '/Users/emm/Downloads'): ");
			String path = s.readLine();
			Path filePath = Paths.get(path, name);
			byte[] fileInBytes = Files.readAllBytes(filePath);

			// ask for the title of the file to upload
			Output.print("Enter the TITLE of the content to upload (example: file): ");
			String title = s.readLine();

			// ask for the description of the file
			Output.print("Enter the DESCRIPTION of the file to upload: ");
			String description = s.readLine();

			// ask if want to protect file with password
			Output.print("Would you like to protect the file with password to avoid other clients access? (Y/N)");
			String protect = s.readLine();
			String password = null;

			if (Objects.equals(protect, "Y")) {
				Output.printInfo("You chose to protect the content with password. Enter the desired password: ");
				password = s.readLine();
				Output.printSuccess("The password you entered will be requested when interacting with the content: " + name);

			} else {
				Output.printInfo("You chose NOT to protect the content with password");
			}

			if (this.stub.uploadContent(fileInBytes, title, description, password)) {
				Output.printSuccess("Content with title: " + title + " has been uploaded to server");
			} else {
				Output.printError("Couldn't upload content with title: " + title + " to server. Perhaps TITLE: " + title + " has been taken");
			}

		} catch (RemoteException e) {
			Output.printError("While uploading contents: " + e.toString());

		} catch (IOException e) {
			Output.printError("While reading file: " + e.toString());
		}
	}

	private void manageDownloadContentRequest() {
		try {
			// list available files
			Output.printInfo("Available contents to download are: ");
			this.manageListContentsRequest();

			// ask for the key of the content to download
			Output.print("Enter the key of the content to download");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			int key = Integer.parseInt(s.readLine());

			// check if content is password protected
			String password = "null";
			if (this.stub.isContentPasswordProtected(key)) {
				Output.printInfo("Content is password protected. Enter password: ");
				password = s.readLine();
			}

			byte[] downloaded = this.stub.downloadContent(password, key);
			if (downloaded == null) {
				Output.printError("Couldn't download content with key: " + key);
			} else {
				// ask for the new name of the file
				Output.print("Enter the new NAME for the downloaded content (example: file.txt): ");
				String name = s.readLine();

				// ask the full path of where to save the file
				Output.print("Enter the full PATH to directory where you want to save the content: (ex: '/Users/emm/Downloads')");
				String path = s.readLine();

				if (this.saveContent(downloaded, path, name)) {
					Output.printSuccess("Downloaded new content at: " + path + "/" + name);
				} else {
					Output.printError("Couldn't save new content at: " + path + "/" + name);
				}
			}

		} catch (Exception e) {
			Output.printError("While downloading content: " + e.toString());
		}
	}

	private boolean saveContent(byte[] content, String path, String name) {
		this.createFolder(String.valueOf(path));

		try (FileOutputStream fos = new FileOutputStream(path + "/" + name)) {
			fos.write(content);
			fos.close();
			return true;

		} catch (Exception e) {
			Output.printError("Couldn't place content in: " + path + "/" + name);
			//e.printStackTrace();
			return false;
		}
	}

	private void createFolder(String path) {
		try {
			new File(path).mkdirs();

		} catch (Exception e) {
			Output.printError("Couldn't create directory: " + path + " for new downloaded content");
			// e.printStackTrace();
		}

	}

	private void manageDeleteContentRequest() {
		try {
			// list available files
			Output.printInfo("Available contents to delete are: ");
			this.manageListContentsRequest();

			// ask for the key of the content to delete
			Output.print("Enter the key of the content to delete: ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			int key = Integer.parseInt(s.readLine());

			// check if content is password protected
			String password = "null";
			if (this.stub.isContentPasswordProtected(key)) {
				Output.printInfo("Content is password protected. Enter password: ");
				password = s.readLine();
			}

			if (!this.stub.deleteContent(password, key)) {
				Output.printError("Couldn't delete content with key: " + key);
			} else {
				Output.printSuccess("Content with key: " + key + " deleted");
			}

		} catch (NullPointerException e) {
			Output.printError("Content does not exist");
		} catch (Exception e) {
			Output.printError("While deleting content: " + e.toString());
		}
	}

	private void manageExitRequest() {
		try {
			this.stub.removeConnectedClient(this.clientImp);

		} catch (RemoteException e) {
			Output.printError("Caught a RemoteException whilst exiting client: " + e.toString());
		}
		Output.printWarning("Exiting client");
		System.exit(0);
	}


	public static void main(String[] args) {

		if (args.length < 2) {
			Output.printError("Usage: <host> <port> [registry_name]");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String registryName = (args.length < 3) ? "MyTube" : args[2];

		Client client = new Client(host, port, registryName);
		client.contactServer();
		Output.printSuccess("Connected to the server at " + host + ":" + port);

		client.serviceLoop();

	}

}
