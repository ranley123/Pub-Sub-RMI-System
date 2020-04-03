import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
    void notify(Message message) throws RemoteException;
}
