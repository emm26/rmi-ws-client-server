
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Server {

	private static Registry startRegistry(Integer port) throws RemoteException {
		if (port == null) {
			port = 1099;
		}
		try {
			Registry registry = LocateRegistry.getRegistry(port);
			registry.list(); // exception if registry non existant
			System.out.println("INFO -> RMI registry already existent at port: " + port);
			return registry;
		} catch (RemoteException e) {
			Registry registry = LocateRegistry.createRegistry(port);
			System.out.println("INFO -> RMI registry created at port: " + port);
			return registry;
		}
	}

	public static void main(String[] args) {
		try {
			Registry registry = startRegistry(null);

		} catch (Exception e) {
			System.err.println("ERROR -> Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
