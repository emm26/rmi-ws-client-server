import common.CentralServerInterface;
import common.Output;
import common.ServerInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

	private String host;
	private int port;
	private String centralServerHost;
	private int centralServerPort;
	private String registryName;
	private Registry registry;
	private ServerImplementation serverImplementation;
	private CentralServerInterface centralServer;


	public Server(String host, int port, String centralServerHost, int centralServerPort, String registryName) {
		this.host = host;
		this.port = port;
		this.centralServerHost = centralServerHost;
		this.centralServerPort = centralServerPort;
		this.registryName = registryName;
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

	private void startServer(String contentsDBName) throws RemoteException {
		int serverIdentifier = centralServer.getConnectedServerIdentifier();


		System.setProperty("java.rmi.server.hostname", host);
		this.registry = startRegistry(port);
		Output.printInfo("RMI registry started at: " + host + ":" + port);
		serverImplementation = new ServerImplementation(contentsDBName, serverIdentifier, centralServer);
		centralServer.addConnectedServer(serverImplementation);
		Output.printSuccess("Connected to the central server at: " + centralServerHost + ":" + centralServerPort + " with identifier: " + serverIdentifier);

		try {
			this.registry.bind(this.registryName, (ServerInterface) serverImplementation);
			Output.printInfo("Binded object to registry with name: " + registryName);
		} catch (AlreadyBoundException e) {
			Output.printError("While binding object: " + e.toString());
		}
		Output.printSuccess("Server is now ready to receive clients");
	}

	private void exitServer() {
		try {
			centralServer.removeConnectedServer(serverImplementation);
			serverImplementation.exit();
			this.registry.unbind(this.registryName);
			UnicastRemoteObject.unexportObject(serverImplementation, true);
		} catch (ConnectException ce){
			Output.printWarning("Central server stopped");
		} catch (Exception e) {
			Output.printError("Couldn't exit server: " + e.toString());
		}
	}

	private void connectToCentralServer() {
		try {
			Registry centralServerRegistry = LocateRegistry.getRegistry(this.centralServerHost, this.centralServerPort);
			centralServer = (CentralServerInterface) centralServerRegistry.lookup("MyTubeCentralServer");

		} catch (Exception e) {
			Output.printError("Couldn't locate central server");
			Output.printInfo("You must start central server first. After starting it, then you can run this Server as: <host> <port> <central_server_host> <central_server_port> [contents_db_name] [registry_name]");
			//e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			Output.printError("Usage: <host> <port> <central_server_host> <central_server_port> [contents_db_name] [registry_name]");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String centralServerHost = args[2];
		int centralServerPort = Integer.parseInt(args[3]);
		String contentsDBName = (args.length < 5) ? "contentsDB" : args[4];
		String registryName = (args.length < 6) ? "MyTube" : args[5];

		Server server = new Server(host, port, centralServerHost, centralServerPort, registryName);

		try {
			server.connectToCentralServer();
			server.startServer(contentsDBName);
		} catch (Exception e) {
			Output.printError("Server exception: " + e.toString());
			// e.printStackTrace();
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
