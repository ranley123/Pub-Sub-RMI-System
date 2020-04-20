import Exceptions.DataLossException;
import Exceptions.DuplicateException;
import Exceptions.QueueIsFullException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface IServer extends Remote{
    void subscribe(String channel, String subscriberName) throws RemoteException;
    void unsubscribe(String channel, String subscriberName) throws RemoteException;
    void publish(Event event) throws RemoteException;
    void register(int port, String name) throws RemoteException;
    void notify(Message message) throws RemoteException;
    void addEvent(Event event) throws RemoteException, DataLossException, QueueIsFullException, DuplicateException;
    void addMessage(Message message) throws RemoteException, DataLossException;
    void addUUID(UUID uuid) throws RemoteException, DuplicateException;
}
