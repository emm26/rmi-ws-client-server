
package common;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{
	void notifyStoppedServer() throws RemoteException;
}
