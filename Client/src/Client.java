import common.Output;
import common.ServerInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Client {
	private String host;
	private int port;
	private String registryName;
	private Registry registry;
	private ServerInterface stub;

	public Client(String host, int port, String registryName) {
		this.host = host;
		this.port = port;
		this.registryName = registryName;
	}

	private void contactServer() throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry(this.host, this.port);
			this.stub = (ServerInterface) registry.lookup(registryName);

		} catch (Exception e) {
			Output.printError("Couldn't contact server: " + e.toString());
			// e.printStackTrace();
		}
	}

	private void serviceLoop() {
		Output.print("Choose one of the following:");
		BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			Output.print("Press 1 to list contents");
			//System.out.println("Press 2 to search a content by its title");
			//System.out.println("Press 3 to search a content by its description");
			Output.print("Press 4 to upload a new content");
			//System.out.println("Press 5 to search a content by its partial title");
			//System.out.println("Press 6 to search a content by its partial description");
			Output.print("Press 7 to exit");

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
			} else if (chosenNum == 7) {
				this.manageExitRequest();
			} else {
				Output.printError("Integer " + String.valueOf(chosenNum) + " is not available. Try again");
			}

			Output.print("Choose one of the following:");

		}
	}

	private void manageListContentsRequest() {
		try {
			List<String> contents = this.stub.listContents();

			if (contents.isEmpty()) {
				Output.printInfo("There are no contents available");
			} else {
				for (String content : contents) {
					Output.print(content);
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
			byte[] fileInBytes =  Files.readAllBytes(filePath);
			// ask for the description of the file
			Output.print("Enter the DESCRIPTION of the file to upload: ");
			String description = s.readLine();

			this.stub.uploadContent(fileInBytes, "", "", 0, name, description);

		} catch (RemoteException e){
			Output.printError("Caught a RemoteException whilst listing contents: " + e.toString());
		} catch (IOException e) {
			Output.printError("Error while reading file: " + e.toString());
		}
	}

	private void manageExitRequest(){
		Output.printInfo("Exiting client");
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

		try {
			client.contactServer();
		} catch (Exception e) {
			Output.printError("Exiting client");
			System.exit(1);
		}

		Output.printSuccess("Successfully connected to the server");
		client.serviceLoop();

	}

}
