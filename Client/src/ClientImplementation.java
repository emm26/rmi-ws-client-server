
import common.ClientInterface;
import common.Output;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImplementation extends UnicastRemoteObject implements ClientInterface {

	public ClientImplementation() throws RemoteException{

	}

	public void notifyStoppedServer() throws RemoteException {
		Output.printInfo("Server stopped, stopping client");
		System.exit(0);
	}

}
