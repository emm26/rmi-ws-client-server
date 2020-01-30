
import common.CentralServerInterface;
import common.ServerInterface;
import entities.Server;
import utils.Output;

import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain {

	private String host;
	private int port;
	private String centralServerHost;
	private int centralServerPort;
	private String registryName;
	private Registry registry;
	private ServerImplementation serverImplementation;
	private CentralServerInterface centralServer;

	public ServerMain(String host, int port, String centralServerHost, int centralServerPort, String registryName) {
		this.host = host;
		this.port = port;
		this.centralServerHost = centralServerHost;
		this.centralServerPort = centralServerPort;
		this.registryName = registryName;
	}

	/**
	 * Starts the registry at the given port and binds the ServerImplementation object.
	 * Once started the server will be ready to receive clients.
	 *
	 * @throws RemoteException
	 */
	private void startServer() throws RemoteException {
		// start registry
		System.setProperty("java.rmi.server.hostname", host);
		this.registry = startRegistry(port);
		Output.printInfo("RMI registry started at: " + host + ":" + port);

		// export server stub to central server
		serverImplementation = new ServerImplementation(centralServer);
		Server serverToAdd = new Server(host, Integer.toString(this.port));
		if (!centralServer.addConnectedServer(serverToAdd, serverImplementation)) {
			Output.printError("Server couldn't be added to the central server");
			System.exit(1);
		}

		// get server identifier
		int serverIdentifier = centralServer.getServerIdFromIPPort(this.host, Integer.toString(this.port));
		serverImplementation.setServerId(serverIdentifier);
		Output.printSuccess("Connected to the central server at: " + centralServerHost + ":" + centralServerPort + " with identifier: " + serverIdentifier);

		// bind registry
		try {
			this.registry.bind(this.registryName, (ServerInterface) serverImplementation);
			Output.printInfo("Binded object to registry with name: " + registryName);
		} catch (AlreadyBoundException e) {
			Output.printError("While binding object: " + e.toString());
		}
		Output.printSuccess("Server is now ready to receive users");
	}

	private static Registry startRegistry(Integer port) throws RemoteException {
		try {
			Registry registry = LocateRegistry.getRegistry(port);
			registry.list(); // exception if registry non existent
			Output.printWarning("RMI registry already existent at port: " + port);
			return registry;
		} catch (RemoteException e) {
			Registry registry = LocateRegistry.createRegistry(port);
			return registry;
		}
	}

	/**
	 * Calls the central server stub to remove the server from the connectedServers list.
	 * It also unbinds the registry and unexports the serverImplementation object.
	 */
	private void exitServer() {
		try {
			// remove server stub from central server
			Server serverToRemove = new Server(this.serverImplementation.getServerId(), this.host, Integer.toString(this.port));
			centralServer.removeConnectedServer(serverToRemove, serverImplementation);
			serverImplementation.exit();

			// unbind registry and unexport
			this.registry.unbind(this.registryName);
			UnicastRemoteObject.unexportObject(serverImplementation, true);

		} catch (ConnectException ce) {
			Output.printWarning("Central server stopped");
		} catch (Exception e) {
			Output.printError("Couldn't exit server: " + e.toString());
		}
	}

	/**
	 * Looks up the central server stub that will be used for further communications server -> central server.
	 */
	private void connectToCentralServer() {
		try {
			Registry centralServerRegistry = LocateRegistry.getRegistry(this.centralServerHost, this.centralServerPort);
			centralServer = (CentralServerInterface) centralServerRegistry.lookup("MyTubeCentralServer");

		} catch (Exception e) {
			Output.printError("Couldn't locate central server");
			Output.printInfo("You must start central server first. After starting it, then you can run this ServerMain as: <host> <port> <central_server_host> <central_server_port> [contents_db_name] [registry_name]");
			//e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			Output.printError("Usage: <host> <port> <central_server_host> <central_server_port> [registry_name]");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String centralServerHost = args[2];
		int centralServerPort = Integer.parseInt(args[3]);
		String registryName = (args.length < 5) ? "MyTube" : args[4];

		ServerMain server = new ServerMain(host, port, centralServerHost, centralServerPort, registryName);

		try {
			server.connectToCentralServer();
			server.startServer();

		} catch (Exception e) {
			Output.printError("ServerMain exception: " + e.toString());
			e.printStackTrace();
		}

		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void start() {
				try {
					mainThread.join();
				} catch (Exception e) {
					Output.printError("Couldn't join main thread");
				}
				server.exitServer();
				Output.printWarning("Exiting server");
				System.exit(0);
			}
		});
	}
}
