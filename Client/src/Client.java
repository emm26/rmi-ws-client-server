import common.DigitalContent;
import common.Output;
import common.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class Client{
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
			//System.out.println("Press 2 to search a content by its title or partial title");
			//System.out.println("Press 3 to search a content by its description or partial description");
			Output.simplePrint("		   Press 4 to upload a new content");
			Output.simplePrint("		   Press 5 to delete a content by its key");
			//System.out.println("Press 6 to search a content by its partial description");
			Output.simplePrint("		   Press 7 to exit");

			int chosenNum = -1;

			try {
				chosenNum = Integer.parseInt(sc.readLine());
			} catch (IOException e) {
				Output.printError("Error while reading/parsing input. Try again ");
				continue;
			}

			if (chosenNum == 1) {
				this.manageListContentsRequest();
			} else if (chosenNum == 4) {
				this.manageUploadContentRequest();
			} else if (chosenNum == 5) {
				this.manageDeleteContentRequest();
			} else if (chosenNum == 7) {
				this.manageExitRequest();
			} else {
				Output.printError("Integer " + chosenNum + " is not available. Try again");
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
					Output.simplePrint("-----------------------------------------------------");
					Output.simplePrint("KEY: " + content.getKey());
					Output.simplePrint("TITLE: " + content.getTitle());
					Output.simplePrint("DESCRIPTION: " + content.getDescription());
					Output.simplePrint("-----------------------------------------------------");
				}
			}

		} catch (RemoteException e) {
			Output.printError("Caught a RemoteException whilst listing contents: " + e.toString());
		}
	}

	private void manageUploadContentRequest() {
		try {
			// ask for the name of the file to upload
			Output.print("Enter the NAME of the file to upload (example; file.txt): ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			String name = s.readLine();

			// ask for the path of the file to upload
			Output.print("Enter the PATH the file to upload is located in (example; C:/example_dir): ");
			String path = s.readLine();
			Path filePath = Paths.get(path, name);
			byte[] fileInBytes = Files.readAllBytes(filePath);

			// ask for the description of the file
			Output.print("Enter the DESCRIPTION of the file to upload: ");
			String description = s.readLine();

			// ask if want to protect file with password
			Output.print("Would you like to protect the file with password to avoid other clients access? (Y/N)");
			String protect = s.readLine();
			String password = null;

			if (Objects.equals(protect, "Y")) {
				Output.printInfo("You chose to protect the file with password. Enter the desired password: ");
				password = s.readLine();
				Output.printSuccess("The password you entered will be requested when trying to retrieve file: " + name);

			} else {
				Output.printInfo("You chose NOT to protect the file with password");
			}

			if (this.stub.uploadContent(fileInBytes, name, description, password)) {
				Output.printSuccess("Content with name: " + name + " has been uploaded to server");
			} else {
				Output.printError("Couldn't upload content with name: " + name + " to server");
			}

		} catch (RemoteException e) {
			Output.printError("While listing contents: " + e.toString());

		} catch (IOException e) {
			Output.printError("While reading file: " + e.toString());
		}
	}


	private void manageDeleteContentRequest(){
		try {
			// ask for the key of the content to delete
			Output.print("Enter the id of the content to delete: ");
			BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
			int key = Integer.parseInt(s.readLine());

			String password = "null";
			// check if content is password protected
			if (this.stub.isContentPasswordProtected(key)){
				Output.printInfo("Content is password protected. Enter password: ");
				password = s.readLine();
			}

			if(!this.stub.deleteContent(password, key)){
				Output.printError("Couldn't delete content with key " + key);
			} else {
				Output.printSuccess("Content with key: " + key + " deleted");
			}

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