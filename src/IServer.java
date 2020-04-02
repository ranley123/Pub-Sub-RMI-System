import java.rmi.Remote;
import java.rmi.RemoteException;
public interface IServer extends Remote{
    public void subscribe(String channel, String subscriberName) throws RemoteException;
    public void unsubscribe(String channel, String subscriberName) throws RemoteException;
    public void publish(Event event) throws RemoteException;
    public void register(int port, String name) throws RemoteException;
    public void notify(Message message) throws RemoteException;
    public void receiveMessage(Message message) throws RemoteException;
    public int addEvent(Event event) throws RemoteException;
}
