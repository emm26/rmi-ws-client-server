import common.ClientInterface;
import utils.Output;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImplementation extends UnicastRemoteObject implements ClientInterface {

	public ClientImplementation() throws RemoteException {

	}

	public void notifyServerExit() throws RemoteException {
		Output.printWarning("Server stopped");
		Output.printWarning("Exiting client");
		//System.exit(0);
		new Thread(() -> System.exit(0)).start();
	}
}
