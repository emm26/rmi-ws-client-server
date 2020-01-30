
import utils.Output;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CentralServerMain {

	private String host;
	private int port;
	private String registryName;
	private CentralServerImplementation centralServerImplementation;
	private Registry registry;

	public CentralServerMain(String host, int port, String registryName) {
		this.host = host;
		this.port = port;
		this.registryName = registryName;
	}

	/**
	 * Starts the registry at the given port and binds the CentralServerImplementation object.
	 * Once started the central server will be ready to receive other servers.
	 *
	 * @throws RemoteException
	 */
	private void startServer() throws RemoteException {
		System.setProperty("java.rmi.server.hostname", host);
		this.registry = startRegistry(port);
		Output.printInfo("RMI registry started at: " + host + ":" + port);

		centralServerImplementation = new CentralServerImplementation();

		try {
			this.registry.bind(this.registryName, centralServerImplementation);
			Output.printInfo("Binded object to registry with name: " + registryName);
		} catch (AlreadyBoundException e) {
			Output.printError("While binding object: " + e.toString());
		}
		Output.printSuccess("Central Server is now ready to receive servers");
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
	 * Calls the binded object in order to notify all connected servers
	 * that the central server is about to exit.
	 */
	private void exitServer() {
		centralServerImplementation.notifyCentralServerStopped();
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			Output.printError("Usage: <host> <port>");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String registryName = "MyTubeCentralServer";

		CentralServerMain cs = new CentralServerMain(host, port, registryName);

		try {
			cs.startServer();
		} catch (Exception e) {
			Output.printError("Central server exception: " + e.toString());
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
				cs.exitServer();
				Output.printWarning("Exiting central server");
				System.exit(0);
			}
		});
	}
}
