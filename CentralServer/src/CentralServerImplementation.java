
import common.CentralServerInterface;
import common.DigitalContent;
import common.Output;
import common.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CentralServerImplementation extends UnicastRemoteObject implements CentralServerInterface {

	private int lastServerIdentifier;
	List<ServerInterface> connectedServers;

	public CentralServerImplementation() throws RemoteException {
		lastServerIdentifier = 0;
		connectedServers = new ArrayList<>();
	}

	public void addConnectedServer(ServerInterface server) throws RemoteException {
		connectedServers.add(server);
		Output.printSuccess("Got a server connection. (Connected servers: " + connectedServers.size() + ")");
	}

	public synchronized int getConnectedServerIdentifier() throws RemoteException {
		lastServerIdentifier += 1;
		return lastServerIdentifier;
	}

	public void removeConnectedServer(ServerInterface server) throws RemoteException {
		connectedServers.remove(server);
		Output.printWarning("A server has disconnected  (Connected servers: " + connectedServers.size() + ")");
	}

	public byte[] downloadContent(String key) throws RemoteException {
		Output.printInfo("Got a content download request for content with key: " + key);

		sortConnectedServers();

		for (ServerInterface server : connectedServers) {
			byte[] content = server.downloadContentLocallyStored(key);
			if (content != null) {
				Output.printSuccess("Sent content to client");
				return content;
			}

		}
		Output.printWarning("Could not find content with key " + key + ". Download operation failed");
		return null;
	}

	/*
	This function sorts the List of connectedServers List<ServerInterface>
	according to the number of contents each server owns. The server with
	more contents will be the first on the list and the others that own less
	contents will follow.

	This will allow for an overall likely quicker search on the servers.
	 */
	private void sortConnectedServers() {
		connectedServers.sort((s1, s2) -> {
			int comparison = 0;
			try {
				// we want to sort from bigger to smaller, hence why multiplying -1
				comparison = Integer.compare(s1.getNumOfStoredContents(), s2.getNumOfStoredContents()) * -1;
			} catch (Exception e) {
				Output.printError("While comparing two ServerInterfaces: " + e.toString());
			}
			return comparison;
		});
	}

	public List<DigitalContent> searchContentsFromTitle(String title) throws RemoteException {
		List<DigitalContent> matching = new ArrayList<>();
		Output.printInfo("Got a content search request for content with title: " + title);

		sortConnectedServers();

		for (ServerInterface server : connectedServers) {
			List<DigitalContent> matchingOnServer;
			matchingOnServer = server.localSearchContentsFromTitle(title);

			if (!matchingOnServer.isEmpty()) {
				matching.addAll(matchingOnServer);
			}
		}

		return matching;
	}

	public List<DigitalContent> searchContentsFromDescription(String description) throws RemoteException {
		List<DigitalContent> matching = new ArrayList<>();
		Output.printInfo("Got a content search request for content with description: " + description);

		sortConnectedServers();

		for (ServerInterface server : connectedServers) {
			List<DigitalContent> matchingOnServer;
			matchingOnServer = server.localSearchContentsFromDescription(description);

			if (!matchingOnServer.isEmpty()) {
				matching.addAll(matchingOnServer);
			}
		}

		return matching;
	}

	public List<DigitalContent> searchContentsFromPartialTitle(String title) throws RemoteException {
		List<DigitalContent> matching = new ArrayList<>();
		Output.printInfo("Got a content search request for content with partial title: " + title);

		sortConnectedServers();

		for (ServerInterface server : connectedServers) {
			List<DigitalContent> matchingOnServer;
			matchingOnServer = server.localSearchContentsFromPartialTitle(title);

			if (!matchingOnServer.isEmpty()) {
				matching.addAll(matchingOnServer);
			}
		}

		return matching;
	}

	public List<DigitalContent> searchContentsFromPartialDescription(String description) throws RemoteException {
		List<DigitalContent> matching = new ArrayList<>();
		Output.printInfo("Got a content search request for content with partial description: " + description);

		sortConnectedServers();

		for (ServerInterface server : connectedServers) {
			List<DigitalContent> matchingOnServer;
			matchingOnServer = server.localSearchContentsFromPartialDescription(description);

			if (!matchingOnServer.isEmpty()) {
				matching.addAll(matchingOnServer);
			}
		}

		return matching;
	}

	public List<DigitalContent> listContents() throws RemoteException {
		List<DigitalContent> allContents = new ArrayList<>();
		Output.printInfo("Got a list all contents request");

		for (ServerInterface server : connectedServers) {
			List<DigitalContent> contentsOnServer;
			contentsOnServer = server.listLocalContents();

			if (!contentsOnServer.isEmpty()) {
				allContents.addAll(contentsOnServer);
			}
		}

		return allContents;
	}

	public void notifyCentralServerStopped() {
		Output.printWarning("Notifying connected servers that central server stopped");
		for (ServerInterface server : connectedServers) {
			try {
				server.notifyCentralServerStopped();
			} catch (Exception e) {
				Output.printError("While notifying central server stopped: " + e.toString());
			}
		}
	}
}
