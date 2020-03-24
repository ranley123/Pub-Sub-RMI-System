import java.rmi.Remote;
import java.rmi.RemoteException;
public interface IServer extends Remote{
    public void subscribe(String channel, SubscriberClient subscriber) throws RemoteException;
    public void unsubscribe(String channel, SubscriberClient subscriber) throws RemoteException;
    public void publish(Event event) throws RemoteException;
    public void update(Event event) throws RemoteException;
    public String ping() throws RemoteException;

    public void addEvent(Event event) throws RemoteException;
//    void delete(Event event);
}
