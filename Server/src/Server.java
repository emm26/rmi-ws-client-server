
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

import common.Output;
import common.ServerInterface;

public class Server {

	private String host;
	private int port;
	private String registryName;
	private Registry registry;
	private ServerImplementation sImp;


	public Server(String host, int port, String registryName){
		this.host = host;
		this.port = port;
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
			Output.printInfo("RMI registry created at port: " + port);
			return registry;
		}
	}

	private void startServer() throws RemoteException{
		//System.setProperty("java.rmi.server.hostname", host);
		this.registry = startRegistry(this.port);
		sImp = new ServerImplementation();
		try {
			this.registry.bind(this.registryName, (ServerInterface) sImp);
		} catch (AlreadyBoundException e) {
			Output.printError("While binding object: " + e.toString());
		}
		Output.printSuccess("Server is now ready to receive clients");
	}

	private void exitServer() {
		try{
			sImp.exit();
			this.registry.unbind(this.registryName);
			UnicastRemoteObject.unexportObject(sImp, true);

		} catch (Exception e){
			Output.printError("Couldn't exit server: " + e.toString());
		}
	}

	public static void main(String[] args) {

		if (args.length < 2){
			Output.printError("Usage: <host> <port> [registry_name]");
			System.exit(1);
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String registryName = (args.length < 3) ? "MyTube" : args[2];

		Server server = new Server(host, port, registryName);

		try {
			server.startServer();

		} catch (Exception e) {
			Output.printError("Server exception: " + e.toString());
			// e.printStackTrace();
		}

		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void start() {
				Output.printWarning("Exiting server");
				try {
					mainThread.join();
				} catch (Exception e) {
					Output.printError("Couldn't join main thread");
				}
				server.exitServer();
				System.exit(0);
			}
		});

	}
}
